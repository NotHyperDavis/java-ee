package com.example;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Context;


public class B {

    @GET
    public void doGet(@Context HttpServletRequest req, @Context HttpServletResponse resp) throws Exception {
        resp.setContentType("text/html;charset=UTF-8");
        HttpClient client = HttpClient.newHttpClient();
        ObjectMapper mapper = new ObjectMapper();
        String showAlertsParam = req.getParameter("showAlerts");

        if (showAlertsParam != null && !showAlertsParam.equals("true") && !showAlertsParam.equals("false")) {
            resp.setStatus(400);
            resp.getWriter().write("{\"status\": 400, \"error\": \"Bad Request\", \"message\": \"O parâmetro 'showAlerts' deve ser 'true' ou 'false'.\"}");
            return;
        }

        boolean showAlerts = (showAlertsParam != null) ?
        Boolean.parseBoolean(showAlertsParam) : true;

        int vivos = 0;
        int mortos = 0;

        String offsetParam = req.getParameter("offset");
        String limitParam = req.getParameter("limit");

        int offset;
        int limit;

        try {
            offset = (offsetParam != null) ? Integer.parseInt(offsetParam) : 1;
            limit = (limitParam != null) ? Integer.parseInt(limitParam) : 20;
        } catch (NumberFormatException e) {
            resp.setStatus(400);
            resp.getWriter().write("{\"status\": 400, \"error\": \"Bad Request\", \"message\": \"Os parâmetros 'offset' e 'limit' devem ser números inteiros.\"}");
            return;
        }

        if (limit < 0 || limit > 50) {
            resp.setStatus(400);
            resp.getWriter().write("{\"status\": 400, \"error\": \"Bad Request\", \"message\": \"O parâmetro 'limit' deve ser um número inteiro entre 0 e 50.\"}");
            return;
        }

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
                        HttpRequest request_name = HttpRequest.newBuilder()
                                .uri(URI.create(url))
                                .GET()
                                .build();

                        HttpResponse<String> response_name = client.send(request_name, HttpResponse.BodyHandlers.ofString());
                        if (response_name.statusCode() != 200) {
                        continue;
                        }
                        

                        JsonNode jsonNode2 = mapper.readTree(response_name.body());
                        String name = jsonNode2.get("name").asText();
                        if (showAlerts) {
                            resp.getWriter().write("[ALERTA FORENSE] O último registo do alien morto foi no episódio: " + name + ".<br>");
                        }
                    }

                } else if (status.equals("Alive")) {
                    vivos++;
                }
            }

            resp.getWriter().write("Vivos: " + vivos + "<br>");
            resp.getWriter().write("Mortos: " + mortos + "<br>");

            String timestamp = LocalDateTime.now().toString();
            String logLine = "[" + timestamp + "] Servlet /census executado com sucesso." + System.lineSeparator();
            Files.writeString(java.nio.file.Path.of("citadela_audit.log"), logLine, StandardOpenOption.CREATE, StandardOpenOption.APPEND);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ServletException("Pedido interrompido", e);
        }
    }

}