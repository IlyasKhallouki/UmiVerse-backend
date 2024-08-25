package com.umiverse.umiversebackend.repository.mysql;

import com.umiverse.umiversebackend.model.Status;
import com.umiverse.umiversebackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    boolean existsByUsername(String username);

    boolean existsBySessionToken(String sessionToken);

    boolean existsByEmail(String email);

    boolean existsByUserID(int userID);

    User findByUsernameAndPassword(String username, String hashedPassword);

    List<User> findAllByStatus(Status status);

    User findByUserID(int id);

    User findBySessionToken(String sessionToken);
}
