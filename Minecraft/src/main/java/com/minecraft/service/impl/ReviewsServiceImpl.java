package com.minecraft.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minecraft.entity.Reviews;
import com.minecraft.mapper.ReviewsMapper;
import com.minecraft.service.ReviewsService;
import org.springframework.stereotype.Service;

@Service
public class ReviewsServiceImpl extends ServiceImpl<ReviewsMapper, Reviews> implements ReviewsService {

    @Override
    public Reviews getReviewById(Integer id) {
        return getById(id);
    }

    @Override
    public void addReview(Reviews review) {
        save(review);
    }

    @Override
    public void updateReview(Reviews review) {
        updateById(review);
    }

    @Override
    public void deleteReview(Integer id) {
        removeById(id);
    }

    @Override
    public void likeReview(Integer reviewId) {
        Reviews review = getById(reviewId);
        if (review != null) {
            review.setLikesCount(review.getLikesCount() + 1);
            updateById(review);
        }
    }

    @Override
    public void unlikeReview(Integer reviewId) {
        Reviews review = getById(reviewId);
        if (review != null && review.getLikesCount() > 0) {
            review.setLikesCount(review.getLikesCount() - 1);
            updateById(review);
        }
    }
}
