package cn.hxh.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Home {
    @GetMapping(value = "/")
    public String home() {
        return html();
    }

    private String[] hrefs = new String[]{"diary", "main", "resources", "swagger", "anyone/owner.jpg", "note", "anyone/owner.pdf"};
    private String[] texts = new String[]{"diary page", "request page", "resources", "swagger page", "password services", "note", "owner"};

    private String html() {
        String top = "<!DOCTYPE html><html><head><title>home</title><link rel=\"icon\" type=\"image/x-icon\" href=\"/anyone/favicon.ico\"><meta charset=\"UTF-8\"><meta name=\"content-type\" content=\"text/html\"></head>";
        String h1 = "<h1>Services List</h1><body bgcolor=\"#F0FFF0\"><ul>";
        String end = "</ul></body><html>";
        return top + h1 + body() + end;
    }

    private String body() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < hrefs.length; i++) {
            sb.append(href(hrefs[i], texts[i]));
        }
        return sb.toString();
    }

    private String href(String path, String text) {
        return "<li><a href=\"/" + path + "\">" + text + "</a></li>";
    }
}
