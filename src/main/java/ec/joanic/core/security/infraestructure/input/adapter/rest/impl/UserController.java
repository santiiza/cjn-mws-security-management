package ec.joanic.core.security.infraestructure.input.adapter.rest.impl;

import ec.joanic.core.general.application.library.entity.EDataIn;
import ec.joanic.core.general.application.library.entity.EDataOut;
import ec.joanic.core.security.application.input.port.UserService;
import ec.joanic.core.security.domain.dto.user.UserDto;
import ec.joanic.core.security.domain.dto.user.UserPatchDto;
import ec.joanic.core.security.domain.dto.user.UserResponse;
import ec.joanic.lib.headers.annotations.ValidateHeaders;
import ec.joanic.lib.headers.context.HeaderContext;
import ec.joanic.lib.logging.loggers.ReactiveAuditLogger;
import ec.joanic.lib.logging.loggers.ReactiveOperationLogger;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

import static ec.joanic.core.security.infraestructure.util.Constants.RECURSO_BASE_USERS;
import static ec.joanic.core.security.infraestructure.util.Constants.RECURSO_ID;

@RestController
@RequestMapping(RECURSO_BASE_USERS)
@AllArgsConstructor
@Validated
public class UserController {
    private final UserService userService;
    private final HeaderContext headerContext;
    private final ReactiveOperationLogger opelog;
    private final ReactiveAuditLogger audlog;

    @GetMapping
    public Mono<ResponseEntity<EDataOut<List<UserResponse>>>> getUsers() {

        return opelog.info("|-> Controller - getAllUsers started")
                .thenMany(userService.getAllUsers())
                .collectList()
                .map(list -> {
                    opelog.info("<-| Controller - getAllUsers finished");
                    return list.isEmpty()
                            ? ResponseEntity.status(HttpStatus.NOT_FOUND).body(new EDataOut<>(Collections.emptyList()))
                            : ResponseEntity.ok(new EDataOut<>(list));
                });
    }

    @ValidateHeaders
    @GetMapping(RECURSO_ID)
    public Mono<ResponseEntity<EDataOut<UserResponse>>> getUserById(
            @PathVariable("id") @NotNull(message = "{field.not.null}") Integer id,
            ServerWebExchange exchange) {

        return opelog.info("|-> Controller - getUserById started: id=[{}]", id)
                .then(userService.getUserById(id))
                .flatMap(response -> opelog.info("<-| Controller - getUserById finished: id=[{}]", id)
                        .thenReturn(ResponseEntity.ok(new EDataOut<>(response))));
    }

    @ValidateHeaders
    @PostMapping
    public Mono<ResponseEntity<Void>> postSaveUser(
            @Valid @RequestBody EDataIn<UserDto> user,
            ServerWebExchange exchange) {

        return opelog.info("|-> Controller - postSaveUser started: dataIn={}", user)
                .then(headerContext.get())
                .flatMap(headers ->
                        userService.createUser(headers.getXUser(), user.getDataIn())
                                .doOnSuccess(response -> audlog.info("<-| Controller - postSaveUser successful"))
                                .doOnError(error -> audlog.error("<-| Controller - postSaveUser failed: {}", error.getMessage()))
                                .then())
                .then(Mono.just(ResponseEntity.status(HttpStatus.CREATED).build()));
    }

    @ValidateHeaders
    @PatchMapping(RECURSO_ID)
    public Mono<ResponseEntity<Void>> patchUpdateUser(
            @PathVariable("id") @NotNull(message = "{00006}") Integer id,
            @Valid @RequestBody EDataIn<UserPatchDto> user,
            ServerWebExchange exchange) {

        return opelog.info("|-> Controller - patchUpdateUser started: id=[{}], dataIn={}", id, user)
                .then(headerContext.get())
                .flatMap(headers ->
                        userService.updateUser(headers.getXUser(), id, user.getDataIn())
                                .doOnSuccess(response -> audlog.info("<-| Controller - patchUpdateUser successful: id=[{}]", id))
                                .doOnError(error -> audlog.error("<-| Controller - patchUpdateUser failed: {}", error.getMessage()))
                                .then())
                .then(Mono.just(ResponseEntity.status(HttpStatus.NO_CONTENT).build()));
    }
}
