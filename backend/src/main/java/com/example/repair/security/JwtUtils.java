package com.example.repair.security;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.security.Key; import java.util.Date;

@Component
public class JwtUtils{
    @Value("${jwt.secret}") private String secret;
    @Value("${jwt.expiration}") private Long exp;
    private Key key(){ return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret)); }
    public String generateToken(String user){
        return Jwts.builder().setSubject(user).setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis()+exp))
                .signWith(key(),SignatureAlgorithm.HS512).compact();}
    public String getUsername(String token){
        return Jwts.parserBuilder().setSigningKey(key()).build()
                .parseClaimsJws(token).getBody().getSubject();}
    public boolean validate(String token){
        try{getUsername(token);return true;}catch(Exception e){return false;}
    }
}
