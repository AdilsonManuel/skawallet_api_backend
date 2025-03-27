/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/SpringFramework/Controller.java to edit this template
 */
package com.ucan.skawallet.back.end.skawallet.controller;

import com.ucan.skawallet.back.end.skawallet.model.Users;
import com.ucan.skawallet.back.end.skawallet.repository.UserRepository;
import com.ucan.skawallet.back.end.skawallet.service.UserService;
import jakarta.mail.MessagingException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/*/users")
@AllArgsConstructor
@Slf4j
public class UserController
{

    @Autowired
    private final UserService userService;
    private final UserRepository userRepository;

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // 1. Criar um novo usuário
    @PostMapping("/registration")
    public ResponseEntity<?> registUser (@RequestBody Users user)
    {
        System.err.println("UserController.registUser()->" + user);
        if (user.getName() == null || user.getName().isEmpty())
        {
            return new ResponseEntity<>("Username is required", HttpStatus.BAD_REQUEST);
        }
        if (user.getPassword() == null || user.getPassword().isEmpty())
        {
            return new ResponseEntity<>("Password is required", HttpStatus.BAD_REQUEST);
        }
        user.setCreatedAt(LocalDateTime.now());

        try
        {
            user.setCreatedAt(LocalDateTime.now());
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            Users newUser = userService.saveUser(user);
            return new ResponseEntity<>(newUser, HttpStatus.CREATED);
        }
        catch (MessagingException e)
        {
            return new ResponseEntity<>("Error creating user", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 2. Obter usuário por ID
    @GetMapping("/{pk_users}")
    public ResponseEntity<?> getUserById (@PathVariable("pk_users") Long pk_users)
    {
        Optional<Users> user = userService.getUserById(pk_users);
        if (user.isPresent())
        {
            return new ResponseEntity<>(user.get(), HttpStatus.OK);
        }
        else
        {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
    }

    // 3. Obter todos os usuários
    @GetMapping("/")
    public ResponseEntity<List<Users>> getAllUsers ()
    {
        List<Users> users = userService.ListUsers();

        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    // 4. Actualizar usuário
    @PatchMapping("/{pk_users}")
    public ResponseEntity<?> updateUser (@PathVariable Long pk_users, @RequestBody Users user)
    {
        Optional<Users> existingUser = userService.getUserById(pk_users);
        if (existingUser.isPresent())
        {
            Users updatedUser = existingUser.get();
            updatedUser.setName(user.getName() != null ? user.getName() : updatedUser.getName());
            updatedUser.setEmail(user.getEmail() != null ? user.getEmail() : updatedUser.getEmail());
            updatedUser.setPhone(user.getPhone() != null ? user.getPhone() : updatedUser.getPhone());
            updatedUser.setType(user.getType() != null ? user.getType() : updatedUser.getType());
            updatedUser.setPassword(user.getPassword() != null ? user.getPassword() : updatedUser.getPassword());

            try
            {
                userService.saveUser(updatedUser);
                return new ResponseEntity<>(updatedUser, HttpStatus.OK);
            }
            catch (Exception e)
            {
                return new ResponseEntity<>("Error updating user", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        else
        {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
    }

    // 5. Deletar usuário
    @DeleteMapping("/{pk_users}")
    public ResponseEntity<?> deleteUser (@PathVariable Long pk_users)
    {
        Optional<Users> user = userService.getUserById(pk_users);
        if (user.isPresent())
        {
            userService.deleteUser(pk_users);
            return new ResponseEntity<>("User deleted successfully", HttpStatus.OK);
        }
        else
        {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/activate")
    public ResponseEntity<String> verifyUser (@RequestParam String code)
    {
        log.info("Recebido código de activação: {}", code);

        Optional<Users> userOpt = userRepository.findByVerificationCode(code);

        if (userOpt.isPresent())
        {
            Users user = userOpt.get();
            user.setEnabled(true);  // Ativar conta
            user.setVerificationCode(null);
            userRepository.save(user);
            log.info("✅ Conta activada: {}", user.getEmail());
            return ResponseEntity.ok("Conta activada com sucesso!");
        }
        else
        {
            log.warn("⚠ Código de verificação inválido: {}", code);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Código inválido.");
        }
    }

}
