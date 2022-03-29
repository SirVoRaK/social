package br.com.rodolfo.social.service;

import br.com.rodolfo.social.exception.NotFoundException;
import br.com.rodolfo.social.model.Comment;
import br.com.rodolfo.social.model.User;
import br.com.rodolfo.social.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserService userService;

    public Comment create(String token, String message) {
        User author;
        try {
            author = userService.validateToken(token).orElseThrow(() -> new IllegalArgumentException("Invalid token"));
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid token");
        }

        if (!token.startsWith("Bearer "))
            throw new IllegalArgumentException("Token must start with 'Bearer '");

        if (message == null || message.isEmpty())
            throw new IllegalArgumentException("Message cannot be empty");

        Comment comment = new Comment();
        comment.setAuthor(author);
        comment.setMessage(message);
        Comment saved = commentRepository.save(comment);
        saved.hidePasswords();
        return saved;
    }

    public Comment reply(String token, String commentId, String message) throws NotFoundException, IllegalArgumentException {
        User author;
        try {
            author = userService.validateToken(token).orElseThrow(() -> new IllegalArgumentException("Invalid token"));
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid token");
        }

        if (!token.startsWith("Bearer "))
            throw new IllegalArgumentException("Token must start with 'Bearer '");

        if (message == null || message.isEmpty())
            throw new IllegalArgumentException("Message cannot be empty");

        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new NotFoundException("Comment not found"));

        Comment reply = new Comment();
        reply.setAuthor(author);
        reply.setMessage(message);
        Comment saved = commentRepository.save(reply);

        comment.getComments().add(saved);
        commentRepository.save(comment);

        saved.hidePasswords();
        return saved;
    }
}
