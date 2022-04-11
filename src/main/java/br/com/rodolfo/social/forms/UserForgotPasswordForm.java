package br.com.rodolfo.social.forms;

public class UserForgotPasswordForm {
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserForgotPasswordForm() {
    }

    public UserForgotPasswordForm(String email) {
        this.email = email;
    }
}
