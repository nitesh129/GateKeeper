package in.project.GateKeeper.Management.controller;

import in.project.GateKeeper.Management.dto.organisation.OrganisationRequest;
import in.project.GateKeeper.Management.dto.organisation.OrganisationPatchRequest;
import in.project.GateKeeper.Management.dto.organisation.OrganisationResponse;
import in.project.GateKeeper.Management.service.OrganisationService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/platform/organisation")
@AllArgsConstructor
public class PlatformOrganisationController {

    private final OrganisationService organisationService;

    // 1 Create
    @PostMapping
    public ResponseEntity<OrganisationResponse> createOrganisation(@Valid @RequestBody OrganisationRequest organisationRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(organisationService.createOrganisation(organisationRequest));
    }

    // Read all
    @GetMapping
    public ResponseEntity<List<OrganisationResponse>> getOrganisations() {
        return ResponseEntity.ok(organisationService.getOrganisations());
    }

    // read one
    @GetMapping("/{id}")
    public ResponseEntity<OrganisationResponse> getOrganisation(@PathVariable Long id) {
        return ResponseEntity.ok(organisationService.getOrganisation(id));
    }

    // update
    @PutMapping("/{id}")
    public ResponseEntity<OrganisationResponse> updateOrganisation(
            @PathVariable Long id,
            @Valid @RequestBody OrganisationRequest organisationRequest) {
        return ResponseEntity.ok(organisationService.updateOrganisation(id, organisationRequest));
    }

    // patch
    @PatchMapping("/{id}")
    public ResponseEntity<OrganisationResponse> patchOrganisation(
            @PathVariable Long id,
            @Valid @RequestBody OrganisationPatchRequest organisationPatchRequest) {
        return ResponseEntity.ok(organisationService.patchOrganisation(id, organisationPatchRequest));
    }

    // soft delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrganisation(@PathVariable Long id) {
        organisationService.deleteOrganisation(id);
        return ResponseEntity.noContent().build();
    }

    // CRUD operations for Tenant admin.

}
