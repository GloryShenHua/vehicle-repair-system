package com.example.repair.security;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component @RequiredArgsConstructor
public class JwtAuthFilter extends GenericFilter{
    private final JwtUtils jwt; private final UserDetailsServiceImpl svc;
    @Override
    public void doFilter(ServletRequest req,ServletResponse res,FilterChain chain)
            throws IOException,ServletException{
        HttpServletRequest r=(HttpServletRequest)req;
        String h=r.getHeader("Authorization");
        if(h!=null && h.startsWith("Bearer ")){
            String token=h.substring(7);
            if(jwt.validate(token)){
                var user=svc.loadUserByUsername(jwt.getUsername(token));
                var auth=new UsernamePasswordAuthenticationToken(
                        user,null,user.getAuthorities());
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(r));
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }
        chain.doFilter(req,res);
    }
}
