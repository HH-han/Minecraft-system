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
        ticketWrapper.in(AttractionTicket::getAttractionId, attractionIds)
                .eq(AttractionTicket::getStatus, 1);
        List<AttractionTicket> allTickets = attractionTicketMapper.selectList(ticketWrapper);
        Map<Long, List<AttractionTicket>> ticketsMap = allTickets.stream()
                .collect(Collectors.groupingBy(AttractionTicket::getAttractionId));
        
        LambdaQueryWrapper<AttractionFacility> facilityWrapper = new LambdaQueryWrapper<>();
        facilityWrapper.in(AttractionFacility::getAttractionId, attractionIds)
                .eq(AttractionFacility::getStatus, 1);
        List<AttractionFacility> allFacilities = attractionFacilityMapper.selectList(facilityWrapper);
        Map<Long, List<AttractionFacility>> facilitiesMap = allFacilities.stream()
                .collect(Collectors.groupingBy(AttractionFacility::getAttractionId));
        
        List<AttractionListVO> voList = attractions.stream().map(attraction -> {
            AttractionListVO vo = new AttractionListVO();
            BeanUtils.copyProperties(attraction, vo);
            
            // 设置默认值
            if (vo.getLikeCount() == null) vo.setLikeCount(0);
            if (vo.getCollectCount() == null) vo.setCollectCount(0);
            if (vo.getCommentCount() == null) vo.setCommentCount(0);
            
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
        
        // 设置默认值
        if (vo.getLikeCount() == null) vo.setLikeCount(0);
        if (vo.getCollectCount() == null) vo.setCollectCount(0);
        if (vo.getCommentCount() == null) vo.setCommentCount(0);

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
        ticketWrapper.eq(AttractionTicket::getAttractionId, id)
                .eq(AttractionTicket::getStatus, 1);
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
        facilityWrapper.eq(AttractionFacility::getAttractionId, id)
                .eq(AttractionFacility::getStatus, 1);
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
        
        // 处理 coverImage Base64 图片
        if (attractionVO.getCoverImage() != null && attractionVO.getCoverImage().startsWith("data:image")) {
            attraction.setCoverImage(imageUtils.processBase64ImageSafe(attractionVO.getCoverImage()));
        }
        
        // 处理 images 字段转换（含 Base64 图片）
        if (attractionVO.getImages() != null && !attractionVO.getImages().isEmpty()) {
            List<String> processedImages = new ArrayList<>();
            for (String img : attractionVO.getImages()) {
                if (img != null && img.startsWith("data:image")) {
                    String url = imageUtils.processBase64ImageSafe(img);
                    if (url != null) {
                        processedImages.add(url);
                    }
                } else {
                    processedImages.add(img);
                }
            }
            try {
                attraction.setImages(objectMapper.writeValueAsString(processedImages));
            } catch (JsonProcessingException e) {
                attraction.setImages(String.join(",", processedImages));
            }
        }
        
        // 处理 tags 字段转换
        if (attractionVO.getTags() != null && !attractionVO.getTags().isEmpty()) {
            try {
                attraction.setTags(objectMapper.writeValueAsString(attractionVO.getTags()));
            } catch (JsonProcessingException e) {
                attraction.setTags(String.join(",", attractionVO.getTags()));
            }
        }
        
        save(attraction);
        
        if (attractionVO.getTickets() != null && !attractionVO.getTickets().isEmpty()) {
            try {
                for (AttractionTicketVO ticketVO : attractionVO.getTickets()) {
                    if (ticketVO.getName() == null || ticketVO.getName().trim().isEmpty()) {
                        continue;
                    }
                    
                    AttractionTicket ticket = new AttractionTicket();
                    ticket.setAttractionId(attraction.getId());
                    ticket.setName(ticketVO.getName());
                    ticket.setDescription(ticketVO.getDescription());
                    if (ticketVO.getPrice() != null) {
                        ticket.setPrice(ticketVO.getPrice());
                    }
                    if (ticketVO.getRules() != null) {
                        ticket.setRules(objectMapper.writeValueAsString(ticketVO.getRules()));
                    }
                    ticket.setStatus(1);
                    attractionTicketMapper.insert(ticket);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        if (attractionVO.getFacilities() != null && !attractionVO.getFacilities().isEmpty()) {
            for (String facilityName : attractionVO.getFacilities()) {
                AttractionFacility facility = new AttractionFacility();
                facility.setAttractionId(attraction.getId());
                facility.setFacilityName(facilityName);
                facility.setStatus(1);
                attractionFacilityMapper.insert(facility);
            }
        }
    }

    @Override
    public void updateAttraction(AttractionVO attractionVO) {
        Attraction attraction = new Attraction();
        BeanUtils.copyProperties(attractionVO, attraction);
        
        // 处理 coverImage Base64 图片
        if (attractionVO.getCoverImage() != null && attractionVO.getCoverImage().startsWith("data:image")) {
            attraction.setCoverImage(imageUtils.processBase64ImageSafe(attractionVO.getCoverImage()));
        }
        
        // 处理 images 字段转换（含 Base64 图片）
        if (attractionVO.getImages() != null && !attractionVO.getImages().isEmpty()) {
            List<String> processedImages = new ArrayList<>();
            for (String img : attractionVO.getImages()) {
                if (img != null && img.startsWith("data:image")) {
                    String url = imageUtils.processBase64ImageSafe(img);
                    if (url != null) {
                        processedImages.add(url);
                    }
                } else {
                    processedImages.add(img);
                }
            }
            try {
                attraction.setImages(objectMapper.writeValueAsString(processedImages));
            } catch (JsonProcessingException e) {
                attraction.setImages(String.join(",", processedImages));
            }
        }
        
        // 处理 tags 字段转换
        if (attractionVO.getTags() != null && !attractionVO.getTags().isEmpty()) {
            try {
                attraction.setTags(objectMapper.writeValueAsString(attractionVO.getTags()));
            } catch (JsonProcessingException e) {
                attraction.setTags(String.join(",", attractionVO.getTags()));
            }
        }
        
        updateById(attraction);
        
        LambdaQueryWrapper<AttractionTicket> ticketWrapper = new LambdaQueryWrapper<>();
        ticketWrapper.eq(AttractionTicket::getAttractionId, attraction.getId());
        attractionTicketMapper.delete(ticketWrapper);
        
        LambdaQueryWrapper<AttractionFacility> facilityWrapper = new LambdaQueryWrapper<>();
        facilityWrapper.eq(AttractionFacility::getAttractionId, attraction.getId());
        attractionFacilityMapper.delete(facilityWrapper);
        
        if (attractionVO.getTickets() != null && !attractionVO.getTickets().isEmpty()) {
            try {
                for (AttractionTicketVO ticketVO : attractionVO.getTickets()) {
                    if (ticketVO.getName() == null || ticketVO.getName().trim().isEmpty()) {
                        continue;
                    }
                    
                    AttractionTicket ticket = new AttractionTicket();
                    ticket.setAttractionId(attraction.getId());
                    ticket.setName(ticketVO.getName());
                    ticket.setDescription(ticketVO.getDescription());
                    if (ticketVO.getPrice() != null) {
                        ticket.setPrice(ticketVO.getPrice());
                    }
                    if (ticketVO.getRules() != null) {
                        ticket.setRules(objectMapper.writeValueAsString(ticketVO.getRules()));
                    }
                    ticket.setStatus(1);
                    attractionTicketMapper.insert(ticket);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        if (attractionVO.getFacilities() != null && !attractionVO.getFacilities().isEmpty()) {
            for (String facilityName : attractionVO.getFacilities()) {
                AttractionFacility facility = new AttractionFacility();
                facility.setAttractionId(attraction.getId());
                facility.setFacilityName(facilityName);
                facility.setStatus(1);
                attractionFacilityMapper.insert(facility);
            }
        }
    }

    @Override
    public void deleteAttraction(Long id) {
        removeById(id);
    }
}
