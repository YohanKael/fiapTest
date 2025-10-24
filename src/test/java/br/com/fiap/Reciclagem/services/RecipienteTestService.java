package br.com.fiap.Reciclagem.services;

import br.com.fiap.Reciclagem.model.Material;
import br.com.fiap.Reciclagem.model.PontoColeta;
import br.com.fiap.Reciclagem.model.Recipiente;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static io.restassured.RestAssured.given;

@Service
public class RecipienteTestService {

    private Recipiente recipienteModel;
    public Response response;
    public final Gson gson;
    public int port;

    private final String BASE_URI = "http://localhost";

    public RecipienteTestService() {
        this.recipienteModel = new Recipiente();

        // Configuração da Gson para tratar LocalDate (CORRIGE ERRO DE JAVA 21)
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDate.class, (JsonSerializer<LocalDate>) (date, typeOfSrc, context) ->
                context.serialize(date.format(DateTimeFormatter.ISO_LOCAL_DATE)));
        gsonBuilder.registerTypeAdapter(LocalDate.class, (JsonDeserializer<LocalDate>) (json, typeOfT, context) ->
                LocalDate.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE));

        this.gson = gsonBuilder.create();
    }

    public void resetModel() {
        this.recipienteModel = new Recipiente();
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    // Método que o professor chamou de 'setFieldsDelivery'
    public void setRecipienteField(String field, String value) {
        switch (field.toLowerCase()) {
            case "capacidademax":
                recipienteModel.setCapacidadeMax(Double.parseDouble(value));
                break;
            case "volumeatual":
                recipienteModel.setVolumeAtual(Double.parseDouble(value));
                break;
            // CORREÇÃO: Recebe ID diretamente e seta no campo Long
            case "idpontocoleta":
                recipienteModel.setIdPontoColeta(Long.parseLong(value));
                break;
            case "idmaterial":
                recipienteModel.setIdMaterial(Long.parseLong(value));
                break;
            case "ultimaatualizacao":
                recipienteModel.setUltimaAtualizacao(LocalDate.parse(value));
                break;
            default:
                throw new IllegalArgumentException("Campo não mapeado no RecipienteTestService: " + field);
        }
    }

    public Response createRecipiente(String endPoint) {
        recipienteModel.setIdRecipiente(null);
        if (recipienteModel.getUltimaAtualizacao() == null) {
            recipienteModel.setUltimaAtualizacao(LocalDate.now());
        }

        String bodyToSend = gson.toJson(recipienteModel);

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

    public void updateModelWithResponseId(Response apiResponse) {
        Long id = apiResponse.jsonPath().getLong("idRecipiente");
        recipienteModel.setIdRecipiente(id);
    }

    public Recipiente getRecipienteModel() {
        return recipienteModel;
    }
}