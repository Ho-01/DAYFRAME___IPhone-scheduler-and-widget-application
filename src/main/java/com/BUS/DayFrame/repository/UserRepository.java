package com.BUS.DayFrame.repository;

import com.BUS.DayFrame.domain.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.name = :name WHERE u.id = :userId")
    void updateName(@Param("userId") Long userId, @Param("name") String name);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.password = :password WHERE u.id = :userId")
    void updatePassword(@Param("userId") Long userId, @Param("password") String password);
}
