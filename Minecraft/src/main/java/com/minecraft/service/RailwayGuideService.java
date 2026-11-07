package com.minecraft.service;

import com.minecraft.entity.RailwayGuide;

import java.util.List;

/**
 * 出行指南
 */
public interface RailwayGuideService {

    List<RailwayGuide> list(String category, String keyword);

    RailwayGuide detail(Long id);

    List<RailwayGuide> related(Long id, Integer limit);
}
