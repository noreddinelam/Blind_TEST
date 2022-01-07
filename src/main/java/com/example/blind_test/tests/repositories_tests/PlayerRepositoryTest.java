package com.example.blind_test.tests.repositories_tests;

import com.example.blind_test.database.repositories.PlayerRepository;
import com.example.blind_test.exception.*;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PlayerRepositoryTest {
    private PlayerRepository repository = PlayerRepository.getRepository();

    @Test
    public void verifyPlayerExistenceDB() {

        assertThat(repository.verifyPlayerExistenceDB("Batman")).isEqualTo(true);

    }



}
