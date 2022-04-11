package br.com.rodolfo.social.controller;

import br.com.rodolfo.social.dto.PostDto;
import br.com.rodolfo.social.exception.ForbiddenException;
import br.com.rodolfo.social.exception.NotFoundException;
import br.com.rodolfo.social.exception.SpringException;
import br.com.rodolfo.social.exception.UnauthorizedException;
import br.com.rodolfo.social.forms.PostForm;
import br.com.rodolfo.social.model.Post;
import br.com.rodolfo.social.service.PostService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts")
@Tag(name = "Posts")
public class PostController {
    @Autowired
    private PostService postService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    @ApiResponse(description = "Created", responseCode = "200")
    @ApiResponse(description = "Bad request", responseCode = "400", content = @Content(schema = @Schema(implementation = SpringException.class)))
    @ApiResponse(description = "Invalid token", responseCode = "403", content = @Content(schema = @Schema(implementation = SpringException.class)))
    public ResponseEntity<PostDto> createPost(@RequestBody PostForm postForm, @RequestHeader("Authorization") String token) throws ForbiddenException {
        Post post = postService.create(token, postForm.getMessage());
        return ResponseEntity.ok(new PostDto(post));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiResponse(description = "Success", responseCode = "204")
    @ApiResponse(description = "Not the author", responseCode = "401", content = @Content(schema = @Schema(implementation = SpringException.class)))
    @ApiResponse(description = "Invalid token", responseCode = "403", content = @Content(schema = @Schema(implementation = SpringException.class)))
    @ApiResponse(description = "Not found", responseCode = "404", content = @Content(schema = @Schema(implementation = SpringException.class)))
    public ResponseEntity<?> deletePost(@PathVariable("id") String id, @RequestHeader("Authorization") String token) throws NotFoundException, ForbiddenException, UnauthorizedException {
        postService.delete(token, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponse(description = "Success", responseCode = "200")
    @ApiResponse(description = "Not found", responseCode = "404", content = @Content(schema = @Schema(implementation = SpringException.class)))
    public ResponseEntity<PostDto> getPost(@PathVariable String id) throws NotFoundException {
        Post post = postService.get(id);
        return ResponseEntity.ok(new PostDto(post));
    }

    @PatchMapping("/{id}/like")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponse(description = "Success", responseCode = "200")
    @ApiResponse(description = "Not found", responseCode = "404", content = @Content(schema = @Schema(implementation = SpringException.class)))
    @ApiResponse(description = "Invalid token", responseCode = "403", content = @Content(schema = @Schema(implementation = SpringException.class)))
    public ResponseEntity<PostDto> like(@PathVariable("id") String id, @RequestHeader("Authorization") String token) throws ForbiddenException, NotFoundException {
        Post post = postService.like(token, id);
        return ResponseEntity.ok(new PostDto(post));
    }

    @GetMapping("/user/{username}")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponse(description = "Success", responseCode = "200")
    @ApiResponse(description = "Not found", responseCode = "404", content = @Content(schema = @Schema(implementation = SpringException.class)))
    public ResponseEntity<Iterable<PostDto>> getPostsByUser(@PathVariable("username") String username) throws NotFoundException {
        List<Post> posts = postService.getByAuthorName(username);
        return ResponseEntity.ok(PostDto.from(posts));
    }
}
