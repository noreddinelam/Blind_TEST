package com.example.blind_test.tests.repositories_tests;

import com.example.blind_test.database.repositories.GameRepository;
import com.example.blind_test.exception.*;
import org.junit.Test;

import static org.assertj.core.api.Assertions.*;

public class GameRepositoryTest {
    private GameRepository repository = GameRepository.getRepository();

    @Test
    public void createGameDB() {
        try {
            assertThat(repository.createGameDB(true,5, 5, 10, true, "Batman").getScore()).isEqualTo(0);
        } catch (CreateGameDBException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getGame() {
        int gameId = 1;
        try {
            assertThat(repository.getGame(gameId).getTotalPlayers()).isEqualTo(3);
        } catch (GetGameDBException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getNbPlayersInGame() {
        int gameId = 3;
        try {
            assertThat(repository.getNbPlayersInGame(gameId)).isEqualTo(5);
        } catch (GetNbPlayersInGameException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void joinGameDB() {
        int gameId = 2;
        String username = "Batman";
        try {
            assertThat(repository.joinGameDB(gameId,username).getUsername()).isEqualTo("Batman");
        } catch (PlayerAlreadyExists e) {
            e.printStackTrace();
        } catch (GameIsFullException e) {
            e.printStackTrace();
        } catch (JoinGameDBException e) {
            e.printStackTrace();
        } catch (GetGameDBException e) {
            e.printStackTrace();
        } catch (GetNbPlayersInGameException e) {
            e.printStackTrace();
        } catch (AddNewPlayerDBException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void deleteGameDB() {
        int gameId = 2;
        try {
            assertThat(repository.deleteGameDB(gameId)).isEqualTo(2);
        } catch (DeleteGameException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void listOfNotStartedGameDb() {
        try {
            assertThat(repository.listOfNotStartedGameDb().get(0).getId()).isEqualTo(2);
        } catch (ListOfNotStartedGameException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void changeGameState() {
        int gameId = 2;
        try {
            repository.changeGameState(gameId);
        } catch (ChangeGameStateException e) {
            e.printStackTrace();
        }
        try {
            assertThat(repository.listOfNotStartedGameDb().get(0).getId()).isEqualTo(1);
        } catch (ListOfNotStartedGameException e) {
            e.printStackTrace();
        }
    }

}
