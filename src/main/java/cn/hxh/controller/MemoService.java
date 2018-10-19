package cn.hxh.controller;

import cn.hxh.common.log.MyLog;
import cn.hxh.storage.interfaces.MemoData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
public class MemoService {
    @Autowired
    MemoData memoData;
    @Autowired
    MyLog log;

    @RequestMapping(value = "/memo", method = RequestMethod.GET)
    public String dairy() {
        return "memo";
    }

    @GetMapping(value = "/memo/{id}")
    @ResponseBody
    public String get(HttpServletRequest request, @PathVariable(value = "id") String fileName) {
        log.record(String.format("Query memo from %s", request.getRemoteAddr()));
        return memoData.query(fileName);
    }

    @GetMapping(value = "/memo/list")
    @ResponseBody
    public Map<String, String> getMemosList(HttpServletRequest request) {
        log.record(String.format("Query memos list from %s", request.getRemoteAddr()));
        return memoData.queryMemos();
    }

    @PostMapping(value = "/memo/{id}/{topic}")
    @ResponseBody
    public int post(HttpServletRequest request, @RequestBody String content,
                    @PathVariable(value = "id") String fileName, @PathVariable(value = "topic") String topic) {
        if (memoData.update(fileName, topic, content)) {
            log.record(String.format("Update memo from %s", request.getRemoteAddr()));
        } else {
            log.record("Fail to update memo.");
        }
        return 0;
    }

    @DeleteMapping(value = "/memo/{topic}")
    @ResponseBody
    public int delete(HttpServletRequest request,
                      @PathVariable(value = "topic") String topic) {
        if (memoData.delete(topic)) {
            log.record(String.format("Delete memo successfully. From %s-> %s", request.getRemoteAddr(), topic));
            return 0;
        } else {
            log.record(String.format("Delete memo unsuccessfully. From %s-> %s", request.getRemoteAddr(), topic));
            return 1;
        }
    }

    @PostMapping(value = "/memo/{topic}")
    @ResponseBody
    public int create(HttpServletRequest request,
                      @PathVariable(value = "topic") String topic) {
        if (memoData.create(topic)) {
            log.record(String.format("Create memo successfully. From %s-> %s", request.getRemoteAddr(), topic));
            return 0;
        } else {
            log.record(String.format("Create memo unsuccessfully. From %s-> %s", request.getRemoteAddr(), topic));
            return 1;
        }
    }
}
