package com.example.user.tkpm;

/**
 * Created by USER on 01/05/2018.
 */

public class HScript {
    String url;
    String script;
    String start;
    String dur;

    public HScript() {
    }

    public HScript(String url, String script, String start, String dur) {
        this.url = url;
        this.script = script;
        this.start = start;
        this.dur = dur;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getDur() {
        return dur;
    }

    public void setDur(String dur) {
        this.dur = dur;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
