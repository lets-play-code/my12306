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

    @当("打开火车票页面")
    public void openTrainTicketPage() {
        browser.launchByUrl("/");
    }

    @当("买火车票{string}")
    public void buyTicket(String trainNumber) {
        browser.clickInRow(trainNumber, "购票");
    }

    @当("查询火车票从{string}到{string}")
    public void queryTrainsByStations(String from, String to) {
        browser.launchByUrl("/");
        browser.input("from-station", from);
        browser.input("to-station", to);
        browser.clickByText("查询");
    }

    @io.cucumber.java.zh_cn.假如("打开登录页面")
    public void openLoginPage() {
        browser.launchByUrl("/login");
    }

    @当("输入用户名{string}和密码{string}")
    public void inputUsernameAndPassword(String username, String password) {
        browser.inputById("email", username);
        browser.inputById("password", password);
    }

    @当("点击{string}按钮")
    public void clickButton(String text) {
        browser.clickByText(text);
    }

    @那么("等待2秒后当前页面URL包含{string}")
    public void waitAndCheckUrl(String path) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            // ignore
        }
        browser.shouldHaveUrlContaining(path);
    }

    @那么("页面包含如下内容:")
    public void shouldDisplayContents(DataTable dataTable) {
        dataTable.asList().forEach(text -> browser.shouldHaveText(text));
    }

    @那么("页面不包含如下内容:")
    public void shouldNotDisplayContents(DataTable dataTable) {
        dataTable.asList().forEach(text -> browser.shouldNotHaveText(text));
    }

    @那么("显示错误信息{string}")
    public void shouldDisplayErrorMessage(String message) {
        browser.shouldHaveText(message);
    }
}
