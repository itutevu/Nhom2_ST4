package com.example.user.tkpm;

/**
 * Created by USER on 01/05/2018.
 */

public class Script {
    String script ;
    String start;
    String dur;

    public Script() {
    }

    public Script(String script, String start, String dur) {
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
}
