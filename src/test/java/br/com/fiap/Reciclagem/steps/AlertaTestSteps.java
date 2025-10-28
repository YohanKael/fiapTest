package br.com.fiap.Reciclagem.steps;

import br.com.fiap.Reciclagem.model.PontoColeta;
import br.com.fiap.Reciclagem.service.PontoColetaService;
import br.com.fiap.Reciclagem.services.AlertaTestService;
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

public class AlertaTestSteps {

    @LocalServerPort
    private int port;

    @Autowired private AlertaTestService testService;

    // Service de Produção (para setup de dependências)
    @Autowired private PontoColetaService pontoColetaService;

    private Long idAlertaCriado;

    private final String BASE_URI = "http://localhost";

    @Before
    public void setup() {
        testService.setPort(this.port);
        testService.resetModel();
        testService.setResponse(null);
        idAlertaCriado = null;
    }

    // --- STEPS DE DADO (SETUP) ---

    @Given("que o ambiente de teste de Alerta está pronto")
    public void queOAmbienteDeTesteDeAlertaEstaPronto() {
    }

    @Given("que um Ponto de Coleta com ID {int} está cadastrado para o Alerta")
    public void queUmPontoDeColetaEstaCadastradoParaOAlerta(long idPonto) {
        // Cria Mock para que a validação de existência do Service funcione
        PontoColeta ponto = PontoColeta.builder().idPonto(idPonto).nome("Ponto Alerta Mock").cidade("Alerta City").build();
        pontoColetaService.salvar(ponto);
    }

    @Given("que eu tenho os dados de um novo Alerta válido")
    public void queEuTenhoOsDadosDeUmNovoAlertaValido(DataTable dataTable) {
        Map<String, String> dados = dataTable.asMaps(String.class, String.class).get(0);
        for (Map.Entry<String, String> entry : dados.entrySet()) {
            // No Gherkin, o ID do Ponto é mapeado para o campo idPontoColeta
            String field = entry.getKey().equals("idPonto") ? "idPontoColeta" : entry.getKey();
            testService.setAlertaField(field, entry.getValue());
        }
    }

    // --- STEPS DE QUANDO (AÇÃO) ---

    @When("eu envio uma requisição POST para {string} de Alerta")
    public void euEnvioUmaRequisicaoPOSTParaDeAlerta(String endpoint) {
        testService.createAlerta(endpoint);
    }

    @When("eu envio uma requisição GET para {string} de Alerta")
    public void euEnvioUmaRequisicaoGETParaDeAlerta(String endpoint) {
        String fullUrl = BASE_URI + ":" + testService.port + endpoint;
        Response apiResponse;

        if (endpoint.contains("{id}")) {
            assertNotNull(idAlertaCriado, "Erro de lógica: Tentativa de GET para {id} sem ID criado previamente.");
            apiResponse = given()
                    .contentType(ContentType.JSON)
                    .when()
                    .get(fullUrl, idAlertaCriado);
        } else {
            apiResponse = given()
                    .contentType(ContentType.JSON)
                    .when()
                    .get(fullUrl);
        }

        testService.setResponse(apiResponse);
    }

    // Passo de atualização de campo (Específico de Alerta)
    @When("eu atualizo a mensagem do Alerta para {string}")
    public void euAtualizoAMensagemDoAlertaPara(String novaMensagem) {
        testService.getAlertaModel().setMensagem(novaMensagem);
    }

    @When("eu envio uma requisição PUT para {string} de Alerta com os dados atualizados")
    public void euEnvioUmaRequisicaoPUTParaDeAlertaComOsDadosAtualizados(String endpoint) {
        // Garante que o ID está no modelo antes de enviar o PUT
        testService.getAlertaModel().setIdAlerta(idAlertaCriado);

        assertNotNull(idAlertaCriado, "O ID do Alerta não pode ser nulo para a requisição PUT.");

        Response apiResponse = given()
                .contentType(ContentType.JSON)
                .body(testService.gson.toJson(testService.getAlertaModel()))
                .when()
                .put(BASE_URI + ":" + testService.port + endpoint, idAlertaCriado);
        testService.setResponse(apiResponse);
    }

    @When("eu envio uma requisição DELETE para {string} de Alerta")
    public void euEnvioUmaRequisicaoDELETEParaDeAlerta(String endpoint) {
        assertNotNull(idAlertaCriado, "O ID do Alerta não pode ser nulo para a requisição DELETE.");

        Response apiResponse = given()
                .when()
                .delete(BASE_URI + ":" + testService.port + endpoint, idAlertaCriado);
        testService.setResponse(apiResponse);
    }

    @And("eu recupero o ID do Alerta criado no contexto")
    public void euRecuperoOIDDoAlertaCriadoNoContexto() {
        idAlertaCriado = testService.response.jsonPath().getLong("idAlerta");
        assertNotNull(idAlertaCriado, "Não foi possível recuperar o ID 'idAlerta' da resposta JSON.");

        // Atualiza o modelo no service para que o PUT/GET use o ID correto
        testService.updateModelWithResponseId(testService.response);
    }


    // --- STEPS DE ENTÃO (VALIDAÇÃO) ---

    @Then("o status da resposta de Alerta deve ser {int} Created")
    public void oStatusDaRespostaDeAlertaDeveSerCreated(int statusCode) {
        assertEquals(statusCode, testService.response.getStatusCode());
    }

    @Then("o status da resposta de Alerta deve ser {int} OK")
    public void oStatusDaRespostaDeAlertaDeveSerOK(int statusCode) {
        assertEquals(statusCode, testService.response.getStatusCode());
    }

    @Then("o status da resposta de Alerta deve ser {int} No Content")
    public void oStatusDaRespostaDeAlertaDeveSerNoContent(int statusCode) {
        assertEquals(statusCode, testService.response.getStatusCode());
    }

    @Then("o status da resposta de Alerta deve ser {int} Not Found")
    public void oStatusDaRespostaDeAlertaDeveSerNotFound(int statusCode) {
        assertEquals(statusCode, testService.response.getStatusCode());
    }
}
