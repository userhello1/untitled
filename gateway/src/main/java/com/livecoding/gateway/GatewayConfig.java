package com.livecoding.gateway;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("customers-route", r -> r
                        .path("/customers/**")
                        .uri("lb://customer-service"))
                .route("products-route", r -> r
                        .path("/products/**")
                        .uri("lb://inventory-service"))
                .route("billing-route", r -> r
                        .path("/bills/**")
                        .uri("lb://billing-service"))
                .build();
    }
}
