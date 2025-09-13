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

    @当("买火车票{string}")
    public void buyTicket(String trainNumber) {
        showTrains();
        browser.clickInRow(trainNumber, "购票");
    }

    @那么("页面包含如下内容:")
    public void shouldDisplayContents(DataTable dataTable) {
        dataTable.asList().forEach(text -> browser.shouldHaveText(text));
    }

    @那么("显示错误信息{string}")
    public void shouldDisplayErrorMessage(String message) {
        browser.shouldHaveText(message);
    }
}
