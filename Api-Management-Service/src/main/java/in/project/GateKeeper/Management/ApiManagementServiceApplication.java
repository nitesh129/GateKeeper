package in.project.GateKeeper.Management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration;

@SpringBootApplication
public class ApiManagementServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiManagementServiceApplication.class, args);
	}

}
