package com.sparta.storyindays.repository;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.storyindays.entity.Post;
import com.sparta.storyindays.entity.QPost;
import com.sparta.storyindays.entity.User;
import com.sparta.storyindays.enums.post.PostType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Post> findAllByPostType(PostType postType) {
        QPost post = QPost.post;

        return jpaQueryFactory.select(post)
            .from(post)
            .where(post.postType.eq(postType))
            .fetch();
    }

    @Override
    public List<Post> findAllByPostTypeAndIsPinned(PostType postType, boolean isPinned) {
        QPost post = QPost.post;
        return jpaQueryFactory.select(post)
            .from(post)
            .where(post.postType.eq(postType))
            .where(post.isPinned.eq(isPinned))
            .fetch();
    }

    @Override
    public List<Post> findAllByPostTypeAndIsPinned(PostType postType, boolean isPinned, Pageable pageable) {
        QPost post = QPost.post;
        OrderSpecifier<?> orderSpecifier = getOrderSpecifier(post, pageable);

        return jpaQueryFactory.select(post)
            .from(post)
            .where(post.postType.eq(postType))
            .where(post.isPinned.eq(isPinned))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .orderBy(orderSpecifier)
            .fetch();
    }

    @Override
    public List<Post> findAllByPostTypeAndIsPinnedAndUser(PostType postType, boolean b, User user, Pageable pageable) {
        QPost post = QPost.post;
        OrderSpecifier<?> orderSpecifier = getOrderSpecifier(post, pageable);
        return jpaQueryFactory.select(post)
            .from(post)
            .where(post.postType.eq(postType))
            .where(post.isPinned.eq(b))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .orderBy(orderSpecifier)
            .fetch();
    }

    @Override
    public List<Post> findAllByUser(User user) {

        QPost post = QPost.post;
        return jpaQueryFactory.select(post)
            .from(post)
            .where(post.user.eq(user))
            .fetch();
    }

    private OrderSpecifier<?> getOrderSpecifier(QPost post ,Pageable pageable){
        Sort sort = pageable.getSort();
        Sort.Order createdAtorder = sort.getOrderFor("createdAt");
        assert createdAtorder != null;
        OrderSpecifier<?> orderSpecifier;
        if(createdAtorder.getDirection().isAscending()) {
            orderSpecifier = new OrderSpecifier<>(Order.ASC, post.createdAt);
        }
        else {
            orderSpecifier = new OrderSpecifier<>(Order.DESC, post.createdAt);
        }

        return orderSpecifier;
    }
}
