package in.project.GateKeeper.Management.service;

import in.project.GateKeeper.Management.dto.request.LoginRequest;
import in.project.GateKeeper.Management.dto.Response.LoginResponse;
import in.project.GateKeeper.Management.entity.ManagementUsers;
import in.project.GateKeeper.Management.entity.OrganisationStatus;
import in.project.GateKeeper.Management.repository.ManagementUserRepo;
import in.project.GateKeeper.Management.exception.ApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class AuthenticationService {
    private final ManagementUserRepo managementUserRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtEncoder jwtEncoder;
    private final long expirationseconds;
    public AuthenticationService(ManagementUserRepo managementUserRepo, PasswordEncoder passwordEncoder, JwtEncoder jwtEncoder, @Value("${security.jwt.expiration-seconds}") long expirationseconds) {
        this.managementUserRepo = managementUserRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtEncoder = jwtEncoder;
        this.expirationseconds = expirationseconds;
    }
   @Transactional(readOnly = true) //
    public LoginResponse login(LoginRequest loginRequest) {
      ManagementUsers user = managementUserRepo.findByUsername(loginRequest.getUsername())
              .orElseThrow(this::invalidCredentials);
      if(!user.isEnabled() || !passwordEncoder.matches(loginRequest.getPassword(), user.getPasswordHash())) {
          throw invalidCredentials();
      }
      if(user.getOrganisation()!=null && user.getOrganisation().getStatus() != null && user.getOrganisation().getStatus() != OrganisationStatus.Active) {
          throw new ApiException(HttpStatus.FORBIDDEN, "Organisation is not active");
      }
       Instant issuedAt = Instant.now();
      String role = user.getRole().getName();
      Long organisationId = user.getOrganisation() != null ? user.getOrganisation().getId() : null;

       JwtClaimsSet.Builder claims = JwtClaimsSet.builder()
              .issuer("gatekeeper-api-management")
              .issuedAt(issuedAt)
              .expiresAt(issuedAt.plusSeconds(expirationseconds))
              .subject(user.getUsername())
              .claim("role", role);
        if(organisationId != null) {
            claims.claim("organisationId", organisationId);
        }

       JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();

        String token = jwtEncoder.encode(JwtEncoderParameters.from(header, claims.build())).getTokenValue();
        return new LoginResponse(token, "Bearer", expirationseconds, role, organisationId);
    }

    public RuntimeException invalidCredentials() {
        return new ApiException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
    }
}
