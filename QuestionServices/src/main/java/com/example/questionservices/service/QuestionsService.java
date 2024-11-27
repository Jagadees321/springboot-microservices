package com.example.questionservices.service;

import com.example.questionservices.model.Questions;
import com.example.questionservices.repository.QuestionsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class QuestionsService {

    @Autowired
    private QuestionsRepository questionsRepository;

    public Questions saveQuestion(Questions questions) {
        return questionsRepository.save(questions);
    }

    public List<Questions> getAllQuestion() {
        return questionsRepository.findAll();
    }

    public Optional<Questions> getAllQuestionByQuestionId(Integer id) {
        return questionsRepository.findById(id);
    }

    public Optional<List<Questions>> getQuestionByQuizId(Integer quizId) {
        return questionsRepository.findByQuizId(quizId);
    }
}
