package com.example.blind_test.tests.repositories_tests;

import com.example.blind_test.database.repositories.QuestionRepository;
import com.example.blind_test.exception.*;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class QuestionRepositoryTest {
    private QuestionRepository repository = QuestionRepository.getRepository();

    @Test
    public void getQuestion(){
        int questionId = 3;
        String username = "Batman";
        try {
            assertThat(repository.getQuestion(questionId).getResponse()).isEqualTo("");
        } catch (QuestionNotFoundException e) {
            e.printStackTrace();
        }

    }





}
