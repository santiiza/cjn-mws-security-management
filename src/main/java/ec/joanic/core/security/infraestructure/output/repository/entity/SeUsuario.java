package ec.joanic.core.security.infraestructure.output.repository.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("seguridad.se_usuario")
public class SeUsuario {
    @Id
    Integer usId;
    Integer usIdSuperior;
    String usNombre;
    String usApellido;
    String usTipoNui;
    String usNui;
    String usLogin;
    String usClave;
    String usGuid;
    LocalDate usFechaNac;
    byte[] usFoto;
    char usGenero;
    String usEmail;
    String usCargo;
    String usDireccion;
    @Column("us_telefono1")
    String usTelefono1;
    @Column("us_telefono2")
    String usTelefono2;
    Double usMetas;
    Double usFactor;
    String usUsuarioIng;
    LocalDateTime usFechaIng;
    String usUsuarioAct;
    LocalDateTime usFechaAct;
    char usEstado;
}
