package com.example.WeeSeed.repository;


import com.example.WeeSeed.entity.Nok;
import com.example.WeeSeed.entity.Pathologist;
import com.example.WeeSeed.entity.User;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByUserId(String userId);
//    Optional<Nok> findByNokId(String nokId);
//    Optional<Pathologist> findByPath(String pathId);
}