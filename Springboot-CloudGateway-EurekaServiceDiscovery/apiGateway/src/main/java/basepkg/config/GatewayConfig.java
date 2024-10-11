package basepkg.config;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class GatewayConfig {
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
            .route("order_create",
                r -> r.path("/order/create")
                .filters(f -> f.rewritePath("/order/create", "/order/create"))
                .uri("lb://orderService")
            )
            .route("order_update",
                r -> r.path("/order/update")
                .uri("lb://orderService")
            )
            .route("order_delete",
                r -> r.path("/order/delete")
                .uri("lb://orderService")
            )
            .route("add_Product",
                r -> r.path("/add/product")
                .filters(f -> f.rewritePath("/add/product", "/addProduct"))
                .uri("lb://Product_Service")
            )
            .build();
    }
}

