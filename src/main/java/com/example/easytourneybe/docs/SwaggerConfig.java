package com.example.easytourneybe.docs;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                        name = "EasyTourney",
                        email = "quockhanhlanglim@gmail.com",
                        url = "http://easy-tourney.mgm-edv.de/"
                ),
                description = "MGM intern winter 2023 project",
                title = "EasyTourney App",
                version = "1.0"
        ),
        servers = {
                @Server(
                        description = "Local Test",
                        url = "http://localhost:8080/api"
                ),
                @Server(
                        description = "Production Test",
                        url = "http://easy-tourney.mgm-edv.de/api"
                )
        },
        security = {
                @SecurityRequirement(
                        name = "bearerAuth"
                )
        }

)
@SecurityScheme(
        name = "bearerAuth",
        description = "JWT auth description",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)

public class SwaggerConfig {
}

