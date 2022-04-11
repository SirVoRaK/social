package br.com.rodolfo.social.forms;

public class UserResetPasswordForm {
    private String code;
    private String email;
    private String password;

    public UserResetPasswordForm(String code, String email, String password) {
        this.code = code;
        this.email = email;
        this.password = password;
    }

    public UserResetPasswordForm() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
