package com.visioners.civic.auth.service;


import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.visioners.civic.auth.dto.AuthTokens;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtTokenService {

    @Value("${jwt.secret}")
    private String secret;
    
    @Value("${jwt.token.access.expiry}")
    private Long accessTokenExpiry; 

    public AuthTokens generateToken(UserDetails userDetails){
        String jti = UUID.randomUUID().toString();
        
        String accessToken= Jwts
                            .builder()
                            .setId(jti)
                            .setSubject(userDetails.getUsername())
                            .setIssuedAt(new Date())
                            .setExpiration(new Date(System.currentTimeMillis()+accessTokenExpiry))
                            .signWith(getKey())
                            .claim("roles", 
                                userDetails
                                    .getAuthorities()
                                    .stream()
                                    .map(GrantedAuthority::getAuthority)
                                    .collect(Collectors.toList())
                            )
                            .compact();

        String refreshToken = getRefreshToken();

        AuthTokens tokens = new AuthTokens(accessToken, refreshToken);
        return tokens;
    }

    private String getRefreshToken() {
        byte[] bytes = new byte[64];
        new SecureRandom().nextBytes(bytes);
        return  Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    public SecretKey getKey(){
        return Keys.hmacShaKeyFor(secret.getBytes());
    }
}
