package com.sparta.storyindays.repository;

import com.sparta.storyindays.entity.Post;
import com.sparta.storyindays.entity.User;
import com.sparta.storyindays.enums.post.PostType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom{
//    List<Post> findAllByPostType(PostType postType);
//
//    List<Post> findAllByPostTypeAndIsPinned(PostType postType, boolean isPinned);
//
//    Page<Post> findAllByPostTypeAndIsPinned(PostType postType, boolean isPinned, Pageable pageable);
//
//    Page<Post> findAllByPostTypeAndIsPinnedAndUser(PostType postType, boolean b, User user, Pageable pageable);
//
//   List<Post> findAllByUser(User user);
}
