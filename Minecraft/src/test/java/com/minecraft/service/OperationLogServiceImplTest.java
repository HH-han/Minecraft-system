package com.minecraft.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.minecraft.dto.request.PageRequest;
import com.minecraft.dto.response.PageResponse;
import com.minecraft.entity.OperationLog;
import com.minecraft.mapper.OperationLogMapper;
import com.minecraft.service.impl.OperationLogServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * OperationLogServiceImpl 单元测试。
 * 重点验证：分页查询（含关键字）、按用户过滤、CRUD 委托。
 */
@ExtendWith(MockitoExtension.class)
class OperationLogServiceImplTest {

    @Mock
    private OperationLogMapper operationLogMapper;

    @InjectMocks
    private OperationLogServiceImpl service;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(service, "baseMapper", operationLogMapper);
    }

    @Test
    void getOperationLogList_shouldReturnPaginatedResult() {
        PageRequest request = new PageRequest();
        request.setPageNum(1);
        request.setPageSize(10);

        OperationLog log1 = new OperationLog();
        log1.setId(1L);
        log1.setOperationType("LOGIN");
        log1.setOperationDesc("用户登录");
        log1.setCreatedAt(LocalDateTime.now());

        Page<OperationLog> mockPage = new Page<>(1, 10);
        mockPage.setRecords(Collections.singletonList(log1));
        mockPage.setTotal(1);

        when(operationLogMapper.selectPage(any(Page.class), any())).thenReturn(mockPage);

        PageResponse<OperationLog> result = service.getOperationLogList(request);

        assertNotNull(result);
        assertEquals(1L, result.getTotal());
        assertEquals(1, result.getRecords().size());
        assertEquals("LOGIN", result.getRecords().get(0).getOperationType());
    }

    @Test
    void getOperationLogList_withKeyword_shouldNotThrow() {
        PageRequest request = new PageRequest();
        request.setPageNum(1);
        request.setPageSize(10);
        request.setKeyword("登录");

        Page<OperationLog> mockPage = new Page<>(1, 10);
        mockPage.setRecords(Collections.emptyList());
        mockPage.setTotal(0);

        when(operationLogMapper.selectPage(any(Page.class), any())).thenReturn(mockPage);

        PageResponse<OperationLog> result = service.getOperationLogList(request);

        assertNotNull(result);
        assertEquals(0L, result.getTotal());
        assertTrue(result.getRecords().isEmpty());
    }

    @Test
    void getOperationLogDetail_shouldDelegateToMapper() {
        OperationLog log = new OperationLog();
        log.setId(1L);
        log.setOperationType("LOGIN");
        when(operationLogMapper.selectById(1L)).thenReturn(log);

        OperationLog result = service.getOperationLogDetail(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("LOGIN", result.getOperationType());
    }

    @Test
    void getLogsByUserId_shouldReturnUserLogs() {
        OperationLog log1 = new OperationLog();
        log1.setId(1L);
        log1.setUserId(100);
        OperationLog log2 = new OperationLog();
        log2.setId(2L);
        log2.setUserId(100);

        when(operationLogMapper.selectList(any())).thenReturn(Arrays.asList(log1, log2));

        List<OperationLog> result = service.getLogsByUserId(100);

        assertEquals(2, result.size());
        assertEquals(100, result.get(0).getUserId());
    }

    @Test
    void saveOperationLog_shouldDelegateToMapper() {
        OperationLog log = new OperationLog();
        log.setOperationType("CREATE");
        log.setOperationDesc("新增美食");

        service.saveOperationLog(log);

        verify(operationLogMapper).insert(log);
    }

    @Test
    void deleteOperationLog_shouldDelegateToMapper() {
        service.deleteOperationLog(1L);
        verify(operationLogMapper).deleteById(1L);
    }
}
