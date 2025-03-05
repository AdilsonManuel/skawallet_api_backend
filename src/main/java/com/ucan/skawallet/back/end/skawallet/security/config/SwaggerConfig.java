/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ucan.skawallet.back.end.skawallet.security.config;

import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author azm
 */
@Configuration
public class SwaggerConfig
{

    @Bean
    public OpenAPI customOpenAPI ()
    {
        return new OpenAPI()
                .info(new io.swagger.v3.oas.models.info.Info()
                        .title("Skawallet API")
                        .version("1.0")
                        .description("API documentation for skawallet project"));
    }
}
