package com.umiverse.umiversebackend.repository;

import com.umiverse.umiversebackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    User findByUsernameAndPassword(String username, String hashedPassword);
}
