package br.com.rodolfo.social.model;

import br.com.rodolfo.social.utils.Crypt;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Document
public class User {
    @Id
    private String id;
    private String username;
    private String email;
    private String password;
    private String avatarUrl;
    private List<String> following;
    private List<String> followers;

    public User() {
        this.followers = new ArrayList<String>();
        this.following = new ArrayList<String>();
    }

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.setPassword(password);
        this.followers = new ArrayList<String>();
        this.following = new ArrayList<String>();
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

    public User setPassword(String password) {
        if (password != null && !password.isEmpty()) password = Crypt.sha256(password);
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

    public List<String> getFollowing() {
        return following;
    }

    public void setFollowing(List<String> following) {
        this.following = following;
    }

    public List<String> getFollowers() {
        return followers;
    }

    public void setFollowers(List<String> followers) {
        this.followers = followers;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", following=" + following +
                ", followers=" + followers +
                '}';
    }

    public void hidePassword() {
        this.password = null;
    }
}
