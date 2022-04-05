package br.com.rodolfo.social.repository;

import br.com.rodolfo.social.model.Post;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends MongoRepository<Post, String> {
    @Query("{\"author.$id\": ObjectId(\"?0\")}")
    List<Post> findByAuthorId(String userId);

    @Query("{\"comments.$id\": ObjectId(\"?0\")}")
    Optional<Post> findByCommentId(String commentId);
}
