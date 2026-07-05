package com.minecraft.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.minecraft.entity.*;

import java.util.List;
import java.util.Map;

public interface OfficialwebsiteService extends IService<OfficialwebsiteHero> {
    List<OfficialwebsiteNavigation> getNavigationList();
    void saveNavigation(OfficialwebsiteNavigation navigation);
    void deleteNavigation(Integer id);

    OfficialwebsiteHero getActiveHero();
    void saveHero(OfficialwebsiteHero hero);

    List<OfficialwebsiteDestinations> getDestinationsList();
    List<OfficialwebsiteDestinations> getFeaturedDestinations();
    void saveDestination(OfficialwebsiteDestinations destination);
    void deleteDestination(Integer id);

    List<OfficialwebsiteExperiences> getExperiencesList();
    void saveExperience(OfficialwebsiteExperiences experience);
    void deleteExperience(Integer id);

    List<OfficialwebsiteNarrative> getNarrativeList();
    void saveNarrative(OfficialwebsiteNarrative narrative);
    void deleteNarrative(Integer id);

    List<OfficialwebsiteStories> getStoriesList();
    List<OfficialwebsiteStories> getFeaturedStories();
    void saveStory(OfficialwebsiteStories story);
    void deleteStory(Integer id);

    List<OfficialwebsiteImmersion> getImmersionList();
    void saveImmersion(OfficialwebsiteImmersion immersion);
    void deleteImmersion(Integer id);

    OfficialwebsiteCta getActiveCta();
    void saveCta(OfficialwebsiteCta cta);

    OfficialwebsiteFooter getFooter();
    void saveFooter(OfficialwebsiteFooter footer);

    List<OfficialwebsiteFooterLinks> getFooterLinks();
    List<OfficialwebsiteFooterLinks> getFooterLinksByCategory(String category);
    void saveFooterLinks(List<OfficialwebsiteFooterLinks> links);
    void deleteFooterLink(Integer id);

    List<OfficialwebsiteHomeConfig> getHomeConfig();
    OfficialwebsiteHomeConfig getHomeConfigByKey(String sectionKey);
    void saveHomeConfig(OfficialwebsiteHomeConfig config);

    Map<String, String> getSettings();
    String getSetting(String key);
    void saveSettings(Map<String, String> settings);
}