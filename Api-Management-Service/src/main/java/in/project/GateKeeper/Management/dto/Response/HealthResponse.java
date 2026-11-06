package in.project.GateKeeper.Management.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
public class HealthResponse {

    private String serviceStatus;
    private String dbStatus;
    private LocalDateTime timestamp;
}