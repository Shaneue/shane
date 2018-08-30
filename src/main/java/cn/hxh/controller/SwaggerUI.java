package cn.hxh.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
public class SwaggerUI {
    @GetMapping("/swagger")
    public RedirectView swagger() {
        return new RedirectView("/swagger-ui.html");
    }
}
