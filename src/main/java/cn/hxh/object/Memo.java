package cn.hxh.object;

import com.fasterxml.jackson.annotation.JsonProperty;

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

    public String getTopic() {
        return topic;
    }


    public String getContent() {
        return content;
    }
}
