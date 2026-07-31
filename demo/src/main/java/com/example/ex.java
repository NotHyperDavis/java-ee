package com.example;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;


public class ex {

    @GET
    public Response doGet(
            @QueryParam("showAlerts") String showAlertsParam,
            @QueryParam("offset") String offsetParam,
            @QueryParam("limit") String limitParam) throws Exception {

        HttpClient client = HttpClient.newHttpClient();
        ObjectMapper mapper = new ObjectMapper();

        if (showAlertsParam != null && !showAlertsParam.equals("true") && !showAlertsParam.equals("false")) {
            return Response.status(400)
                    .entity("{\"status\": 400, \"error\": \"Bad Request\", \"message\": \"O parâmetro 'showAlerts' deve ser 'true' ou 'false'.\"}")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        boolean showAlerts = (showAlertsParam != null) ? Boolean.parseBoolean(showAlertsParam) : true;

        int vivos = 0;
        int mortos = 0;

        int offset;
        int limit;

        try {
            offset = (offsetParam != null) ? Integer.parseInt(offsetParam) : 1;
            limit = (limitParam != null) ? Integer.parseInt(limitParam) : 20;
        } catch (NumberFormatException e) {
            return Response.status(400)
                    .entity("{\"status\": 400, \"error\": \"Bad Request\", \"message\": \"Os parâmetros 'offset' e 'limit' devem ser números inteiros.\"}")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        if (limit < 0 || limit > 50) {
            return Response.status(400)
                    .entity("{\"status\": 400, \"error\": \"Bad Request\", \"message\": \"O parâmetro 'limit' deve ser um número inteiro entre 0 e 50.\"}")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        StringBuilder html = new StringBuilder();

        try {
            for (int i = offset; i < offset + limit; i++) {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("https://rickandmortyapi.com/api/character/" + i))
                        .GET()
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() != 200) {
                    continue;
                }

                JsonNode jsonNode = mapper.readTree(response.body());
                String status = jsonNode.get("status").asText();
                String url = jsonNode.get("episode").get(0).asText();
                String species = jsonNode.get("species").asText();

                if (status.equals("Dead")) {
                    mortos++;

                    if (species.equals("Alien")) {
                        HttpRequest requestName = HttpRequest.newBuilder()
                                .uri(URI.create(url))
                                .GET()
                                .build();

                        HttpResponse<String> responseName = client.send(requestName, HttpResponse.BodyHandlers.ofString());

                        if (responseName.statusCode() != 200) {
                            continue;
                        }

                        JsonNode jsonNode2 = mapper.readTree(responseName.body());
                        String name = jsonNode2.get("name").asText();
                        if (showAlerts) {
                            html.append("[ALERTA FORENSE] O ultimo registo do alien morto foi no episodio: ")
                                .append(name).append(".<br>");
                        }
                    }

                } else if (status.equals("Alive")) {
                    vivos++;
                }
            }

            html.append("Vivos: ").append(vivos).append("<br>");
            html.append("Mortos: ").append(mortos).append("<br>");

            String timestamp = LocalDateTime.now().toString();
            String logLine = "[" + timestamp + "] Path /test executado com sucesso." + System.lineSeparator();
            Files.writeString(Path.of("citadela_audit.log"), logLine,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new WebApplicationException("Pedido interrompido", e);
        }

        return Response.ok(html.toString()).type(MediaType.TEXT_HTML).build();
    }
}