package com.example;
 
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
 
public class ex1resolvido {
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
            String url = jsonNode.get("location").get("url").asText();
 
            if (status.equals("Dead")) {
                HttpRequest request_name = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .GET()
                        .build();
 
                HttpResponse<String> response_name = client.send(request_name, HttpResponse.BodyHandlers.ofString());
                JsonNode jsonNode2 = mapper.readTree(response_name.body());
                String name = jsonNode.get("name").asText();
                System.out.println("[ALERTA FORENSE] O último registo do alien morto foi no episódio: " + name+ ".");
 
            }
 
        }
    }
}
