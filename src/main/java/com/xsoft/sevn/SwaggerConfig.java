package com.xsoft.sevn;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig extends WebMvcConfigurationSupport {
    @Bean
    Docket docket() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder ()
                .title("API测试文档")
                .description("DEMO项目的接口测试文档")
                .version("1.0")
                .build();
    }

    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler ("/**").addResourceLocations (
                "classpath:/static/");
        registry.addResourceHandler ("swagger-ui.html").addResourceLocations (
                "classpath:/META-INF/resources/");
        registry.addResourceHandler ("/webjars/**").addResourceLocations (
                "classpath:/META-INF/resources/webjars/");
        super.addResourceHandlers (registry);
    }
}
