package com.example.springstarbucksapi;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
class SpringStarbucksApiApplicationTests {

    @Test
    void contextLoads() {
        // get environment variable kong_api_url
        String kong_api_url = System.getenv("KONG_API_URL");
    }

}
