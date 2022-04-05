package br.com.rodolfo.social.repository;

import br.com.rodolfo.social.model.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface CommentRepository extends MongoRepository<Comment, String> {
    @Query("{\"comments.$id\": ObjectId(\"?0\")}")
    Optional<Comment> findByCommentId(String commentId);
}
