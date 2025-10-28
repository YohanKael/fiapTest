package br.com.fiap.Reciclagem.services;

import br.com.fiap.Reciclagem.model.Alerta;
import br.com.fiap.Reciclagem.model.PontoColeta;
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
public class AlertaTestService {

    private Alerta alertaModel;
    public Response response;
    public final Gson gson;
    public int port;

    private final String BASE_URI = "http://localhost";

    public AlertaTestService() {
        this.alertaModel = new Alerta();

        // Configuração do Gson para serializar e deserializar LocalDate (igual ao Recipiente)
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDate.class, (JsonSerializer<LocalDate>) (date, typeOfSrc, context) ->
                context.serialize(date.format(DateTimeFormatter.ISO_LOCAL_DATE)));
        gsonBuilder.registerTypeAdapter(LocalDate.class, (JsonDeserializer<LocalDate>) (json, typeOfT, context) ->
                LocalDate.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE));

        this.gson = gsonBuilder.create();
    }

    public void resetModel() {
        this.alertaModel = new Alerta();
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    /**
     * Mapeia valores de String (vindos do Gherkin DataTable) para os campos do modelo Alerta.
     */
    public void setAlertaField(String field, String value) {
        if (value == null || value.isEmpty()) return;

        switch (field.toLowerCase()) {
            case "mensagem":
                alertaModel.setMensagem(value);
                break;
            case "idpontocoleta":
                PontoColeta ponto = PontoColeta.builder().idPonto(Long.parseLong(value)).build();
                alertaModel.setIdPonto(ponto);
                break;
            case "ultimaatualizacao":
                alertaModel.setUltimaAtualizacao(LocalDate.parse(value, DateTimeFormatter.ISO_LOCAL_DATE));
                break;
            case "idalerta":
                alertaModel.setIdAlerta(Long.parseLong(value));
                break;
            default:
                throw new IllegalArgumentException("Campo não mapeado no AlertaTestService: " + field);
        }
    }

    /**
     * Executa a requisição POST para criação de um novo Alerta.
     */
    public Response createAlerta(String endPoint) {
        alertaModel.setIdAlerta(null);

        // Se a data não foi setada, usamos a data atual (similar ao Recipiente)
        if (alertaModel.getUltimaAtualizacao() == null) {
            alertaModel.setUltimaAtualizacao(LocalDate.now());
        }

        String bodyToSend = gson.toJson(alertaModel);

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
        Long id = apiResponse.jsonPath().getLong("idAlerta");
        alertaModel.setIdAlerta(id);
    }

    public Alerta getAlertaModel() {
        return alertaModel;
    }
}
