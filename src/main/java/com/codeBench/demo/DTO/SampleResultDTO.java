package com.codeBench.demo.DTO;

public class SampleResultDTO {
    private Integer sampleNumber;
    private String input;
    private String expected;
    private String got;
    private String verdict;

    public SampleResultDTO() {}

    public SampleResultDTO(Integer sampleNumber, String input, String expected, String got, String verdict) {
        this.sampleNumber = sampleNumber;
        this.input = input;
        this.expected = expected;
        this.got = got;
        this.verdict = verdict;
    }

    public Integer getSampleNumber() {
        return sampleNumber;
    }

    public void setSampleNumber(Integer sampleNumber) {
        this.sampleNumber = sampleNumber;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getExpected() {
        return expected;
    }

    public void setExpected(String expected) {
        this.expected = expected;
    }

    public String getGot() {
        return got;
    }

    public void setGot(String got) {
        this.got = got;
    }

    public String getVerdict() {
        return verdict;
    }

    public void setVerdict(String verdict) {
        this.verdict = verdict;
    }
}
