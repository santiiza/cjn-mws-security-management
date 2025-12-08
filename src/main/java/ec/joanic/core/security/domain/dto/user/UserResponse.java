package ec.joanic.core.security.domain.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
    @JsonProperty("id")
    Integer id;
    @JsonProperty("superiorId")
    Integer superiorId;
    @JsonProperty("firstName")
    String firstName;
    @JsonProperty("lastName")
    String lastName;
    @JsonProperty("documentType")
    String documentType;
    @JsonProperty("documentNumber")
    String documentNumber;
    @JsonProperty("username")
    String username;
    @JsonProperty("birthDate")
    LocalDate birthDate;
    @JsonProperty("photo")
    byte[] photo;
    @JsonProperty("gender")
    char gender;
    @JsonProperty("email")
    String email;
    @JsonProperty("position")
    String position;
    @JsonProperty("address")
    String address;
    @JsonProperty("phone1")
    String phone1;
    @JsonProperty("phone2")
    String phone2;
    @JsonProperty("goals")
    Double goals;
    @JsonProperty("factor")
    Double factor;
    @JsonProperty("status")
    char status;
}
