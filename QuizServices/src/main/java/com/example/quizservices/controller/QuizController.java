package com.example.quizservices.controller;

import com.example.quizservices.client.QuestionServiceClient;
import com.example.quizservices.model.ErrorResponse;
import com.example.quizservices.model.Questions;
import com.example.quizservices.model.Quiz;
import com.example.quizservices.service.QuizService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/quiz")
@Slf4j
public class QuizController {

    @Autowired
    private QuizService quizService;

    @Autowired
    private QuestionServiceClient serviceClient;

    @PostMapping
    public ResponseEntity<Object> saveQuiz(@RequestBody Quiz quiz) {
        log.info("Saving quiz record...");
        Quiz saveQuizRecord = quizService.saveQuiz(quiz);
        saveQuizRecord.getQuestionsList().get(0).setQuizId(saveQuizRecord.getQuizId());
        Questions questions = serviceClient.saveQuestion(saveQuizRecord.getQuestionsList().get(0));
        saveQuizRecord.setQuestionsList(List.of(questions));
        return ResponseEntity.ok(saveQuizRecord);
    }

    @PostMapping("/save")
    public ResponseEntity<Object> saveQuestionAgainstExistingQuiz(@RequestBody Quiz quiz) {
        log.info("Saving question against quiz record...");
        Questions questions = serviceClient.saveQuestion(quiz.getQuestionsList().get(0));
        quiz.setQuestionsList(List.of(questions));
        return ResponseEntity.ok(quiz);
    }

    static int count = 0;
    @GetMapping
    @Retry(name = "getAllQuizRetry", fallbackMethod = "getAllQuizRetryFallback")
    public ResponseEntity<Object> getAllQuizzes() {
        log.info("Getting all quiz records Retry Attempts is {}",count++);
        log.info("Getting all quiz records...");
        List<Quiz> quizList = quizService.getAllQuizzes();
        if (!quizList.isEmpty()) {
            List<Quiz> updateQuizList = quizList.stream().map(q -> {
                q.setQuestionsList(serviceClient.getQuestionByQuizId(q.getQuizId()));
                return q;
            }).collect(Collectors.toList());
            return ResponseEntity.ok(updateQuizList);
        } else {
            return ResponseEntity.ok("No quiz records were found");
        }
    }
    public ResponseEntity<Object> getAllQuizRetryFallback(Exception e){

        return ResponseEntity.ok("Retry Attempt Failed.. Not getting all quiz records");
    }

    @GetMapping("/{quizId}")
    @CircuitBreaker(name = "QuizCircuitBreaker", fallbackMethod = "getQuizByIdFallbackMethod")
    public ResponseEntity<Object> getQuizByQuizId(@PathVariable Integer quizId) {
        log.info("Get quiz by id: " + quizId);
        Optional<Quiz> quizOptional = quizService.getByQuizId(quizId);
        if (quizOptional.isPresent()) {
            List<Questions> questionList = serviceClient.getQuestionByQuizId(quizId);
            Quiz quiz = quizOptional.get();
            quiz.setQuestionsList(questionList);
            return ResponseEntity.ok(quiz);
        } else {
            return ResponseEntity.ok("Quiz not found for id: " + quizId);
        }

    }

    public ResponseEntity<Object> getQuizByIdFallbackMethod(Integer quizId, Exception ex){
        ErrorResponse errorResponse = ErrorResponse.builder()
                .error(ex.getMessage())
                .message("Server is down, please try again after sometime..")
                .time(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @GetMapping("/rate/{quizId}")
    @RateLimiter(name="TestRateLimiter", fallbackMethod = "rateLimterFallbackMethod")
    public ResponseEntity<Object> getQuizByQuizIdRateLimiter(@PathVariable Integer quizId) {
        log.info("getQuizByQuizIdRateLimiter quiz by id: " + quizId);
        Optional<Quiz> quizOptional = quizService.getByQuizId(quizId);
        if (quizOptional.isPresent()) {
            List<Questions> questionList = serviceClient.getQuestionByQuizId(quizId);
            Quiz quiz = quizOptional.get();
            quiz.setQuestionsList(questionList);
            return ResponseEntity.ok(quiz);
        } else {
            return ResponseEntity.ok("Quiz not found for id: " + quizId);
        }

    }

    public ResponseEntity<Object> rateLimterFallbackMethod(Integer quizId, Exception e){
        log.info("Fallback method of rate limiter");
        return ResponseEntity.status(HttpStatus.BANDWIDTH_LIMIT_EXCEEDED)
                .body("Server is down, Please try again...");
    }
}
