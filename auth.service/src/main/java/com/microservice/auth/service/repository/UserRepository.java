package com.microservice.auth.service.repository;

import com.microservice.auth.service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository <User, Long> {
    Optional<User> findByUserName(String userName);

    @Query("SELECT u FROM APPLICATION_USER u WHERE u.userName = :username OR u.email = :email")
    List<User> findByUserNameOrEmail(@Param("username") String username, @Param("email") String email);
}
