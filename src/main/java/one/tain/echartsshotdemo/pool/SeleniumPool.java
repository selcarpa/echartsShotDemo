package one.tain.echartsshotdemo.pool;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class SeleniumPool {
    private static final List<WebDriver> IDLE_CHROME_DRIVERS = new ArrayList<>();
    private static final List<WebDriver> BUSY_CHROME_DRIVERS = new ArrayList<>();
    private static final int MAX_CHROME_DRIVER = 10;

    static {
        Runtime.getRuntime().addShutdownHook(
                new Thread() {
                    @Override
                    public void run() {
                        BUSY_CHROME_DRIVERS.forEach(
                                webDriver -> {
                                    try {
                                        webDriver.quit();
                                    } catch (Exception e) {
                                        log.error(e.getMessage(), e);
                                    }
                                }
                        );
                        IDLE_CHROME_DRIVERS.forEach(
                                webDriver -> {
                                    try {
                                        webDriver.quit();
                                    } catch (Exception e) {
                                        log.error(e.getMessage(), e);
                                    }
                                }
                        );
                        super.run();
                    }
                }
        );
    }

    private final String driverPath;

    public SeleniumPool(@Value("${chrome.driver.path}") String driverPath) {
        this.driverPath = driverPath;
    }

    public WebDriver get() {
        WebDriver webDriver;
        if (!IDLE_CHROME_DRIVERS.isEmpty()) {
            webDriver = IDLE_CHROME_DRIVERS.remove(0);

        } else {
            log.info("new chrome driver");
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless");
            options.addArguments("--remote-allow-origins=*");
            options.addArguments("--disable-gpu");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--window-size=1200,800");
            options.addArguments("--hide-scrollbars");
            ChromeDriverService service = new ChromeDriverService.Builder()
                    .usingDriverExecutable(new java.io.File(driverPath))
                    .usingAnyFreePort()
                    .build();
            webDriver = new ChromeDriver(service, options);
        }
        BUSY_CHROME_DRIVERS.add(webDriver);
        return (WebDriver) Proxy
                .newProxyInstance(
                        SeleniumPool.class.getClassLoader(),
                        new Class<?>[]{WebDriver.class, TakesScreenshot.class},
                        (proxy, method, args) -> {
                            if (!"quit".equals(method.getName())) {
                                return method.invoke(webDriver, args);
                            } else {
                                quitOrRecycle(webDriver);
                                return null;
                            }
                        });
    }

    private static void quitOrRecycle(WebDriver webDriver) {
        if (IDLE_CHROME_DRIVERS.size() > MAX_CHROME_DRIVER) {
            webDriver.quit();
            BUSY_CHROME_DRIVERS.remove(webDriver);
        } else {
            BUSY_CHROME_DRIVERS.remove(webDriver);
            IDLE_CHROME_DRIVERS.add(webDriver);
//            webDriver.getWindowHandles().forEach(
//                    windowHandle -> {
//                        try {
//                            webDriver.switchTo().window(windowHandle);
//                            webDriver.close();
//                        } catch (Exception e) {
//                            log.error(e.getMessage(), e);
//                        }
//                    }
//            );
        }
    }
}
