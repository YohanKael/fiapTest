package br.com.fiap.Reciclagem.services;

import br.com.fiap.Reciclagem.model.PontoColeta;
import com.google.gson.Gson;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.springframework.stereotype.Service;

import static io.restassured.RestAssured.given;

@Service
public class PontoColetaTestService {

    private PontoColeta pontoColetaModel;
    public Response response;
    public final Gson gson;
    public int port;

    private final String BASE_URI = "http://localhost";

    public PontoColetaTestService() {
        this.pontoColetaModel = new PontoColeta();
        this.gson = new Gson();
    }

    /**
     * Reseta o DTO do PontoColeta para um novo cenário de teste.
     */
    public void resetModel() {
        this.pontoColetaModel = new PontoColeta();
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    /**
     * Mapeia valores de String (vindos do Gherkin DataTable) para os campos do modelo.
     */
    public void setPontoColetaField(String field, String value) {
        if (value == null || value.isEmpty()) return;

        switch (field.toLowerCase()) {
            case "nome":
                pontoColetaModel.setNome(value);
                break;
            case "endereco":
                pontoColetaModel.setEndereco(value);
                break;
            case "bairro":
                pontoColetaModel.setBairro(value);
                break;
            case "cidade":
                pontoColetaModel.setCidade(value);
                break;
            case "estado":
                pontoColetaModel.setEstado(value);
                break;
            case "cep":
                pontoColetaModel.setCep(value);
                break;
            case "idponto":
                pontoColetaModel.setIdPonto(Long.parseLong(value));
                break;
            default:
                throw new IllegalArgumentException("Campo não mapeado no PontoColetaTestService: " + field);
        }
    }

    /**
     * Executa a requisição POST para criação de um novo Ponto de Coleta.
     */
    public Response createPontoColeta(String endPoint) {
        pontoColetaModel.setIdPonto(null); // Garante que o ID é nulo na criação

        String bodyToSend = gson.toJson(pontoColetaModel);

        response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(bodyToSend)
                .when()
                .post(BASE_URI + ":" + port + endPoint)
                .then()
                .extract()
                .response();

        return response;
    }

    /**
     * Atualiza o modelo de teste com o ID retornado na resposta da criação.
     */
    public void updateModelWithResponseId(Response apiResponse) {
        Long id = apiResponse.jsonPath().getLong("idPonto");
        pontoColetaModel.setIdPonto(id);
    }

    public PontoColeta getPontoColetaModel() {
        return pontoColetaModel;
    }
}
