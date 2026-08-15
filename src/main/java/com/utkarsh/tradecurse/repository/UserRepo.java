package com.utkarsh.tradecurse.repository;

import com.utkarsh.tradecurse.entity.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepo extends JpaRepository<UserModel,Integer> {
    Optional<UserModel> findByUsername(String username);
}
