package ec.joanic.core.security.infraestructure.output.adapter.mapper;

import ec.joanic.core.security.domain.dto.user.UserDto;
import ec.joanic.core.security.domain.dto.user.UserResponse;
import ec.joanic.core.security.infraestructure.output.repository.entity.SeUsuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {
    @Mapping(target = "id", source = "seUsuario.usId")
    @Mapping(target = "superiorId", source = "seUsuario.usIdSuperior")
    @Mapping(target = "firstName", source = "seUsuario.usNombre")
    @Mapping(target = "lastName", source = "seUsuario.usApellido")
    @Mapping(target = "documentType", source = "seUsuario.usTipoNui")
    @Mapping(target = "documentNumber", source = "seUsuario.usNui")
    @Mapping(target = "username", source = "seUsuario.usLogin")
    @Mapping(target = "birthDate", source = "seUsuario.usFechaNac")
    @Mapping(target = "photo", source = "seUsuario.usFoto")
    @Mapping(target = "gender", source = "seUsuario.usGenero")
    @Mapping(target = "email", source = "seUsuario.usEmail")
    @Mapping(target = "position", source = "seUsuario.usCargo")
    @Mapping(target = "address", source = "seUsuario.usDireccion")
    @Mapping(target = "phone1", source = "seUsuario.usTelefono1")
    @Mapping(target = "phone2", source = "seUsuario.usTelefono2")
    @Mapping(target = "goals", source = "seUsuario.usMetas")
    @Mapping(target = "factor", source = "seUsuario.usFactor")
    @Mapping(target = "status", source = "seUsuario.usEstado")
    UserResponse toUserResponse(SeUsuario seUsuario);

    @Mapping(target = "usIdSuperior", source = "user.superiorId")
    @Mapping(target = "usNombre", source = "user.firstName")
    @Mapping(target = "usApellido", source = "user.lastName")
    @Mapping(target = "usTipoNui", source = "user.documentType")
    @Mapping(target = "usNui", source = "user.documentNumber")
    @Mapping(target = "usLogin", source = "user.username")
    @Mapping(target = "usGuid", source = "user.uniqueness")
    @Mapping(target = "usFechaNac", source = "user.birthDate")
    @Mapping(target = "usFoto", source = "user.photo")
    @Mapping(target = "usGenero", source = "user.gender")
    @Mapping(target = "usEmail", source = "user.email")
    @Mapping(target = "usCargo", source = "user.position")
    @Mapping(target = "usDireccion", source = "user.address")
    @Mapping(target = "usTelefono1", source = "user.phone1")
    @Mapping(target = "usTelefono2", source = "user.phone2")
    @Mapping(target = "usMetas", source = "user.goals")
    @Mapping(target = "usFactor", source = "user.factor")
    SeUsuario toSeUsuario(UserDto user);
}
