package ec.joanic.core.security.domain.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPatchDto {
    @JsonProperty("superiorId")
    Integer superiorId;
    @Size(max = 50, message = "{field.size.max}")
    @JsonProperty("firstName")
    String firstName;
    @Size(max = 50, message = "{field.size.max}")
    @JsonProperty("lastName")
    String lastName;
    @Size(max = 5, message = "{field.size.max}")
    @JsonProperty("documentType")
    String documentType;
    @Size(max = 15, message = "{field.size.max}")
    @JsonProperty("documentNumber")
    String documentNumber;
    @Size(max = 16, message = "{field.size.max}")
    @JsonProperty("username")
    String username;
    @Size(max = 254, message = "{field.size.max}")
    @JsonProperty("password")
    String password;
    @Size(max = 254, message = "{field.size.max}")
    @JsonProperty("uniqueness")
    String uniqueness;
    @JsonProperty("birthDate")
    LocalDate birthDate;
    @JsonProperty("photo")
    byte[] photo;
    @JsonProperty("gender")
    char gender;
    @Size(max = 80, message = "{field.size.max}")
    @JsonProperty("email")
    String email;
    @Size(max = 5, message = "{field.size.max}")
    @JsonProperty("position")
    String position;
    @Size(max = 254, message = "{field.size.max}")
    @JsonProperty("address")
    String address;
    @Size(max = 12, message = "{field.size.max}")
    @JsonProperty("phone1")
    String phone1;
    @Size(max = 12, message = "{field.size.max}")
    @JsonProperty("phone2")
    String phone2;
    @JsonProperty("goals")
    Double goals;
    @JsonProperty("factor")
    Double factor;
    @JsonProperty("status")
    char status;
}
