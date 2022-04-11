package br.com.rodolfo.social.dto;

import br.com.rodolfo.social.model.Comment;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CommentDto {
    private String message;
    private UserDto author;
    private LocalDateTime date;
    private List<CommentDto> comments;

    public CommentDto() {
    }

    public CommentDto(Comment comment) {
        this.message = comment.getMessage();
        this.author = new UserDto(comment.getAuthor());
        this.date = comment.getDate();
        this.comments = convert(comment.getComments());
    }

    public static List<CommentDto> convert(List<Comment> comments) {
        List<CommentDto> listDto = new ArrayList<CommentDto>();
        for (Comment comment : comments)
            listDto.add(new CommentDto(comment));
        return listDto;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public UserDto getAuthor() {
        return author;
    }

    public void setAuthor(UserDto author) {
        this.author = author;
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
