package br.com.rodolfo.social.service;

import br.com.rodolfo.social.exception.ForbiddenException;
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

    public CommentService(CommentRepository commentRepository, UserService userService) {
        this.commentRepository = commentRepository;
        this.userService = userService;
    }

    public Comment create(String token, String message) throws ForbiddenException {
        if (!token.startsWith("Bearer "))
            throw new IllegalArgumentException("Token must start with 'Bearer '");

        User author;
        try {
            author = userService.validateToken(token).orElseThrow(() -> new ForbiddenException("Invalid token"));
        } catch (Exception e) {
            throw new ForbiddenException("Invalid token");
        }

        if (message == null || message.isEmpty())
            throw new IllegalArgumentException("Message cannot be empty");

        Comment comment = new Comment();
        comment.setAuthor(author);
        comment.setMessage(message);
        Comment saved = commentRepository.save(comment);
        saved.hidePasswords();
        return saved;
    }

    public Comment reply(String token, String commentId, String message) throws NotFoundException, IllegalArgumentException, ForbiddenException {
        if (!token.startsWith("Bearer "))
            throw new IllegalArgumentException("Token must start with 'Bearer '");

        User author;
        try {
            author = userService.validateToken(token).orElseThrow(() -> new ForbiddenException("Invalid token"));
        } catch (Exception e) {
            throw new ForbiddenException("Invalid token");
        }

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

    public void delete(String token, String commentId) throws NotFoundException, ForbiddenException {
        if (!token.startsWith("Bearer "))
            throw new IllegalArgumentException("Token must start with 'Bearer '");

        User author;
        try {
            author = userService.validateToken(token).orElseThrow(() -> new ForbiddenException("Invalid token"));
        } catch (Exception e) {
            throw new ForbiddenException("Invalid token");
        }

        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new NotFoundException("Comment not found"));

        if (!comment.getAuthor().equals(author))
            throw new ForbiddenException("You are not the author of this comment");

        this.forceDelete(null, comment);
    }

    private void forceDelete(String id, Comment comment) throws NotFoundException {
        if (comment == null && id == null) throw new NotFoundException("Comment not found");
        if (comment == null)
            comment = commentRepository.findById(id).orElseThrow(() -> new NotFoundException("Comment not found"));

        if (comment.getComments().size() > 0) {
            comment.getComments().forEach(c -> {
                try {
                    this.forceDelete(c.getId(), null);
                } catch (NotFoundException ignored) {
                }
            });
        }

        this.commentRepository.delete(comment);
    }
}
