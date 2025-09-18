package com.visioners.civic.Jwt;


import java.security.SecureRandom;
import java.util.Date;
import java.util.Properties;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;
    
    @Value("${jwt.token.access.expiry}")
    private Long accessTokenExpiry; 

    public Properties generateToken(UserDetails userDetails){
        String jti = UUID.randomUUID().toString();
        
        String accessToken= Jwts
                            .builder()
                            .setId(jti)
                            .setSubject(userDetails.getUsername())
                            .setIssuedAt(new Date())
                            .setExpiration(new Date(System.currentTimeMillis()+accessTokenExpiry))
                            .signWith(getKey())
                            .claim("role", userDetails.getAuthorities())
                            .compact();

        String refreshToken = getRefreshToken();

        Properties tokens = new Properties();
        tokens.setProperty("accessToken", accessToken);
        tokens.setProperty("refreshToken", refreshToken);
        return tokens;

    }

    private String getRefreshToken() {
        return new SecureRandom().toString();
    }

    private SecretKey getKey(){
        return Keys.hmacShaKeyFor(secret.getBytes());
    }
    
    public String extractMobileNumber(String token) {
        return extractAllClaims(token).getSubject();
    }

    private Date getExpiration(String token){
        return extractAllClaims(token).getExpiration();
    }
    
    public boolean validateToken(String token) {
        return getExpiration(token).before(new Date());
    }

    Claims extractAllClaims(String token){
        return Jwts
            .parserBuilder()
            .setSigningKey(getKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
    }
}
