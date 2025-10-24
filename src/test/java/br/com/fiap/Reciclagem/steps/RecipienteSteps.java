package br.com.fiap.Reciclagem.steps;

import br.com.fiap.Reciclagem.CucumberSpringContextConfiguration;
import br.com.fiap.Reciclagem.model.Material;
import br.com.fiap.Reciclagem.model.PontoColeta;
import br.com.fiap.Reciclagem.model.Recipiente; // Importado o Modelo Recipiente
import br.com.fiap.Reciclagem.service.MaterialService;
import br.com.fiap.Reciclagem.service.PontoColetaService;
import br.com.fiap.Reciclagem.service.RecipienteService;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import io.restassured.module.jsv.JsonSchemaValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.time.LocalDate;
import java.util.List;
import java.util.Map; // Mantido apenas para Map interno do PUT/GET, mas não é usado no payload

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RecipienteSteps extends CucumberSpringContextConfiguration {

    @LocalServerPort
    private int port;

    @Autowired
    private PontoColetaService pontoColetaService;

    @Autowired
    private MaterialService materialService;

    @Autowired
    private RecipienteService recipienteService;

    private Response response;
    private Long idRecipienteCriado;
    // CORREÇÃO: MUDANÇA PARA O POJO Recipiente
    private Recipiente recipientePayload;

    private final String BASE_URI = "http://localhost";
    private final String ENDPOINT = "/recipientes";
    private final String ENDPOINT_ID = "/recipientes/{id}";

    // Executado antes de cada cenário
    @Before
    public void setup() {
        // Limpar os mocks para isolar cada cenário
        pontoColetaService.limparBase();
        materialService.limparBase();
        recipienteService.limparBase();
    }

    @Given("que o ambiente de teste está pronto")
    public void queOAmbienteDeTesteEstaPronto() {
        // A limpeza do mock e o carregamento do contexto do Spring já foram feitos.
    }

    @Given("que um Ponto de Coleta com ID {int} e um Material com ID {int} estão cadastrados")
    public void queUmPontoDeColetaEUmMaterialEstaoCadastrados(long idPonto, long idMaterial) {
        // Garante que as dependências existem no Service Mock antes do teste
        PontoColeta ponto = PontoColeta.builder().idPonto(idPonto).nome("Ponto Mock").cidade("Teste City").build();
        Material material = Material.builder().idMaterial(idMaterial).nomeMaterial("Mock Plastic").build();

        // Persiste nos Services Mocks (usando salvar para setar o ID)
        pontoColetaService.salvar(ponto);
        materialService.salvar(material);
    }

    // CORREÇÃO: MUDANÇA DA ASSINATURA PARA LIST<RECIPIENTE>
    @Given("que eu tenho os dados de um novo recipiente válido")
    public void queEuTenhoOsDadosDeUmNovoRecipienteValido(List<Recipiente> recipientes) {
        // O Cucumber converte automaticamente a DataTable para o POJO Recipiente
        recipientePayload = recipientes.get(0);

        // Configura campos que não vieram da DataTable ou precisam ser nulos/atualizados
        recipientePayload.setUltimaAtualizacao(LocalDate.now());
        recipientePayload.setIdRecipiente(null); // Garante que será gerado um novo ID

        // Ajusta os objetos de relacionamento (Recipiente agora tem objetos aninhados no Gherkin)
        // Se o Gherkin não enviar o objeto Ponto/Material completo, fazemos o mock aqui:
        if (recipientePayload.getPontoColeta() == null) {
            recipientePayload.setPontoColeta(new PontoColeta());
            recipientePayload.getPontoColeta().setIdPonto(Long.parseLong(recipientePayload.getPontoColeta().toString()));
        }
        if (recipientePayload.getMaterial() == null) {
            recipientePayload.setMaterial(new Material());
            recipientePayload.getMaterial().setIdMaterial(Long.parseLong(recipientePayload.getMaterial().toString()));
        }

        // NOTA: Para este método funcionar, o Gherkin deve usar colunas como:
        // | pontoColeta.idPonto | material.idMaterial | capacidadeMax | volumeAtual |
        // O Cucumber automaticamente tenta popular os atributos aninhados.
    }

    @When("eu envio uma requisição POST para {string}")
    public void euEnvioUmaRequisicaoPOSTPara(String endpoint) {
        // O RestAssured serializa o POJO Recipiente para JSON
        response = given()
                .contentType("application/json")
                .body(recipientePayload)
                .when()
                .post(BASE_URI + ":" + port + endpoint);
    }

    @Then("o status da resposta deve ser {int} {word}")
    public void oStatusDaRespostaDeveSer(int statusCode, String statusText) {
        assertEquals(statusCode, response.getStatusCode());
    }

    @And("eu recupero o ID do recipiente criado no contexto")
    public void euRecuperoOIDDoRecipienteCriadoNoContexto() {
        // Salva o ID do corpo de resposta
        idRecipienteCriado = response.jsonPath().getLong("idRecipiente");

        // Atualiza o POJO local com o ID gerado (necessário para o PUT/DELETE)
        recipientePayload.setIdRecipiente(idRecipienteCriado);
    }

    @And("o corpo da resposta deve corresponder ao JSON Schema {string}")
    public void oCorpoDaRespostaDeveCorresponderAoJSONSchema(String schemaName) {
        // Validação de Contrato
        response.then().body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/" + schemaName));
    }

    @When("eu envio uma requisição GET para {string}")
    public void euEnvioUmaRequisicaoGETPara(String endpoint) {
        response = given()
                .contentType("application/json")
                .when()
                .get(BASE_URI + ":" + port + endpoint, idRecipienteCriado);
    }

    @And("o campo {string} na resposta deve ser igual a {double}")
    public void oCampoNaRespostaDeveSerIgualA(String campo, double valorEsperado) {
        // Garante que a comparação seja feita corretamente, convertendo double para float, se necessário.
        response.then().body(campo, equalTo((float) valorEsperado));
    }

    @When("eu atualizo o volume do recipiente para {double}")
    public void euAtualizoOVolumeDoRecipientePara(double novoVolume) {
        // Atualiza o POJO Recipiente diretamente
        recipientePayload.setVolumeAtual(novoVolume);
    }

    @When("eu envio uma requisição PUT para {string} com os dados atualizados")
    public void euEnvioUmaRequisicaoPUTParaComOsDadosAtualizados(String endpoint) {
        response = given()
                .contentType("application/json")
                .body(recipientePayload)
                .when()
                .put(BASE_URI + ":" + port + endpoint, idRecipienteCriado);
    }

    @When("eu envio uma requisição DELETE para {string}")
    public void euEnvioUmaRequisicaoDELETEPara(String endpoint) {
        response = given()
                .when()
                .delete(BASE_URI + ":" + port + endpoint, idRecipienteCriado);
    }
}
