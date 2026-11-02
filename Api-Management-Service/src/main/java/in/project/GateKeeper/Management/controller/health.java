package in.project.GateKeeper.Management.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
public class health {

    private String serviceName;
    private String serviceDescription;

    public health(@Value("${info.app.name}") String serviceName, @Value("${info.app.description}") String serviceDescription) {
        this.serviceName = serviceName;
        this.serviceDescription = serviceDescription;
    }
    @GetMapping
    public ResponseEntity<String> getHealth() {
        return ResponseEntity.ok("Service is up and running. Service Name: " + serviceName + ", Description: " + serviceDescription);
    }
}

