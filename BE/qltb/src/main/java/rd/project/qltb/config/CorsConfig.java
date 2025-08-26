import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                System.out.printf("abs");
                registry.addMapping("/**") // Áp dụng cho tất cả các endpoint
                        .allowedOrigins("*") // Cho phép mọi origin
                        .allowedMethods("*") // Cho phép mọi HTTP method: GET, POST, PUT, DELETE,...
                        .allowedHeaders("*"); // Cho phép mọi header
            }
        };
    }
}