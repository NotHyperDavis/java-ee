import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ApiTest {
    public static void main(String[] args) throws IOException, InterruptedException{
        HttpClient client = HttpClient.newHttpClient();
        for (int i= 1; i< 20; i++) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://rickandmortyapi.com/api/character/" +i))
                .GET()
                .build();

        // Esta linha faz o pedido HTTP e guarda a respota na variavel response
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString() ); 

        System.out.println(response.body());
        System.out.println(i);
        }
    }
}
