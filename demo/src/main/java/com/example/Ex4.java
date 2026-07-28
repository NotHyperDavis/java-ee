package com.example;
 
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
 
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
 
public class Ex4 {
    public static void main(String[] args) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        ObjectMapper mapper = new ObjectMapper();
 
        for (int i = 1; i < 21; i++) {
 
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://rickandmortyapi.com/api/character/" + i))
                    .GET()
                    .build();
 
            // Esta linha faz o pedido HTTP e guarda a respota na variavel response
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JsonNode jsonNode = mapper.readTree(response.body());
            String status = jsonNode.get("status").asText();
            String url = jsonNode.get("episode").get(0).asText();
 
            if (status.equals("Dead")) {
                HttpRequest request_name = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .GET()
                        .build();
            
                HttpResponse<String> response_name = client.send(request_name, HttpResponse.BodyHandlers.ofString());
                JsonNode jsonNode2 = mapper.readTree(response_name.body());
                String name = jsonNode2.get("name").asText();
                System.out.println("[ALERTA FORENSE] O último registo do alien morto foi no episódio: " + name + ".");
            }
 
        }
    }
}
 