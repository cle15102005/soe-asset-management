package vn.edu.hust.soict.soe.assetmanagement.user.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Role entity — maps to the `roles` table.
 * Codes match exactly: SYSTEM_ADMIN, ASSET_MANAGER, WAREHOUSE,
 * APPROVING_AUTH, FINANCE_AUDIT.
 */

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;
}