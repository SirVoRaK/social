package br.com.rodolfo.social.model;

import br.com.rodolfo.social.utils.Crypt;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.security.NoSuchAlgorithmException;
import java.util.Objects;

@Document
public class User {
    @Id
    private String id;
    private String username;
    private String email;
    private String password;
    private String avatarUrl;

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
        if (password != null) password = Crypt.sha256(password);
        this.password = password;
        return this;
    }

    public String getAvatarUrl() {
        return this.avatarUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public User setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
        return this;
    }
}
