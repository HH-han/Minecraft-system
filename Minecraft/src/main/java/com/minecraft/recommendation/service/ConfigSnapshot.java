package com.minecraft.recommendation.service;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 推荐参数只读快照（非 final POJO，以兼容项目 RedisTemplate 的默认类型序列化）。
 * <p>
 * 两级结构：global 键值表 + category(分类) -> 键值表。
 */
public class ConfigSnapshot implements Serializable {

    private static final long serialVersionUID = 1L;

    private Map<String, String> global = new LinkedHashMap<>();

    private Map<String, Map<String, String>> categories = new LinkedHashMap<>();

    public Map<String, String> getGlobal() {
        return global;
    }

    public void setGlobal(Map<String, String> global) {
        this.global = global == null ? new LinkedHashMap<>() : global;
    }

    public Map<String, Map<String, String>> getCategories() {
        return categories;
    }

    public void setCategories(Map<String, Map<String, String>> categories) {
        this.categories = categories == null ? new LinkedHashMap<>() : categories;
    }

    public String globalValue(String key) {
        return global.get(key);
    }

    public String categoryValue(String category, String key) {
        Map<String, String> map = categories.get(category);
        return map == null ? null : map.get(key);
    }
}
