package org.lastrix.mafp.roles.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class UserRoleId implements Serializable {
    @Column(name = "user_id", nullable = false, updatable = false)
    private UUID userId;
    @Column(name = "role_id", nullable = false, updatable = false)
    private Integer roleId;
}
