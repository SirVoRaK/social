package br.com.rodolfo.social.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document
public class Post {
    @Id
    private String id;

    @DBRef
    private User author;

    private String message;

    @DBRef
    private List<User> likes;

    private LocalDateTime date;

    @DBRef
    private List<Comment> comments;

    public Post hidePasswords() {
        this.author.hidePassword();
        if (!this.comments.isEmpty()) this.comments.forEach(Comment::hidePasswords);
        if (!this.likes.isEmpty()) this.likes.forEach(User::hidePassword);
        return this;
    }

    public Post() {
        this.date = LocalDateTime.now();
        this.likes = new ArrayList<User>();
        this.comments = new ArrayList<Comment>();
    }

    public Post(User author, String message) {
        this.author = author;
        this.message = message;
        this.date = LocalDateTime.now();
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setLikes(List<User> likes) {
        this.likes = likes;
    }

    public List<User> getLikes() {
        return likes;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }
}
