package com.utkarsh.tradecurse.controller;

import com.utkarsh.tradecurse.dto.UserDto;
import com.utkarsh.tradecurse.entity.UserModel;
import com.utkarsh.tradecurse.model.UserPrincipal;
import com.utkarsh.tradecurse.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserModel user){
        return authService.register(user);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserModel user){
        return authService.login(user);
    }

    @GetMapping("me")
    public ResponseEntity<?> getMe(@AuthenticationPrincipal UserPrincipal userPrincipal){
        return ResponseEntity.ok()
                .body(
                        new UserDto(userPrincipal.getId(), userPrincipal.getUsername())
                );
    }

}
