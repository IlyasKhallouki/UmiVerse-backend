package com.umiverse.umiversebackend.repository.mysql;

import com.umiverse.umiversebackend.model.Status;
import com.umiverse.umiversebackend.model.UnverifiedUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UnverifiedUserRepository extends JpaRepository<UnverifiedUser, Integer> {


}
