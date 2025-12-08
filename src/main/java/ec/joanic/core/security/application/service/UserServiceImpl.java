package ec.joanic.core.security.application.service;

import ec.joanic.core.security.application.input.port.UserService;
import ec.joanic.core.security.application.output.port.UserPort;
import ec.joanic.core.security.domain.dto.user.UserDto;
import ec.joanic.core.security.domain.dto.user.UserPatchDto;
import ec.joanic.core.security.domain.dto.user.UserResponse;
import ec.joanic.core.security.infraestructure.util.ReactiveValidator;
import ec.joanic.lib.logging.loggers.ReactiveOperationLogger;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static ec.joanic.core.security.infraestructure.util.Constants.COD_00006;
import static ec.joanic.core.security.infraestructure.util.Constants.COD_00012;

@Service
@RequiredArgsConstructor
@Validated
public class UserServiceImpl implements UserService {

    private final UserPort userPort;
    private final ReactiveValidator reactiveValidator;
    private final ReactiveOperationLogger opeLog;

    @Override
    public Flux<UserResponse> getAllUsers() {
        return opeLog.debug("|--> Service - getAllUsers started")
            .thenMany(userPort.findAll())
            .doOnComplete(() -> opeLog.debug("<--| Service - getAllUsers finished successfully"));
    }

    @Override
    public Mono<UserResponse> getUserById(Integer id) {
        return opeLog.debug("|--> Service - getUserById started - id=[{}]", id)
                .then(reactiveValidator.validateNotNull(id, COD_00006))
                .flatMap(validId -> userPort.findUserById(validId)
                        .flatMap(company -> opeLog.debug("<--| Service - getUserById finished successfully: id=[{}]", validId)
                                .thenReturn(company)));
    }

    @Override
    public Mono<Void> createUser(String user, @Valid UserDto userDto) {
        return opeLog.debug("|--> Service - createCompany started: company={}", userDto)
                .then(reactiveValidator.validateNotBlank(user, COD_00012))
                .flatMap(validUser ->
                        userPort.createUser(validUser, userDto)
                                .doOnSuccess(save -> opeLog.debug("<--| Service - createCompany finished successfully")));
    }

    @Override
    public Mono<Void> updateUser(String user, Integer id, @Valid UserPatchDto userDto) {
        return opeLog.debug("|--> Service - updateUser started: id=[{}], user={}", id, userDto)
                .then(reactiveValidator.validateNotBlank(user, COD_00012))
                .flatMap(validUser -> reactiveValidator.validateNotNull(id, COD_00006)
                        .flatMap(validId -> userPort.updateUser(validUser, validId, userDto)
                                .flatMap(update ->
                                        opeLog.debug("<--| Service - updateCompany finished successfully: id=[{}] updated", id)
                                                .then())));
    }
}
