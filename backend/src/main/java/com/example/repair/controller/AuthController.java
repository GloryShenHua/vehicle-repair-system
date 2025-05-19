package com.example.repair.controller;
import com.example.repair.dto.*;
import com.example.repair.entity.User;
import com.example.repair.enums.Role;
import com.example.repair.repository.UserRepository;
import com.example.repair.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;

@RestController @RequestMapping("/api/auth") @RequiredArgsConstructor
public class AuthController{
    private final AuthenticationManager mgr;
    private final JwtUtils jwt;
    private final UserRepository repo;
    private final PasswordEncoder enc;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest r) {
        try {
            mgr.authenticate(new UsernamePasswordAuthenticationToken(r.username(), r.password()));
            String token = jwt.generateToken(r.username());
            return ResponseEntity.ok(new JwtResponse(token));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body(Map.of("message", "用户名或密码错误"));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).body(Map.of("message", "认证失败：" + e.getMessage()));
        }catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "系统错误"));
        }
    }


    @PostMapping("/register")
    public void register(@RequestBody LoginRequest r){
        if(repo.findByUsername(r.username()).isPresent())throw new RuntimeException("exists");
        User u=new User(); u.setUsername(r.username());
        u.setPassword(enc.encode(r.password())); u.setRole(Role.USER); repo.save(u);
    }
}

