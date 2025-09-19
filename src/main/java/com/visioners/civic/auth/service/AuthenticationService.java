package com.visioners.civic.auth.service;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.visioners.civic.auth.dto.AuthTokens;
import com.visioners.civic.auth.dto.LoginRequest;
import com.visioners.civic.auth.dto.LoginResponse;
import com.visioners.civic.auth.dto.RegisterRequest;
import com.visioners.civic.auth.dto.RegisterResponse;
import com.visioners.civic.exception.RoleNotFoundException;
import com.visioners.civic.role.entity.Role;
import com.visioners.civic.role.repository.RoleRepository;
import com.visioners.civic.user.entity.Users;
import com.visioners.civic.user.repository.UsersRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    
    private final UsersRepository usersRepository;
    private final PasswordEncoder bcryptPasswordEncoder;
    private final RoleRepository roleRepository;
    private final JwtTokenService jwtService;
    private final MyUserDetailsService myUserDetailsService;
    private final AuthenticationManager authenticationManager;

    private final String DEFAULT_ROLE = "USER";
    
    public RegisterResponse register(RegisterRequest registerRequest){
        String mobileNumber = registerRequest.mobileNumber();
        String password = bcryptPasswordEncoder.encode(registerRequest.password());

        Role role = roleRepository
                    .findByName(DEFAULT_ROLE)
                    .orElseThrow(() -> new RoleNotFoundException ("Role: " + DEFAULT_ROLE + " not found"));

        Set<Role> roles = new HashSet<>();
        roles.add(role);

        Users user = new Users();
        user.setMobileNumber(mobileNumber);
        user.setPassword(password);
        user.setRoles(roles);

        usersRepository.save(user);

        return new RegisterResponse(mobileNumber, user.getCreatedAt());
    }

    public LoginResponse login(LoginRequest loginRequest){

        String mobileNumber = loginRequest.mobileNumber();
        String password = loginRequest.password();
        
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(mobileNumber, password);

        //throws AuthenticationExcpetion on invalid credentials
        authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        
        UserDetails userDetails = myUserDetailsService.loadUserByUsername(mobileNumber);
    
        AuthTokens tokens = jwtService.generateToken(userDetails);
        return LoginResponse.builder()
                            .mobileNumber(mobileNumber)
                            .accessToken(tokens.accessToken())
                            .refreshToken(tokens.refreshToken())
                            .timestamp(Instant.now())
                            .build();
    }
}
