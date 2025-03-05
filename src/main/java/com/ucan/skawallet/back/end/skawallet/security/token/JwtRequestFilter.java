/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ucan.skawallet.back.end.skawallet.security.token;

import com.ucan.skawallet.back.end.skawallet.repository.UserTokenRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Component
@AllArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter
{

    @Autowired
    private final JwtUtil jwtUtil;

    @Autowired
    private final UserTokenRepository userTokenRepository;

//    public JwtRequestFilter(JwtUtil jwtUtil)
//    {
//        this.jwtUtil = jwtUtil;
//    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException
    {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer "))
        {
            String token = authHeader.substring(7);

            // Verificar se o token existe na base de dados
            userTokenRepository.findByAccessToken(token)
                    .ifPresent(userToken ->
                    {
                        if (jwtUtil.validateToken(token, userToken.getUser().getName()))
                        {
                            List<GrantedAuthority> authorities = jwtUtil.extractRoles(token).stream()
                                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                                    .collect(Collectors.toList());

                            UsernamePasswordAuthenticationToken authentication
                                    = new UsernamePasswordAuthenticationToken(userToken.getUser().getName(), null, authorities);
                            SecurityContextHolder.getContext().setAuthentication(authentication);
                        }
                    });
        }

        chain.doFilter(request, response);
    }
}
