/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ucan.skawallet.back.end.skawallet.dto;

import lombok.Data;

@Data
public class LoginRequestDTO
{

    private String identifier; // Nome ou telefone
    private String password;
}
