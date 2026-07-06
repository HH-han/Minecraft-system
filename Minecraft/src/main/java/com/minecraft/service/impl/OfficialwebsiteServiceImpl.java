package com.minecraft.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minecraft.entity.*;
import com.minecraft.mapper.*;
import com.minecraft.service.OfficialwebsiteService;
import com.minecraft.utils.ImageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OfficialwebsiteServiceImpl extends ServiceImpl<OfficialwebsiteHeroMapper, OfficialwebsiteHero> implements OfficialwebsiteService {

    @Autowired
    private OfficialwebsiteNavigationMapper navigationMapper;

    @Autowired
    private OfficialwebsiteDestinationsMapper destinationsMapper;

    @Autowired
    private OfficialwebsiteExperiencesMapper experiencesMapper;

    @Autowired
    private OfficialwebsiteNarrativeMapper narrativeMapper;

    @Autowired
    private OfficialwebsiteStoriesMapper storiesMapper;

    @Autowired
    private OfficialwebsiteImmersionMapper immersionMapper;

    @Autowired
    private OfficialwebsiteCtaMapper ctaMapper;

    @Autowired
    private OfficialwebsiteFooterMapper footerMapper;

    @Autowired
    private OfficialwebsiteFooterLinksMapper footerLinksMapper;

    @Autowired
    private OfficialwebsiteHomeConfigMapper homeConfigMapper;

    @Autowired
    private OfficialwebsiteSettingsMapper settingsMapper;

    @Autowired
    private ImageUtils imageUtils;

    @Override
    public List<OfficialwebsiteNavigation> getNavigationList() {
        LambdaQueryWrapper<OfficialwebsiteNavigation> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(OfficialwebsiteNavigation::getSortOrder);
        return navigationMapper.selectList(wrapper);
    }

    @Override
    @Transactional
    public void saveNavigation(OfficialwebsiteNavigation navigation) {
        if (navigation.getId() != null) {
            navigationMapper.updateById(navigation);
        } else {
            navigationMapper.insert(navigation);
        }
    }

    @Override
    @Transactional
    public void deleteNavigation(Integer id) {
        navigationMapper.deleteById(id);
    }

    @Override
    public OfficialwebsiteHero getActiveHero() {
        LambdaQueryWrapper<OfficialwebsiteHero> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OfficialwebsiteHero::getIsActive, 1);
        return baseMapper.selectOne(wrapper);
    }

    @Override
    @Transactional
    public void saveHero(OfficialwebsiteHero hero) {
        if (hero.getBgImage() != null && hero.getBgImage().startsWith("data:image")) {
            hero.setBgImage(imageUtils.processBase64ImageSafe(hero.getBgImage()));
        }
        if (hero.getId() != null) {
            baseMapper.updateById(hero);
        } else {
            baseMapper.insert(hero);
        }
    }

    @Override
    public List<OfficialwebsiteDestinations> getDestinationsList() {
        LambdaQueryWrapper<OfficialwebsiteDestinations> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(OfficialwebsiteDestinations::getSortOrder);
        return destinationsMapper.selectList(wrapper);
    }

    @Override
    public List<OfficialwebsiteDestinations> getFeaturedDestinations() {
        LambdaQueryWrapper<OfficialwebsiteDestinations> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OfficialwebsiteDestinations::getIsFeatured, 1)
                .orderByAsc(OfficialwebsiteDestinations::getSortOrder);
        return destinationsMapper.selectList(wrapper);
    }

    @Override
    @Transactional
    public void saveDestination(OfficialwebsiteDestinations destination) {
        if (destination.getImageUrl() != null && destination.getImageUrl().startsWith("data:image")) {
            destination.setImageUrl(imageUtils.processBase64ImageSafe(destination.getImageUrl()));
        }
        if (destination.getId() != null) {
            destinationsMapper.updateById(destination);
        } else {
            destinationsMapper.insert(destination);
        }
    }

    @Override
    @Transactional
    public void deleteDestination(Integer id) {
        destinationsMapper.deleteById(id);
    }

    @Override
    public List<OfficialwebsiteExperiences> getExperiencesList() {
        LambdaQueryWrapper<OfficialwebsiteExperiences> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OfficialwebsiteExperiences::getIsActive, 1)
                .orderByAsc(OfficialwebsiteExperiences::getSortOrder);
        return experiencesMapper.selectList(wrapper);
    }

    @Override
    @Transactional
    public void saveExperience(OfficialwebsiteExperiences experience) {
        if (experience.getId() != null) {
            experiencesMapper.updateById(experience);
        } else {
            experiencesMapper.insert(experience);
        }
    }

    @Override
    @Transactional
    public void deleteExperience(Integer id) {
        experiencesMapper.deleteById(id);
    }

    @Override
    public List<OfficialwebsiteNarrative> getNarrativeList() {
        LambdaQueryWrapper<OfficialwebsiteNarrative> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OfficialwebsiteNarrative::getIsActive, 1)
                .orderByAsc(OfficialwebsiteNarrative::getSortOrder);
        return narrativeMapper.selectList(wrapper);
    }

    @Override
    @Transactional
    public void saveNarrative(OfficialwebsiteNarrative narrative) {
        if (narrative.getImageUrl() != null && narrative.getImageUrl().startsWith("data:image")) {
            narrative.setImageUrl(imageUtils.processBase64ImageSafe(narrative.getImageUrl()));
        }
        if (narrative.getId() != null) {
            narrativeMapper.updateById(narrative);
        } else {
            narrativeMapper.insert(narrative);
        }
    }

    @Override
    @Transactional
    public void deleteNarrative(Integer id) {
        narrativeMapper.deleteById(id);
    }

    @Override
    public List<OfficialwebsiteStories> getStoriesList() {
        LambdaQueryWrapper<OfficialwebsiteStories> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(OfficialwebsiteStories::getSortOrder);
        return storiesMapper.selectList(wrapper);
    }

    @Override
    public List<OfficialwebsiteStories> getFeaturedStories() {
        LambdaQueryWrapper<OfficialwebsiteStories> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OfficialwebsiteStories::getIsFeatured, 1)
                .orderByDesc(OfficialwebsiteStories::getSortOrder);
        return storiesMapper.selectList(wrapper);
    }

    @Override
    @Transactional
    public void saveStory(OfficialwebsiteStories story) {
        if (story.getAuthorAvatar() != null && story.getAuthorAvatar().startsWith("data:image")) {
            story.setAuthorAvatar(imageUtils.processBase64ImageSafe(story.getAuthorAvatar()));
        }
        if (story.getId() != null) {
            storiesMapper.updateById(story);
        } else {
            storiesMapper.insert(story);
        }
    }

    @Override
    @Transactional
    public void deleteStory(Integer id) {
        storiesMapper.deleteById(id);
    }

    @Override
    public List<OfficialwebsiteImmersion> getImmersionList() {
        LambdaQueryWrapper<OfficialwebsiteImmersion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OfficialwebsiteImmersion::getIsActive, 1);
        return immersionMapper.selectList(wrapper);
    }

    @Override
    @Transactional
    public void saveImmersion(OfficialwebsiteImmersion immersion) {
        if (immersion.getId() != null) {
            immersionMapper.updateById(immersion);
        } else {
            immersionMapper.insert(immersion);
        }
    }

    @Override
    @Transactional
    public void deleteImmersion(Integer id) {
        immersionMapper.deleteById(id);
    }

    @Override
    public OfficialwebsiteCta getActiveCta() {
        LambdaQueryWrapper<OfficialwebsiteCta> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OfficialwebsiteCta::getIsActive, 1);
        return ctaMapper.selectOne(wrapper);
    }

    @Override
    @Transactional
    public void saveCta(OfficialwebsiteCta cta) {
        if (cta.getId() != null) {
            ctaMapper.updateById(cta);
        } else {
            ctaMapper.insert(cta);
        }
    }

    @Override
    public OfficialwebsiteFooter getFooter() {
        LambdaQueryWrapper<OfficialwebsiteFooter> wrapper = new LambdaQueryWrapper<>();
        wrapper.last("LIMIT 1");
        return footerMapper.selectOne(wrapper);
    }

    @Override
    @Transactional
    public void saveFooter(OfficialwebsiteFooter footer) {
        if (footer.getId() != null) {
            footerMapper.updateById(footer);
        } else {
            footerMapper.insert(footer);
        }
    }

    @Override
    public List<OfficialwebsiteFooterLinks> getFooterLinks() {
        LambdaQueryWrapper<OfficialwebsiteFooterLinks> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(OfficialwebsiteFooterLinks::getCategory)
                .orderByAsc(OfficialwebsiteFooterLinks::getSortOrder);
        return footerLinksMapper.selectList(wrapper);
    }

    @Override
    public List<OfficialwebsiteFooterLinks> getFooterLinksByCategory(String category) {
        LambdaQueryWrapper<OfficialwebsiteFooterLinks> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OfficialwebsiteFooterLinks::getCategory, category)
                .orderByAsc(OfficialwebsiteFooterLinks::getSortOrder);
        return footerLinksMapper.selectList(wrapper);
    }

    @Override
    @Transactional
    public void saveFooterLinks(List<OfficialwebsiteFooterLinks> links) {
        for (OfficialwebsiteFooterLinks link : links) {
            if (link.getId() != null) {
                footerLinksMapper.updateById(link);
            } else {
                footerLinksMapper.insert(link);
            }
        }
    }

    @Override
    @Transactional
    public void deleteFooterLink(Integer id) {
        footerLinksMapper.deleteById(id);
    }

    @Override
    public List<OfficialwebsiteHomeConfig> getHomeConfig() {
        LambdaQueryWrapper<OfficialwebsiteHomeConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(OfficialwebsiteHomeConfig::getId);
        return homeConfigMapper.selectList(wrapper);
    }

    @Override
    public OfficialwebsiteHomeConfig getHomeConfigByKey(String sectionKey) {
        LambdaQueryWrapper<OfficialwebsiteHomeConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OfficialwebsiteHomeConfig::getSectionKey, sectionKey);
        return homeConfigMapper.selectOne(wrapper);
    }

    @Override
    @Transactional
    public void saveHomeConfig(OfficialwebsiteHomeConfig config) {
        if (config.getId() != null) {
            homeConfigMapper.updateById(config);
        } else {
            homeConfigMapper.insert(config);
        }
    }

    @Override
    public Map<String, String> getSettings() {
        List<OfficialwebsiteSettings> settings = settingsMapper.selectList(null);
        Map<String, String> result = new HashMap<>();
        for (OfficialwebsiteSettings setting : settings) {
            result.put(setting.getSettingKey(), setting.getSettingValue());
        }
        return result;
    }

    @Override
    public String getSetting(String key) {
        LambdaQueryWrapper<OfficialwebsiteSettings> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OfficialwebsiteSettings::getSettingKey, key);
        OfficialwebsiteSettings setting = settingsMapper.selectOne(wrapper);
        return setting != null ? setting.getSettingValue() : null;
    }

    @Override
    @Transactional
    public void saveSettings(Map<String, String> settings) {
        for (Map.Entry<String, String> entry : settings.entrySet()) {
            LambdaQueryWrapper<OfficialwebsiteSettings> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(OfficialwebsiteSettings::getSettingKey, entry.getKey());
            OfficialwebsiteSettings setting = settingsMapper.selectOne(wrapper);
            if (setting != null) {
                setting.setSettingValue(entry.getValue());
                settingsMapper.updateById(setting);
            } else {
                setting = new OfficialwebsiteSettings();
                setting.setSettingKey(entry.getKey());
                setting.setSettingValue(entry.getValue());
                settingsMapper.insert(setting);
            }
        }
    }
}