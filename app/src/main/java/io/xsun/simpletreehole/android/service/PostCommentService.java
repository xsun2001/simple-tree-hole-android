package io.xsun.simpletreehole.android.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import io.xsun.simpletreehole.android.data.Comment;
import io.xsun.simpletreehole.android.data.Post;

public final class PostCommentService {

    private final static PostCommentService instance = new PostCommentService();
    private final Map<Long, Post> posts = new HashMap<>();
    private final Map<Long, Comment> comments = new HashMap<>();

    private Long _createPost(long userId, String content) {
        var user = Objects.requireNonNull(UserService.getInstance().getIdMap().get(userId));
        var post = new Post(posts.size(), user, LocalDateTime.now(), content, new HashSet<>(), new HashSet<>());
        posts.put(post.getId(), post);
        return post.getId();
    }

    private Long _createComment(long postId, long userId, String content) {
        var post = Objects.requireNonNull(posts.get(postId));
        var user = Objects.requireNonNull(UserService.getInstance().getIdMap().get(userId));
        var comment = new Comment(comments.size(), user, LocalDateTime.now(), content, post, new HashSet<>());
        post.getComments().add(comment);
        comments.put(comment.getId(), comment);
        return comment.getId();
    }

    private static <T> boolean toggle(Collection<T> collection, T t) {
        if (collection.contains(t)) {
            collection.remove(t);
            return false;
        } else {
            collection.add(t);
            return true;
        }
    }

    private boolean _toggleLikePost(long postId, long userId) {
        var post = Objects.requireNonNull(posts.get(postId));
        var user = Objects.requireNonNull(UserService.getInstance().getIdMap().get(userId));
        return toggle(post.getLikers(), user);
    }

    private boolean _toggleLikeComment(long commentId, long userId) {
        var comment = Objects.requireNonNull(comments.get(commentId));
        var user = Objects.requireNonNull(UserService.getInstance().getIdMap().get(userId));
        return toggle(comment.getLikers(), user);
    }

    public void createPost(long userId, String content, TaskRunner.Callback<Long> callback) {
        TaskRunner.getInstance().execute(() -> _createPost(userId, content), callback);
    }

    public void createComment(long postId, long userId, String content, TaskRunner.Callback<Long> callback) {
        TaskRunner.getInstance().execute(() -> _createComment(postId, userId, content), callback);
    }

    public void toggleLikePost(long postId, long userId, TaskRunner.Callback<Boolean> callback) {
        TaskRunner.getInstance().execute(() -> _toggleLikePost(postId, userId), callback);
    }

    public void toggleLikeComment(long postId, long userId, TaskRunner.Callback<Boolean> callback) {
        TaskRunner.getInstance().execute(() -> _toggleLikeComment(postId, userId), callback);
    }

    public void postList(int offset, int pageSize, TaskRunner.Callback<List<Post>> callback) {
        TaskRunner.getInstance().execute(() -> {
            var list = new ArrayList<>(posts.values());
            list.sort(Comparator.comparing(Post::getId).reversed());
            return list.subList(offset, Math.min(list.size(), offset + pageSize));
        }, callback);
    }

    private PostCommentService() {
        var random = new Random();
        for (int i = 0; i < 100; i++) {
            _createPost(random.nextInt(100), "Post Content Post Content Post Content Post Content Post Content Post Content Post Content Post Content Post Content Post Content");
            _createComment(i, random.nextInt(100), "Comment test 1");
            _createComment(i, random.nextInt(100), "Comment test 2");
        }
        for (int i = 0; i < 100; i++) {
            var likerCount = random.nextInt(10);
            var likerSet = new HashSet<Integer>();
            while (likerSet.size() < likerCount) {
                likerSet.add(random.nextInt(100));
            }
            for (var liker : likerSet) {
                _toggleLikePost(i, liker);
            }
        }
    }

    public static PostCommentService getInstance() {
        return instance;
    }
}
