package in.project.GateKeeper.Management.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "management_users")
@Getter
@Setter
@NoArgsConstructor
public class ManagementUsers {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organisation_id")
    private Organisation organisation;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id",nullable = false)
    private Role role;

    private String username;

    private String PasswordHash;
    private boolean enabled;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
