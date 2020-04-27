package com.shippings.repositories;

import com.shippings.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User getById(long id);
    Optional<User> findByUsername(String username);

    Boolean existsByUsername(String username);

    Optional<String> findByRole(String role);
}
