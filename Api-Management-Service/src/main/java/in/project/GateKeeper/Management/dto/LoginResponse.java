package in.project.GateKeeper.Management.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private String tokentype;
    private long expiresInSeconds;
    private String role;
    private Long organisationId;
}
