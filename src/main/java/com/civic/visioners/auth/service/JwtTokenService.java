package com.civic.visioners.auth.service;


import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtTokenService {

    @Value("${jwt.secret}")
    private String secret;
    
    @Value("${jwt.token.access.expiry}")
    private Long accessTokenExpiry; 

    public String generateToken(UserDetails userDetails){
        String jti = UUID.randomUUID().toString();
        List<String> roles = userDetails
                                    .getAuthorities()
                                    .stream()
                                    .map(GrantedAuthority::getAuthority)
                                    .collect(Collectors.toList());
        
        System.out.println("Roles going into JWT: " + roles);

        String accessToken= Jwts
                            .builder()
                            .setId(jti)
                            .setSubject(userDetails.getUsername())
                            .setIssuedAt(new Date())
                            .setExpiration(new Date(System.currentTimeMillis()+accessTokenExpiry))
                            .signWith(getKey())
                            .claim("roles", roles)
                            .compact();
        return accessToken;      
    }

    public SecretKey getKey(){
        return Keys.hmacShaKeyFor(secret.getBytes());
    }
}
