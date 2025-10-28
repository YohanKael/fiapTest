package br.com.fiap.Reciclagem.services;

import br.com.fiap.Reciclagem.model.Material;
import com.google.gson.Gson;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.springframework.stereotype.Service;

import static io.restassured.RestAssured.given;

@Service
public class MaterialTestService {

    private Material materialModel;
    public Response response;
    public final Gson gson;
    public int port;

    private final String BASE_URI = "http://localhost";

    public MaterialTestService() {
        this.materialModel = new Material();
        this.gson = new Gson();
    }

    /**
     * Reseta o DTO do Material para um novo cenário de teste.
     */
    public void resetModel() {
        this.materialModel = new Material();
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
    public void setMaterialField(String field, String value) {
        if (value == null || value.isEmpty()) return;

        switch (field.toLowerCase()) {
            case "nomematerial":
                materialModel.setNomeMaterial(value);
                break;
            case "idmaterial":
                materialModel.setIdMaterial(Long.parseLong(value));
                break;
            default:
                throw new IllegalArgumentException("Campo não mapeado no MaterialTestService: " + field);
        }
    }

    /**
     * Executa a requisição POST para criação de um novo Material.
     */
    public Response createMaterial(String endPoint) {
        materialModel.setIdMaterial(null); // Garante que o ID é nulo na criação

        String bodyToSend = gson.toJson(materialModel);

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
        // O campo ID retornado deve ser idMaterial
        Long id = apiResponse.jsonPath().getLong("idMaterial");
        materialModel.setIdMaterial(id);
    }

    public Material getMaterialModel() {
        return materialModel;
    }
}
