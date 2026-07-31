package com.example;

import java.util.ArrayList;
import java.util.List;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/personagem")
public class PersonagemResource {

    // Lista em memória para guardar as personagens criadas
    private static List<Personagem> personagens = new ArrayList<>();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response criar(Personagem personagem) {
        personagens.add(personagem);
        return Response.status(201).entity(personagem).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listarTodas() {
        return Response.ok(personagens).build();
    }
}