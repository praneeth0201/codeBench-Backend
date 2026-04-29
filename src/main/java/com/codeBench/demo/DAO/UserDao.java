package com.codeBench.demo.DAO;

import com.codeBench.demo.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserDao extends JpaRepository<User,Long> {
    Optional<User> findByUsername(String userName);
    boolean existsByEmail(String email);
}
