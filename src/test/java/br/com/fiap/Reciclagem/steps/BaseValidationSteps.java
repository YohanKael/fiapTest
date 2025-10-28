package br.com.fiap.Reciclagem.steps;

import br.com.fiap.Reciclagem.services.PontoColetaTestService;
import br.com.fiap.Reciclagem.services.RecipienteTestService;
import br.com.fiap.Reciclagem.services.MaterialTestService;
import io.cucumber.java.en.And;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertTrue;

// Esta classe contém os steps genéricos que TODOS os modelos usam.
public class BaseValidationSteps {

    // Injeta TODOS os Services de Teste para descobrir qual tem a última Response
    @Autowired(required = false) private RecipienteTestService recipienteTestService;
    @Autowired(required = false) private PontoColetaTestService pontoColetaTestService;
    @Autowired(required = false) private MaterialTestService materialTestService;

    /**
     * Retorna a Response mais recente de qualquer um dos Test Services.
     * Isso garante que a validação use a resposta da última requisição feita.
     */
    private Response getCurrentResponse() {
        if (recipienteTestService != null && recipienteTestService.response != null) {
            return recipienteTestService.response;
        }
        if (pontoColetaTestService != null && pontoColetaTestService.response != null) {
            return pontoColetaTestService.response;
        }
        if (materialTestService != null && materialTestService.response != null) {
            return materialTestService.response;
        }
        throw new IllegalStateException("Nenhuma Response de API ativa encontrada em nenhum Test Service.");
    }

    // Passo genérico para validação de valores string
    @And("o campo {string} na resposta deve ser igual a {string}")
    public void oCampoNaRespostaDeveSerIgualAString(String campo, String valorEsperado) {
        Response response = getCurrentResponse();
        String valorAtual = response.jsonPath().getString(campo);
        Assertions.assertEquals(valorEsperado, valorAtual,
                "O campo '" + campo + "' não corresponde ao valor esperado. " +
                        "Esperado: <" + valorEsperado + "> mas foi: <" + valorAtual + ">"
        );
    }

    // Passo genérico para validação de valores double
    @And("o campo {string} na resposta deve ser igual a {double}")
    public void oCampoNaRespostaDeveSerIgualADouble(String campo, double valorEsperado) {
        Response response = getCurrentResponse();
        Double valorAtual = response.jsonPath().getDouble(campo);

        Assertions.assertEquals(
                valorEsperado,
                valorAtual,
                0.01, // Tolerância
                "O campo '" + campo + "' não corresponde ao valor esperado."
        );
    }

    // Passo para validação de corpo vazio
    @And("o corpo da resposta deve ser vazio")
    public void oCorpoDaRespostaDeveSerVazio() {
        Response response = getCurrentResponse();
        assertTrue(response.body().asString().isEmpty() || response.body().asString().trim().isEmpty(),
                "O corpo da resposta deveria ser vazio, mas contém: " + response.body().asString());
    }

    // Passo genérico para validação de Schemas
    @And("o corpo da resposta deve corresponder ao JSON Schema {string}")
    public void oCorpoDaRespostaDeveCorresponderAoJSONSchema(String schemaName) {
        getCurrentResponse().then().body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/" + schemaName));
    }
}