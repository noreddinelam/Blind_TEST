package com.example.blind_test.database.repositories;

import com.example.blind_test.database.SQLStatements;
import com.example.blind_test.front.models.Question;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class QuestionRepository extends Repository {

    private static final QuestionRepository repository = new QuestionRepository();

    private QuestionRepository() {
    }

    public static QuestionRepository getRepository() {
        initConnectionToDatabase();
        return repository;
    }

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

    public Question getQuestion(int questionId){
        try (PreparedStatement stmt = connectionDB.prepareStatement(SQLStatements.GET_QUESTION)) {
            stmt.setInt(1, questionId);
            ResultSet resultSet = stmt.executeQuery();
            return mapper.resultSetToQuestion(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Question getQuestionByOrder(int gameId, int questionOrder) {
        try (PreparedStatement stmt = connectionDB.prepareStatement(SQLStatements.GET_QUESTION_BY_ORDER)) {
            stmt.setInt(1, gameId);
            stmt.setInt(2, questionOrder);
            ResultSet resultSet = stmt.executeQuery();
            return mapper.resultSetToQuestion(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Integer insertQuestionInQuestionGame(int questionId,int gameId,int order){
        try (PreparedStatement stmt = connectionDB.prepareStatement(SQLStatements.INSERT_QUESTION_IN_QUESTION_GAME)) {
            stmt.setInt(1, questionId);
            stmt.setInt(2, gameId);
            stmt.setInt(3, order);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

}
