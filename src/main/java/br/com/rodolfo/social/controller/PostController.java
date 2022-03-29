package br.com.rodolfo.social.controller;

import br.com.rodolfo.social.exception.NotFoundException;
import br.com.rodolfo.social.forms.PostForm;
import br.com.rodolfo.social.model.Post;
import br.com.rodolfo.social.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/{id}")
    public ResponseEntity<Post> getPost(@PathVariable String id) throws NotFoundException {
        Post post = postService.get(id);
        return ResponseEntity.ok(post);
    }

    @PatchMapping("/{id}/like")
    public ResponseEntity<Post> like(@PathVariable("id") String id, @RequestHeader("Authorization") String token) {
        Post post = postService.like(token, id);
        return ResponseEntity.ok(post);
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<Iterable<Post>> getPostsByUser(@PathVariable("username") String username, @RequestParam(value = "limit", required = false, defaultValue = "10") Integer end, @RequestParam(value = "offset", required = false, defaultValue = "0") Integer start) throws NotFoundException {
        List<Post> posts = postService.getByAuthorName(username, start, end);
        return ResponseEntity.ok(posts);
    }
}
