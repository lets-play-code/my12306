package com.agiletour.cucumber;

import com.agiletour.support.CurrentTimeProvider;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

/**
 * Test configuration that overrides CurrentTimeProvider with a fake implementation.
 */
@TestConfiguration
public class TestTimeConfiguration {

    @Bean
    @Primary
    public CurrentTimeProvider currentTimeProvider(FakeCurrentTimeProvider fakeCurrentTimeProvider) {
        return fakeCurrentTimeProvider;
    }
}
