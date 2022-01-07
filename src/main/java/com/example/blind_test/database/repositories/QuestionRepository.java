package com.example.blind_test.database.repositories;

import com.example.blind_test.database.SQLStatements;
import com.example.blind_test.exception.ChangeQuestionStateException;
import com.example.blind_test.exception.QuestionNotFoundException;
import com.example.blind_test.exception.VerifyQuestionStateException;
import com.example.blind_test.front.models.Question;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class QuestionRepository extends Repository {

    private static final QuestionRepository repository = new QuestionRepository();

    private QuestionRepository() {
    }

    public static QuestionRepository getRepository() {
        initConnectionToDatabase();
        return repository;
    }

    //TODO : delete this if not used
    public String getResponseForQuestion(int questionId) {
        try (PreparedStatement stmt = connectionDB.prepareStatement(SQLStatements.GET_RESPONSE_FOR_QUESTION)) {
            stmt.setInt(1, questionId);
            ResultSet resultSet = stmt.executeQuery();
            return mapper.resultSetToQuestionResponse(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
            return "";
        }
    }

    public Question getQuestion(int questionId) throws QuestionNotFoundException {
        try {
            PreparedStatement stmt = connectionDB.prepareStatement(SQLStatements.GET_QUESTION);
            stmt.setInt(1, questionId);
            ResultSet resultSet = stmt.executeQuery();
            return mapper.resultSetToQuestion(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new QuestionNotFoundException();
        }
    }


    public Question getQuestionByOrder(int gameId, int questionOrder) {
        try {
            PreparedStatement stmt = connectionDB.prepareStatement(SQLStatements.GET_QUESTION_BY_ORDER);
            stmt.setInt(1, gameId);
            stmt.setInt(2, questionOrder);
            ResultSet resultSet = stmt.executeQuery();
            return mapper.resultSetToQuestion(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    //TODO : use this function to generate questions
    public Integer insertQuestionInQuestionGame(int questionId, int gameId, int orderQuestion) {
        try (PreparedStatement stmt = connectionDB.prepareStatement(SQLStatements.INSERT_QUESTION_IN_QUESTION_GAME)) {
            stmt.setInt(1, questionId);
            stmt.setInt(2, gameId);
            stmt.setInt(3, orderQuestion);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }


    public Integer changeQuestionState(int questionId) throws ChangeQuestionStateException {
        try (PreparedStatement stmt = connectionDB.prepareStatement(SQLStatements.CHANGE_QUESTION_STATE)) {
            stmt.setInt(1, questionId);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new ChangeQuestionStateException();
        }

    }

    public Integer verifyQuestionState(int questionId) throws VerifyQuestionStateException {
        try (PreparedStatement stmt = connectionDB.prepareStatement(SQLStatements.VERIFY_QUESTION_STATE)) {
            stmt.setInt(1, questionId);
            ResultSet resultSet = stmt.executeQuery();
            int qs = -1;
            while (resultSet.next())
                qs = resultSet.getInt(1);
            return qs;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new VerifyQuestionStateException();
        }
    }

}
