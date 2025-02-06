/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ucan.skawallet.back.end.skawallet.service;

import com.ucan.skawallet.back.end.skawallet.model.Users;
import com.ucan.skawallet.back.end.skawallet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OAuth2UserService
{

    private final UserRepository userRepository;

    public Users processOAuthPostLogin (OAuth2User oAuth2User)
    {
        String email = oAuth2User.getAttribute("email");

        Users user = userRepository.findByEmail(email)
                .orElseGet(() ->
                {
                    Users newUser = new Users();
                    newUser.setEmail(email);
                    newUser.setName(oAuth2User.getAttribute("name"));
                    newUser.setEnabled(true);
                    return userRepository.save(newUser);
                });

        return user;
    }
}
