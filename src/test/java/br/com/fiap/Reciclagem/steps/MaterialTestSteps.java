package br.com.fiap.Reciclagem.steps;

import br.com.fiap.Reciclagem.model.Material;
import br.com.fiap.Reciclagem.service.MaterialService;
import br.com.fiap.Reciclagem.services.MaterialTestService;
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

public class MaterialTestSteps {

    @LocalServerPort
    private int port;

    // Service de Teste (gerencia o RestAssured e o DTO)
    @Autowired private MaterialTestService testService;

    // Service de Produção (para injeção de dependência - pode ser mockado)
    @Autowired private MaterialService materialService;

    private Long idMaterialCriado;

    private final String BASE_URI = "http://localhost";

    @Before
    public void setup() {
        // Configuração de ambiente e porta
        testService.setPort(this.port);
        testService.resetModel();
        testService.setResponse(null);
        idMaterialCriado = null; // Limpa o ID para o próximo cenário
    }

    // --- STEPS DE DADO (SETUP) ---

    @Given("que o ambiente de teste de Material está pronto")
    public void queOAmbienteDeTesteDeMaterialEstaPronto() {
        // Nada a fazer
    }

    @Given("que eu tenho os dados de um novo Material válido")
    public void queEuTenhoOsDadosDeUmNovoMaterialValido(DataTable dataTable) {
        Map<String, String> dados = dataTable.asMaps(String.class, String.class).get(0);
        for (Map.Entry<String, String> entry : dados.entrySet()) {
            testService.setMaterialField(entry.getKey(), entry.getValue());
        }
    }

    // --- STEPS DE QUANDO (AÇÃO) ---

    // Passo de POST (Específico)
    @When("eu envio uma requisição POST para {string} de Material")
    public void euEnvioUmaRequisicaoPOSTParaDeMaterial(String endpoint) {
        testService.createMaterial(endpoint);
    }

    // Passo de GET (Específico)
    @When("eu envio uma requisição GET para {string} de Material")
    public void euEnvioUmaRequisicaoGETParaDeMaterial(String endpoint) {
        String fullUrl = BASE_URI + ":" + testService.port + endpoint;

        Response apiResponse;

        if (endpoint.contains("{id}")) {
            assertNotNull(idMaterialCriado, "Erro de lógica: Tentativa de GET para {id} sem ID criado previamente.");
            apiResponse = given()
                    .contentType(ContentType.JSON)
                    .when()
                    .get(fullUrl, idMaterialCriado); // RestAssured substitui o {id}
        } else {
            apiResponse = given()
                    .contentType(ContentType.JSON)
                    .when()
                    .get(fullUrl);
        }

        testService.setResponse(apiResponse);
    }

    // Passo de atualização de campo (Específico de Material)
    @When("eu atualizo o nome do Material para {string}")
    public void euAtualizoONomeDoMaterialPara(String novoNome) {
        testService.getMaterialModel().setNomeMaterial(novoNome);
    }

    // Passo de PUT (Específico)
    @When("eu envio uma requisição PUT para {string} de Material com os dados atualizados")
    public void euEnvioUmaRequisicaoPUTParaDeMaterialComOsDadosAtualizados(String endpoint) {
        Material updatedMaterial = testService.getMaterialModel();

        assertNotNull(idMaterialCriado, "O ID do Material não pode ser nulo para a requisição PUT.");

        Response apiResponse = given()
                .contentType(ContentType.JSON)
                .body(testService.gson.toJson(updatedMaterial))
                .when()
                .put(BASE_URI + ":" + testService.port + endpoint, idMaterialCriado);
        testService.setResponse(apiResponse);
    }

    // Passo de DELETE (Específico)
    @When("eu envio uma requisição DELETE para {string} de Material")
    public void euEnvioUmaRequisicaoDELETEParaDeMaterial(String endpoint) {
        assertNotNull(idMaterialCriado, "O ID do Material não pode ser nulo para a requisição DELETE.");

        Response apiResponse = given()
                .when()
                .delete(BASE_URI + ":" + testService.port + endpoint, idMaterialCriado);
        testService.setResponse(apiResponse);
    }

    @And("eu recupero o ID do Material criado no contexto")
    public void euRecuperoOIDDoMaterialCriadoNoContexto() {
        idMaterialCriado = testService.response.jsonPath().getLong("idMaterial");
        assertNotNull(idMaterialCriado, "Não foi possível recuperar o ID 'idMaterial' da resposta JSON.");

        // Atualiza o modelo no service para que o PUT/GET use o ID correto
        testService.updateModelWithResponseId(testService.response);
    }


    // --- STEPS DE ENTÃO (VALIDAÇÃO) ---
    // MANTIDOS os Steps de Status, pois são específicos (com prefixo "de Material")

    @Then("o status da resposta de Material deve ser {int} Created")
    public void oStatusDaRespostaDeMaterialDeveSerCreated(int statusCode) {
        assertEquals(statusCode, testService.response.getStatusCode());
    }

    @Then("o status da resposta de Material deve ser {int} OK")
    public void oStatusDaRespostaDeMaterialDeveSerOK(int statusCode) {
        assertEquals(statusCode, testService.response.getStatusCode());
    }

    @Then("o status da resposta de Material deve ser {int} No Content")
    public void oStatusDaRespostaDeMaterialDeveSerNoContent(int statusCode) {
        assertEquals(statusCode, testService.response.getStatusCode());
    }

    @Then("o status da resposta de Material deve ser {int} Not Found")
    public void oStatusDaRespostaDeMaterialDeveSerNotFound(int statusCode) {
        assertEquals(statusCode, testService.response.getStatusCode());
    }

    // REMOVIDOS OS PASSOS DE VALIDAÇÃO DE CAMPO/SCHEMA/CORPO VAZIO
}