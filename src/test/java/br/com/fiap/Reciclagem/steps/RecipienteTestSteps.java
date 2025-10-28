package br.com.fiap.Reciclagem.steps;

import br.com.fiap.Reciclagem.model.Material;
import br.com.fiap.Reciclagem.model.PontoColeta;
import br.com.fiap.Reciclagem.model.Recipiente;
import br.com.fiap.Reciclagem.service.MaterialService;
import br.com.fiap.Reciclagem.service.PontoColetaService;
import br.com.fiap.Reciclagem.service.RecipienteService;
import br.com.fiap.Reciclagem.services.RecipienteTestService;
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
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class RecipienteTestSteps {

    @LocalServerPort
    private int port;

    @Autowired private RecipienteTestService testService;

    // Services de Produção (Mocks) - Mantenha a injeção para o setup
    @Autowired private PontoColetaService pontoColetaService;
    @Autowired private MaterialService materialService;
    @Autowired private RecipienteService recipienteService;

    private Long idRecipienteCriado;

    private final String BASE_URI = "http://localhost";

    @Before
    public void setup() {

        // Configuração de ambiente e porta
        testService.setPort(this.port);
        testService.resetModel();
        testService.setResponse(null);
        idRecipienteCriado = null;
    }

    // --- STEPS DE DADO (SETUP) ---

    @Given("que o ambiente de teste está pronto")
    public void queOAmbienteDeTesteEstaPronto() {
    }

    @Given("que um Ponto de Coleta com ID {int} e um Material com ID {int} estão cadastrados")
    public void queUmPontoDeColetaEUmMaterialEstaoCadastrados(long idPonto, long idMaterial) {
        // Cria Mocks para que a validação de existência do Service funcione
        PontoColeta ponto = PontoColeta.builder().idPonto(idPonto).nome("Ponto Mock").cidade("Teste City").build();
        Material material = Material.builder().idMaterial(idMaterial).nomeMaterial("Mock Plastic").build();

        pontoColetaService.salvar(ponto);
        materialService.salvar(material);
    }

    @Given("que eu tenho os dados de um novo recipiente válido")
    public void queEuTenhoOsDadosDeUmNovoRecipienteValido(DataTable dataTable) {
        Map<String, String> dados = dataTable.asMaps(String.class, String.class).get(0);
        for (Map.Entry<String, String> entry : dados.entrySet()) {
            testService.setRecipienteField(entry.getKey(), entry.getValue());
        }
    }

    // --- STEPS DE QUANDO (AÇÃO) ---

    @When("eu envio uma requisição POST para {string}")
    public void euEnvioUmaRequisicaoPOSTPara(String endpoint) {
        testService.createRecipiente(endpoint);
    }

    @When("eu envio uma requisição GET para {string}")
    public void euEnvioUmaRequisicaoGETPara(String endpoint) {
        String fullUrl = BASE_URI + ":" + testService.port + endpoint;
        Response apiResponse;

        if (endpoint.contains("{id}")) {
            assertNotNull(idRecipienteCriado, "Erro de lógica: Tentativa de GET para {id} sem ID criado previamente.");
            apiResponse = given()
                    .contentType(ContentType.JSON)
                    .when()
                    .get(fullUrl, idRecipienteCriado);
        } else {
            apiResponse = given()
                    .contentType(ContentType.JSON)
                    .when()
                    .get(fullUrl);
        }

        testService.setResponse(apiResponse);
    }

    @When("eu atualizo o volume do recipiente para {double}")
    public void euAtualizoOVolumeDoRecipientePara(double novoVolume) {
        testService.getRecipienteModel().setVolumeAtual(novoVolume);
    }

    @When("eu envio uma requisição PUT para {string} com os dados atualizados")
    public void euEnvioUmaRequisicaoPUTParaComOsDadosAtualizados(String endpoint) {
        Recipiente updatedRecipiente = testService.getRecipienteModel();

        assertNotNull(idRecipienteCriado, "O ID do Recipiente não pode ser nulo para a requisição PUT.");

        Response apiResponse = given()
                .contentType(ContentType.JSON)
                .body(testService.gson.toJson(updatedRecipiente))
                .when()
                .put(BASE_URI + ":" + testService.port + endpoint, idRecipienteCriado);
        testService.setResponse(apiResponse);
    }

    @When("eu envio uma requisição DELETE para {string}")
    public void euEnvioUmaRequisicaoDELETEPara(String endpoint) {
        assertNotNull(idRecipienteCriado, "O ID do Recipiente não pode ser nulo para a requisição DELETE.");

        Response apiResponse = given()
                .when()
                .delete(BASE_URI + ":" + testService.port + endpoint, idRecipienteCriado);
        testService.setResponse(apiResponse);
    }

    @And("eu recupero o ID do recipiente criado no contexto")
    public void euRecuperoOIDDoRecipienteCriadoNoContexto() {
        idRecipienteCriado = testService.response.jsonPath().getLong("idRecipiente");
        assertNotNull(idRecipienteCriado, "Não foi possível recuperar o ID 'idRecipiente' da resposta JSON.");

        testService.updateModelWithResponseId(testService.response);
    }


    // --- STEPS DE ENTÃO (VALIDAÇÃO) ---


    @Then("o status da resposta deve ser {int} Created")
    public void oStatusDaRespostaDeveSerCreated(int statusCode) {

        assertEquals(statusCode, testService.response.getStatusCode());
    }

    @Then("o status da resposta deve ser {int} OK")
    public void oStatusDaRespostaDeveSerOK(int statusCode) {
        assertEquals(statusCode, testService.response.getStatusCode());
    }

    @Then("o status da resposta deve ser {int} No Content")
    public void oStatusDaRespostaDeveSerNoContent(int statusCode) {
        assertEquals(statusCode, testService.response.getStatusCode());
    }

    @Then("o status da resposta deve ser {int} Not Found")
    public void oStatusDaRespostaDeveSerNotFound(int statusCode) {
        assertEquals(statusCode, testService.response.getStatusCode());
    }

}