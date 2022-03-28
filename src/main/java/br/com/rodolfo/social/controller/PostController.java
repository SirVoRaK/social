package br.com.rodolfo.social.controller;

import br.com.rodolfo.social.forms.PostForm;
import br.com.rodolfo.social.model.Post;
import br.com.rodolfo.social.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/posts")
public class PostController {
    @Autowired
    private PostService postService;

    @PostMapping("/")
    public ResponseEntity<Post> createPost(@RequestBody PostForm postForm, @RequestHeader("Authorization") String token) {
        Post post = postService.create(token, postForm.getMessage());
        return ResponseEntity.ok(post);
    }
}
