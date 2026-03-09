package com.minecraft.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.minecraft.dto.request.PageRequest;
import com.minecraft.dto.response.PageResponse;
import com.minecraft.entity.Attraction;
import com.minecraft.vo.AttractionVO;

import java.util.List;

public interface AttractionService extends IService<Attraction> {
    PageResponse<AttractionVO> getAttractionList(PageRequest request);
    AttractionVO getAttractionDetail(Long id, Long userId);
    void addAttraction(AttractionVO attractionVO);
    void updateAttraction(AttractionVO attractionVO);
    void deleteAttraction(Long id);
    List<AttractionVO> getRecommendAttractions(String city, String season, Integer limit);
    List<AttractionVO> getHotAttractions(Integer limit);
}
