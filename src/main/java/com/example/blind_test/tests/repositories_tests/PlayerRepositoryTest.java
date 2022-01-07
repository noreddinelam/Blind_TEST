package com.example.blind_test.tests.repositories_tests;

import com.example.blind_test.database.repositories.PlayerRepository;
import com.example.blind_test.exception.*;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PlayerRepositoryTest {
    private PlayerRepository repository = PlayerRepository.getRepository();

    @Test
    public void verifyPlayerExistenceDB(){
        int gameId = 3;
        String username = "Batman";
        try {
            assertThat(repository.verifyPlayerExistenceDB(username, gameId)).isEqualTo(false);
        } catch (PlayerAlreadyExists e) {
            e.printStackTrace();
        }

    }

    @Test
    public void addNewPlayerDB(){
        int gameId = 3;
        String username = "Batman";

        try {
            try {
                assertThat(repository.addNewPlayerDB(username, gameId).getScore()).isEqualTo(0);
            } catch (AddNewPlayerDBException e) {
                e.printStackTrace();
            }
        } catch (PlayerAlreadyExists e) {
            e.printStackTrace();
        }

    }

    @Test
    public void deleteAllPlayerDB(){
        int gameId = 3;
        try {
            assertThat(repository.deleteAllPlayerDB(gameId)).isEqualTo(false);
        } catch (DeleteAllPlayerDBException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void getPlayersOfGame(){
        int gameId = 4;
        String username = "Batmannn";
        try {
            assertThat(repository.getPlayersOfGame(gameId).get(0).getUsername()).isEqualTo(username);
        } catch (GetPlayersOfGameException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void modifyScore(){
        int gameId = 3;
        String username = "Batman";
        try {
            assertThat(repository.verifyPlayerExistenceDB(username, gameId)).isEqualTo(false);
        } catch (PlayerAlreadyExists e) {
            e.printStackTrace();
        }

    }



}
