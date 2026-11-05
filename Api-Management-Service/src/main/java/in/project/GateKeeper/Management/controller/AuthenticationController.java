package in.project.GateKeeper.Management.controller;

import in.project.GateKeeper.Management.dto.LoginRequest;
import in.project.GateKeeper.Management.dto.LoginResponse;
import in.project.GateKeeper.Management.service.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {
    private final AuthenticationService authenticationServive;

    public AuthenticationController(AuthenticationService authenticationServive) {
        this.authenticationServive = authenticationServive;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        LoginResponse loginResponse = authenticationServive.login(loginRequest);
        return ResponseEntity.ok(loginResponse);
    }
}
