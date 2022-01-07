package com.example.blind_test.tests.repositories_tests;

import com.example.blind_test.database.repositories.QuestionRepository;
import com.example.blind_test.exception.*;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class QuestionRepositoryTest {
    private QuestionRepository repository = QuestionRepository.getRepository();

    @Test
    public void getResponseForQuestion(){
        int questionId = 3;
        assertThat(repository.getResponseForQuestion(questionId)).isEqualTo("");
    }

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

    @Test
    public void getQuestionByOrder(){
        int questionOrder = 3;
        int gameId = 3 ;
        assertThat(repository.getQuestionByOrder(gameId, questionOrder).getResponse()).isEqualTo("");

    }

    @Test
    public void insertQuestionInQuestionGame(){
        int questionId = 3;
        int gameId = 3;
        int order = 7;
        assertThat(repository.insertQuestionInQuestionGame(questionId,gameId,order));


    }

    @Test
    public void changeQuestionState(){
        int questionId = 3;

        try {
            assertThat(repository.changeQuestionState(questionId));
        } catch (ChangeQuestionStateException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void verifyQuestionState(){
        int questionId = 3;
        try {
            assertThat(repository.verifyQuestionState(questionId));
        } catch (VerifyQuestionStateException e) {
            e.printStackTrace();
        }


    }





}
