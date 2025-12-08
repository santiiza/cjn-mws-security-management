package ec.joanic.core.security.application.input.port;

import ec.joanic.core.security.domain.dto.user.UserDto;
import ec.joanic.core.security.domain.dto.user.UserPatchDto;
import ec.joanic.core.security.domain.dto.user.UserResponse;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Validated
public interface UserService {
    Flux<UserResponse> getAllUsers();
    Mono<UserResponse> getUserById(Integer id);
    Mono<Void> createUser(String user, @Valid UserDto userDto);
    Mono<Void> updateUser(String user, Integer id, @Valid UserPatchDto userDto);
}
