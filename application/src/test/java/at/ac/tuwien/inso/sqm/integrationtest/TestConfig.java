package at.ac.tuwien.inso.sqm.integrationtest;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;

@Configuration
public class TestConfig {

    @Bean
    public JavaMailSender mailSender() {
        return Mockito.mock(JavaMailSender.class);
    }
}
