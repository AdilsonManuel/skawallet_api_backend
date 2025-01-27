/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/SpringFramework/Service.java to edit this template
 */
package com.ucan.skawallet.back.end.skawallet.service;

import com.ucan.skawallet.back.end.skawallet.model.UserTokens;
import com.ucan.skawallet.back.end.skawallet.model.Users;
import com.ucan.skawallet.back.end.skawallet.repository.UserRepository;
import com.ucan.skawallet.back.end.skawallet.repository.UserTokenRepository;
import com.ucan.skawallet.back.end.skawallet.security.token.JwtUtil;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 *
 * @author azm
 */
@Service
@AllArgsConstructor
public class UserService implements UserDetailsService
{

    @Autowired
    private final UserRepository userRepository;
    private final UserTokenRepository userTokenRepository;
    private final static String USER_NOT_FOUND_MSG = "User With name %s not found";
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final JwtUtil jwtUtil;

    public List<Users> ListUsers ()
    {
        return userRepository.findAll();
    }

    public Users saveUser (Users user)
    {
        return userRepository.save(user);
    }

    public Optional<Users> getUserById (Long pkUsers)
    {
        return userRepository.findById(pkUsers);
    }

    public Optional<Users> getUserByEmail (String email)
    {
        return userRepository.findByEmail(email);
    }

    public void deleteUser (Long pkUsers)
    {
        userRepository.deleteById(pkUsers);
    }

    @Override
    public UserDetails loadUserByUsername (String email) throws UsernameNotFoundException
    {
        return userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(String.format(USER_NOT_FOUND_MSG, email)));
    }

    public Optional<Users> findByUsername (String username)
    {
        // Lógica para buscar o usuário
        return userRepository.findByName(username);
    }

//    public Optional<Users> findByNameOrPhone(String identifier)
//    {
//        return userRepository.findByName(identifier)
//                .or(() -> userRepository.findByPhone(identifier));
//    }
    public String authenticate (String identifier, String password)
    {
        // Buscar o usuário com base no identifier (nome ou telefone)
        Users user = findUserByIdentifier(identifier);

        // Validar a senha
        validatePassword(password, user);

        // Validar o tipo de usuário (UserType)
        if (user.getType() == null)
        {
            throw new RuntimeException("Tipo de usuário não definido para o identificador fornecido");
        }

        // Gerar o token
        String token = generateAndSaveToken(user);

        return token;
    }

// Método para buscar usuário pelo identifier
    public Users findUserByIdentifier (String identifier)
    {
        return userRepository.findByName(identifier)
                .or(() -> userRepository.findByPhone(identifier))
                .or(() -> userRepository.findByEmail(identifier))
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado para a credencial  fornecida"));
    }

// Método para validar a senha
    private void validatePassword (String rawPassword, Users user)
    {
        if (!passwordEncoder.matches(rawPassword, user.getPassword()))
        {
            throw new RuntimeException("Senha inválida");
        }
    }

// Método para gerar e salvar o token
    private String generateAndSaveToken (Users user)
    {
        // Criar as roles com base no tipo de usuário
        List<String> roles = List.of(user.getType().name());

        // Gerar o token JWT
        String token = jwtUtil.generateToken(user.getName(), roles);

        // Salvar o token no banco de dados
        UserTokens userToken = new UserTokens();
        userToken.setUser(user);
        userToken.setAccessToken(token);
        userToken.setExpiresAt(LocalDateTime.now().plusHours(1)); // Expira em 1 hora
        userToken.setIssuedAt(LocalDateTime.now());
        userTokenRepository.save(userToken);

        return token;
    }

}
