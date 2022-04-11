package br.com.rodolfo.social.controller;

import br.com.rodolfo.social.dto.CommentDto;
import br.com.rodolfo.social.exception.ForbiddenException;
import br.com.rodolfo.social.exception.NotFoundException;
import br.com.rodolfo.social.exception.SpringException;
import br.com.rodolfo.social.forms.CommentForm;
import br.com.rodolfo.social.model.Comment;
import br.com.rodolfo.social.service.CommentService;
import br.com.rodolfo.social.service.PostService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comments")
@Tag(name = "Comment")
public class CommentController {
    @Autowired
    private CommentService commentService;

    @Autowired
    private PostService postService;

    @PostMapping("/post/{postId}")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponse(description = "Created", responseCode = "200")
    @ApiResponse(description = "Not found", responseCode = "404", content = @Content(schema = @Schema(implementation = SpringException.class)))
    @ApiResponse(description = "Invalid token", responseCode = "403", content = @Content(schema = @Schema(implementation = SpringException.class)))
    @ApiResponse(description = "Bad request", responseCode = "400", content = @Content(schema = @Schema(implementation = SpringException.class)))
    public ResponseEntity<CommentDto> createComment(@RequestHeader("Authorization") String token, @PathVariable("postId") String postId, @RequestBody CommentForm commentForm) throws NotFoundException, ForbiddenException {
        Comment comment = commentService.create(token, commentForm.getMessage(), postId, this.postService);
        return ResponseEntity.ok(new CommentDto(comment));
    }

    @PostMapping("/reply/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponse(description = "Success", responseCode = "200")
    @ApiResponse(description = "Not found", responseCode = "404", content = @Content(schema = @Schema(implementation = SpringException.class)))
    @ApiResponse(description = "Invalid token", responseCode = "403", content = @Content(schema = @Schema(implementation = SpringException.class)))
    @ApiResponse(description = "Bad request", responseCode = "400", content = @Content(schema = @Schema(implementation = SpringException.class)))
    public ResponseEntity<CommentDto> replyComment(@RequestHeader("Authorization") String token, @PathVariable("commentId") String commentId, @RequestBody CommentForm commentForm) throws NotFoundException, ForbiddenException {
        Comment comment = commentService.reply(token, commentId, commentForm.getMessage());
        return ResponseEntity.ok(new CommentDto(comment));
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiResponse(description = "Success", responseCode = "204")
    @ApiResponse(description = "Not the author", responseCode = "401", content = @Content(schema = @Schema(implementation = SpringException.class)))
    @ApiResponse(description = "Invalid token", responseCode = "403", content = @Content(schema = @Schema(implementation = SpringException.class)))
    @ApiResponse(description = "Not found", responseCode = "404", content = @Content(schema = @Schema(implementation = SpringException.class)))
    public ResponseEntity<?> deleteComment(@RequestHeader("Authorization") String token, @PathVariable("commentId") String commentId) throws NotFoundException, ForbiddenException {
        commentService.delete(token, commentId, postService);
        return ResponseEntity.noContent().build();
    }
}
