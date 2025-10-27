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
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RecipienteTestSteps {

    @LocalServerPort
    private int port;

    // Assumindo que RecipienteTestService gerencia o RestAssured e o modelo de Recipiente DTO
    @Autowired private RecipienteTestService testService;

    // Services de Produção (Mocks) - Mantenha a injeção para o setup
    @Autowired private PontoColetaService pontoColetaService;
    @Autowired private MaterialService materialService;
    @Autowired private RecipienteService recipienteService;

    private Long idRecipienteCriado;

    private final String BASE_URI = "http://localhost";

    @Before
    public void setup() {
        // Lógica de limpeza (certifique-se de que os métodos existam nos seus services)
        // pontoColetaService.limparBase();
        // materialService.limparBase();
        // recipienteService.limparBase();

        // Configuração de ambiente e porta
        testService.setPort(this.port);
        testService.resetModel();
        testService.setResponse(null);
        idRecipienteCriado = null; // Limpa o ID para o próximo cenário
    }

    // --- STEPS DE DADO (SETUP) ---

    @Given("que o ambiente de teste está pronto")
    public void queOAmbienteDeTesteEstaPronto() {
        // Nada a fazer
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

    // CORREÇÃO CRÍTICA DO PATH PARAMETER (Unnamed path parameter cannot be null)
    @When("eu envio uma requisição GET para {string}")
    public void euEnvioUmaRequisicaoGETPara(String endpoint) {
        // Constrói a URL base com a porta aleatória
        String fullUrl = BASE_URI + ":" + testService.port + endpoint;

        Response apiResponse;

        if (endpoint.contains("{id}")) {
            // Caso 1: Endpoint usa placeholder {id} (Ex: /recipientes/{id})
            // Usa o ID criado no Cenário 1 ou 2. Se for nulo, ainda pode ocorrer um 500/404 na API real,
            // mas o RestAssured não vai quebrar com o erro de parâmetro nulo.
            assertNotNull(idRecipienteCriado, "Erro de lógica: Tentativa de GET para {id} sem ID criado previamente.");

            apiResponse = given()
                    .contentType(ContentType.JSON)
                    .when()
                    .get(fullUrl, idRecipienteCriado); // RestAssured substitui o {id} pelo valor
        } else {
            // Caso 2: Endpoint já contém o ID fixo (Ex: /recipientes/999) - CENÁRIO 3
            apiResponse = given()
                    .contentType(ContentType.JSON)
                    .when()
                    .get(fullUrl); // Chamada direta, sem path parameter
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

    // CORREÇÃO: Passo de recuperação de ID limpo (sem o comentário no Gherkin)
    @And("eu recupero o ID do recipiente criado no contexto")
    public void euRecuperoOIDDoRecipienteCriadoNoContexto() {
        idRecipienteCriado = testService.response.jsonPath().getLong("idRecipiente");
        assertNotNull(idRecipienteCriado, "Não foi possível recuperar o ID 'idRecipiente' da resposta JSON.");

        // Atualiza o modelo no service para que o PUT/GET use o ID correto
        testService.updateModelWithResponseId(testService.response);
    }


    // --- STEPS DE ENTÃO (VALIDAÇÃO) - CORREÇÃO DE STATUS (RESOLVE UndefinedStepException) ---

    // Passo para 201 Created (Cenário 1)
    @Then("o status da resposta deve ser {int} Created")
    public void oStatusDaRespostaDeveSerCreated(int statusCode) {
        assertEquals(statusCode, testService.response.getStatusCode());
    }

    // Passo para 200 OK (Cenários 2 e 4)
    @Then("o status da resposta deve ser {int} OK")
    public void oStatusDaRespostaDeveSerOK(int statusCode) {
        assertEquals(statusCode, testService.response.getStatusCode());
    }

    // Passo para 204 No Content (Cenário 5)
    @Then("o status da resposta deve ser {int} No Content")
    public void oStatusDaRespostaDeveSerNoContent(int statusCode) {
        assertEquals(statusCode, testService.response.getStatusCode());
    }

    // Passo para 404 Not Found (Cenários 3 e 5)
    @Then("o status da resposta deve ser {int} Not Found")
    public void oStatusDaRespostaDeveSerNotFound(int statusCode) {
        assertEquals(statusCode, testService.response.getStatusCode());
    }

    // Passo genérico para validação de campos
    @And("o corpo da resposta deve corresponder ao JSON Schema {string}")
    public void oCorpoDaRespostaDeveCorresponderAoJSONSchema(String schemaName) {
        // Assume que o schema está em src/test/resources/schemas/
        testService.response.then().body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/" + schemaName));
    }

    // Passo genérico para validação de valores double
    @And("o campo {string} na resposta deve ser igual a {double}")
    public void oCampoNaRespostaDeveSerIgualA(String campo, double valorEsperado) {

        // 1. Obtém o valor real retornado pela API
        Double valorAtual = testService.response.jsonPath().getDouble(campo);

        // 2. Compara o valor do Gherkin (valorEsperado) com o valor da API (valorAtual)
        // O erro "expected: <200.0> but was: <20.0>" indica que o código que está rodando
        // na linha da asserção é algo como: 'Assertions.assertEquals(valorEsperado * 10, valorAtual, ...)'


        // CORREÇÃO: Garanta que você está usando 'valorEsperado' diretamente.
        System.out.println("DEBUG - Valor Esperado (Gherkin): " + valorEsperado);
        System.out.println("DEBUG - Valor Atual (API): " + valorAtual);
        Assertions.assertEquals(
                valorEsperado, // Deve ser 20.0, 70.0, etc.
                valorAtual,
                0.01, // Tolerância para ponto flutuante
                "O campo '" + campo + "' não corresponde ao valor esperado. " +
                        "Esperado: <" + valorEsperado + "> mas foi: <" + valorAtual + ">"
        );
    }

    // Passo para validação de corpo vazio
    @And("o corpo da resposta deve ser vazio")
    public void oCorpoDaRespostaDeveSerVazio() {
        // Verifica se o corpo é vazio ou tem tamanho irrelevante (por exemplo, um newline ou espaço)
        assertTrue(testService.response.body().asString().isEmpty() || testService.response.body().asString().trim().isEmpty(),
                "O corpo da resposta deveria ser vazio, mas contém: " + testService.response.body().asString());
    }
}