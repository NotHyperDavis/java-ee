import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class LogicCount {
    public static void main(String[] args) throws IOException, InterruptedException{
        HttpClient client = HttpClient.newHttpClient();

    //1.  Váriaveis se estão vivos ou mortos
            int vivos = 0;
            int mortos = 0;
        
        for (int i= 1; i< 21; i++){
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://rickandmortyapi.com/api/character/" +i))
                .GET()
                .build();
                System.out.println(i);

        // Esta linha faz o pedido HTTP e guarda a respota na variavel response
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString() );


        String json=response.body();{

            if (json.contains("\"status\":\"Alive\"")){
                vivos += 1;
            } else if (json.contains("\"status\":\"Dead\"")) {
                mortos += 1;
            }
        }
        
        
        }
        System.out.println("CENSO: Detetados "+ vivos + " personagens Vivos e " + mortos +" personagens MORTOS nos primeiros 20 registos.");
    }
}