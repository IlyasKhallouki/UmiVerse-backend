package com.umiverse.umiversebackend.repository.mysql;

import com.umiverse.umiversebackend.model.UnverifiedUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UnverifiedUserRepository extends JpaRepository<UnverifiedUser, Integer> {
    UnverifiedUser findUnverifiedUsersByVerificationToken(String verificationCode);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    UnverifiedUser getUnverifiedUserByUsername(String username);
    UnverifiedUser getUnverifiedUserByEmail(String email);
}
