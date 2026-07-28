package com.example;
 
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
 
public class App
{
    public static void main( String[] args )
    {
        String jsonString = "{\"id\":\"1\", \"nome\":\"David\"}";
 
        ObjectMapper mapper = new ObjectMapper();
 
        try {
            JsonNode jsonNode = mapper.readTree(jsonString);
            int id = jsonNode.get("id").asInt();
            String nome = jsonNode.get("nome").asText();
   
            System.out.println("Id: " + id);
            System.out.println("Nome: " + nome);
        }
        catch(Exception e) {
            System.out.println( "Exception generica." );    
        }
 
 
 
        System.out.println( "Hello World!" );
    }
}