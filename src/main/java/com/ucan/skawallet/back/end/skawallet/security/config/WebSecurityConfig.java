/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ucan.skawallet.back.end.skawallet.security.config;

import com.ucan.skawallet.back.end.skawallet.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 *
 * @author azm
 */
@Configuration
@AllArgsConstructor
@EnableWebSecurity
public class WebSecurityConfig
{

    private final UserService userService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

//    // Injeção de dependências via construtor (padrão recomendado em Spring 3.x)
//    public WebSecurityConfig(UserService userService, BCryptPasswordEncoder bCryptPasswordEncoder)
//    {
//        this.userService = userService;
//        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
//    }

    // Configuração de autorização
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception
    {
        http
                .csrf(csrf -> csrf.disable()) // Desabilitar CSRF, se necessário
                .authorizeHttpRequests(authz -> authz // Substituir authorizeRequests por authorizeHttpRequests
                .requestMatchers("/api/*/**") // Permitir acesso a essas rotas
                .permitAll()
                .anyRequest().authenticated() // Exige autenticação para qualquer outra requisição
                )
                .formLogin(form -> form
                .loginPage("/login") // Defina explicitamente a URL para a página de login
                .permitAll() // Permite o acesso à página de login sem autenticação
                );

        return http.build();  // Retorna a configuração finalizada
    }

    // Configuração do AuthenticationManagerBuilder
    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception
    {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.authenticationProvider(daoAuthenticationProvider());
        return authenticationManagerBuilder.build();
    }

    // Configuração do DaoAuthenticationProvider
    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider()
    {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(bCryptPasswordEncoder); // Configura o encoder de senha
        daoAuthenticationProvider.setUserDetailsService(userService); // Configura o UserDetailsService
        return daoAuthenticationProvider;
    }

//    // Configuração do PasswordEncoder
//    @Bean
//    public BCryptPasswordEncoder passwordEncoder()
//    {
//        return new BCryptPasswordEncoder();
//    }

}
