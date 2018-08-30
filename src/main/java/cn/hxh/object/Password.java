package cn.hxh.object;

import cn.hxh.util.HH;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class Password implements Comparable<Password> {
    @JsonProperty
    private byte[] where;
    @JsonProperty
    private byte[] account;
    @JsonProperty
    private byte[] password;
    @JsonProperty
    private List<Pair> ext;

    public void setAccount(byte[] account) {
        this.account = account;
    }

    public void setPassword(byte[] password) {
        this.password = password;
    }

    public void addPair(String key, String value) {
        if (this.ext == null) {
            this.ext = new ArrayList<>();
        }
        this.ext.add(new Pair(key.getBytes(), value.getBytes()));
    }

    public void setWhere(byte[] where) {
        this.where = where;
    }

    public byte[] getWhere() {
        return where;
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
