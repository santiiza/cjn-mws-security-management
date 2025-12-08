package ec.joanic.core.security.infraestructure.output.adapter;

import ec.joanic.core.general.application.library.enums.GeneralStatus;
import ec.joanic.core.security.application.output.port.UserPort;
import ec.joanic.core.security.domain.dto.user.UserDto;
import ec.joanic.core.security.domain.dto.user.UserPatchDto;
import ec.joanic.core.security.domain.dto.user.UserResponse;
import ec.joanic.core.security.infraestructure.output.adapter.mapper.UserMapper;
import ec.joanic.core.security.infraestructure.output.repository.UserRepository;
import ec.joanic.core.security.infraestructure.output.repository.entity.SeUsuario;
import ec.joanic.core.security.infraestructure.util.ReactiveValidator;
import ec.joanic.lib.exceptions.context.CustomConflictException;
import ec.joanic.lib.exceptions.context.CustomInternalServerErrorException;
import ec.joanic.lib.exceptions.context.CustomNotFoundException;
import ec.joanic.lib.logging.loggers.ReactiveAuditLogger;
import ec.joanic.lib.logging.loggers.ReactiveOperationLogger;
import ec.joanic.lib.messages.context.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static ec.joanic.core.security.infraestructure.util.Constants.*;

@Service
@RequiredArgsConstructor
public class UserAdapter implements UserPort {

    private final MessageService messageService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder encoder;
    private final ReactiveOperationLogger opeLog;
    private final ReactiveAuditLogger audLog;
    private final ReactiveValidator reactiveValidator;
    private final TransactionalOperator txOperator;

    @Override
    public Flux<UserResponse> findAll() {
        return opeLog.debug("|---> Adapter - findAll started")
                .thenMany(userRepository.findAll()
                        .doOnSubscribe(sub -> opeLog.debug("|---> Adapter - findAll in database"))
                        .switchIfEmpty(
                                messageService.getMessageReactive(COD_40000)
                                        .flatMap(msg -> Mono.error(new CustomNotFoundException(COD_40000, msg))))
                        .flatMap(this::mapToResponse)
                        .doOnComplete(() -> opeLog.debug("<---| Adapter - findAll successfully"))
                        .doOnError(error -> opeLog.error("<---| Adapter - findAll error getting information from database: {}", error.getMessage())));
    }

    @Override
    public Mono<UserResponse> findUserById(Integer id) {
        return opeLog.debug("|---> Adapter - findUserById started: id=[{}]", id)
                .then(userRepository.findById(id)
                        .doOnSubscribe(sub -> opeLog.debug("|---> Adapter - findUserById querying database: id=[{}]", id))
                        .flatMap(this::mapToResponse)
                        .flatMap(response -> opeLog.debug("<---| Adapter - findUserById completed: id=[{}]", response.getId())
                                .thenReturn(response))
                        .switchIfEmpty(messageService.getMessageReactive(COD_40001, new Object[]{id})
                                .flatMap(msg -> Mono.error(new CustomNotFoundException(COD_40001, msg))))
                        .doOnError(error -> opeLog.error("<---| Adapter - findUserById failed - id=[{}] : error={}", id, error.getMessage()))
                );
    }

    @Override
    public Mono<Void> createUser(String user, UserDto userDto) {
        return audLog.info("|---> Adapter - createUser started: user= '{}', data={}", user, userDto)
                .then(mapToEntity(user, userDto))
                .flatMap(this::persistEntity)
                .flatMap(response ->
                        audLog.info("<---| Adapter - createUser successful: code='{}'", response.getUsId())
                                .then())
                .doOnError(error -> audLog.error("<---| Adapter - createUser failed: {}", error.getMessage()))
                .doFinally(signal -> audLog.debug("<---| Adapter - createUser finished with signal: {}", signal));
    }

    @Override
    public Mono<Void> updateUser(String user, Integer id, UserPatchDto userDto) {
        return audLog.info("|---> Adapter - updateUser started: user= '{}', id=[{}], data={}", user, id, userDto)
                .then(userRepository.findById(id)
                        .switchIfEmpty(
                                messageService.getMessageReactive(COD_40001, new Object[]{id})
                                        .flatMap(msg -> Mono.error(new CustomNotFoundException(COD_40001, msg))))
                        .flatMap(existing -> mapToUpdatedEntity(user, userDto, existing))
                        .flatMap(this::persistEntity)
                        .flatMap(response ->
                                audLog.info("<---| Adapter - updateUser successful: user= '{}', code='{}'", user, id)
                                        .then())
                        .doOnError(error -> audLog.error("<---| Adapter - updateUser failed: {}", error.getMessage()))
                        .doFinally(signal -> audLog.debug("<---| Adapter - updateUser finished with signal: {}", signal))
                        .as(txOperator::transactional));
    }

    private Mono<SeUsuario> mapToUpdatedEntity(String user, UserPatchDto dto, SeUsuario existing) {
        return Mono.fromCallable(() -> {
            reactiveValidator.applyIfNotNull(dto.getSuperiorId(), existing::setUsIdSuperior);
            reactiveValidator.applyIfNotNull(dto.getFirstName(), existing::setUsNombre);
            reactiveValidator.applyIfNotNull(dto.getLastName(), existing::setUsApellido);
            reactiveValidator.applyIfNotNull(dto.getDocumentType(), existing::setUsTipoNui);
            reactiveValidator.applyIfNotNull(dto.getDocumentNumber(), existing::setUsNui);
            reactiveValidator.applyIfNotNull(encoder.encode(dto.getPassword()), existing::setUsClave);
            reactiveValidator.applyIfNotNull(dto.getUniqueness(), existing::setUsGuid);
            reactiveValidator.applyIfNotNull(dto.getBirthDate(), existing::setUsFechaNac);
            reactiveValidator.applyIfNotNull(dto.getPhoto(), existing::setUsFoto);
            reactiveValidator.applyIfNotNull(dto.getGender(), existing::setUsGenero);
            reactiveValidator.applyIfNotNull(dto.getEmail(), existing::setUsEmail);
            reactiveValidator.applyIfNotNull(dto.getPosition(), existing::setUsCargo);
            reactiveValidator.applyIfNotNull(dto.getAddress(), existing::setUsDireccion);
            reactiveValidator.applyIfNotNull(dto.getPhone1(), existing::setUsTelefono1);
            reactiveValidator.applyIfNotNull(dto.getPhone2(), existing::setUsTelefono2);
            reactiveValidator.applyIfNotNull(dto.getGoals(), existing::setUsMetas);
            reactiveValidator.applyIfNotNull(dto.getFactor(), existing::setUsFactor);
            reactiveValidator.applyIfNotNull(dto.getStatus(), existing::setUsEstado);
            existing.setUsUsuarioAct(user);
            existing.setUsFechaAct(LocalDateTime.now());
            return existing;
        }).onErrorResume(error ->
                messageService.getMessageReactive(COD_00011)
                        .flatMap(msg -> Mono.error(new CustomInternalServerErrorException(COD_00011, msg, error))));
    }

    private Mono<UserResponse> mapToResponse(SeUsuario entity) {
        return Mono.just(entity)
                .map(userMapper::toUserResponse)
                .onErrorResume(error ->
                        messageService.getMessageReactive(COD_00010)
                                .flatMap(msg -> Mono.error(new CustomInternalServerErrorException(COD_00010, msg, error))));
    }

    private Mono<SeUsuario> mapToEntity(String user, UserDto dto) {
        return Mono.fromCallable(() -> {
            SeUsuario entity = userMapper.toSeUsuario(dto);
            entity.setUsClave(encoder.encode(dto.getPassword()));
            entity.setUsUsuarioIng(user);
            entity.setUsFechaIng(LocalDateTime.now());
            entity.setUsEstado(GeneralStatus.VIGENTE.getCode());
            return entity;
        }).onErrorResume(error ->
                messageService.getMessageReactive(COD_00011)
                        .flatMap(msg -> Mono.error(new CustomInternalServerErrorException(COD_00011, msg, error))));
    }

    private Mono<SeUsuario> persistEntity(SeUsuario entity) {
        return userRepository.save(entity)
                .onErrorResume(error ->
                        messageService.getMessageReactive(COD_00007)
                                .flatMap(msg -> Mono.error(new CustomConflictException(COD_00007, msg, error))));
    }
}
