package com.example.FairShare.repository;

import com.example.FairShare.model.Expense;
import com.example.FairShare.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    Optional<User> findByEmail(String email) throws UsernameNotFoundException;
    User findByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);

    List<User> findByGroupGroupId(Long groupId);

}
