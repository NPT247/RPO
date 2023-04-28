package com.npt247.backend.repositories;

import com.npt247.backend.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByToken(String valueOf);

    Optional<User> findByLogin(String login);
}
