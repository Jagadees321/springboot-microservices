package com.example.quizservices.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
public class Questions {

    private Integer id;
    private String question;
    private Integer quizId;
	
}
