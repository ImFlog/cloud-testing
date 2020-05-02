package com.github.imflog.awscloudtesting

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class ApplicationTests {

    @Test
    fun `context loads`() {
    }

    @Test
    fun `Should consume new captured pokemon correctly`() {
        TODO(
            "We have to start a container with localstack, " +
                    "change the configuration according to that " +
                    "Listen for pokemon to be inserted."
        )
    }
}
