package in.project.GateKeeper.Management.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import org.springframework.stereotype.Service;

@Getter
@Service
public class LoginRequest {
    @NotBlank
    private String username;

    @NotBlank
    private String password;
}
