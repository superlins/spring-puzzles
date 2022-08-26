package org.example.data.jdbc;

import org.example.data.jdbc.repo.ActorRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author renc
 */
@SpringBootTest
class ActorRepositoryTest {

    @Autowired
    ActorRepository actorRepository;

    @Test
    public void test() {
        actorRepository.findById(2).ifPresent(System.out::println);
    }
}
