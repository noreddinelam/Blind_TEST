package com.example.blind_test.tests.repositories_tests;

import static org.assertj.core.api.Assertions.*;

import com.example.blind_test.database.repositories.GameRepository;
import com.example.blind_test.exception.GetGameDBException;
import org.junit.Test;

class GameRepositoryTest {

    private GameRepository repository = GameRepository.getRepository();

    @Test
    void listOfNotStartedGameDb() {
    }

    @Test
    void changeGameState() {
    }

    @Test
    void changeCurrentQuestionId() {
    }

    @Test
    void getGame(){
        int gameId = 1;
        try {
            assertThat(repository.getGame(gameId).getPlayers()).isEqualTo(3);
        } catch (GetGameDBException e) {
            e.printStackTrace();
        }
    }
}
