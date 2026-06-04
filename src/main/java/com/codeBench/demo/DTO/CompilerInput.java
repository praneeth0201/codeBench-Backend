package com.codeBench.demo.DTO;

import lombok.Getter;
import lombok.Setter;


public class CompilerInput {

    private String sessionId;

    private String input;

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }
}
