package one.tain.echartsshotdemo.service;

import one.tain.echartsshotdemo.cache.OptionsCache;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Service
public class ShotServiceImpl implements ShotService {
    private final ServletWebServerApplicationContext servletWebServerApplicationContext;
    private final OptionsCache optionsCache;
    private final String driverPath;

    public ShotServiceImpl(ServletWebServerApplicationContext servletWebServerApplicationContext, OptionsCache optionsCache, @Value("${chrome.driver.path}") String driverPath) {
        this.servletWebServerApplicationContext = servletWebServerApplicationContext;
        this.optionsCache = optionsCache;
        this.driverPath = driverPath;
    }

    @Override
    public List<String> base64(String url, Collection<String> params) throws IOException {
        final List<String> result = new ArrayList<>();

        final String optionsId = UUID.randomUUID().toString();
        optionsCache.put(optionsId, params);

        try (CloseAbleDriver closeAbleDriver = new CloseAbleDriver(1100, params.size() * 600 + 200)) {
            WebDriver driver = closeAbleDriver.driver;
            driver.get("http://127.0.0.1:" + servletWebServerApplicationContext.getWebServer().getPort() + url + "?optionsId=" + optionsId);

            for (int i = 0; i < params.size(); i++) {
                WebElement webElement = driver.findElement(By.id("charts" + i));
                String screenshotAs1 = webElement.getScreenshotAs(OutputType.BASE64);
                result.add(screenshotAs1);

            }
            return result;

        }
    }

    private class CloseAbleDriver implements AutoCloseable {
        public final WebDriver driver;

        private CloseAbleDriver(int weight, int height) {
            this.driver = initDriver(weight, height);
        }

        @Override
        public void close() {
            driver.quit();

        }

        private WebDriver initDriver(int weight, int height) {
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless");
            options.addArguments("--remote-allow-origins=*");
            options.addArguments("--disable-gpu");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments(String.format("--window-size=%d,%d", weight, height));
            options.addArguments("--hide-scrollbars");
            ChromeDriverService service = new ChromeDriverService.Builder().usingDriverExecutable(new java.io.File(driverPath)).usingAnyFreePort().build();
            return new ChromeDriver(service, options);
        }
    }
}
