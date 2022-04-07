package br.com.rodolfo.social.service;

import br.com.rodolfo.social.exception.ForbiddenException;
import br.com.rodolfo.social.exception.NotFoundException;
import br.com.rodolfo.social.model.Comment;
import br.com.rodolfo.social.model.Post;
import br.com.rodolfo.social.model.User;
import br.com.rodolfo.social.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentService {
    @Autowired
    private final CommentRepository commentRepository;

    @Autowired
    private final UserService userService;

    public CommentService(CommentRepository commentRepository, UserService userService) {
        this.commentRepository = commentRepository;
        this.userService = userService;
    }

    public Comment create(String token, String message) throws ForbiddenException, NotFoundException {
        return this.create(token, message, null, null);
    }

    public Comment create(String token, String message, String postId, PostService postService) throws ForbiddenException, NotFoundException {
        if (postId != null)
            if (!postService.exists(postId))
                throw new NotFoundException("Post not found");

        User author = userService.validateToken(token);

        if (message == null || message.isEmpty())
            throw new IllegalArgumentException("Message cannot be empty");

        Comment comment = new Comment();
        comment.setAuthor(author);
        comment.setMessage(message);
        Comment saved = commentRepository.save(comment);
        saved.hidePasswords();

        if (postId != null) postService.comment(postId, comment);

        return saved.hidePasswords();
    }

    public Comment reply(String token, String commentId, String message) throws NotFoundException, IllegalArgumentException, ForbiddenException {
        User author = userService.validateToken(token);

        if (message == null || message.isEmpty())
            throw new IllegalArgumentException("Message cannot be empty");

        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new NotFoundException("Comment not found"));

        Comment reply = new Comment();
        reply.setAuthor(author);
        reply.setMessage(message);

        Comment saved = commentRepository.save(reply);
        comment.getComments().add(saved);

        commentRepository.save(comment);

        return saved.hidePasswords();
    }

    public void delete(String token, String commentId, PostService postService) throws NotFoundException, ForbiddenException {
        User author = userService.validateToken(token);

        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new NotFoundException("Comment not found"));

        if (!comment.getAuthor().equals(author))
            throw new ForbiddenException("You are not the author of this comment");

        try {
            Post post = postService.getByCommentId(commentId);
            post.getComments().remove(comment);
            postService.save(post);
        } catch (Exception ignored) {
        }

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
        try {
            Comment parent = this.getByCommentId(comment.getId());
            parent.getComments().remove(comment);
            this.save(parent);
        } catch (Exception ignored) {
        }
        this.commentRepository.delete(comment);
    }

    private Comment save(Comment comment) {
        return this.commentRepository.save(comment);
    }

    private Comment getByCommentId(String commentId) throws NotFoundException {
        return this.commentRepository.findByCommentId(commentId).orElseThrow(() -> new NotFoundException("Comment not found"));
    }
}
