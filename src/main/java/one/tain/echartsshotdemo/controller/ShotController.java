package one.tain.echartsshotdemo.controller;

import lombok.AllArgsConstructor;
import one.tain.echartsshotdemo.service.ShotService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestControllerAdvice
@RequestMapping("shot")
@AllArgsConstructor
public class ShotController {
    private ShotService shotService;

    @PostMapping(value = "base64", consumes = "text/plain", produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> base64(@RequestParam(value = "path", required = false) String url, @RequestBody String params) throws IOException {
        if (url == null) {
            url = "/";
        }
        params = params.replace("\n", "");
        return ResponseEntity.ok(shotService.base64(url, params));
    }
}
