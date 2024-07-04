package com.sparta.storyindays.repository;

import com.sparta.storyindays.entity.Post;
import com.sparta.storyindays.entity.User;
import com.sparta.storyindays.enums.post.PostType;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepositoryCustom {

    List<Post> findAllByPostType(PostType postType);

    List<Post> findAllByPostTypeAndIsPinned(PostType postType, boolean isPinned);

    List<Post> findAllByPostTypeAndIsPinned(PostType postType, boolean isPinned, Pageable pageable);

    List<Post> findAllByPostTypeAndIsPinnedAndUser(PostType postType, boolean b, User user, Pageable pageable);

    List<Post> findAllByUser(User user);
}
