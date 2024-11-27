package com.example.apigateway.filter;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

@Component
public class EndPointValidator {

    //list of end points which does not require authentication
    public static final List<String> allowedEndPoints = List.of(
            "/auth/register",
            "/auth/token");


    public Predicate<ServerHttpRequest> isSecure =
            request -> allowedEndPoints.stream()
                    .noneMatch(url -> request.getURI().getPath().contains(url));
}
