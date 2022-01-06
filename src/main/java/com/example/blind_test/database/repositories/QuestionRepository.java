package com.example.blind_test.database.repositories;

import com.example.blind_test.database.SQLStatements;

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

    public List<String> getChoicesForQuestion(int questionId){
        try (PreparedStatement stmt = connectionDB.prepareStatement(SQLStatements.GET_CHOICES_FOR_QUESTION)) {
            stmt.setInt(1, questionId);
            ResultSet resultSet = stmt.executeQuery();
            return mapper.resultSetToQuestionChoicesList(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
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
