package com.minecraft.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.minecraft.entity.Reviews;

public interface ReviewsService extends IService<Reviews> {
    Reviews getReviewById(Integer id);
    void addReview(Reviews review);
    void updateReview(Reviews review);
    void deleteReview(Integer id);
    void likeReview(Integer reviewId);
    void unlikeReview(Integer reviewId);
}
