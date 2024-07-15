package com.umiverse.umiversebackend.repository.mysql;

import com.umiverse.umiversebackend.model.Status;
import com.umiverse.umiversebackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    User findByUsernameAndPassword(String username, String hashedPassword);

    List<User> findAllByStatus(Status status);
}
