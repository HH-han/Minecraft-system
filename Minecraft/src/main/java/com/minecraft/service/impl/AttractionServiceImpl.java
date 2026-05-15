package com.minecraft.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minecraft.dto.request.PageRequest;
import com.minecraft.dto.response.PageResponse;
import com.minecraft.entity.Attraction;
import com.minecraft.entity.AttractionFacility;
import com.minecraft.entity.AttractionTicket;
import com.minecraft.mapper.AttractionFacilityMapper;
import com.minecraft.mapper.AttractionMapper;
import com.minecraft.mapper.AttractionTicketMapper;
import com.minecraft.service.AttractionService;
import com.minecraft.service.LikeService;
import com.minecraft.utils.ImageUtils;
import com.minecraft.vo.AttractionDetailVO;
import com.minecraft.vo.AttractionListVO;
import com.minecraft.vo.AttractionTicketVO;
import com.minecraft.vo.AttractionVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AttractionServiceImpl extends ServiceImpl<AttractionMapper, Attraction> implements AttractionService {

    @Autowired
    private LikeService likeService;
    @Autowired
    private ImageUtils imageUtils;
    @Autowired
    private AttractionTicketMapper attractionTicketMapper;
    @Autowired
    private AttractionFacilityMapper attractionFacilityMapper;
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public PageResponse<AttractionListVO> getAttractionList(PageRequest request) {
        Page<Attraction> page = new Page<>(request.getPageNum(), request.getPageSize());
        LambdaQueryWrapper<Attraction> wrapper = new LambdaQueryWrapper<>();

        if (request.getKeyword() != null) {
            wrapper.like(Attraction::getName, request.getKeyword())
                    .or()
                    .like(Attraction::getCity, request.getKeyword());
        }

        if (request.getSortBy() != null) {
            if ("likeCount".equals(request.getSortBy())) {
                wrapper.orderByDesc(Attraction::getLikeCount);
            } else if ("collectCount".equals(request.getSortBy())) {
                wrapper.orderByDesc(Attraction::getCollectCount);
            } else {
                wrapper.orderByDesc(Attraction::getCreateTime);
            }
        } else {
            wrapper.orderByDesc(Attraction::getCreateTime);
        }

        Page<Attraction> result = page(page, wrapper);
        
        List<Attraction> attractions = result.getRecords();
        if (attractions.isEmpty()) {
            return new PageResponse<>(new ArrayList<>(), result.getTotal(), request.getPageNum(), request.getPageSize());
        }
        
        List<Long> attractionIds = attractions.stream().map(Attraction::getId).collect(Collectors.toList());
        
        LambdaQueryWrapper<AttractionTicket> ticketWrapper = new LambdaQueryWrapper<>();
        ticketWrapper.in(AttractionTicket::getAttractionId, attractionIds);
        List<AttractionTicket> allTickets = attractionTicketMapper.selectList(ticketWrapper);
        Map<Long, List<AttractionTicket>> ticketsMap = allTickets.stream()
                .collect(Collectors.groupingBy(AttractionTicket::getAttractionId));
        
        LambdaQueryWrapper<AttractionFacility> facilityWrapper = new LambdaQueryWrapper<>();
        facilityWrapper.in(AttractionFacility::getAttractionId, attractionIds);
        List<AttractionFacility> allFacilities = attractionFacilityMapper.selectList(facilityWrapper);
        Map<Long, List<AttractionFacility>> facilitiesMap = allFacilities.stream()
                .collect(Collectors.groupingBy(AttractionFacility::getAttractionId));
        
        List<AttractionListVO> voList = attractions.stream().map(attraction -> {
            AttractionListVO vo = new AttractionListVO();
            BeanUtils.copyProperties(attraction, vo);
            
            if (attraction.getImages() != null && !attraction.getImages().isEmpty()) {
                try {
                    vo.setImages(objectMapper.readValue(attraction.getImages(), new TypeReference<List<String>>() {}));
                } catch (JsonProcessingException e) {
                    vo.setImages(List.of(attraction.getImages().split(",")));
                }
            } else {
                vo.setImages(new ArrayList<>());
            }
            
            if (attraction.getTags() != null) {
                try {
                    vo.setTags(objectMapper.readValue(attraction.getTags(), new TypeReference<List<String>>() {}));
                } catch (JsonProcessingException e) {
                    vo.setTags(new ArrayList<>());
                }
            } else {
                vo.setTags(new ArrayList<>());
            }
            
            List<AttractionTicket> attractionTickets = ticketsMap.getOrDefault(attraction.getId(), Collections.emptyList());
            List<AttractionTicketVO> ticketVOList = attractionTickets.stream().map(ticket -> {
                AttractionTicketVO ticketVO = new AttractionTicketVO();
                BeanUtils.copyProperties(ticket, ticketVO);
                if (ticket.getRules() != null) {
                    try {
                        ticketVO.setRules(objectMapper.readValue(ticket.getRules(), new TypeReference<List<String>>() {}));
                    } catch (JsonProcessingException e) {
                        ticketVO.setRules(new ArrayList<>());
                    }
                } else {
                    ticketVO.setRules(new ArrayList<>());
                }
                return ticketVO;
            }).collect(Collectors.toList());
            vo.setTickets(ticketVOList);
            
            List<AttractionFacility> attractionFacilities = facilitiesMap.getOrDefault(attraction.getId(), Collections.emptyList());
            List<String> facilityNames = attractionFacilities.stream()
                    .map(AttractionFacility::getFacilityName)
                    .collect(Collectors.toList());
            vo.setFacilities(facilityNames);
            
            return vo;
        }).collect(Collectors.toList());

        return new PageResponse<>(voList, result.getTotal(), request.getPageNum(), request.getPageSize());
    }

    @Override
    public AttractionDetailVO getAttractionDetail(Long id, Long userId) {
        Attraction attraction = getById(id);
        if (attraction == null) {
            return null;
        }
        
        AttractionDetailVO vo = new AttractionDetailVO();
        BeanUtils.copyProperties(attraction, vo);

        if (attraction.getImages() != null && !attraction.getImages().isEmpty()) {
            try {
                vo.setImages(objectMapper.readValue(attraction.getImages(), new TypeReference<List<String>>() {}));
            } catch (JsonProcessingException e) {
                vo.setImages(List.of(attraction.getImages().split(",")));
            }
        } else {
            vo.setImages(new ArrayList<>());
        }
        
        if (attraction.getTags() != null) {
            try {
                vo.setTags(objectMapper.readValue(attraction.getTags(), new TypeReference<List<String>>() {}));
            } catch (JsonProcessingException e) {
                vo.setTags(new ArrayList<>());
            }
        } else {
            vo.setTags(new ArrayList<>());
        }

        if (userId != null) {
            vo.setIsLiked(likeService.isLiked("attraction", id, userId));
        }
        
        LambdaQueryWrapper<AttractionTicket> ticketWrapper = new LambdaQueryWrapper<>();
        ticketWrapper.eq(AttractionTicket::getAttractionId, id);
        List<AttractionTicket> tickets = attractionTicketMapper.selectList(ticketWrapper);
        
        List<AttractionTicketVO> ticketVOList = tickets.stream().map(ticket -> {
            AttractionTicketVO ticketVO = new AttractionTicketVO();
            BeanUtils.copyProperties(ticket, ticketVO);
            if (ticket.getRules() != null) {
                try {
                    ticketVO.setRules(objectMapper.readValue(ticket.getRules(), new TypeReference<List<String>>() {}));
                } catch (JsonProcessingException e) {
                    ticketVO.setRules(new ArrayList<>());
                }
            } else {
                ticketVO.setRules(new ArrayList<>());
            }
            return ticketVO;
        }).collect(Collectors.toList());
        vo.setTickets(ticketVOList);
        
        LambdaQueryWrapper<AttractionFacility> facilityWrapper = new LambdaQueryWrapper<>();
        facilityWrapper.eq(AttractionFacility::getAttractionId, id);
        List<AttractionFacility> facilities = attractionFacilityMapper.selectList(facilityWrapper);
        List<String> facilityNames = facilities.stream()
                .map(AttractionFacility::getFacilityName)
                .collect(Collectors.toList());
        vo.setFacilities(facilityNames);

        return vo;
    }

    @Override
    public List<AttractionVO> getRecommendAttractions(String city, String season, Integer limit) {
        LambdaQueryWrapper<Attraction> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Attraction::getStatus, 1);

        if (city != null) {
            wrapper.eq(Attraction::getCity, city);
        }

        if (season != null) {
            wrapper.like(Attraction::getSeason, season);
        }

        wrapper.orderByDesc(Attraction::getLikeCount)
                .last("LIMIT " + (limit != null ? limit : 10));

        List<Attraction> list = list(wrapper);
        return list.stream().map(item -> {
            AttractionVO vo = new AttractionVO();
            BeanUtils.copyProperties(item, vo);
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public List<AttractionVO> getHotAttractions(Integer limit) {
        LambdaQueryWrapper<Attraction> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Attraction::getStatus, 1)
                .orderByDesc(Attraction::getLikeCount)
                .last("LIMIT " + (limit != null ? limit : 10));

        List<Attraction> list = list(wrapper);
        return list.stream().map(item -> {
            AttractionVO vo = new AttractionVO();
            BeanUtils.copyProperties(item, vo);
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public void addAttraction(AttractionVO attractionVO) {
        Attraction attraction = new Attraction();
        BeanUtils.copyProperties(attractionVO, attraction);
        save(attraction);
    }

    @Override
    public void updateAttraction(AttractionVO attractionVO) {
        Attraction attraction = new Attraction();
        BeanUtils.copyProperties(attractionVO, attraction);
        updateById(attraction);
    }

    @Override
    public void deleteAttraction(Long id) {
        removeById(id);
    }
    
    @Override
    public boolean save(Attraction attraction) {
        try {
            if (attraction.getCoverImage() != null && attraction.getCoverImage().startsWith("data:image")) {
                String processedCoverImage = imageUtils.processBase64Image(attraction.getCoverImage());
                attraction.setCoverImage(processedCoverImage);
            }
            
            if (attraction.getImages() != null && attraction.getImages().startsWith("[")) {
                try {
                    String[] imageArray = attraction.getImages().replace("[", "").replace("]", "").replaceAll("\\\"", "").split(",");
                    StringBuilder processedImages = new StringBuilder();
                    
                    for (String image : imageArray) {
                        if (image.trim().startsWith("data:image")) {
                            String processedImage = imageUtils.processBase64Image(image.trim());
                            processedImages.append(processedImage).append(",");
                        } else {
                            processedImages.append(image.trim()).append(",");
                        }
                    }
                    
                    if (processedImages.length() > 0) {
                        processedImages.setLength(processedImages.length() - 1);
                    }
                    
                    attraction.setImages(processedImages.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            
            return super.save(attraction);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean updateById(Attraction attraction) {
        try {
            if (attraction.getCoverImage() != null && attraction.getCoverImage().startsWith("data:image")) {
                String processedCoverImage = imageUtils.processBase64Image(attraction.getCoverImage());
                attraction.setCoverImage(processedCoverImage);
            }
            
            if (attraction.getImages() != null && attraction.getImages().startsWith("[")) {
                try {
                    String[] imageArray = attraction.getImages().replace("[", "").replace("]", "").replaceAll("\\\"", "").split(",");
                    StringBuilder processedImages = new StringBuilder();
                    
                    for (String image : imageArray) {
                        if (image.trim().startsWith("data:image")) {
                            String processedImage = imageUtils.processBase64Image(image.trim());
                            processedImages.append(processedImage).append(",");
                        } else {
                            processedImages.append(image.trim()).append(",");
                        }
                    }
                    
                    if (processedImages.length() > 0) {
                        processedImages.setLength(processedImages.length() - 1);
                    }
                    
                    attraction.setImages(processedImages.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            
            return super.updateById(attraction);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
