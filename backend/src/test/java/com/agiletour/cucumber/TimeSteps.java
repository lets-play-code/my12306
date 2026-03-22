package com.agiletour.cucumber;

import io.cucumber.java.zh_cn.假如;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Cucumber steps for controlling time in tests.
 */
public class TimeSteps {

    @Autowired
    private FakeCurrentTimeProvider fakeCurrentTimeProvider;

    @假如("当前时间是{string}")
    public void 当前时间是(String isoDateTime) {
        fakeCurrentTimeProvider.setTime(isoDateTime);
    }
}
