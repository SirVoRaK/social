package br.com.rodolfo.social.controller;

import br.com.rodolfo.social.exception.ForbiddenException;
import br.com.rodolfo.social.exception.NotFoundException;
import br.com.rodolfo.social.model.Post;
import br.com.rodolfo.social.service.FeedService;
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
    public List<Post> getFeed(@RequestHeader("Authorization") String token) throws ForbiddenException, NotFoundException {
        return feedService.getFeed(token);
    }
}
