package com.agiletour.cucumber;

import com.github.leeonky.cucumber.restful.RestfulStep;
import io.cucumber.java.Before;
import io.cucumber.java.zh_cn.假如;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Cucumber steps for API authentication support.
 * Allows setting Authorization header for API test scenarios.
 */
public class ApiAuthSteps {

    @Autowired
    private RestfulStep restfulStep;

    @Before
    public void resetAuthHeader() {
        // Remove the Authorization header by setting it to an empty string
        restfulStep.header("Authorization", "");
    }

    @假如("Authorization头是{string}")
    public void authorization头是(String value) {
        restfulStep.header("Authorization", value);
    }
}
