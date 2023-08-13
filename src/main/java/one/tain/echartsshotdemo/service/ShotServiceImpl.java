package one.tain.echartsshotdemo.service;

import one.tain.echartsshotdemo.cache.OptionsCache;
import org.apache.tomcat.util.codec.binary.Base64;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
public class ShotServiceImpl implements ShotService {
    private final ServletWebServerApplicationContext servletWebServerApplicationContext;
    private final OptionsCache optionsCache;
    private final String driverPath;

    public ShotServiceImpl(ServletWebServerApplicationContext servletWebServerApplicationContext,
                           OptionsCache optionsCache,
                           @Value("${chrome.driver.path}") String driverPath) {
        this.servletWebServerApplicationContext = servletWebServerApplicationContext;
        this.optionsCache = optionsCache;
        this.driverPath = driverPath;
    }

    @Override
    public String base64(String url, String params) throws IOException {

        String optionsId = UUID.randomUUID().toString();
        optionsCache.put(optionsId, params);

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
        WebDriver driver = new ChromeDriver(service, options);
        driver.get("http://127.0.0.1:" + servletWebServerApplicationContext.getWebServer().getPort() + url + "?optionsId=" + optionsId);

        WebElement mainCharts = driver.findElement(By.id("mainCharts"));
        int x = mainCharts.getLocation().x;
        int y = mainCharts.getLocation().y;
        int height = mainCharts.getSize().height;
        int width = mainCharts.getSize().width;


        byte[] screenshotAs = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
        InputStream inputStream = new ByteArrayInputStream(screenshotAs);
        BufferedImage image = ImageIO.read(inputStream);
        BufferedImage subImage = image.getSubimage(x, y, width, height);


        driver.quit();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(subImage, "png", os);

        return Base64.encodeBase64String(os.toByteArray());
    }
}
