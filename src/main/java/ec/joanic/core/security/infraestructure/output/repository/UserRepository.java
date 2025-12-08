package ec.joanic.core.security.infraestructure.output.repository;

import ec.joanic.core.security.infraestructure.output.repository.entity.SeUsuario;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface UserRepository extends R2dbcRepository<SeUsuario, Integer> {
}
