package br.com.rodolfo.social.forms;

public class PostForm {
    private String message;

    public PostForm() {
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "PostForm{" +
                "message='" + message + '\'' +
                '}';
    }
}
