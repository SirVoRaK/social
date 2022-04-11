package br.com.rodolfo.social.controller;

import br.com.rodolfo.social.dto.PostDto;
import br.com.rodolfo.social.exception.ForbiddenException;
import br.com.rodolfo.social.exception.NotFoundException;
import br.com.rodolfo.social.exception.SpringException;
import br.com.rodolfo.social.service.FeedService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/feed")
@Tag(name = "Feed")
public class FeedController {
    @Autowired
    private FeedService feedService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @ApiResponse(description = "Invalid token", responseCode = "403", content = @Content(schema = @Schema(implementation = SpringException.class)))
    public List<PostDto> getFeed(@RequestHeader("Authorization") String token) throws ForbiddenException, NotFoundException {
        return PostDto.from(feedService.getFeed(token));
    }
}
