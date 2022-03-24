package br.com.rodolfo.social.forms;

import br.com.rodolfo.social.model.User;

import java.security.NoSuchAlgorithmException;

public class UserForm {
    private String username;
    private String email;
    private String password;

    public User convert() {
        try {
            return new User(this.username, this.email, this.password);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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
