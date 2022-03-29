package br.com.rodolfo.social.repository;

import br.com.rodolfo.social.model.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CommentRepository extends MongoRepository<Comment, String> {
}
