package cn.hxh.controller;

import cn.hxh.common.Response;
import cn.hxh.storage.interfaces.MemoData;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class MemoService {
    private final MemoData memoData;

    @Autowired
    public MemoService(MemoData memoData) {
        this.memoData = memoData;
    }

    @GetMapping(value = "/memo/{id}")
    @ResponseBody
    public Response getMemoById(HttpServletRequest request, @PathVariable(value = "id") String fileName) {
        log.info(String.format("Query memo from %s", request.getRemoteAddr()));
        return new Response(memoData.query(fileName));
    }

    @GetMapping(value = "/memo/list")
    @ResponseBody
    public Response getMemosList(HttpServletRequest request) {
        log.info(String.format("Query memos list from %s", request.getRemoteAddr()));
        List<Map<String, String>> memos = memoData.queryMemos();
        memos.sort((a, b) -> (b.get("time") == null ? "" : b.get("time")).compareTo(a.get("time") == null ? "" : a.get("time")));
        return new Response(memoData.queryMemos());
    }

    @PostMapping(value = "/memo/{id}/{topic}")
    @ResponseBody
    public Response updateMemo(HttpServletRequest request, @RequestBody String content,
                               @PathVariable(value = "id") String fileName, @PathVariable(value = "topic") String topic) {
        Response re = new Response();
        if (memoData.update(fileName, topic, content)) {
            log.info(String.format("Update memo from %s", request.getRemoteAddr()));
        } else {
            log.info("Fail to update memo.");
            re.setFailure();
        }
        return re;
    }

    @DeleteMapping(value = "/memo/{topic}")
    @ResponseBody
    public Response deleteMemo(HttpServletRequest request,
                               @PathVariable(value = "topic") String topic) {
        Response re = new Response();
        if (memoData.delete(topic)) {
            log.info(String.format("Delete memo successfully. From %s-> %s", request.getRemoteAddr(), topic));
        } else {
            log.info(String.format("Delete memo unsuccessfully. From %s-> %s", request.getRemoteAddr(), topic));
            re.setFailure();
        }
        return re;
    }

    @PostMapping(value = "/memo/{topic}")
    @ResponseBody
    public Response createMemo(HttpServletRequest request,
                               @PathVariable(value = "topic") String topic) {
        Response re = new Response();
        if (memoData.create(topic)) {
            log.info(String.format("Create memo successfully. From %s-> %s", request.getRemoteAddr(), topic));
        } else {
            log.info(String.format("Create memo unsuccessfully. From %s-> %s", request.getRemoteAddr(), topic));
            re.setFailure();
        }
        return re;
    }

    @PostMapping(value = "/memo")
    @ResponseBody
    public Response nameModify(@RequestBody @Valid NameModify body) {
        return memoData.reName(body.getOldName(), body.getNewName());
    }

    @Getter
    static class NameModify {
        @JsonProperty
        @NotBlank
        String oldName;

        @JsonProperty
        @NotBlank
        String newName;
    }
}
