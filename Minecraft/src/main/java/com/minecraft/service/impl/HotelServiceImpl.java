package com.minecraft.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minecraft.dto.request.PageRequest;
import com.minecraft.dto.response.PageResponse;
import com.minecraft.entity.Hotel;
import com.minecraft.entity.HotelFacility;
import com.minecraft.entity.HotelRoom;
import com.minecraft.mapper.HotelFacilityMapper;
import com.minecraft.mapper.HotelMapper;
import com.minecraft.mapper.HotelRoomMapper;
import com.minecraft.service.HotelService;
import com.minecraft.utils.ImageUtils;
import com.minecraft.vo.AttractionVO;
import com.minecraft.vo.HotelDetailVO;
import com.minecraft.vo.HotelListVO;
import com.minecraft.vo.HotelRoomVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class HotelServiceImpl extends ServiceImpl<HotelMapper, Hotel> implements HotelService {
    @Autowired
    private ImageUtils imageUtils;
    
    @Autowired
    private HotelRoomMapper hotelRoomMapper;
    
    @Autowired
    private HotelFacilityMapper hotelFacilityMapper;
    
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public PageResponse<HotelListVO> getHotelList(PageRequest request) {
        Page<Hotel> page = new Page<>(request.getPageNum(), request.getPageSize());
        LambdaQueryWrapper<Hotel> wrapper = new LambdaQueryWrapper<>();

        if (request.getKeyword() != null) {
            wrapper.like(Hotel::getName, request.getKeyword())
                    .or()
                    .like(Hotel::getCity, request.getKeyword());
        }

        if (request.getSortBy() != null) {
            if ("rating".equals(request.getSortBy())) {
                wrapper.orderByDesc(Hotel::getRating);
            } else if ("price".equals(request.getSortBy())) {
                wrapper.orderByAsc(Hotel::getPrice);
            }
        } else {
            wrapper.orderByDesc(Hotel::getCreateTime);
        }

        Page<Hotel> result = page(page, wrapper);
        
        List<Hotel> hotels = result.getRecords();
        if (hotels.isEmpty()) {
            return new PageResponse<>(new ArrayList<>(), result.getTotal(), request.getPageNum(), request.getPageSize());
        }
        
        List<Long> hotelIds = hotels.stream().map(Hotel::getId).collect(Collectors.toList());
        
        LambdaQueryWrapper<HotelRoom> roomWrapper = new LambdaQueryWrapper<>();
        roomWrapper.in(HotelRoom::getHotelId, hotelIds);
        List<HotelRoom> allRooms = hotelRoomMapper.selectList(roomWrapper);
        Map<Long, List<HotelRoom>> roomsMap = allRooms.stream()
                .collect(Collectors.groupingBy(HotelRoom::getHotelId));
        
        LambdaQueryWrapper<HotelFacility> facilityWrapper = new LambdaQueryWrapper<>();
        facilityWrapper.in(HotelFacility::getHotelId, hotelIds);
        List<HotelFacility> allFacilities = hotelFacilityMapper.selectList(facilityWrapper);
        Map<Long, List<HotelFacility>> facilitiesMap = allFacilities.stream()
                .collect(Collectors.groupingBy(HotelFacility::getHotelId));
        
        List<HotelListVO> voList = hotels.stream().map(hotel -> {
            HotelListVO vo = new HotelListVO();
            BeanUtils.copyProperties(hotel, vo);
            
            if (hotel.getImages() != null && !hotel.getImages().isEmpty()) {
                try {
                    vo.setImages(objectMapper.readValue(hotel.getImages(), new TypeReference<List<String>>() {}));
                } catch (JsonProcessingException e) {
                    vo.setImages(List.of(hotel.getImages().split(",")));
                }
            } else {
                vo.setImages(new ArrayList<>());
            }
            
            List<HotelRoom> hotelRooms = roomsMap.getOrDefault(hotel.getId(), Collections.emptyList());
            List<HotelRoomVO> roomVOList = hotelRooms.stream().map(room -> {
                HotelRoomVO roomVO = new HotelRoomVO();
                BeanUtils.copyProperties(room, roomVO);
                if (room.getFacilities() != null) {
                    try {
                        roomVO.setFacilities(objectMapper.readValue(room.getFacilities(), new TypeReference<List<String>>() {}));
                    } catch (JsonProcessingException e) {
                        roomVO.setFacilities(new ArrayList<>());
                    }
                } else {
                    roomVO.setFacilities(new ArrayList<>());
                }
                return roomVO;
            }).collect(Collectors.toList());
            vo.setRooms(roomVOList);
            
            List<HotelFacility> hotelFacilities = facilitiesMap.getOrDefault(hotel.getId(), Collections.emptyList());
            List<String> facilityNames = hotelFacilities.stream()
                    .map(HotelFacility::getFacilityName)
                    .collect(Collectors.toList());
            vo.setFacilities(facilityNames);
            
            return vo;
        }).collect(Collectors.toList());

        return new PageResponse<>(voList, result.getTotal(), request.getPageNum(), request.getPageSize());
    }

    @Override
    public HotelDetailVO getHotelDetail(Long id) {
        Hotel hotel = getById(id);
        if (hotel == null) {
            return null;
        }
        
        HotelDetailVO vo = new HotelDetailVO();
        BeanUtils.copyProperties(hotel, vo);
        
        if (hotel.getImages() != null && !hotel.getImages().isEmpty()) {
            try {
                vo.setImages(objectMapper.readValue(hotel.getImages(), new TypeReference<List<String>>() {}));
            } catch (JsonProcessingException e) {
                vo.setImages(List.of(hotel.getImages().split(",")));
            }
        } else {
            vo.setImages(new ArrayList<>());
        }
        
        LambdaQueryWrapper<HotelRoom> roomWrapper = new LambdaQueryWrapper<>();
        roomWrapper.eq(HotelRoom::getHotelId, id);
        List<HotelRoom> rooms = hotelRoomMapper.selectList(roomWrapper);
        
        List<HotelRoomVO> roomVOList = rooms.stream().map(room -> {
            HotelRoomVO roomVO = new HotelRoomVO();
            BeanUtils.copyProperties(room, roomVO);
            if (room.getFacilities() != null) {
                try {
                    roomVO.setFacilities(objectMapper.readValue(room.getFacilities(), new TypeReference<List<String>>() {}));
                } catch (JsonProcessingException e) {
                    roomVO.setFacilities(new ArrayList<>());
                }
            } else {
                roomVO.setFacilities(new ArrayList<>());
            }
            return roomVO;
        }).collect(Collectors.toList());
        vo.setRooms(roomVOList);
        
        LambdaQueryWrapper<HotelFacility> facilityWrapper = new LambdaQueryWrapper<>();
        facilityWrapper.eq(HotelFacility::getHotelId, id);
        List<HotelFacility> facilities = hotelFacilityMapper.selectList(facilityWrapper);
        List<String> facilityNames = facilities.stream()
                .map(HotelFacility::getFacilityName)
                .collect(Collectors.toList());
        vo.setFacilities(facilityNames);
        
        return vo;
    }

    @Override
    public List<Hotel> getRecommendHotels(String city, Integer limit) {
        LambdaQueryWrapper<Hotel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Hotel::getStatus, 1);

        if (city != null) {
            wrapper.eq(Hotel::getCity, city);
        }

        wrapper.orderByDesc(Hotel::getRating)
                .last("LIMIT " + (limit != null ? limit : 10));

        return list(wrapper);
    }

    @Override
    public void addHotel(Hotel hotel) {
        save(hotel);
    }

    @Override
    public void updateHotel(Hotel hotel) {
        updateById(hotel);
    }

    @Override
    public void deleteHotel(Long id) {
        removeById(id);
    }
    
    @Override
    public boolean save(Hotel hotel) {
        try {
            if (hotel.getCoverImage() != null && hotel.getCoverImage().startsWith("data:image")) {
                String processedCoverImage = imageUtils.processBase64Image(hotel.getCoverImage());
                hotel.setCoverImage(processedCoverImage);
            }
            
            if (hotel.getImages() != null && hotel.getImages().startsWith("[")) {
                try {
                    String[] imageArray = hotel.getImages().replace("[", "").replace("]", "").replaceAll("\\\"", "").split(",");
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
                    
                    hotel.setImages(processedImages.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            
            return super.save(hotel);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean updateById(Hotel hotel) {
        try {
            if (hotel.getCoverImage() != null && hotel.getCoverImage().startsWith("data:image")) {
                String processedCoverImage = imageUtils.processBase64Image(hotel.getCoverImage());
                hotel.setCoverImage(processedCoverImage);
            }
            
            if (hotel.getImages() != null && hotel.getImages().startsWith("[")) {
                try {
                    String[] imageArray = hotel.getImages().replace("[", "").replace("]", "").replaceAll("\\\"", "").split(",");
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
                    
                    hotel.setImages(processedImages.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            
            return super.updateById(hotel);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
