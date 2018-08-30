package cn.hxh.controller;

import cn.hxh.common.log.MyLog;
import cn.hxh.storage.interfaces.NoteData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Controller
public class NoteService {
    @Autowired
    NoteData noteData;
    @Autowired
    MyLog log;

    @RequestMapping(value = "/note", method = RequestMethod.GET)
    public String dairy() {
        return "note";
    }

    @GetMapping(value = "/note/shane")
    @ResponseBody
    public String get(HttpServletRequest request) {
        log.record(String.format("Query note from %s", request.getRemoteAddr()));
        return noteData.query();
    }

    @PostMapping(value = "/note/shane")
    @ResponseBody
    public int post(HttpServletRequest request,@RequestBody String note) {
        noteData.update(note);
        log.record(String.format("Update note from %s", request.getRemoteAddr()));
        return 0;
    }
}
