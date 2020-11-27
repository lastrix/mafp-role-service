package org.lastrix.mafp.roles.persistence.mapper;

import org.lastrix.mafp.roles.model.dto.RoleDto;
import org.lastrix.mafp.roles.persistence.entity.Role;
import org.lastrix.rest.EntityMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class RoleMapper implements EntityMapper<Role, RoleDto> {

}
