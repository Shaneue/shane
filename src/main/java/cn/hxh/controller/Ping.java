package cn.hxh.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
public class Ping {
    @GetMapping(value = "/ping")
    public String ping() {
        return "pong";
    }

    @GetMapping("/api")
    public RedirectView api() {
        return new RedirectView("/doc.html");
    }
}
