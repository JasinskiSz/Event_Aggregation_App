package com.sda.eventapp.repository;

import com.sda.eventapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByUsername(String userName);

    @Query(value = "SELECT u.*  FROM user as u " +
            "INNER JOIN users_events us ON u.id = us.user_id " +
            "INNER JOIN event e ON us.event_id = e.id " +
            "WHERE e.id = ?1", nativeQuery = true)
    List<User> findAllUserInEvent(@Param("id") Long id);
}
