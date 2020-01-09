package cn.hxh.object;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class Memo {
    @JsonProperty
    String topic;

    @JsonProperty
    String content;

    public Memo() {

    }

    public Memo(String topic, String content) {
        this.topic = topic;
        this.content = content;
    }

}
