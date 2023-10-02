package com.agiletour.cucumber;

import com.microsoft.playwright.*;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Component
public class Browser {
    private Playwright playwright;
    private com.microsoft.playwright.Browser browser;
    private Page page;
    private BrowserContext browserContext;

    public void closeContext() {
        browserContext.close();
    }

    @PostConstruct
    public void createWebDriver() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions() // or firefox, webkit
                .setHeadless(false)
                .setSlowMo(100));
    }

    @PreDestroy
    public void quit() {
        browser.close();
        playwright.close();
    }

    public void createContextAndPage() {
        browserContext = browser.newContext();
        page = browserContext.newPage();
    }

    public void reset() {
        closeContext();
    }

    public void launchByUrl(String path) {
        String url = "http://localhost:9090" + path;
        page.navigate(url);
    }

    public void clickByText(String text) {
        page.click(String.format("button:has-text('%s')", text));
    }

    public void clickByText(String id, String text) {
        page.click(String.format("button[data-testid='%s']:has-text('%s')", id, text));
    }

    public void input(String name, String value) {
        Locator locator = page.locator(String.format("[data-testid='%s']", name));
        locator.fill(value);
    }

    public void click() {
        Locator locator = page.locator("[data-testid='save-button']");
        locator.click();
    }

    public Page getPage() {
        return this.page;
    }
}
