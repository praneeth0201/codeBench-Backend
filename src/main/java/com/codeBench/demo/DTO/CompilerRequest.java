package com.codeBench.demo.DTO;



import lombok.Getter;
import lombok.Setter;


public class CompilerRequest {

    private String language;

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    private String code;
}
