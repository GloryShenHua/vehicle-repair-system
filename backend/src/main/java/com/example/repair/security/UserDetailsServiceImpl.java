package com.example.repair.security;
import com.example.repair.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service @RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService{
    private final UserRepository repo;
    @Override
    public UserDetails loadUserByUsername(String username)throws UsernameNotFoundException{
        return repo.findByUsername(username).map(UserDetailsImpl::new)
                .orElseThrow(()->new UsernameNotFoundException("Not found"));
    }
}

