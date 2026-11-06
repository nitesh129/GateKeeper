package in.project.GateKeeper.Management.dto.Response;

import in.project.GateKeeper.Management.entity.ManagementUsers;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TenantAdminResponse {
    private Long id;
    private String username;
    private boolean enabled;
    private Long organisationId;

    public static TenantAdminResponse from(ManagementUsers user) {
        return new TenantAdminResponse(
                user.getId(),
                user.getUsername(),
                user.isEnabled(),
                user.getOrganisation().getId()
        );
    }
}
