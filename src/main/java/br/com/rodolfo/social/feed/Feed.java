package br.com.rodolfo.social.feed;

import br.com.rodolfo.social.exception.NotFoundException;
import br.com.rodolfo.social.model.Post;
import br.com.rodolfo.social.service.PostService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Feed {
    private List<Post> posts;

    public Feed(List<String> usernames, PostService service) throws NotFoundException {
        this.posts = new ArrayList<Post>();
        for (String username : usernames) {
            List<Post> userPosts = service.getByAuthorName(username);
            this.posts.addAll(userPosts);
        }
    }

    public List<Post> shuffled() {
        Collections.shuffle(this.posts);
        return this.posts;
    }

    public List<Post> sortedByDate() {
        return this.posts
                .stream()
                .sorted(Comparator.comparing(Post::getDate))
                .collect(Collectors.toList());
    }
}
