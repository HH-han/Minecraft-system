package com.minecraft.utils;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Arrays;

/**
 * Excel导入导出工具类
 * 使用Hutool的ExcelUtil来实现Excel的导入导出功能
 */
public class ExcelUtils {

    /**
     * 从Excel文件中读取数据
     * @param file 上传的Excel文件
     * @param headerRowIndex 表头所在行索引（从0开始）
     * @return 读取的数据列表，每个元素是一个Map，键为表头，值为对应单元格的值
     * @throws IOException 文件读取异常
     */
    public static List<Map<String, Object>> importExcel(MultipartFile file, int headerRowIndex) throws IOException {
        try (InputStream inputStream = file.getInputStream()) {
            // 创建ExcelReader时直接指定表头行索引
            ExcelReader reader = ExcelUtil.getReader(inputStream, headerRowIndex);
            // 读取所有数据
            return reader.readAll();
        }
    }

    /**
     * 从Excel文件中读取数据并转换为指定类型的对象列表
     * @param file 上传的Excel文件
     * @param headerRowIndex 表头所在行索引（从0开始）
     * @param clazz 要转换的对象类型
     * @param <T> 对象类型
     * @return 转换后的对象列表
     * @throws IOException 文件读取异常
     */
    public static <T> List<T> importExcel(MultipartFile file, int headerRowIndex, Class<T> clazz) throws IOException {
        try (InputStream inputStream = file.getInputStream()) {
            // 创建ExcelReader时直接指定表头行索引
            ExcelReader reader = ExcelUtil.getReader(inputStream, headerRowIndex);
            // 读取并转换为指定类型
            return reader.readAll(clazz);
        }
    }

    /**
     * 导出数据到Excel文件
     * @param dataList 要导出的数据列表
     * @param headers 表头数组
     * @param out 输出流
     */
    public static void exportExcel(List<? extends Map<String, Object>> dataList, String[] headers, OutputStream out) {
        // 创建ExcelWriter
        ExcelWriter writer = ExcelUtil.getWriter();
        
        // 设置表头
        writer.writeHeadRow(Arrays.asList(headers));
        
        // 写入数据
        for (Map<String, Object> data : dataList) {
            // 转换Map为List，按照表头顺序添加值
            List<Object> rowData = new ArrayList<>();
            for (String header : headers) {
                rowData.add(data.get(header));
            }
            writer.writeRow(rowData);
        }
        
        // 输出到流
        writer.flush(out);
        writer.close();
    }

    /**
     * 导出对象列表到Excel文件
     * @param dataList 要导出的对象列表
     * @param out 输出流
     * @param <T> 对象类型
     */
    public static <T> void exportExcel(List<T> dataList, OutputStream out) {
        // 创建ExcelWriter
        ExcelWriter writer = ExcelUtil.getWriter();
        
        // 写入数据
        writer.write(dataList, true);
        
        // 输出到流
        writer.flush(out);
        writer.close();
    }

    /**
     * 导出对象列表到Excel文件，并指定表头别名
     * @param dataList 要导出的对象列表
     * @param headerAlias 表头别名映射，键为属性名，值为表头名称
     * @param out 输出流
     * @param <T> 对象类型
     */
    public static <T> void exportExcel(List<T> dataList, Map<String, String> headerAlias, OutputStream out) {
        // 创建ExcelWriter
        ExcelWriter writer = ExcelUtil.getWriter();
        
        // 设置表头别名
        for (Map.Entry<String, String> entry : headerAlias.entrySet()) {
            writer.addHeaderAlias(entry.getKey(), entry.getValue());
        }
        
        // 写入数据
        writer.write(dataList, true);
        
        // 输出到流
        writer.flush(out);
        writer.close();
    }
}
