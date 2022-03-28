package br.com.rodolfo.social.repository;

import br.com.rodolfo.social.model.Post;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PostRepository extends MongoRepository<Post, String> {
}
