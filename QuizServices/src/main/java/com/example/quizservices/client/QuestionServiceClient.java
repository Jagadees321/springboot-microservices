package com.example.quizservices.client;

import com.example.quizservices.model.Questions;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

//@FeignClient(url = "localhost:8082/question", value = "questionClient")
@FeignClient(name="QUESTION-SERVICE")
public interface QuestionServiceClient {

//    @GetMapping("/quiz/{quizId}") uncomment when you want to user url and name
    @GetMapping("/question/quiz/{quizId}")
    List<Questions> getQuestionByQuizId(@PathVariable Integer quizId);

//    @PostMapping uncomment when you want to user url and name because in url we have already specified the request mapping
    @PostMapping("/question")
    Questions saveQuestion(@RequestBody Questions questions);
}
