package cn.hxh.object;

import cn.hxh.util.HH;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class Memo {
    @JsonProperty
    String topic;

    @JsonProperty
    String content;

    @JsonProperty
    String time;

    public Memo() {

    }

    public Memo(String topic, String content) {
        this.topic = topic;
        this.content = content;
        this.time = HH.timeNow();
    }
}
