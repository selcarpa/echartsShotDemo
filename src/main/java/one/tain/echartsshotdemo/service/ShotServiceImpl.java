package one.tain.echartsshotdemo.service;

import lombok.AllArgsConstructor;
import one.tain.echartsshotdemo.cache.OptionsCache;
import one.tain.echartsshotdemo.pool.SeleniumPool;
import org.apache.tomcat.util.codec.binary.Base64;
import org.openqa.selenium.*;
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
@AllArgsConstructor
public class ShotServiceImpl implements ShotService {
    private final ServletWebServerApplicationContext servletWebServerApplicationContext;
    private final OptionsCache optionsCache;
    private final SeleniumPool seleniumPool;

    @Override
    public String base64(String url, String params) throws IOException {

        String optionsId = UUID.randomUUID().toString();
        optionsCache.put(optionsId, params);

        WebDriver driver = seleniumPool.get();
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
