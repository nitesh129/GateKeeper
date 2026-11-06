package in.project.GateKeeper.Management.service;

import in.project.GateKeeper.Management.dto.request.OrganisationRequest;
import in.project.GateKeeper.Management.dto.request.OrganisationPatchRequest;
import in.project.GateKeeper.Management.dto.Response.OrganisationResponse;
import in.project.GateKeeper.Management.dto.request.TenantAdminRequest;
import in.project.GateKeeper.Management.dto.Response.TenantAdminResponse;
import in.project.GateKeeper.Management.entity.ManagementUsers;
import in.project.GateKeeper.Management.entity.Organisation;
import in.project.GateKeeper.Management.entity.OrganisationStatus;
import in.project.GateKeeper.Management.entity.Role;
import in.project.GateKeeper.Management.repository.ManagementUserRepo;
import in.project.GateKeeper.Management.repository.OrganisationRepo;
import in.project.GateKeeper.Management.repository.RoleRepo;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrganisationService {

    private final OrganisationRepo organisationRepo;
    private final ManagementUserRepo managementUserRepo;
    private final RoleRepo roleRepo;
    private final PasswordEncoder passwordEncoder;

    public OrganisationService(
            OrganisationRepo organisationRepo,
            ManagementUserRepo managementUserRepo,
            RoleRepo roleRepo,
            PasswordEncoder passwordEncoder) {
        this.organisationRepo = organisationRepo;
        this.managementUserRepo = managementUserRepo;
        this.roleRepo = roleRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public OrganisationResponse createOrganisation(OrganisationRequest organisationRequest) {
        ensureUniqueNameAndSlug(organisationRequest.getName(), organisationRequest.getSlug(), null);

        LocalDateTime now = LocalDateTime.now();
        Organisation organisation = new Organisation();
        organisation.setName(organisationRequest.getName());
        organisation.setSlug(organisationRequest.getSlug());
        organisation.setStatus(OrganisationStatus.Pending);
        organisation.setCreatedAt(now);
        organisation.setUpdatedAt(now);

        return OrganisationResponse.from(organisationRepo.save(organisation));
    }

    @Transactional(readOnly = true)
    public List<OrganisationResponse> getOrganisations() {
        return organisationRepo.findAllByStatusNot(OrganisationStatus.Deleted)
                .stream()
                .map(OrganisationResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public OrganisationResponse getOrganisation(Long id) {
        return OrganisationResponse.from(getActiveOrganisation(id));
    }

    @Transactional
    public OrganisationResponse updateOrganisation(Long id, OrganisationRequest organisationRequest) {
        Organisation organisation = getActiveOrganisation(id);
        ensureUniqueNameAndSlug(organisationRequest.getName(), organisationRequest.getSlug(), id);

        organisation.setName(organisationRequest.getName());
        organisation.setSlug(organisationRequest.getSlug());
        organisation.setUpdatedAt(LocalDateTime.now());
        return OrganisationResponse.from(organisationRepo.save(organisation)); // no need to write save in update method because we use Transactional, but it's good to have it for clarity and explicitness
    }

    @Transactional
    public OrganisationResponse patchOrganisation(Long id, OrganisationPatchRequest organisationPatchRequest) {
        if (organisationPatchRequest.getName() == null
                && organisationPatchRequest.getSlug() == null
                && organisationPatchRequest.getStatus() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "At least one field must be provided");
        }

        if (organisationPatchRequest.getStatus() == OrganisationStatus.Deleted) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Use the delete endpoint to delete an organisation");
        }

        Organisation organisation = getActiveOrganisation(id);
        String name = organisationPatchRequest.getName() != null
                ? organisationPatchRequest.getName()
                : organisation.getName();
        String slug = organisationPatchRequest.getSlug() != null
                ? organisationPatchRequest.getSlug()
                : organisation.getSlug();

        ensureUniqueNameAndSlug(name, slug, id);
        organisation.setName(name);
        organisation.setSlug(slug);
        if (organisationPatchRequest.getStatus() != null) {
            organisation.setStatus(organisationPatchRequest.getStatus());
        }
        organisation.setUpdatedAt(LocalDateTime.now());
        return OrganisationResponse.from(organisationRepo.save(organisation));
    }

    @Transactional
    public void deleteOrganisation(Long id) {
        Organisation organisation = getActiveOrganisation(id);
        LocalDateTime now = LocalDateTime.now();
        organisation.setStatus(OrganisationStatus.Deleted);
        organisation.setDeletedAt(now);
        organisation.setUpdatedAt(now);
        organisationRepo.save(organisation);
    }

    @Transactional
    public TenantAdminResponse createTenantAdmin(Long organisationId, TenantAdminRequest request) {
        Organisation organisation = getActiveOrganisation(organisationId);
        ensureTenantAdminUsernameAvailable(request.getUsername(), null);
        Role role = roleRepo.findByName("TENANT_ADMIN")
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR, "Tenant admin role is not configured"));
        LocalDateTime now = LocalDateTime.now();
        ManagementUsers user = new ManagementUsers();
        user.setOrganisation(organisation);
        user.setRole(role);
        user.setUsername(request.getUsername());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setEnabled(request.isEnabled());
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        return TenantAdminResponse.from(managementUserRepo.save(user));
    }

    @Transactional(readOnly = true)
    public TenantAdminResponse getTenantAdmin(Long organisationId) {
        getActiveOrganisation(organisationId);
        return TenantAdminResponse.from(getTenantAdminUser(organisationId));
    }

    @Transactional
    public TenantAdminResponse updateTenantAdmin(Long organisationId, TenantAdminRequest request) {
        getActiveOrganisation(organisationId);
        ManagementUsers user = getTenantAdminUser(organisationId);
        ensureTenantAdminUsernameAvailable(request.getUsername(), user.getId());

        user.setUsername(request.getUsername());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setEnabled(request.isEnabled());
        user.setUpdatedAt(LocalDateTime.now());
        return TenantAdminResponse.from(managementUserRepo.save(user));
    }

    @Transactional
    public void deleteTenantAdmin(Long organisationId) {
        getActiveOrganisation(organisationId);
        managementUserRepo.delete(getTenantAdminUser(organisationId));
    }

    private Organisation getActiveOrganisation(Long id) {
        return organisationRepo.findByIdAndStatusNot(id, OrganisationStatus.Deleted)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Organisation not found"));
    }

    private ManagementUsers getTenantAdminUser(Long organisationId) {
        return managementUserRepo.findByOrganisation_Id(organisationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tenant admin not found"));
    }

    private void ensureTenantAdminUsernameAvailable(String username, Long excludedId) {
        boolean usernameExists = excludedId == null
                ? managementUserRepo.existsByUsernameIgnoreCase(username)
                : managementUserRepo.existsByUsernameIgnoreCaseAndIdNot(username, excludedId);
        if (usernameExists) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
        }
    }

    private void ensureUniqueNameAndSlug(String name, String slug, Long excludedId) {
        boolean nameExists = excludedId == null
                ? organisationRepo.existsByNameIgnoreCase(name)
                : organisationRepo.existsByNameIgnoreCaseAndIdNot(name, excludedId);
        if (nameExists) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Organisation name already exists");
        }

        boolean slugExists = excludedId == null
                ? organisationRepo.existsBySlugIgnoreCase(slug)
                : organisationRepo.existsBySlugIgnoreCaseAndIdNot(slug, excludedId);
        if (slugExists) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Organisation slug already exists");
        }
    }
}
