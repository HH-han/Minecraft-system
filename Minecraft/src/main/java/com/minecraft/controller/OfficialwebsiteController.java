package com.minecraft.controller;

import com.minecraft.dto.response.ApiResponse;
import com.minecraft.entity.*;
import com.minecraft.service.OfficialwebsiteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "官网管理")
@RestController
@RequestMapping("/api/officialwebsite")
public class OfficialwebsiteController {

    @Autowired
    private OfficialwebsiteService officialwebsiteService;

    @Operation(summary = "获取导航菜单")
    @GetMapping("/navigation")
    public ApiResponse<List<OfficialwebsiteNavigation>> getNavigationList() {
        return ApiResponse.success(officialwebsiteService.getNavigationList());
    }

    @Operation(summary = "保存导航菜单")
    @PostMapping("/navigation")
    public ApiResponse<Void> saveNavigation(@RequestBody OfficialwebsiteNavigation navigation) {
        officialwebsiteService.saveNavigation(navigation);
        return ApiResponse.success("保存成功", null);
    }

    @Operation(summary = "删除导航菜单")
    @DeleteMapping("/navigation/{id}")
    public ApiResponse<Void> deleteNavigation(@PathVariable Integer id) {
        officialwebsiteService.deleteNavigation(id);
        return ApiResponse.success("删除成功", null);
    }

    @Operation(summary = "获取英雄区配置")
    @GetMapping("/hero")
    public ApiResponse<OfficialwebsiteHero> getHero() {
        return ApiResponse.success(officialwebsiteService.getActiveHero());
    }

    @Operation(summary = "保存英雄区配置")
    @PostMapping("/hero")
    public ApiResponse<Void> saveHero(@RequestBody OfficialwebsiteHero hero) {
        officialwebsiteService.saveHero(hero);
        return ApiResponse.success("保存成功", null);
    }

    @Operation(summary = "获取目的地列表")
    @GetMapping("/destinations")
    public ApiResponse<List<OfficialwebsiteDestinations>> getDestinationsList() {
        return ApiResponse.success(officialwebsiteService.getDestinationsList());
    }

    @Operation(summary = "获取推荐目的地")
    @GetMapping("/destinations/featured")
    public ApiResponse<List<OfficialwebsiteDestinations>> getFeaturedDestinations() {
        return ApiResponse.success(officialwebsiteService.getFeaturedDestinations());
    }

    @Operation(summary = "保存目的地")
    @PostMapping("/destinations")
    public ApiResponse<Void> saveDestination(@RequestBody OfficialwebsiteDestinations destination) {
        officialwebsiteService.saveDestination(destination);
        return ApiResponse.success("保存成功", null);
    }

    @Operation(summary = "删除目的地")
    @DeleteMapping("/destinations/{id}")
    public ApiResponse<Void> deleteDestination(@PathVariable Integer id) {
        officialwebsiteService.deleteDestination(id);
        return ApiResponse.success("删除成功", null);
    }

    @Operation(summary = "获取体验项目")
    @GetMapping("/experiences")
    public ApiResponse<List<OfficialwebsiteExperiences>> getExperiencesList() {
        return ApiResponse.success(officialwebsiteService.getExperiencesList());
    }

    @Operation(summary = "保存体验项目")
    @PostMapping("/experiences")
    public ApiResponse<Void> saveExperience(@RequestBody OfficialwebsiteExperiences experience) {
        officialwebsiteService.saveExperience(experience);
        return ApiResponse.success("保存成功", null);
    }

    @Operation(summary = "删除体验项目")
    @DeleteMapping("/experiences/{id}")
    public ApiResponse<Void> deleteExperience(@PathVariable Integer id) {
        officialwebsiteService.deleteExperience(id);
        return ApiResponse.success("删除成功", null);
    }

    @Operation(summary = "获取叙事章节")
    @GetMapping("/narrative")
    public ApiResponse<List<OfficialwebsiteNarrative>> getNarrativeList() {
        return ApiResponse.success(officialwebsiteService.getNarrativeList());
    }

    @Operation(summary = "保存叙事章节")
    @PostMapping("/narrative")
    public ApiResponse<Void> saveNarrative(@RequestBody OfficialwebsiteNarrative narrative) {
        officialwebsiteService.saveNarrative(narrative);
        return ApiResponse.success("保存成功", null);
    }

    @Operation(summary = "删除叙事章节")
    @DeleteMapping("/narrative/{id}")
    public ApiResponse<Void> deleteNarrative(@PathVariable Integer id) {
        officialwebsiteService.deleteNarrative(id);
        return ApiResponse.success("删除成功", null);
    }

    @Operation(summary = "获取旅行故事")
    @GetMapping("/stories")
    public ApiResponse<List<OfficialwebsiteStories>> getStoriesList() {
        return ApiResponse.success(officialwebsiteService.getStoriesList());
    }

    @Operation(summary = "获取推荐旅行故事")
    @GetMapping("/stories/featured")
    public ApiResponse<List<OfficialwebsiteStories>> getFeaturedStories() {
        return ApiResponse.success(officialwebsiteService.getFeaturedStories());
    }

    @Operation(summary = "保存旅行故事")
    @PostMapping("/stories")
    public ApiResponse<Void> saveStory(@RequestBody OfficialwebsiteStories story) {
        officialwebsiteService.saveStory(story);
        return ApiResponse.success("保存成功", null);
    }

    @Operation(summary = "删除旅行故事")
    @DeleteMapping("/stories/{id}")
    public ApiResponse<Void> deleteStory(@PathVariable Integer id) {
        officialwebsiteService.deleteStory(id);
        return ApiResponse.success("删除成功", null);
    }

    @Operation(summary = "获取沉浸式引用")
    @GetMapping("/immersion")
    public ApiResponse<List<OfficialwebsiteImmersion>> getImmersionList() {
        return ApiResponse.success(officialwebsiteService.getImmersionList());
    }

    @Operation(summary = "保存沉浸式引用")
    @PostMapping("/immersion")
    public ApiResponse<Void> saveImmersion(@RequestBody OfficialwebsiteImmersion immersion) {
        officialwebsiteService.saveImmersion(immersion);
        return ApiResponse.success("保存成功", null);
    }

    @Operation(summary = "删除沉浸式引用")
    @DeleteMapping("/immersion/{id}")
    public ApiResponse<Void> deleteImmersion(@PathVariable Integer id) {
        officialwebsiteService.deleteImmersion(id);
        return ApiResponse.success("删除成功", null);
    }

    @Operation(summary = "获取CTA配置")
    @GetMapping("/cta")
    public ApiResponse<OfficialwebsiteCta> getCta() {
        return ApiResponse.success(officialwebsiteService.getActiveCta());
    }

    @Operation(summary = "保存CTA配置")
    @PostMapping("/cta")
    public ApiResponse<Void> saveCta(@RequestBody OfficialwebsiteCta cta) {
        officialwebsiteService.saveCta(cta);
        return ApiResponse.success("保存成功", null);
    }

    @Operation(summary = "获取页脚配置")
    @GetMapping("/footer")
    public ApiResponse<OfficialwebsiteFooter> getFooter() {
        return ApiResponse.success(officialwebsiteService.getFooter());
    }

    @Operation(summary = "保存页脚配置")
    @PostMapping("/footer")
    public ApiResponse<Void> saveFooter(@RequestBody OfficialwebsiteFooter footer) {
        officialwebsiteService.saveFooter(footer);
        return ApiResponse.success("保存成功", null);
    }

    @Operation(summary = "获取页脚链接")
    @GetMapping("/footer/links")
    public ApiResponse<List<OfficialwebsiteFooterLinks>> getFooterLinks(
            @RequestParam(required = false) String category) {
        if (category != null) {
            return ApiResponse.success(officialwebsiteService.getFooterLinksByCategory(category));
        }
        return ApiResponse.success(officialwebsiteService.getFooterLinks());
    }

    @Operation(summary = "保存页脚链接")
    @PostMapping("/footer/links")
    public ApiResponse<Void> saveFooterLinks(@RequestBody List<OfficialwebsiteFooterLinks> links) {
        officialwebsiteService.saveFooterLinks(links);
        return ApiResponse.success("保存成功", null);
    }

    @Operation(summary = "删除页脚链接")
    @DeleteMapping("/footer/links/{id}")
    public ApiResponse<Void> deleteFooterLink(@PathVariable Integer id) {
        officialwebsiteService.deleteFooterLink(id);
        return ApiResponse.success("删除成功", null);
    }

    @Operation(summary = "获取首页配置")
    @GetMapping("/home/config")
    public ApiResponse<List<OfficialwebsiteHomeConfig>> getHomeConfig() {
        return ApiResponse.success(officialwebsiteService.getHomeConfig());
    }

    @Operation(summary = "获取首页配置(按key)")
    @GetMapping("/home/config/{sectionKey}")
    public ApiResponse<OfficialwebsiteHomeConfig> getHomeConfigByKey(@PathVariable String sectionKey) {
        return ApiResponse.success(officialwebsiteService.getHomeConfigByKey(sectionKey));
    }

    @Operation(summary = "保存首页配置")
    @PostMapping("/home/config")
    public ApiResponse<Void> saveHomeConfig(@RequestBody OfficialwebsiteHomeConfig config) {
        officialwebsiteService.saveHomeConfig(config);
        return ApiResponse.success("保存成功", null);
    }

    @Operation(summary = "获取网站设置")
    @GetMapping("/settings")
    public ApiResponse<Map<String, String>> getSettings() {
        return ApiResponse.success(officialwebsiteService.getSettings());
    }

    @Operation(summary = "获取单个网站设置")
    @GetMapping("/settings/{key}")
    public ApiResponse<String> getSetting(@PathVariable String key) {
        return ApiResponse.success(officialwebsiteService.getSetting(key));
    }

    @Operation(summary = "保存网站设置")
    @PostMapping("/settings")
    public ApiResponse<Void> saveSettings(@RequestBody Map<String, String> settings) {
        officialwebsiteService.saveSettings(settings);
        return ApiResponse.success("保存成功", null);
    }

    @Operation(summary = "获取首页完整数据")
    @GetMapping("/home")
    public ApiResponse<Map<String, Object>> getHomeData() {
        Map<String, Object> homeData = new HashMap<>();
        homeData.put("hero", officialwebsiteService.getActiveHero());
        homeData.put("destinations", officialwebsiteService.getDestinationsList());
        homeData.put("experiences", officialwebsiteService.getExperiencesList());
        homeData.put("narrative", officialwebsiteService.getNarrativeList());
        homeData.put("stories", officialwebsiteService.getFeaturedStories());
        homeData.put("immersion", officialwebsiteService.getImmersionList());
        homeData.put("cta", officialwebsiteService.getActiveCta());
        homeData.put("footer", officialwebsiteService.getFooter());
        homeData.put("footerLinks", officialwebsiteService.getFooterLinks());
        homeData.put("settings", officialwebsiteService.getSettings());
        return ApiResponse.success(homeData);
    }
}