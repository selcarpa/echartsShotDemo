package one.tain.echartsshotdemo.service;

import java.io.IOException;

public interface ShotService {
    String base64(String url, String params) throws IOException;
}
