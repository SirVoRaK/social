package br.com.rodolfo.social.forms;

public class CommentForm {
    private String message = "";

    public CommentForm() {
    }

    public CommentForm(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
