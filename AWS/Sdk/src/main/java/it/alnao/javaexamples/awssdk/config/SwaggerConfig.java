package it.alnao.javaexamples.awssdk.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
/* ex versione
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
*/
@Configuration
public class SwaggerConfig {
    /* 
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("it.alnao.javaexamples.awssdk"))
                .paths(PathSelectors.any())
                .build();
    }
                */
}