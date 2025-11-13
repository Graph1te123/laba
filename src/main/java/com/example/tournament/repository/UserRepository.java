package com.example.tournament.repository;

import com.example.tournament.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Репозиторий для работы с сущностью User.
 * Spring Data JPA автоматически создаёт реализацию.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Находит пользователя по имени (username).
     * @param username уникальное имя пользователя
     * @return Optional с пользователем или пустой, если не найден
     */
    Optional<User> findByUsername(String username);

    /**
     * Находит пользователя по электронной почте.
     * @param email уникальный email
     * @return Optional с пользователем или пустой, если не найден
     */
    Optional<User> findByEmail(String email);
}