import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class AleinAlert {
    public static void main(String[] args) throws IOException, InterruptedException{
        HttpClient client = HttpClient.newHttpClient();

        for (int i= 1; i< 21; i++){
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://rickandmortyapi.com/api/character/" +i))
                .GET()
                .build();
                System.out.println(i);

        // Esta linha faz o pedido HTTP e guarda a respota na variavel response
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString() );
        
        String json=response.body().toLowerCase();

        boolean isMorto = json.contains("\"status\":\"dead\"");
        boolean isAlien = json.contains("\"species\":\"alien\"");

        if (isAlien && isMorto) {
            System.out.println( "[PERIGO] Um Alien foi encontrado morto com o ID " + i + "!");
        }
        
        }
        
        
        }
    }
