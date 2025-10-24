package br.com.fiap.Reciclagem;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

// IMPORTANTE: coloque a classe principal do seu Spring Boot aqui
@SpringBootTest(classes = ReciclagemApplication.class)
@CucumberContextConfiguration
public class CucumberSpringConfiguration {
}
