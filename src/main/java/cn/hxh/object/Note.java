package cn.hxh.object;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Note {
    @JsonProperty
    String topic;

    @JsonProperty
    String content;

    public Note() {

    }

    public Note(String topic, String content) {
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
