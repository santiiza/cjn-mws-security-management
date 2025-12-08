package ec.joanic.core.security.infraestructure.util;

import ec.joanic.lib.exceptions.context.CustomBadRequestException;
import ec.joanic.lib.messages.context.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
public class ReactiveValidator {

    private final MessageService messageService;

    public Mono<String> validateNotBlank(String value, String code) {
        if (value == null || value.trim().isEmpty()) {
            return messageService.getMessageReactive(code)
                    .flatMap(msg -> Mono.error(new CustomBadRequestException(code, msg)));
        }
        return Mono.just(value);
    }

    public <T> Mono<T> validateNotNull(T value, String code) {
        if (value == null) {
            return messageService.getMessageReactive(code)
                    .flatMap(msg -> Mono.error(new CustomBadRequestException(code, msg)));
        }
        return Mono.just((T) value);
    }

    public <T> Mono<T> validateCondition(boolean condition, String code) {
        if (!condition) {
            return messageService.getMessageReactive(code)
                    .flatMap(msg -> Mono.error(new CustomBadRequestException(code, msg)));
        }
        return Mono.empty();
    }

    public <T> void applyIfNotNull(T value, Consumer<T> setter) {
        if (value != null) {
            setter.accept(value);
        }
    }
}

