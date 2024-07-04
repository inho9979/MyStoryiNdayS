package com.sparta.storyindays.service;

import com.sparta.storyindays.dto.comment.CommentResDto;
import com.sparta.storyindays.dto.post.*;
import com.sparta.storyindays.entity.Comment;
import com.sparta.storyindays.entity.Follow;
import com.sparta.storyindays.entity.Post;
import com.sparta.storyindays.entity.User;
import com.sparta.storyindays.enums.post.PostType;
import com.sparta.storyindays.enums.user.Auth;
import com.sparta.storyindays.exception.BusinessLogicException;
import com.sparta.storyindays.repository.CommentRepository;
import com.sparta.storyindays.repository.PostRepository;
import com.sparta.storyindays.util.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserService userService;
    private final FollowService followService;
    private final CommentRepository commentRepository;
    private final MessageSource messageSource;

    public PostResDto writePost(PostReqDto reqDto, User user) {

        User curUser = userService.findById(user.getId());
        Post post = postRepository.save(reqDto.toPostEntity(curUser));
        PostResDto postReqDto = new PostResDto(post);

        return postReqDto;
    }

    public PostGetResDto getAllPost(int page, boolean isAsc) {

        Pageable pageable = Utils.getPageable(page, isAsc);

        PostGetResDto postGetResDto = new PostGetResDto(postRepository.findAllByPostType(PostType.NOTICE)
                , postRepository.findAllByPostTypeAndIsPinned(PostType.NORMAL, true)
                , postRepository.findAllByPostTypeAndIsPinned(PostType.NORMAL, false, pageable));

        return postGetResDto;
    }

    public PostGetResDto getUserPost(String userName, int page, boolean isAsc) {
        Pageable pageable = Utils.getPageable(page, isAsc);

        User searchUser = userService.findByUserName(userName);

        PostGetResDto postGetResDto = new PostGetResDto(postRepository.findAllByPostType(PostType.NOTICE)
                , postRepository.findAllByPostTypeAndIsPinned(PostType.NORMAL, true)
                , postRepository.findAllByPostTypeAndIsPinnedAndUser(PostType.NORMAL, false, searchUser, pageable));

        return postGetResDto;
    }

    @Transactional
    public PostUpdateResDto updatePost(long postId, PostReqDto reqDto, User user) {

        User curUser = userService.findById(user.getId());

        Post post = findById(postId);
        if (!post.getUser().getUsername().equals(curUser.getUsername())) {
            throw new BusinessLogicException(messageSource.getMessage(
                    "only.account.can.post.option",
                    new String[]{"수정"},
                    "Wrong Request",
                    Locale.getDefault()
            ));
        }

        post.update(reqDto);
        return new PostUpdateResDto(post);
    }

    public void deletePost(long postId, User user) {

        User curUser = userService.findById(user.getId());

        Post post = findById(postId);
        if (!post.getUser().getUsername().equals(curUser.getUsername())) {
            throw new BusinessLogicException(messageSource.getMessage(
                    "only.myaccount.can.post.option",
                    new String[]{"삭제"},
                    "Wrong Request",
                    Locale.getDefault()
            ));
        }
        if (post.getUser().getAuth().equals(Auth.USER) && post.getPostType().equals(PostType.NOTICE)) {
            throw new BusinessLogicException(messageSource.getMessage(
                    "only.admin.can.delete",
                    null,
                    "Wrong Request",
                    Locale.getDefault()
            ));
        }
        postRepository.delete(post);
    }

    public PostNotifyResDto writeNoticePost(PostReqDto reqDto, User user) {

        User curUser = userService.findById(user.getId());

        Post post = postRepository.save(reqDto.toNoticePostEntity(curUser));
        PostNotifyResDto postReqDto = new PostNotifyResDto(post);

        return postReqDto;
    }

    @Transactional
    public PostUpdateResDto updatePostByAdmin(long postId, PostReqDto reqDto) {

        Post post = findById(postId);
        post.update(reqDto);

        return new PostUpdateResDto(post);
    }

    public void deletePostByAdmin(long postId) {

        Post post = findById(postId);
        postRepository.delete(post);
    }

    @Transactional
    public PostUpdateResDto pinPost(long postId, boolean isPinned) {

        Post post = findById(postId);
        post.setPin(isPinned);

        return new PostUpdateResDto(post);
    }

    public Page<PostUpdateResDto> getFollowPost(int page, boolean isAsc, User user) {

        List<Follow> followeeList = followService.getFolloweeList(user.getUsername());
        List<Post> postList = new ArrayList<>();
        for (Follow follow : followeeList) {
            if (follow.isFollow()) {
                postList.addAll(postRepository.findAllByUser(follow.getFolloweeUser()));
            }
        }

        List<Post> sortedPostList = isAsc ?
                postList.stream().sorted(Comparator.comparing(Post::getCreatedAt).reversed()).toList() :
                postList.stream().sorted(Comparator.comparing(Post::getCreatedAt)).toList();

        Pageable pageable = Utils.getPageable(page, isAsc);

        Page<Post> pagingList = Utils.getCustomPage(pageable, sortedPostList);
        return pagingList.map(PostUpdateResDto::new);
    }

    public PostCommentResDto getPost(long postId) {

        Post curPost = findById(postId);

        List<Comment> comments = commentRepository.findAllByPostId(postId);

        List<CommentResDto> commentResDtos = new ArrayList<>();
        for (Comment comment : comments) {
            commentResDtos.add(new CommentResDto(comment.getId(), comment.getUser().getUsername(), comment.getComment(), comment.getCreatedAt()));
        }

        return new PostCommentResDto(commentResDtos, curPost);
    }

    public Post findById(long id) {
        return postRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException(messageSource.getMessage(
                        "no.exist.post",
                        null,
                        "Wrong Request",
                        Locale.getDefault()
                )));
    }
}
