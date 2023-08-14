package one.tain.echartsshotdemo.controller;

import lombok.AllArgsConstructor;
import one.tain.echartsshotdemo.model.ShotRequest;
import one.tain.echartsshotdemo.service.ShotService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestControllerAdvice
@RequestMapping("shot")
@AllArgsConstructor
public class ShotController {
    private ShotService shotService;

    @PostMapping(value = "base64")
    public ResponseEntity<List<String>> base64(@RequestParam(value = "path", required = false) String url, @RequestBody ShotRequest shotRequest) throws IOException {
        if (url == null) {
            url = "/";
        }
        return ResponseEntity.ok(shotService.base64(url, shotRequest.getOptions()));
    }
}
