package org.lastrix.mafp.roles.persistence.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "user_role")
@Data
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class UserRole {
    public UserRole(UserRoleId id) {
        this.id = id;
        this.deleted = false;
    }

    @EmbeddedId
    private UserRoleId id;

    @Column(name = "is_deleted", nullable = false)
    private Boolean deleted;
}
