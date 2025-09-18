package com.visioners.civic.service;

import java.time.Instant;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.visioners.civic.Jwt.JwtService;
import com.visioners.civic.dto.auth.LoginRequest;
import com.visioners.civic.dto.auth.LoginResponse;
import com.visioners.civic.dto.auth.RegisterRequest;
import com.visioners.civic.dto.auth.RegisterResponse;
import com.visioners.civic.entity.Role;
import com.visioners.civic.entity.Users;
import com.visioners.civic.exception.RoleNotFoundException;
import com.visioners.civic.repository.RoleRepository;
import com.visioners.civic.repository.UsersRepository;
import com.visioners.civic.service.userdetails.MyUserDetailsService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    
    private final UsersRepository usersRepository;
    private final PasswordEncoder bcryptPasswordEncoder;
    private final RoleRepository roleRepository;
    private final JwtService jwtService;
    private final MyUserDetailsService myUserDetailsService;

    public RegisterResponse register(RegisterRequest registerRequest){
        String mobileNumber = registerRequest.mobileNumber();
        String password = bcryptPasswordEncoder.encode(registerRequest.password());

        String roleName = "CITIZEN";
        Role role = roleRepository
                    .findByName(roleName)
                    .orElseThrow(() -> new RoleNotFoundException ("Role: " + roleName + " not found"));

        Set<Role> roles = new HashSet<>();
        roles.add(role);

        Users user = new Users();
        user.setMobileNumber(mobileNumber);
        user.setPassword(password);
        user.setRoles(roles);

        roleRepository.save(role);
        usersRepository.save(user);

        return new RegisterResponse(mobileNumber, user.getCreatedAt());
    }

    public LoginResponse login(LoginRequest loginRequest, AuthenticationManager authenticationManager){

        String mobileNumber = loginRequest.mobileNumber();
        String password = loginRequest.password();
        
        UserDetails userDetails = myUserDetailsService.loadUserByUsername(mobileNumber);
    
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = UsernamePasswordAuthenticationToken.unauthenticated(mobileNumber, password);

        //throws AuthenticationExcpetion on invalid credentials
        authenticationManager.authenticate(usernamePasswordAuthenticationToken);

        Properties tokens = jwtService.generateToken(userDetails);

        return LoginResponse.builder()
                            .mobileNuber(mobileNumber)
                            .accessToken(tokens.getProperty("accessToken"))
                            .refreshToken(tokens.getProperty("refreshToken"))
                            .timestamp(Instant.now())
                            .build();
    }
}
