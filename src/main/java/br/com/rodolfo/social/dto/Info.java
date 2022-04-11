package br.com.rodolfo.social.dto;

public class Info {
    private String message;

    public Info(String message) {
        this.message = message;
    }

    public Info() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
