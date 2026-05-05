package vn.edu.hust.soict.soe.assetmanagement.user.entity;

import jakarta.persistence.*;
import lombok.*;
import vn.edu.hust.soict.soe.assetmanagement.common.BaseEntity;

import java.util.UUID;

/**
 * ManagingUnit entity — maps to the `managing_units` table.
 * Represents a department or subsidiary unit within the SOE.
 * Assets and stock transactions are scoped to a managing unit.
 */
@Entity
@Table(name = "managing_units")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ManagingUnit extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false, unique = true, length = 20)
    private String code;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    // Self-referencing for parent unit hierarchy
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private ManagingUnit parent;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;
}