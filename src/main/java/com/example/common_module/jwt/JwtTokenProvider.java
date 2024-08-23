package com.example.common_module.jwt;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTokenProvider {
    @Value("${spring.jwt.secret}")
    private String jwtSecretKey;
    @Value("${spring.jwt.accessTokenExpirationTime}")
    private Long jwtAccessTokenExpirationTime;
    @Value("${spring.jwt.refreshTokenExpirationTime}")
    private Long jwtRefreshTokenExpirationTime;

    public String generateAccessToken(Authentication authentication) {
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        Date expireDate = new Date(new Date().getTime() + jwtAccessTokenExpirationTime);
        return Jwts.builder()
                .setSubject(customUserDetails.getUsername()) // email
                .claim("user-id", customUserDetails.getId())
//                .claim("user-email", customUserDetails.get)
                .setIssuedAt(new Date())
                .setExpiration(expireDate)
                .signWith(SignatureAlgorithm.HS512, jwtSecretKey)
                .compact();
    }

    public String generateRefreshToken(Authentication authentication) {
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        Date expireDate = new Date(new Date().getTime() + jwtRefreshTokenExpirationTime);
        return Jwts.builder()
                .setSubject(customUserDetails.getUsername()) // email
                .claim("user-id", customUserDetails.getId())
//                .claim("user-email", customUserDetails.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(expireDate)
                .signWith(SignatureAlgorithm.HS512, jwtSecretKey)
                .compact();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public Long getUserIdFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(jwtSecretKey)
                .parseClaimsJws(token)
                .getBody()
                .get("user-id", Long.class);
    }

    public String getEmailFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(jwtSecretKey)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

//    public String getUserEmailFromToken(String token) {
//        return Jwts.parser()
//                .setSigningKey(jwtSecretKey)
//                .parseClaimsJws(token)
//                .getBody()
//                .get("user-email", String.class);
//    }

    public Date getExpirationFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(jwtSecretKey)
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public Boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(jwtSecretKey).parseClaimsJws(token);
            return true;
        } catch (SignatureException ex) {
            System.out.println("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            System.out.println("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            System.out.println("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            System.out.println("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            System.out.println("JWT claims string is empty.");
        }
        return false;
    }
}
