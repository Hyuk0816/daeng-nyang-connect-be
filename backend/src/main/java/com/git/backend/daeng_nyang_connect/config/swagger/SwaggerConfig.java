package com.git.backend.daeng_nyang_connect.config.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .components(new Components())
                .info(apiInfo());
    }

    private Info apiInfo() {
        return new Info()
                .title("댕냥커넥트 API")
                .description("댕냥커넥트 Swagger UI 테스트")
                .version("1.0.0");
    }

    @Bean
    public GroupedOpenApi getAllApi() {
        return GroupedOpenApi
                .builder()
                .group("ALL")
                .packagesToScan("com.git.backend.daeng_nyang_connect")
                .build();
    }

    @Bean
    public GroupedOpenApi getMyPageApi() {
        return createGroupedOpenApi("myPage_마이페이지", "/api/myPage/**");
    }

    @Bean
    public GroupedOpenApi getAnimalApi() {
        return createGroupedOpenApi("animal_파양동물", "/api/animal/**");
    }

    @Bean
    public GroupedOpenApi getReviewApi() {
        return createGroupedOpenApi("review_입양 후기", "/api/review/**");
    }

    @Bean
    public GroupedOpenApi getLostApi() {
        return createGroupedOpenApi("lost_미아센터", "/api/lost/**");
    }
    @Bean
    public GroupedOpenApi getMateApi() {
        return createGroupedOpenApi("mate_댕냥메이트", "/api/mate/**");
    }

    @Bean
    public GroupedOpenApi getMyPetApi() {
        return createGroupedOpenApi("myPet_나의 댕냥이", "/api/my_pet/**");
    }

    @Bean
    public GroupedOpenApi getTipsApi() {
        return createGroupedOpenApi("tips_댕냥 꿀팁", "/api/tips/**");
    }

    private GroupedOpenApi createGroupedOpenApi(String groupName, String path) {
        return GroupedOpenApi.builder()
                .group(groupName)
                .pathsToMatch(path)
                .build();
    }
}

