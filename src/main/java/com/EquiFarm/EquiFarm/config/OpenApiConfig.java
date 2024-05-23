package com.EquiFarm.EquiFarm.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

import org.springframework.context.annotation.Bean;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI usersMicroserviceOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("EquiFarm")
                        .description("""
                                EquiFarm is a comprehensive platform that aims to revolutionize the agricultural value chains and provide a range of financial services and smart solutions for farmers and agri-businesses.
                                 """)
                        .version("1.0"));
    }
    @Bean
    public GroupedOpenApi api() {
        return GroupedOpenApi.builder()
                .group("api")
                .packagesToScan("com.EquiFarm.EquiFarm")
                .addOpenApiCustomizer(openApi -> {
                    openApi.getComponents()
                            .addSecuritySchemes("bearer-token",
                                    new SecurityScheme()
                                            .type(SecurityScheme.Type.HTTP)
                                            .scheme("bearer")
                                            .bearerFormat("JWT")
                            );
                    openApi.addSecurityItem(
                            new SecurityRequirement().addList("bearer-token")
                    );
                }).build();
    }
}

