package com.ead.authuser.services;

import com.ead.authuser.models.UserModel;
import com.ead.authuser.specifications.SpecificationTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    List<UserModel> findAll();

    Page<UserModel> findAll(SpecificationTemplate.UserSpec spec, Pageable pageable);

    Optional<UserModel> findById(UUID userId);

    void delete(UserModel userModel);

    UserModel save(UserModel userModel);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

}
