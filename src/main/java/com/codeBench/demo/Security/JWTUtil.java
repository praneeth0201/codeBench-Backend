package com.codeBench.demo.Security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;


import java.security.Key;
import java.util.Date;
import java.util.List;


@Component
public class JWTUtil {
    private static final String SECRET = "mysecretkeymysecretkeymysecretkey12345";

    private final Key key = Keys.hmacShaKeyFor(SECRET.getBytes());
    private final long jwtExpirationMs= 1000*60*30;

    public String generateToken(String userName, List<String> roles){
        return Jwts.builder()
                .setSubject(userName)
                .claim("roles",roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis()+jwtExpirationMs))
                .signWith(key)
                .compact();
    }

    public String extractUserName(String token){
        return getClaims(token).getSubject();
    }

    public List<String> extractRoles(String token){
        return getClaims(token).get("roles",List.class);
    }

    public boolean validateToken(String token){
        try{
            getClaims(token);
            return true;
        }
        catch (Exception e){
            return false;
        }
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
