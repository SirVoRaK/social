package br.com.rodolfo.social.model;

import br.com.rodolfo.social.utils.Crypt;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.security.NoSuchAlgorithmException;

@Document
public class User {
    @Id
    private String id;
    private String username;
    private String email;
    private String password;

    public User() {
    }

    public User(String username, String email, String password) throws NoSuchAlgorithmException {
        this.username = username;
        this.email = email;
        this.setPassword(password);
    }

    public String getId() {
        return this.id;
    }

    public String getUsername() {
        return this.username;
    }

    public User setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getEmail() {
        return this.email;
    }

    public User setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getPassword() {
        return this.password;
    }

    public User setPassword(String password) throws NoSuchAlgorithmException {
        password = Crypt.sha256(password);
        this.password = password;
        return this;
    }
}
