package com.example.quizservices.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

import org.slf4j.Logger;

@Data
@Builder
public class ErrorResponse {

    private String message;
    private String error;
    private LocalDateTime time;
	
}
