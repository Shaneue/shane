package cn.hxh.controller;

import cn.hxh.common.Response;
import cn.hxh.common.log.MyLog;
import cn.hxh.storage.interfaces.MemoData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
public class MemoService {
    private final MemoData memoData;
    private final MyLog log;

    @Autowired
    public MemoService(MemoData memoData, MyLog log) {
        this.memoData = memoData;
        this.log = log;
    }

    @GetMapping(value = "/memo/{id}")
    @ResponseBody
    public Response get(HttpServletRequest request, @PathVariable(value = "id") String fileName) {
        log.record(String.format("Query memo from %s", request.getRemoteAddr()));
        return new Response(memoData.query(fileName));
    }

    @GetMapping(value = "/memo/list")
    @ResponseBody
    public Response getMemosList(HttpServletRequest request) {
        log.record(String.format("Query memos list from %s", request.getRemoteAddr()));
        return new Response(memoData.queryMemos());
    }

    @PostMapping(value = "/memo/{id}/{topic}")
    @ResponseBody
    public Response post(HttpServletRequest request, @RequestBody String content,
                         @PathVariable(value = "id") String fileName, @PathVariable(value = "topic") String topic) {
        Response re = new Response();
        if (memoData.update(fileName, topic, content)) {
            log.record(String.format("Update memo from %s", request.getRemoteAddr()));
        } else {
            log.record("Fail to update memo.");
            re.setFailure();
        }
        return re;
    }

    @DeleteMapping(value = "/memo/{topic}")
    @ResponseBody
    public Response delete(HttpServletRequest request,
                           @PathVariable(value = "topic") String topic) {
        Response re = new Response();
        if (memoData.delete(topic)) {
            log.record(String.format("Delete memo successfully. From %s-> %s", request.getRemoteAddr(), topic));
        } else {
            log.record(String.format("Delete memo unsuccessfully. From %s-> %s", request.getRemoteAddr(), topic));
            re.setFailure();
        }
        return re;
    }

    @PostMapping(value = "/memo/{topic}")
    @ResponseBody
    public Response create(HttpServletRequest request,
                           @PathVariable(value = "topic") String topic) {
        Response re = new Response();
        if (memoData.create(topic)) {
            log.record(String.format("Create memo successfully. From %s-> %s", request.getRemoteAddr(), topic));
        } else {
            log.record(String.format("Create memo unsuccessfully. From %s-> %s", request.getRemoteAddr(), topic));
            re.setFailure();
        }
        return re;
    }
}
