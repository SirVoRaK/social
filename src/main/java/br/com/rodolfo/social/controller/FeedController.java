package br.com.rodolfo.social.controller;

import br.com.rodolfo.social.exception.ForbiddenException;
import br.com.rodolfo.social.exception.NotFoundException;
import br.com.rodolfo.social.model.Post;
import br.com.rodolfo.social.service.FeedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/feed")
public class FeedController {
    @Autowired
    private FeedService feedService;

    @GetMapping
    public List<Post> getFeed(@RequestHeader("Authorization") String token) throws ForbiddenException, NotFoundException {
        return feedService.getFeed(token);
    }
}
