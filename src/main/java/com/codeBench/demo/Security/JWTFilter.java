package com.codeBench.demo.Security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class JWTFilter extends OncePerRequestFilter {

    JWTUtil jwtUtil;
    public JWTFilter(JWTUtil jwtUtil){
        this.jwtUtil=jwtUtil;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();

        if (path.startsWith("/api/auth")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader=request.getHeader("Authorization");

        String userName=null;
        String token=null;

        if(authHeader!=null && authHeader.startsWith("Bearer ")){
            token=authHeader.substring(7);
            userName=jwtUtil.extractUserName(token);

        }

        if(userName!=null && SecurityContextHolder.getContext().getAuthentication()==null){

            if(jwtUtil.validateToken(token)){
                List<String> roles=jwtUtil.extractRoles(token);

                Set<GrantedAuthority> authorities=roles
                        .stream()
                        .map(role -> new SimpleGrantedAuthority(role))
                        .collect(Collectors.toSet());




                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userName,
                                null,
                                authorities
                        );


                SecurityContextHolder.getContext().setAuthentication(authentication);

            }



        }
        filterChain.doFilter(request,response);


    }
}
