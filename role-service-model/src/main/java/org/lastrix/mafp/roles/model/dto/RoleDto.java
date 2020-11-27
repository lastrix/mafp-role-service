package org.lastrix.mafp.roles.model.dto;

import lombok.*;
import org.lastrix.rest.ValidGroup;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class RoleDto {
    // readonly, should not be used to identify role
    private Integer id;

    // unique role name, use as identifier
    @NotNull
    @Size(min = 3, max = 128)
    private String name;

    @NotNull(groups = ValidGroup.Create.class)
    @Size(max = 2048)
    private String description;
}
