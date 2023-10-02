package com.agiletour.cucumber;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.zh_cn.当;
import io.cucumber.java.zh_cn.那么;
import org.springframework.beans.factory.annotation.Autowired;

public class UiSteps {
    @Autowired
    private Browser browser;

    @Before("@ui")
    public void reset() {
        browser.createContextAndPage();
    }

    @当("查询火车票")
    public void showTrains() {
        browser.launchByUrl("/");
    }

    @那么("应该看到车票信息:")
    public void shouldDisplayTrains(DataTable table) {
    }
}
