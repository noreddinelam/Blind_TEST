package com.example.blind_test.tests.repositories_tests;

import com.example.blind_test.database.repositories.GameRepository;
import com.example.blind_test.exception.GetGameDBException;
import org.junit.Test;

import static org.assertj.core.api.Assertions.*;

public class GameRepositoryTest {
    private GameRepository repository = GameRepository.getRepository();

    @Test
    public void createGameDB() {
    }

    @Test
    public void getGame() {
        int gameId = 1;
        try {
            assertThat(repository.getGame(gameId).getPlayers()).isEqualTo(3);
        } catch (GetGameDBException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getNbPlayersInGame() {
    }

    @Test
    public void joinGameDB() {
    }

    @Test
    public void deleteGameDB() {
    }

    @Test
    public void listOfNotStartedGameDb() {
    }

    @Test
    public void changeGameState() {
    }

    @Test
    public void changeCurrentQuestionId() {
    }
}
