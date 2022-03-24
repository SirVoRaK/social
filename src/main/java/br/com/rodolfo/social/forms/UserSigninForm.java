package br.com.rodolfo.social.forms;

import br.com.rodolfo.social.model.User;

public class UserSigninForm {

    private String email;
    private String password;

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

    public User convert() throws Exception {
        return (new User()).setEmail(this.email).setPassword(this.password);
    }
}
