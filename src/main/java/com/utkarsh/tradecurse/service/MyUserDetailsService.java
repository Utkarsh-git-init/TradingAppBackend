package com.utkarsh.tradecurse.service;

import com.utkarsh.tradecurse.entity.UserModel;
import com.utkarsh.tradecurse.model.UserPrincipal;
import com.utkarsh.tradecurse.repository.UserRepo;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service("myUserDetailsService")
public class MyUserDetailsService implements UserDetailsService {

    private final UserRepo userRepo;

    public MyUserDetailsService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserModel user=userRepo.findByUsername(username).orElse(null);
        if(user==null){
            throw new UsernameNotFoundException("user Not found");
        }
        return new UserPrincipal(user);
    }
}
