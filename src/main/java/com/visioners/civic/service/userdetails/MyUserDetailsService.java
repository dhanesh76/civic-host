package com.visioners.civic.service.userdetails;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.visioners.civic.entity.Users;
import com.visioners.civic.repository.UsersRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MyUserDetailsService implements UserDetailsService{

    private final UsersRepository usersRepository;

    @Override
    public UserDetails loadUserByUsername(String mobileNumber) throws UsernameNotFoundException {

        Users user = usersRepository.findByMobileNumber(mobileNumber)
            .orElseThrow(() -> new UserNotFoundException(mobileNumber));
       
        return new UserPrincipal(user) ;
    }
}
