package br.com.rodolfo.social.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Document
public class Comment {
    @Id
    private String id;
    private String message;

    @DBRef
    private User author;
    private LocalDateTime date;

    @DBRef
    private List<Comment> comments;

    public Comment() {
        this.date = LocalDateTime.now();
        this.comments = new ArrayList<Comment>();
    }

    public Comment(String message, User author) {
        this.message = message;
        this.author = author;
        this.comments = new ArrayList<Comment>();
        this.date = LocalDateTime.now();
    }

    public Comment hidePasswords() {
        this.author.setPassword(null);
        if (!this.comments.isEmpty()) this.comments.forEach(Comment::hidePasswords);
        return this;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comment comment = (Comment) o;
        return Objects.equals(id, comment.id) && Objects.equals(message, comment.message) && Objects.equals(author, comment.author) && Objects.equals(date, comment.date) && Objects.equals(comments, comment.comments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, message, author, date, comments);
    }
}
