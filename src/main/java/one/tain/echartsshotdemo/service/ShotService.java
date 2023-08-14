package one.tain.echartsshotdemo.service;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

public interface ShotService {
    List<String> base64(String url, Collection<String> params) throws IOException;
}
