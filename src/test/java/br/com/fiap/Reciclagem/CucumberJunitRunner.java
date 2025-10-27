// CucumberJunitRunner.java
package br.com.fiap.Reciclagem;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

// As anotações do JUnit 4
@RunWith(Cucumber.class)
@CucumberOptions(
        features = "classpath:features", // Aponta para o Gherkin
        glue = "br.com.fiap.Reciclagem.steps", // Aponta para os steps
        plugin = {"pretty", "html:target/cucumber-report.html"}
)

// As anotações do Spring (como antes)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = br.com.fiap.Reciclagem.ReciclagemApplication.class)
public class CucumberJunitRunner {
    // Esta classe fica vazia.
}