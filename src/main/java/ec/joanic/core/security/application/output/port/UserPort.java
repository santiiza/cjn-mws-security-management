package ec.joanic.core.security.application.output.port;

import ec.joanic.core.security.domain.dto.user.UserDto;
import ec.joanic.core.security.domain.dto.user.UserPatchDto;
import ec.joanic.core.security.domain.dto.user.UserResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserPort {
    Flux<UserResponse> findAll();
    Mono<UserResponse> findUserById(Integer id);
    Mono<Void> createUser(String user, UserDto userDto);
    Mono<Void> updateUser(String user, Integer id, UserPatchDto userDto);
}
