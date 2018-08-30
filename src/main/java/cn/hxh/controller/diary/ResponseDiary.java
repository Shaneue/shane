package cn.hxh.controller.diary;

import cn.hxh.object.Diary;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.Valid;

public class ResponseDiary {
    @JsonProperty
    int code = 0;
    @JsonProperty
    @Valid
    Diary diary;

    public ResponseDiary(int code, @Valid Diary diary) {
        this.code = code;
        this.diary = diary;
    }
}
