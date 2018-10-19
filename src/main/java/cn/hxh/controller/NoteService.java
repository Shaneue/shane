package cn.hxh.controller;

import cn.hxh.common.log.MyLog;
import cn.hxh.storage.interfaces.NoteData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

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

    @GetMapping(value = "/note/{id}")
    @ResponseBody
    public String get(HttpServletRequest request, @PathVariable(value = "id") String fileName) {
        log.record(String.format("Query note from %s", request.getRemoteAddr()));
        return noteData.query(fileName);
    }

    @GetMapping(value = "/note/list")
    @ResponseBody
    public Map<String, String> getNotesList(HttpServletRequest request) {
        log.record(String.format("Query notes list from %s", request.getRemoteAddr()));
        return noteData.queryNotes();
    }

    @PostMapping(value = "/note/{id}/{topic}")
    @ResponseBody
    public int post(HttpServletRequest request, @RequestBody String content,
                    @PathVariable(value = "id") String fileName, @PathVariable(value = "topic") String topic) {
        if (noteData.update(fileName, topic, content)) {
            log.record(String.format("Update note from %s", request.getRemoteAddr()));
        } else {
            log.record("Fail to update note.");
        }
        return 0;
    }

    @DeleteMapping(value = "/note/{topic}")
    @ResponseBody
    public int delete(HttpServletRequest request,
                      @PathVariable(value = "topic") String topic) {
        if (noteData.delete(topic)) {
            log.record(String.format("Delete note successfully. From %s-> %s", request.getRemoteAddr(), topic));
            return 0;
        } else {
            log.record(String.format("Delete note unsuccessfully. From %s-> %s", request.getRemoteAddr(), topic));
            return 1;
        }
    }

    @PostMapping(value = "/note/{topic}")
    @ResponseBody
    public int create(HttpServletRequest request,
                      @PathVariable(value = "topic") String topic) {
        if (noteData.create(topic)) {
            log.record(String.format("Create note successfully. From %s-> %s", request.getRemoteAddr(), topic));
            return 0;
        } else {
            log.record(String.format("Create note unsuccessfully. From %s-> %s", request.getRemoteAddr(), topic));
            return 1;
        }
    }
}
