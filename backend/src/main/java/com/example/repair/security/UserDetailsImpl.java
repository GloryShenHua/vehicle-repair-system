package com.example.repair.security;
import com.example.repair.entity.User;
import org.springframework.security.core.*;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.*;

public class UserDetailsImpl implements UserDetails{
    private final User user;
    public UserDetailsImpl(User u){this.user=u;}
    public Long getId(){return user.getId();}
    @Override public Collection<? extends GrantedAuthority> getAuthorities(){
        return List.of((GrantedAuthority)()->"ROLE_"+user.getRole().name());
    }
    @Override public String getPassword(){return user.getPassword();}
    @Override public String getUsername(){return user.getUsername();}
    @Override public boolean isAccountNonExpired(){return true;}
    @Override public boolean isAccountNonLocked(){return true;}
    @Override public boolean isCredentialsNonExpired(){return true;}
    @Override public boolean isEnabled(){return true;}
}
