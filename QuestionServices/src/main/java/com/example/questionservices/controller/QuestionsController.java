package com.example.questionservices.controller;

import com.example.questionservices.model.Questions;
import com.example.questionservices.service.QuestionsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/question")
@Slf4j
public class QuestionsController {

    @Autowired
    private QuestionsService questionsService;

    @PostMapping
    public ResponseEntity<Object> saveQuestion(@RequestBody Questions questions) {
        log.info("Saving Question to database...");
        return ResponseEntity.ok(questionsService.saveQuestion(questions));
    }

    @GetMapping
    public ResponseEntity<Object> getAllQuestion() {
        log.info("Getting all Question from database...");
        return ResponseEntity.ok(questionsService.getAllQuestion());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getQuestionByQuestionId(@PathVariable Integer id) {
        log.info("Getting Question by question id... {}", id);
        Optional<Questions> questionsOptional = questionsService.getAllQuestionByQuestionId(id);
        if (questionsOptional.isPresent()) {
            return ResponseEntity.ok(questionsOptional.get());
        } else {
            return ResponseEntity.ok("Question not found for id : " + id);
        }
    }

    @GetMapping("/quiz/{quizId}")
    public ResponseEntity<Object> getQuestionByQuizId(@PathVariable Integer quizId) {
        log.info("Getting Question by quiz Id : {}", quizId);
        Optional<List<Questions>> questionsListOpt = questionsService.getQuestionByQuizId(quizId);
        if (questionsListOpt.isPresent()) {
            return ResponseEntity.ok(questionsListOpt.get());
        } else {
            return ResponseEntity.ok("No Question Present for quiz id : " + quizId);
        }

    }


}
