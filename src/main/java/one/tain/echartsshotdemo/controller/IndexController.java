package one.tain.echartsshotdemo.controller;

import lombok.AllArgsConstructor;
import one.tain.echartsshotdemo.cache.OptionsCache;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.concurrent.ExecutionException;

@ControllerAdvice
@AllArgsConstructor
@RequestMapping("/")
public class IndexController {
    private final OptionsCache optionsCache;
    @GetMapping("/")
    public String index(@RequestParam("optionsId")String optionsId, Model model) throws ExecutionException {
        model.addAttribute("option",optionsCache.get(optionsId));
        return "index.html";
    }
}
