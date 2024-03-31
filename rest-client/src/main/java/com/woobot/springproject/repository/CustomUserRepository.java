package com.woobot.springproject.repository;

import com.woobot.springproject.entity.CustomUser;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CustomUserRepository extends CrudRepository<CustomUser, Integer> {
    Optional<CustomUser> findByUsername(String username);
}
