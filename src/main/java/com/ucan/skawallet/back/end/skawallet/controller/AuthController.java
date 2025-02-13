/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ucan.skawallet.back.end.skawallet.controller;

import com.ucan.skawallet.back.end.skawallet.dto.LoginRequestDTO;
import com.ucan.skawallet.back.end.skawallet.model.Users;
import com.ucan.skawallet.back.end.skawallet.service.UserService;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/*/auth")
@RequiredArgsConstructor
public class AuthController
{

    @Autowired
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO request)
    {
        // Autentica o utilizador e gera o token
        String token = userService.authenticate(request.getIdentifier(), request.getPassword());

        // Buscar o utilizador pelo identificador para obter detalhes adicionais
        Users user = userService.findUserByIdentifier(request.getIdentifier());

        System.err.println("User -> " + user);
        // Validar o tipo de utilizador
        if (user.getType() == null)
        {
            throw new RuntimeException("Tipo de utilizador não definido para o identificador fornecido");
        }

        // Extrair a role do utilizador a partir do tipo
        String role = user.getType().name();
        List<String> roles = Collections.singletonList(role); // Adiciona a role em uma lista

        // Preparar a resposta
        Map<String, Object> response = new HashMap<>();
        response.put("User Name", user.getName()); // Retorna o nome do utilizador
        response.put("Role", roles);              // Retorna a role
        response.put("Token", token);             // Retorna o token gerado

        return ResponseEntity.ok(response);
    }
}
