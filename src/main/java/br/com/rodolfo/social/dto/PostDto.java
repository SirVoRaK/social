package br.com.rodolfo.social.dto;

import br.com.rodolfo.social.model.Post;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PostDto {
    private UserDto author;
    private String message;
    private List<UserDto> likes;
    private LocalDateTime date;
    private List<CommentDto> comments;

    public PostDto() {
    }

    public PostDto(Post post) {
        this.author = new UserDto(post.getAuthor());
        this.message = post.getMessage();
        this.likes = UserDto.convert(post.getLikes());
        this.date = post.getDate();
        this.comments = CommentDto.convert(post.getComments());
    }

    public static List<PostDto> from(List<Post> posts) {
        List<PostDto> listPosts = new ArrayList<>();
        for (Post post : posts)
            listPosts.add(new PostDto(post));
        return listPosts;
    }

    public UserDto getAuthor() {
        return author;
    }

    public void setAuthor(UserDto author) {
        this.author = author;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<UserDto> getLikes() {
        return likes;
    }

    public void setLikes(List<UserDto> likes) {
        this.likes = likes;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public List<CommentDto> getComments() {
        return comments;
    }

    public void setComments(List<CommentDto> comments) {
        this.comments = comments;
    }
}
