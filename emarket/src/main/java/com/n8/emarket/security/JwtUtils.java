package com.n8.emarket.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {

    private final String JWT_SECRET = "EmarketGroup8SuperSecretKeyForSpringSecurityJwtToken2026";
    private final int JWT_EXPIRATION_MS = 86400000;
    private final Key key = Keys.hmacShaKeyFor(JWT_SECRET.getBytes());

    public String generateToken(String email, String role, Long idBranch, Long idUser) {
        return Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .claim("idUser", idUser)
                .claim("idBranch", idBranch)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + JWT_EXPIRATION_MS))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String getEmailFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
    }

    public String getRoleFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().get("role", String.class);
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(authToken);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            System.out.println("Lỗi xác thực JWT: " + e.getMessage());
        }
        return false;
    }
}