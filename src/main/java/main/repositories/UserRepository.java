package main.repositories;

import main.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Integer> {

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    boolean existsByCode(String code);

    Optional<User> findByCode(String code);
}
