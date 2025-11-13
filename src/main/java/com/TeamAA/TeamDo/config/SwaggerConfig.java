package com.TeamAA.TeamDo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!prod") // 운영환경(prod)에서는 Swagger 비활성화
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("TeamAA API 문서")
                        .version("v1.0.0")
                        .description("캡스톤 프로젝트 백엔드 API 명세서입니다."));
    }
}
