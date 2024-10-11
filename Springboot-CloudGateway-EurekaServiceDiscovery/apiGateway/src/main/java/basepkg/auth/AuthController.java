package basepkg.auth;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
public class AuthController {

    @Autowired
    public KCAuthService kcAuthService;

//    @PostMapping("/login")
//    public Mono<Void> login(@RequestBody AuthenticationRequest authenticationRequest, ServerWebExchange exchange){
//        return kcAuthService.authenticateUser(authenticationRequest,exchange);
//    }

    @PostMapping("/login")
    public Mono<Void> login(@RequestBody AuthenticationRequest authenticationRequest, ServerWebExchange exchange){
        kcAuthService.authenticateUser(authenticationRequest,exchange);
        return null;
    }

    @PostMapping("/register")
    public void register(@RequestBody SignUpRequest signUpRequest, ServerHttpResponse response){
        kcAuthService.signUpUser(signUpRequest,response);
    }

    @PostMapping("/access-token-testing-with-redis-keycloak")
    public void access_token_testing_redis_keycloak(@RequestBody SignUpRequest signUpRequest, ServerHttpResponse response){
        kcAuthService.signUpUser(signUpRequest,response);
    }

}
