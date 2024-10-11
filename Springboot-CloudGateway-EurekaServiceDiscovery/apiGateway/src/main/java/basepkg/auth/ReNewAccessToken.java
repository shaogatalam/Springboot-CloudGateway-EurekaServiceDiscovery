package basepkg.auth;

import com.nimbusds.jose.shaded.gson.JsonParser;
import org.springframework.data.util.Pair;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import java.net.URI;
import java.util.Map;
import java.util.HashMap;

public class ReNewAccessToken {

    private final WebClient.Builder webClientBuilder;

    // Inject WebClient.Builder using constructor or autowiring

    public ReNewAccessToken(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    public Map<String, String> refreshAccessToken(String refreshToken) {

        URI uri = UriComponentsBuilder
                .fromUriString("http://localhost/auth/realms/springbootmicroservice/protocol/openid-connect/token")
                .build()
                .toUri();

        // Build the request body
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "refresh_token");
        formData.add("client_id", "newClientMS");
        formData.add("client_secret", "2kiXtBFGUj9gyA2HDSPee5yVWWIxLeqS");
        formData.add("refresh_token", refreshToken);

        // Send the request to Keycloak and retrieve the new access token
        String response = webClientBuilder.build()
                .post()
                .uri(uri)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .bodyToMono(String.class)
                .block();

        String newaccessToken="";
        String newrefreshToken="";

        Map<String, String> tokens = new HashMap<>();

        // Extract and return the new access token from the response
        if (response != null) {
            //return JsonParser.parseString(response).getAsJsonObject().get("access_token").getAsString();
            newaccessToken  = JsonParser.parseString(response).getAsJsonObject().get("access_token").getAsString();
            newrefreshToken = JsonParser.parseString(response).getAsJsonObject().get("refresh_token").getAsString();
        }

        tokens.put("accessToken",newaccessToken);
        tokens.put("refreshToken",newrefreshToken);

        return tokens;
    }
}
