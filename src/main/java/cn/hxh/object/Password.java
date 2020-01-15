package cn.hxh.object;

import cn.hxh.util.HH;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Setter
@Getter
public class Password implements Comparable<Password> {
    @JsonProperty
    private byte[] where;
    @JsonProperty
    private byte[] account;
    @JsonProperty
    private byte[] password;
    @JsonProperty
    private List<Pair> ext;
    @JsonProperty
    private String time;


    public void addPair(String key, String value) {
        if (this.ext == null) {
            this.ext = new ArrayList<>();
        }
        this.ext.add(new Pair(key.getBytes(), value.getBytes()));
    }


    public void clean() {
        HH.eraseArray(account);
        HH.eraseArray(password);
        HH.eraseArray(where);
        if (ext != null) {
            for (Pair pair : ext) {
                pair.clean();
            }
        }
    }

    public List<Map> convertExt() {
        List<Map> list = new ArrayList<>();
        for (Pair pair : ext) {
            list.add(pair.getMap());
        }
        return list;
    }

    @Override
    public int compareTo(Password other) {
        int i = 0;
        while (i <= other.where.length && i <= this.where.length) {
            if (this.where[i] > other.where[i]) {
                return 1;
            } else if (this.where[i] < other.where[i]) {
                return -1;
            } else {
                i++;
            }
        }
        if (i < other.where.length) return 1;
        else return -1;
    }

    static class Pair {
        @JsonProperty
        byte[] key;
        @JsonProperty
        byte[] value;

        Pair() {
            // for jackson
        }

        Pair(byte[] key, byte[] value) {
            this.key = key;
            this.value = value;
        }

        void clean() {
            HH.eraseArray(key);
            HH.eraseArray(value);
        }

        public Map<String, String> getMap() {
            Map<String, String> map = new HashMap<>();
            map.put("key", new String(key));
            map.put("value", new String(value));
            return map;
        }

        @Override
        public String toString() {
            return "\t" + new String(key) + ':' + new String(value) + '\t';
        }
    }

    @Override
    public String toString() {
        return new String(where) + ' ' + new String(account) + ' ' + new String(password) + (ext == null ? "" : '\t' + ext.toString()) + '\n';
    }
}
