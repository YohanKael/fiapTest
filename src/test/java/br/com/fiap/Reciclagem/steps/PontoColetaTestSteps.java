package br.com.fiap.Reciclagem.steps;

import br.com.fiap.Reciclagem.model.PontoColeta;
import br.com.fiap.Reciclagem.service.PontoColetaService;
import br.com.fiap.Reciclagem.services.PontoColetaTestService;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import io.restassured.http.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PontoColetaTestSteps {

    @LocalServerPort
    private int port;

    // Service de Teste (gerencia o RestAssured e o DTO)
    @Autowired private PontoColetaTestService testService;

    // Service de Produção (para injeção de dependência - pode ser mockado)
    @Autowired private PontoColetaService pontoColetaService;

    private Long idPontoColetaCriado;

    private final String BASE_URI = "http://localhost";

    @Before
    public void setup() {
        // Configuração de ambiente e porta
        testService.setPort(this.port);
        testService.resetModel();
        testService.setResponse(null);
        idPontoColetaCriado = null;
    }

    // --- STEPS DE DADO (SETUP) ---

    @Given("que o ambiente de teste de Ponto de Coleta está pronto")
    public void queOAmbienteDeTesteDePontoDeColetaEstaPronto() {
    }

    @Given("que eu tenho os dados de um novo Ponto de Coleta válido")
    public void queEuTenhoOsDadosDeUmNovoPontoColetaValido(DataTable dataTable) {
        Map<String, String> dados = dataTable.asMaps(String.class, String.class).get(0);
        for (Map.Entry<String, String> entry : dados.entrySet()) {
            testService.setPontoColetaField(entry.getKey(), entry.getValue());
        }
    }

    // --- STEPS DE QUANDO (AÇÃO) ---

    // Passo de POST (Necessário ser específico)
    @When("eu envio uma requisição POST para {string} de Ponto de Coleta")
    public void euEnvioUmaRequisicaoPOSTParaDePontoDeColeta(String endpoint) {
        testService.createPontoColeta(endpoint);
    }

    // Passo de GET (Garantido ser específico)
    @When("eu envio uma requisição GET para {string} de Ponto de Coleta")
    public void euEnvioUmaRequisicaoGETParaDePontoDeColeta(String endpoint) {
        String fullUrl = BASE_URI + ":" + testService.port + endpoint;

        Response apiResponse;

        if (endpoint.contains("{id}")) {
            assertNotNull(idPontoColetaCriado, "Erro de lógica: Tentativa de GET para {id} sem ID criado previamente.");
            apiResponse = given()
                    .contentType(ContentType.JSON)
                    .when()
                    .get(fullUrl, idPontoColetaCriado);
        } else {
            apiResponse = given()
                    .contentType(ContentType.JSON)
                    .when()
                    .get(fullUrl);
        }

        testService.setResponse(apiResponse);
    }

    // Passo de atualização de campo (Específico de PontoColeta)
    @When("eu atualizo o nome do Ponto de Coleta para {string}")
    public void euAtualizoONomeDoPontoColetaPara(String novoNome) {
        testService.getPontoColetaModel().setNome(novoNome);
    }

    // Passo de PUT (Garantido ser específico)
    @When("eu envio uma requisição PUT para {string} de Ponto de Coleta com os dados atualizados")
    public void euEnvioUmaRequisicaoPUTParaDePontoDeColetaComOsDadosAtualizados(String endpoint) {
        PontoColeta updatedPontoColeta = testService.getPontoColetaModel();

        assertNotNull(idPontoColetaCriado, "O ID do Ponto de Coleta não pode ser nulo para a requisição PUT.");

        Response apiResponse = given()
                .contentType(ContentType.JSON)
                .body(testService.gson.toJson(updatedPontoColeta))
                .when()
                .put(BASE_URI + ":" + testService.port + endpoint, idPontoColetaCriado);
        testService.setResponse(apiResponse);
    }

    // Passo de DELETE (Garantido ser específico)
    @When("eu envio uma requisição DELETE para {string} de Ponto de Coleta")
    public void euEnvioUmaRequisicaoDELETEParaDePontoDeColeta(String endpoint) {
        assertNotNull(idPontoColetaCriado, "O ID do Ponto de Coleta não pode ser nulo para a requisição DELETE.");

        Response apiResponse = given()
                .when()
                .delete(BASE_URI + ":" + testService.port + endpoint, idPontoColetaCriado);
        testService.setResponse(apiResponse);
    }

    @And("eu recupero o ID do Ponto de Coleta criado no contexto")
    public void euRecuperoOIDDoPontoColetaCriadoNoContexto() {
        idPontoColetaCriado = testService.response.jsonPath().getLong("idPonto");
        assertNotNull(idPontoColetaCriado, "Não foi possível recuperar o ID 'idPonto' da resposta JSON.");

        // Atualiza o modelo no service para que o PUT/GET use o ID correto
        testService.updateModelWithResponseId(testService.response);
    }


    // --- STEPS DE ENTÃO (VALIDAÇÃO) ---

    @Then("o status da resposta de Ponto de Coleta deve ser {int} Created")
    public void oStatusDaRespostaDePontoDeColetaDeveSerCreated(int statusCode) {
        assertEquals(statusCode, testService.response.getStatusCode());
    }

    @Then("o status da resposta de Ponto de Coleta deve ser {int} OK")
    public void oStatusDaRespostaDePontoDeColetaDeveSerOK(int statusCode) {
        assertEquals(statusCode, testService.response.getStatusCode());
    }

    @Then("o status da resposta de Ponto de Coleta deve ser {int} No Content")
    public void oStatusDaRespostaDePontoDeColetaDeveSerNoContent(int statusCode) {
        assertEquals(statusCode, testService.response.getStatusCode());
    }

    @Then("o status da resposta de Ponto de Coleta deve ser {int} Not Found")
    public void oStatusDaRespostaDePontoDeColetaDeveSerNotFound(int statusCode) {
        assertEquals(statusCode, testService.response.getStatusCode());
    }

}