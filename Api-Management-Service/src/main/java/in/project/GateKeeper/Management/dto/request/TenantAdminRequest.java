package in.project.GateKeeper.Management.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class TenantAdminRequest {
    @NotBlank
    @Size(max=100)
    private String username;

    @NotBlank
    @Size(min=8 , max=100)
    private String password;

    private boolean enabled = true;
}
