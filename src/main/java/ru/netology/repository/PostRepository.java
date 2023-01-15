package ru.netology.repository;

import org.springframework.stereotype.Repository;
import ru.netology.exception.NotFoundException;
import ru.netology.model.Post;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class PostRepository {

    private final AtomicLong id = new AtomicLong(1);
    private final ConcurrentHashMap<Long, Post> posts = new ConcurrentHashMap<>();

    public List<Post> all() {
        return posts.values().stream().
                filter(post -> !post.isRemoved()).
                toList();
    }

    public Optional<Post> getById(long id) {
        if (posts.get(id).isRemoved()) {
            noSuchPostError(id);
        }
        return Optional.ofNullable(posts.get(id));
    }

    public Post save(Post post) {

        long postID = post.getId();

        if (postID != 0) {
            if (posts.containsKey(postID)) {
                throw new NotFoundException("Unable to add post with id: " + postID);
            }
            posts.replace(postID, post);
        }

        if (postID == 0) {
            post.setId(id.getAndIncrement());
            posts.put(id.longValue(), post);
        }
        return post;
    }

    public void removeById(long id) {
        if (posts.get(id).isRemoved()) {
            noSuchPostError(id);
        }
        posts.get(id).setRemoved(true);
    }

    public void noSuchPostError(long id) {
        String msg = "Post with ID {" + id + "} not found";
        throw new NotFoundException(msg);
    }
}
