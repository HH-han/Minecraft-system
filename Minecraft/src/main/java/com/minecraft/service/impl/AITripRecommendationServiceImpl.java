package com.minecraft.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minecraft.dto.request.AITripGenerateDTO;
import com.minecraft.entity.AITripRecommendation;
import com.minecraft.entity.GroupTripPlan;
import com.minecraft.exception.BusinessException;
import com.minecraft.mapper.AITripRecommendationMapper;
import com.minecraft.service.AITripRecommendationService;
import com.minecraft.service.GroupMemberService;
import com.minecraft.service.GroupTripPlanService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class AITripRecommendationServiceImpl extends ServiceImpl<AITripRecommendationMapper, AITripRecommendation> implements AITripRecommendationService {

    @Autowired
    private GroupMemberService groupMemberService;

    @Autowired
    private GroupTripPlanService groupTripPlanService;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    @Transactional
    public AITripRecommendation generateTrip(AITripGenerateDTO dto, Long userId) {
        if (dto.getGroupId() != null && !groupMemberService.isMember(dto.getGroupId(), userId)) {
            throw new BusinessException(403, "不是群成员");
        }

        String recommendationJson = generateMockRecommendation(dto.getDestination(), dto.getDays(), dto.getPreferences());

        AITripRecommendation recommendation = new AITripRecommendation();
        recommendation.setGroupId(dto.getGroupId());
        recommendation.setUserId(userId);
        recommendation.setDestination(dto.getDestination());
        recommendation.setDays(dto.getDays());
        recommendation.setPreferences(dto.getPreferences());
        recommendation.setRecommendationJson(recommendationJson);
        recommendation.setStatus(1);
        save(recommendation);

        return recommendation;
    }

    @Override
    @Transactional
    public void adoptTrip(Long recommendationId, Long userId) {
        AITripRecommendation recommendation = getById(recommendationId);
        if (recommendation == null) {
            throw new BusinessException(404, "推荐记录不存在");
        }
        if (!recommendation.getUserId().equals(userId)) {
            throw new BusinessException(403, "没有权限");
        }

        try {
            JsonNode root = objectMapper.readTree(recommendation.getRecommendationJson());
            JsonNode days = root.get("days");

            LocalDate startDate = LocalDate.now();
            for (int i = 0; i < days.size(); i++) {
                JsonNode dayNode = days.get(i);
                int dayIndex = i + 1;
                String title = dayNode.has("title") ? dayNode.get("title").asText() : "";
                
                if (dayNode.has("items")) {
                    JsonNode items = dayNode.get("items");
                    for (JsonNode item : items) {
                        GroupTripPlan plan = new GroupTripPlan();
                        plan.setGroupId(recommendation.getGroupId());
                        plan.setDayIndex(dayIndex);
                        plan.setDate(startDate.plusDays(i));
                        plan.setTimeSlot(item.has("timeSlot") ? item.get("timeSlot").asText() : "");
                        plan.setActivity(item.has("activity") ? item.get("activity").asText() : title);
                        plan.setLocation(item.has("location") ? item.get("location").asText() : "");
                        plan.setTransport(item.has("transport") ? item.get("transport").asText() : "");
                        plan.setNotes(item.has("notes") ? item.get("notes").asText() : "");
                        plan.setCreatedBy(userId);
                        groupTripPlanService.save(plan);
                    }
                }
            }

            recommendation.setStatus(3);
            updateById(recommendation);
        } catch (Exception e) {
            throw new BusinessException(500, "解析推荐数据失败");
        }
    }

    @Override
    public List<AITripRecommendation> getHistory(Long userId) {
        return baseMapper.selectByUserId(userId);
    }

    private String generateMockRecommendation(String destination, Integer days, String preferences) {
        try {
            StringBuilder json = new StringBuilder();
            json.append("{\"destination\":\"").append(destination).append("\",");
            json.append("\"days\":[");
            
            for (int i = 1; i <= days; i++) {
                if (i > 1) json.append(",");
                json.append("{\"day\":").append(i).append(",");
                json.append("\"title\":\"Day ").append(i).append("：").append(destination).append("游览\",");
                json.append("\"items\":[");
                
                json.append("{\"timeSlot\":\"上午\",\"activity\":\"抵达并入住酒店\",\"location\":\"市中心酒店\",\"transport\":\"飞机\",\"notes\":\"办理入住手续\"}");
                json.append(",{\"timeSlot\":\"下午\",\"activity\":\"").append(destination).append("市区游览\",\"location\":\"市区景点\",\"transport\":\"步行\",\"notes\":\"自由活动\"}");
                json.append(",{\"timeSlot\":\"晚上\",\"activity\":\"品尝当地美食\",\"location\":\"特色餐厅\",\"transport\":\"打车\",\"notes\":\"品尝当地特色\"}");
                
                json.append("]}");
            }
            json.append("]}");
            
            return json.toString();
        } catch (Exception e) {
            return "{\"destination\":\"" + destination + "\",\"days\":[]}";
        }
    }
}