package com.example.questionservices.repository;

import com.example.questionservices.model.Questions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionsRepository extends JpaRepository<Questions, Integer> {
    Optional<List<Questions>> findByQuizId(Integer quizId);
}
