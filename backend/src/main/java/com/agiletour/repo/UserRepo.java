package com.agiletour.repo;

import com.agiletour.entity.User;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface UserRepo extends Repository<User, Long> {
    Optional<User> findByUsername(String username);
}
