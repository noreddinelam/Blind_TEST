package com.example.blind_test.database.repositories;

import com.example.blind_test.database.SQLStatements;
import com.example.blind_test.database.SQLTablesInformation;
import com.example.blind_test.exception.*;
import com.example.blind_test.front.models.Game;
import com.example.blind_test.front.models.Player;
import com.example.blind_test.shared.communication.PlayerGame;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class GameRepository extends Repository {
    private static final GameRepository repository = new GameRepository();

    private GameRepository() {
    }

    public static GameRepository getRepository() {
        initConnectionToDatabase();
        return repository;
    }

    public Game createGameDB(boolean type, int current_question, int rounds
            , int players, int timeQuestion, boolean state) throws CreateGameDBException {
        try {
            PreparedStatement stmt = connectionDB.prepareStatement(SQLStatements.CREATE_GAME,
                    Statement.RETURN_GENERATED_KEYS);
            stmt.setBoolean(1, type);
            stmt.setInt(2, current_question);
            stmt.setInt(3, rounds);
            stmt.setInt(4, players);
            stmt.setInt(5, timeQuestion);
            stmt.setBoolean(6, state);
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            int gameId = -1;
            while (rs.next()) {
                gameId = rs.getInt(1);
            }
            return new Game.GameBuilder(1).currentQuestion(gameId).type(type).currentQuestion(current_question)
                    .rounds(rounds).players(players).timeQuestion(timeQuestion).state(state).build();
        } catch (Exception e) {
            e.printStackTrace();
            throw new CreateGameDBException();
        }
    }

    public Game getGame(int gameId) throws GetGameDBException {
        try {
            PreparedStatement getGame = connectionDB.prepareStatement(SQLStatements.GET_GAME_FROM_ID);
            getGame.setInt(1, gameId);
            return mapper.resultSetToGame(getGame.executeQuery());
        } catch (SQLException e) {
            e.printStackTrace();
            throw new GetGameDBException();
        }
    }

    public int getNbPlayersInGame(int gameId) throws GetNbPlayersInGameException {
        int players = -1;
        try {
            PreparedStatement getPlayers = connectionDB.prepareStatement(SQLStatements.GET_PLAYERS_FROM_GAME);
            getPlayers.setInt(1, gameId);
            ResultSet rs = getPlayers.executeQuery();
            while (rs.next())
                players = rs.getInt(SQLTablesInformation.GAME_PLAYERS);
            return players;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new GetNbPlayersInGameException();
        }
    }

    public PlayerGame joinGameDB(int gameId, String username) throws PlayerAlreadyExists, GameIsFullException,
            JoinGameDBException, GetGameDBException, GetNbPlayersInGameException {
        Player player = null;
        try {
            Game game = getGame(gameId);
            if (getNbPlayersInGame(gameId) > 0) {
                PreparedStatement decPlayers = connectionDB.prepareStatement(SQLStatements.DEC_PLAYERS_IN_GAME);
                decPlayers.setInt(1, gameId);
                decPlayers.execute();
                player = PlayerRepository.getRepository().addNewPlayerDB(username, gameId);
                return new PlayerGame(game, player);
            }
            throw new GameIsFullException();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new JoinGameDBException();
        }
    }


    public List<Game> listOfNotStartedGameDb() throws SQLException {
        List<Game> games = new ArrayList<>();
        try {
            PreparedStatement stmt = connectionDB.prepareStatement(SQLStatements.LIST_OF_GAME_NOT_STARTED);
            games = mapper.resultSetToGames(stmt.executeQuery());
            return games;
        } catch (SQLException e) {
            e.printStackTrace();
            return games;
        }
    }

    public Integer changeGameState(Integer gameState, Integer gameId) throws ChangeGameStateException {
        try (PreparedStatement stmt = connectionDB.prepareStatement(SQLStatements.CHANGE_GAME_STATE)) {
            stmt.setInt(1, gameState);
            stmt.setInt(2, gameId);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new ChangeGameStateException();
        }
    }

    public Integer changeCurrentQuestionId(Integer currentQuestion, Integer gameId) throws ChangeCurrentQuestionIdException {
        try (PreparedStatement stmt = connectionDB.prepareStatement(SQLStatements.CHANGE_GAME_STATE)) {
            stmt.setInt(1, currentQuestion);
            stmt.setInt(2, gameId);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new ChangeCurrentQuestionIdException();
        }
    }

}
