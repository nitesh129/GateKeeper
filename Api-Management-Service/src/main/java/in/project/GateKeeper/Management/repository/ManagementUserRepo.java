package in.project.GateKeeper.Management.repository;

import in.project.GateKeeper.Management.entity.ManagementUsers;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

//import java.util.Optional;
@Repository
public interface ManagementUserRepo extends JpaRepository<ManagementUsers, Long> {
     Optional<ManagementUsers> findByUsername(String username);
}

