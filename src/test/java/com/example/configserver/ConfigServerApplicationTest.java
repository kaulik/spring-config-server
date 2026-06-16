package com.example.configserver;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.cloud.config.server.git.uri=file://${user.home}/config-repo-test",
    "spring.cloud.config.server.git.clone-on-start=false"
})
class ConfigServerApplicationTest {

    @Test
    void contextLoads() {
    }
}
