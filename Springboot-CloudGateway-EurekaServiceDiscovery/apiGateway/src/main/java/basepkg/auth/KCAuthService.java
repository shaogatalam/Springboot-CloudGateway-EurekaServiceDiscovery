package basepkg.auth;

import ch.qos.logback.classic.Logger;
import com.nimbusds.jose.shaded.gson.JsonParser;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.MediaType;

import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import static java.util.Collections.singletonList;
//import static org.yaml.snakeyaml.TypeDescription.log;

import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Service
public class KCAuthService {


    //private RedisTemplate<String,Object> redisTemplate;
//    @Autowired
//    public KCAuthService(RedisTemplate redisTemplate) {
//        this.redisTemplate = redisTemplate;
//    }
    //public KCAuthService() {}
    //private WebClient webClient;

//    @Autowired
//    public KCAuthService(WebClient.Builder webClientBuilder) {
//        this.webClient = webClientBuilder.baseUrl("http://localhost:8282/realms/springbootmicroservice").build();
//    }

//    public String authenticateUser(AuthenticationRequest authenticationRequest,ServerHttpResponse response) {
//
//        String usernameR = authenticationRequest.getUsername();
//        String passwordR = authenticationRequest.getPassword();
//
//        // Default value
//        String username =  "user43@example.com";
//        String password =  "qwerty";
//
//        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
//        formData.add("grant_type", "password");
//        formData.add("client_id", "newClientMS");
//        formData.add("client_secret", "2kiXtBFGUj9gyA2HDSPee5yVWWIxLeqS");
//        formData.add("username", username);
//        formData.add("password", password);
//        formData.add("scope", "roles");
//
//        String tokenResponse = webClient.post()
//                .uri("/protocol/openid-connect/token")
//                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
//                .body(BodyInserters.fromFormData(formData))
//                .retrieve()
//                .bodyToMono(String.class)
//                .block();
//
//        if (tokenResponse != null) {
//            String accessToken   = JsonParser.parseString(tokenResponse).getAsJsonObject().get("access_token").getAsString();
//            String refresh_token = JsonParser.parseString(tokenResponse).getAsJsonObject().get("refresh_token").getAsString();
//            System.out.println("access token after login -public String authenticateUser- : " + accessToken);
//
//            // Create the KeycloakUserDetails with the user's authorities.
//            KeycloakUserDetails userDetails = keycloakService.getKeycloakUserDetails(accessToken);
//            // Create the authentication token using KeycloakAuthenticationToken.
//            KeycloakAuthenticationToken authenticationToken = new KeycloakAuthenticationToken(userDetails, userDetails.getAuthorities().isEmpty());
//            // Set the authentication object in the SecurityContextHolder.
//            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
//            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//            if (authentication != null && authentication.isAuthenticated()) {
//                String username11  = authentication.getName();
//                String authorities = authentication.getAuthorities().toString();
//                // You can also access other authentication details as needed, such as the user's credentials, principal, etc.
//                System.out.println("User : " + username11);
//                System.out.println("authorities : " + authorities);
//                //return "Authenticated User: " + username11 + ", Authorities: " + authorities;
//            } else {
//                System.out.println("User not authenticated : ");
//            }
//
//
////            Cookie jwtCookie = new Cookie("JwtToken", accessToken);
////            jwtCookie.setHttpOnly(true);
////            jwtCookie.setSecure(true);          // Set 'Secure' flag for HTTPS-only transmission
////            jwtCookie.setPath("/");             // Set the cookie path as per your requirements
////            response.addCookie(jwtCookie);
////
////            Cookie RjwtCookie = new Cookie("RefreshJwtToken", refresh_token);
////            RjwtCookie.setHttpOnly(true);
////            RjwtCookie.setSecure(true);
////            RjwtCookie.setPath("/");
////            response.addCookie(RjwtCookie);
//
//            // Set the JWT and refresh token as HTTP-only cookies in the response
//            ResponseCookie jwtCookie = ResponseCookie.from("JwtToken", accessToken)
//                    .httpOnly(true)
//                    .secure(true) // Set 'secure' flag for HTTPS-only transmission
//                    .path("/") // Set the cookie path as per your requirements
//                    .build();
//
//            response.addCookie(jwtCookie);
//
//            ResponseCookie refreshJwtCookie = ResponseCookie.from("RefreshJwtToken", refresh_token)
//                    .httpOnly(true)
//                    .secure(true)
//                    .path("/")
//                    .build();
//
//            response.addCookie(refreshJwtCookie);
//
//
//            return null;
//
//        } else {
//            return null;
//        }
//    }

    private RedisTemplate<String, Object> redisTemplate;
    private WebClient webClient;

    @Autowired
    public KCAuthService(RedisTemplate<String, Object> redisTemplate, WebClient.Builder webClientBuilder) {
        this.redisTemplate = redisTemplate;
        this.webClient = webClientBuilder.baseUrl("http://localhost:8282/realms/springbootmicroservice").build();
    }


    public RedisTemplate<String, Object> getRedisTemplate() {
        return redisTemplate;
    }
    public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public Mono<Void> authenticateUser(AuthenticationRequest authenticationRequest, ServerWebExchange exchange) {

        String username = authenticationRequest.getUsername();
        String password = authenticationRequest.getPassword();

        //        WebClient webClient1 = WebClient.builder().build();
        //        webClient1.get()
        //                  .uri("https://jsonplaceholder.typicode.com/todos/1")
        //                  .retrieve()
        //                  .bodyToMono(String.class)
        //                  .onErrorReturn("Error occurred")
        //                  .block();
        //

        return webClient.post()
            .uri("/protocol/openid-connect/token")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(
                BodyInserters.fromFormData(
                    "grant_type", "password")
                    .with("client_id", "newClientMS")
                    .with("client_secret", "2kiXtBFGUj9gyA2HDSPee5yVWWIxLeqS")
                    .with("username", username)
                    .with("password", password)
                    .with("scope", "roles")
            )
            .retrieve()
            .bodyToMono(String.class)
            .flatMap(tokenResponse -> handleTokenResponse(tokenResponse, exchange));
    }

    private Mono<Void> handleTokenResponse(String tokenResponse, ServerWebExchange exchange) {

        String accessToken = JsonParser.parseString(tokenResponse)
            .getAsJsonObject()
            .get("access_token")
            .getAsString();
        String refreshToken = JsonParser.parseString(tokenResponse)
            .getAsJsonObject()
            .get("refresh_token")
            .getAsString();

        JwksClient jwksClient = new JwksClient();
        String publicKey = jwksClient.getRsaSigningPublicKey();
        System.out.println("API GATEWAY SERVICE :: access token decoder Public key from KEYCLOAK :: " + publicKey);

        String accessTokenRedisKey = "access_token:" + accessToken;
        String accessTokenDecoderRedisKey = "access_token_decoder:" + accessToken;

        redisTemplate.opsForValue().set(accessTokenRedisKey, accessToken);
        redisTemplate.opsForValue().set(accessTokenDecoderRedisKey, publicKey);

        exchange.getResponse().addCookie(ResponseCookie.from("AccessToken", accessToken)
            .httpOnly(true)
            .secure(true)
            .path("/")
            .build());

        exchange.getResponse().addCookie(ResponseCookie.from("RefreshToken", refreshToken)
            .httpOnly(true)
            .secure(true)
            .path("/")
            .build());

        exchange.getRequest().mutate()
            .header(HttpHeaders.COOKIE, "AccessToken=" + accessToken)
            .build();

        // Return an empty Mono as the response is already handled.
        return Mono.empty();
    }

    public Mono<Void> signUpUser(SignUpRequest signUpRequest, ServerHttpResponse response) {

        String username = signUpRequest.getEmail();
        String email    = signUpRequest.getEmail();
        String password = signUpRequest.getPassword();

        WebClient webClient = WebClient.builder()
            .baseUrl("http://localhost:8282")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
            .build();

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("grant_type", "client_credentials");
        requestBody.add("client_id", "newClientMS");
        requestBody.add("client_secret", "2kiXtBFGUj9gyA2HDSPee5yVWWIxLeqS");

        return webClient.post()
            .uri("/realms/springbootmicroservice/protocol/openid-connect/token")
            .body(BodyInserters.fromFormData(requestBody))
            .retrieve()
            .bodyToMono(TokenResponse.class)
            .flatMap(tokenResponse -> {
                if (tokenResponse != null) {
                    String accessToken = tokenResponse.getAccess_token();

                    WebClient userClient = WebClient.builder()
                            .baseUrl("http://localhost:8282")
                            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                            .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                            .build();

                    UserRepresentation userRepresentation = new UserRepresentation();
                    userRepresentation.setUsername(username);
                    userRepresentation.setEmail(email);
                    userRepresentation.setEnabled(true);
                    CredentialRepresentation passwordCredential = new CredentialRepresentation();
                    passwordCredential.setType(CredentialRepresentation.PASSWORD);
                    passwordCredential.setValue(password);
                    passwordCredential.setTemporary(false);
                    userRepresentation.setCredentials(List.of(passwordCredential));
                    userRepresentation.setRealmRoles(Collections.singletonList("USER")); // Replace "USER" with the desired role name

                    return userClient.post()
                        .uri("/admin/realms/springbootmicroservice/users")
                        .bodyValue(userRepresentation)
                        .retrieve()
                        .toBodilessEntity()
                        .doOnSuccess(voidResponseEntity -> {
                            if (voidResponseEntity.getStatusCode() == HttpStatus.CREATED) {
                                System.out.println("User registration successful.");
                            } else {
                                System.out.println("User registration failed.");
                            }
                        })
                        .then(); // Return a Mono<Void> indicating the async process is complete
                } else {
                    return Mono.empty();
                }
            });
    }


    //    public Mono<Void> authenticateUser(AuthenticationRequest authenticationRequest, ServerWebExchange exchange) {
    //
    //        String username = authenticationRequest.getUsername();
    //        String password = authenticationRequest.getPassword();
    //
    //        Mono<Void> res = webClient.post()
    //            .uri("/protocol/openid-connect/token")
    //            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
    //            .body(BodyInserters
    //                .fromFormData("grant_type", "password")
    //                .with("client_id", "newClientMS")
    //                .with("client_secret", "2kiXtBFGUj9gyA2HDSPee5yVWWIxLeqS")
    //                .with("username", username)
    //                .with("password", password)
    //                .with("scope", "roles")
    //            )
    //            .retrieve()
    //            .bodyToMono(String.class)
    //            .flatMap
    //            (
    //                tokenResponse -> {
    //
    //                    String accessToken   = JsonParser.parseString(tokenResponse).getAsJsonObject().get("access_token").getAsString();
    //                    String refresh_token = JsonParser.parseString(tokenResponse).getAsJsonObject().get("refresh_token").getAsString();
    //
    //                    JwksClient jwksClient       = new JwksClient();
    //                    String publicKey            = jwksClient.getRsaSigningPublicKey();
    //                    System.out.println("API GATEWAY SERVICE :: access token decoder Public key from KEYCLOAK :: "+ publicKey);
    //
    //                    String accessTokenn_rediskey         = "access_token:" + accessToken;
    //                    String accessToken_decoder_rediskey  = "access_token_decoder:" + accessToken;
    //
    //                    redisTemplate.opsForValue().set(accessTokenn_rediskey, accessToken);
    //                    redisTemplate.opsForValue().set(accessToken_decoder_rediskey, publicKey);
    //
    //
    //                    exchange.getResponse().addCookie(ResponseCookie.from("AccessToken", accessToken)
    //                            .httpOnly(true)
    //                            .secure(true)
    //                            .path("/")
    //                            .build());
    //
    //                    exchange.getResponse().addCookie(ResponseCookie.from("RefreshToken", refresh_token)
    //                            .httpOnly(true)
    //                            .secure(true)
    //                            .path("/")
    //                            .build());
    //
    //                    exchange.getRequest().mutate()
    //                            .header(HttpHeaders.COOKIE, "AccessToken=" + accessToken)
    //                            .build();
    //
    //                    // Return an empty Mono as the response is already handled.
    //                    return Mono.empty();
    //                }
    //            );
    //
    //        return null;
    //    }


}

















//WORKING CODE
//package basepkg.service;
//
//import com.nimbusds.jose.shaded.gson.JsonParser;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.MediaType;
//import org.springframework.stereotype.Service;
//import org.springframework.web.reactive.function.client.WebClient;
//import reactor.core.publisher.Mono;
//
//@Service
//public class KeycloakAuthenticationService {
//
//    private WebClient webClient;
//
//    @Autowired
//    public KeycloakAuthenticationService(WebClient.Builder webClientBuilder) {
//        this.webClient = webClientBuilder.baseUrl("http://localhost:8282/realms/springbootmicroservice").build();
//    }
//
//    public String authenticateUser(String username, String password) {
//
//        // Default values are provided directly in the method body
//        username = "user43@example.com";
//        password = "qwerty";
//
//        String tokenResponse = webClient.post()
//                .uri("/protocol/openid-connect/token")
//                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE) // Set the content type header
//                .body(Mono.just(getTokenRequestBody(username, password)), String.class)
//                .retrieve()
//                .bodyToMono(String.class)
//                .block(); // Wait for the response and get the body as a string
//
//        if (tokenResponse != null) {
//            return extractAccessTokenFromResponse(tokenResponse);
//        } else {
//            // Handle the case when tokenResponse is null (e.g., error occurred)
//            return null;
//        }
//    }
//
//    private String getTokenRequestBody(String username, String password) {
//        return "grant_type=password&client_id=newClientMS&client_secret=2kiXtBFGUj9gyA2HDSPee5yVWWIxLeqS&" +
//                "username=" + username + "&password=" + password + "&scope=roles";
//    }
//
//    private String extractAccessTokenFromResponse(String response) {
//        String accessToken = JsonParser.parseString(response).getAsJsonObject().get("access_token").getAsString();
//        System.out.println("Access Token: " + accessToken);
//        return accessToken;
//    }
//}


