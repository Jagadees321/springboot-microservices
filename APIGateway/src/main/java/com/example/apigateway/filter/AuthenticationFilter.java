package com.example.apigateway.filter;

import com.example.apigateway.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AbstractGatewayFilterFactory.NameConfig> {

    @Autowired
    private EndPointValidator endPointValidator;

    @Autowired
    private JwtUtil jwtUtil;

    public AuthenticationFilter(){
        super(NameConfig.class);
    }


    @Override
    public GatewayFilter apply(NameConfig config) {
        return ((exchange, chain) -> {
            log.info("Authenticating the request received from gateway");
            if (endPointValidator.isSecure.test(exchange.getRequest())) {
                if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                    //write the logic for custom message headers is not valid
                    log.error("Headers are not valid or may be not present");
                    return handleInvalidHeaders(exchange);
                }

                String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
                String token = StringUtils.EMPTY; // ""
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    token = authHeader.substring(7);
                }

                try {
                    jwtUtil.validateToken(token);
                    log.info("Token is valid");
                }catch(Exception e) {
                    log.error("Token provide is not valid, please check your credentials");
                    //return the custom message
                    return handleInvalidToken(exchange);
                }
            }
            return chain.filter(exchange);
        });
    }

    private Mono<Void> handleInvalidToken(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        //create a message
        String errorMessage = "{\"error\":\"oken provide is not valid, please check your credentials\"}";
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(errorMessage.getBytes(StandardCharsets.UTF_8));
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }

    private Mono<Void> handleInvalidHeaders(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        //create a message
        String errorMessage = "{\"error\":\"Headers are not valid or may be not present\"}";
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(errorMessage.getBytes(StandardCharsets.UTF_8));
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }
}
