package ru.yandex.practicum.catsgram.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.model.Post;

import java.time.Instant;
import java.util.*;

@Service
public class PostService {
    private final Map<Long, Post> posts = new HashMap<>();
    private final UserService userService;

    public PostService(UserService userService) {
        this.userService = userService;
    }

    public Collection<Post> findAll(int from, int size, String sort) {
        List<Post> allPosts = new ArrayList<>(posts.values());
        if (from < 0) {
            throw new ConditionsNotMetException("Неверный параметр для начала вывода списка");
        }

        if (size <= 0) {
            throw new ConditionsNotMetException("Неверное колличество постов для отображения");
        }

        if (!"desc".equalsIgnoreCase(sort) && !"asc".equalsIgnoreCase(sort)) {
            throw new ConditionsNotMetException("Неверный параметр для сортировки");
        }

        Comparator<Post> comparator = sort.equals("asc") ? Comparator.comparing(Post::getPostDate)
                : Comparator.comparing(Post::getPostDate, Comparator.reverseOrder());

        allPosts = allPosts.stream()
                .sorted(comparator)
                .skip(from)
                .limit(size)
                .toList();
        return allPosts;
    }

    public Post create(Post post) {
        if (post.getDescription() == null || post.getDescription().isBlank()) {
            throw new ConditionsNotMetException("Описание не может быть пустым");
        }

        if (userService.findUserById(post.getAuthorId()) == null) {
            throw new ConditionsNotMetException("Пользователя с id: " + post.getAuthorId() + "не найден.");
        }

        post.setId(getNextId());
        post.setPostDate(Instant.now());
        posts.put(post.getId(), post);
        return post;
    }

    public Post update(Post newPost) {
        if (newPost.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }

        if (userService.findUserById(newPost.getAuthorId()) == null) {
            throw new ConditionsNotMetException("Пользователя с id: " + newPost.getAuthorId() + "не найден.");
        }

        if (posts.containsKey(newPost.getId())) {
            Post oldPost = posts.get(newPost.getId());
            if (newPost.getDescription() == null || newPost.getDescription().isBlank()) {
                throw new ConditionsNotMetException("Описание не может быть пустым");
            }
            oldPost.setDescription(newPost.getDescription());
            return oldPost;
        }
        throw new NotFoundException("Пост с id = " + newPost.getId() + " не найден");
    }

    public Post getPostById(int postId) {
        return posts.values()
                .stream()
                .filter((post) -> post.getId() == postId)
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Пост с id: " + postId + " не найден."));
    }

    private long getNextId() {
        long currentMaxId = posts.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
