package com.civic.visioners.auth.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.visioners.civic.auth.userdetails.UserPrincipal;
import com.visioners.civic.exception.UserNotFoundException;
import com.visioners.civic.user.entity.Users;
import com.visioners.civic.user.repository.UsersRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class MyUserDetailsService implements UserDetailsService {

    private final UsersRepository usersRepository;

    @Override
    public UserDetails loadUserByUsername(String mobileNumber) {

        Users user = usersRepository.findByMobileNumberWithRoles(mobileNumber)
                .orElseThrow(() -> new UserNotFoundException(mobileNumber));

        // force initialization
        user.getRoles().size(); 
        
        // user.getRoles().forEach(r -> System.out.println("Role loaded: " + r.getName()));

        return new UserPrincipal(user);
    }
}

