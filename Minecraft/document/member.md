# Minecraft 旅游系统 — 会员系统开发方案

基于现有系统架构（Spring Boot 3 + MyBatis Plus + Vue 3 + MySQL 8），下面给出一套可直接落地的会员系统完整设计与代码骨架。

---

## 一、需求设计

### 1. 会员等级体系

| 等级 | 名称 | 成长值门槛 | 折扣 | 积分倍率 | 专属权益 |
|------|------|-----------|------|---------|---------|
| 0 | 普通用户 | 0 | 无 | 1x | — |
| 1 | 白银会员 | 500 | 98折 | 1.2x | 生日礼包 |
| 2 | 黄金会员 | 2000 | 95折 | 1.5x | 生日礼包 + 优先客服 |
| 3 | 铂金会员 | 5000 | 92折 | 2x | 免费取消订单 |
| 4 | 钻石会员 | 10000 | 88折 | 3x | 全部权益 + 专属客服 |

### 2. 核心功能

- **会员开通/续费**：月卡、季卡、年卡（付费会员），或成长值免费升级
- **成长值体系**：消费、签到、评价、分享等行为获得成长值，自动升降级
- **积分体系**：与现有 `points_record` 打通，会员享受积分倍率
- **会员权益**：下单折扣、积分加速、专属优惠券、生日礼包、优先服务
- **签到**：每日签到得成长值/积分，连续签到有额外奖励
- **会员订单**：开通记录、续费记录、退款

---

## 二、数据库设计

在 `Minecraft/src/main/resources/static/sql/` 下新增 `member.sql`：

```sql
-- ============ 会员等级配置 ============
CREATE TABLE `member_level` (
  `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '等级ID',
  `level`         INT          NOT NULL COMMENT '等级序号 0-4',
  `name`          VARCHAR(32)  NOT NULL COMMENT '等级名称',
  `icon`          VARCHAR(255) DEFAULT NULL COMMENT '等级图标',
  `growth_min`    INT          NOT NULL DEFAULT 0 COMMENT '成长值下限',
  `growth_max`    INT          NOT NULL DEFAULT 0 COMMENT '成长值上限(0=无上限)',
  `discount`      DECIMAL(4,2) NOT NULL DEFAULT 1.00 COMMENT '折扣率 0.88=88折',
  `points_rate`   DECIMAL(4,2) NOT NULL DEFAULT 1.00 COMMENT '积分倍率',
  `benefits`      VARCHAR(512) DEFAULT NULL COMMENT '权益描述(JSON)',
  `sort`          INT          NOT NULL DEFAULT 0,
  `status`        TINYINT      NOT NULL DEFAULT 1 COMMENT '1启用 0停用',
  `create_time`   DATETIME     DEFAULT CURRENT_TIMESTAMP,
  `update_time`   DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_level` (`level`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员等级配置';

-- ============ 用户会员信息 ============
CREATE TABLE `user_member` (
  `id`            BIGINT       NOT NULL AUTO_INCREMENT,
  `user_id`       BIGINT       NOT NULL COMMENT '用户ID',
  `level_id`      BIGINT       NOT NULL DEFAULT 0 COMMENT '当前等级ID',
  `growth`        INT          NOT NULL DEFAULT 0 COMMENT '成长值',
  `is_paid`       TINYINT      NOT NULL DEFAULT 0 COMMENT '是否付费会员 0否 1是',
  `expire_time`   DATETIME     DEFAULT NULL COMMENT '付费会员到期时间',
  `birthday`      DATE         DEFAULT NULL COMMENT '生日(用于礼包)',
  `last_sign_date` DATE        DEFAULT NULL COMMENT '最后签到日期',
  `sign_continuous` INT        NOT NULL DEFAULT 0 COMMENT '连续签到天数',
  `create_time`   DATETIME     DEFAULT CURRENT_TIMESTAMP,
  `update_time`   DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户会员信息';

-- ============ 会员套餐 ============
CREATE TABLE `member_package` (
  `id`            BIGINT       NOT NULL AUTO_INCREMENT,
  `name`          VARCHAR(64)  NOT NULL COMMENT '套餐名称',
  `type`          TINYINT      NOT NULL COMMENT '1月卡 2季卡 3年卡',
  `duration_days` INT          NOT NULL COMMENT '有效天数',
  `price`         DECIMAL(10,2) NOT NULL COMMENT '价格',
  `original_price` DECIMAL(10,2) DEFAULT NULL COMMENT '原价',
  `give_points`   INT          NOT NULL DEFAULT 0 COMMENT '赠送积分',
  `give_growth`   INT          NOT NULL DEFAULT 0 COMMENT '赠送成长值',
  `description`   VARCHAR(512) DEFAULT NULL,
  `sort`          INT          NOT NULL DEFAULT 0,
  `status`        TINYINT      NOT NULL DEFAULT 1,
  `create_time`   DATETIME     DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员套餐';

-- ============ 会员开通订单 ============
CREATE TABLE `member_order` (
  `id`            BIGINT       NOT NULL AUTO_INCREMENT,
  `order_no`      VARCHAR(64)  NOT NULL COMMENT '订单号',
  `user_id`       BIGINT       NOT NULL,
  `package_id`    BIGINT       NOT NULL,
  `package_name`  VARCHAR(64)  NOT NULL,
  `amount`        DECIMAL(10,2) NOT NULL COMMENT '实付金额',
  `pay_type`      TINYINT      DEFAULT NULL COMMENT '1支付宝 2微信 3余额',
  `status`        TINYINT      NOT NULL DEFAULT 0 COMMENT '0待支付 1已支付 2已取消 3已退款',
  `pay_time`      DATETIME     DEFAULT NULL,
  `start_time`    DATETIME     DEFAULT NULL COMMENT '会员生效时间',
  `end_time`      DATETIME     DEFAULT NULL COMMENT '会员到期时间',
  `create_time`   DATETIME     DEFAULT CURRENT_TIMESTAMP,
  `update_time`   DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员订单';

-- ============ 成长值/签到流水 ============
CREATE TABLE `member_growth_record` (
  `id`            BIGINT       NOT NULL AUTO_INCREMENT,
  `user_id`       BIGINT       NOT NULL,
  `change_value`  INT          NOT NULL COMMENT '变化值(正负)',
  `after_value`   INT          NOT NULL COMMENT '变化后成长值',
  `source`        TINYINT      NOT NULL COMMENT '1消费 2签到 3评价 4分享 5开通会员 6管理员调整',
  `biz_id`        VARCHAR(64)  DEFAULT NULL COMMENT '业务ID(订单号等)',
  `remark`        VARCHAR(255) DEFAULT NULL,
  `create_time`   DATETIME     DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='成长值流水';

-- ============ 签到记录 ============
CREATE TABLE `member_sign_record` (
  `id`            BIGINT       NOT NULL AUTO_INCREMENT,
  `user_id`       BIGINT       NOT NULL,
  `sign_date`     DATE         NOT NULL,
  `continuous`    INT          NOT NULL DEFAULT 1 COMMENT '当日连续天数',
  `give_growth`   INT          NOT NULL DEFAULT 0,
  `give_points`   INT          NOT NULL DEFAULT 0,
  `create_time`   DATETIME     DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_date` (`user_id`,`sign_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='签到记录';

-- ============ 会员权益发放记录(生日礼包/优惠券等) ============
CREATE TABLE `member_benefit_record` (
  `id`            BIGINT       NOT NULL AUTO_INCREMENT,
  `user_id`       BIGINT       NOT NULL,
  `benefit_type`  VARCHAR(32)  NOT NULL COMMENT 'BIRTHDAY/COUPON/...',
  `level_id`      BIGINT       DEFAULT NULL,
  `content`       VARCHAR(512) DEFAULT NULL COMMENT '发放内容JSON',
  `status`        TINYINT      NOT NULL DEFAULT 1 COMMENT '1已发放 0未发放',
  `create_time`   DATETIME     DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user_type` (`user_id`,`benefit_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员权益发放记录';

-- ============ 初始化等级 ============
INSERT INTO `member_level`(`level`,`name`,`growth_min`,`growth_max`,`discount`,`points_rate`,`benefits`,`sort`) VALUES
(0,'普通用户',0,499,1.00,1.00,'[]',0),
(1,'白银会员',500,1999,0.98,1.20,'["生日礼包"]',1),
(2,'黄金会员',2000,4999,0.95,1.50,'["生日礼包","优先客服"]',2),
(3,'铂金会员',5000,9999,0.92,2.00,'["生日礼包","优先客服","免费取消"]',3),
(4,'钻石会员',10000,0,0.88,3.00,'["生日礼包","优先客服","免费取消","专属客服"]',4);

INSERT INTO `member_package`(`name`,`type`,`duration_days`,`price`,`original_price`,`give_points`,`give_growth`,`description`,`sort`) VALUES
('月卡会员',1,30,15.00,20.00,100,100,'享30天会员权益',1),
('季卡会员',2,90,40.00,60.00,350,350,'享90天会员权益',2),
('年卡会员',3,365,148.00,240.00,1500,1500,'享365天会员权益',3);
```

---

## 三、后端实现

### 1. 实体类

`entity/MemberLevel.java`、`UserMember.java`、`MemberPackage.java`、`MemberOrder.java`、`MemberGrowthRecord.java`、`MemberSignRecord.java`，示例：

```java
package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("user_member")
public class UserMember {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long levelId;
    private Integer growth;
    private Integer isPaid;
    private LocalDateTime expireTime;
    private java.time.LocalDate birthday;
    private java.time.LocalDate lastSignDate;
    private Integer signContinuous;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
```

### 2. Mapper

```java
package com.minecraft.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minecraft.entity.UserMember;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMemberMapper extends BaseMapper<UserMember> {}
```

其余 Mapper 同构，略。

### 3. DTO / VO

```java
// dto/MemberOpenDTO.java
@Data
public class MemberOpenDTO {
    @NotNull private Long packageId;
    @NotNull private Integer payType; // 1支付宝 2微信 3余额
}

// dto/MemberGrowthDTO.java
@Data
public class MemberGrowthDTO {
    @NotNull private Integer changeValue;
    @NotNull private Integer source;
    private String bizId;
    private String remark;
}

// vo/MemberInfoVO.java
@Data
public class MemberInfoVO {
    private Long userId;
    private String nickname;
    private Integer growth;
    private Integer level;
    private String levelName;
    private String levelIcon;
    private BigDecimal discount;
    private BigDecimal pointsRate;
    private Integer isPaid;
    private LocalDateTime expireTime;
    private Integer nextLevelGrowth; // 距下一级所需成长值
    private Integer todaySigned;     // 今日是否已签到
    private Integer signContinuous;
}
```

### 4. 核心 Service

```java
package com.minecraft.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minecraft.dto.MemberOpenDTO;
import com.minecraft.entity.*;
import com.minecraft.mapper.*;
import com.minecraft.vo.MemberInfoVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final UserMemberMapper userMemberMapper;
    private final MemberLevelMapper levelMapper;
    private final MemberPackageMapper packageMapper;
    private final MemberOrderMapper orderMapper;
    private final MemberGrowthRecordMapper growthMapper;
    private final MemberSignRecordMapper signMapper;

    /** 获取或初始化用户会员信息 */
    public UserMember getOrCreate(Long userId) {
        UserMember um = userMemberMapper.selectOne(
                new LambdaQueryWrapper<UserMember>().eq(UserMember::getUserId, userId));
        if (um == null) {
            um = new UserMember();
            um.setUserId(userId);
            um.setGrowth(0);
            um.setLevelId(levelMapper.selectOne(
                    new LambdaQueryWrapper<MemberLevel>().eq(MemberLevel::getLevel, 0)).getId());
            um.setIsPaid(0);
            um.setSignContinuous(0);
            userMemberMapper.insert(um);
        }
        return um;
    }

    /** 会员信息 + 权益 */
    public MemberInfoVO getInfo(Long userId) {
        UserMember um = getOrCreate(userId);
        MemberLevel lv = levelMapper.selectById(um.getLevelId());
        MemberInfoVO vo = new MemberInfoVO();
        vo.setUserId(userId);
        vo.setGrowth(um.getGrowth());
        vo.setLevel(lv.getLevel());
        vo.setLevelName(lv.getName());
        vo.setLevelIcon(lv.getIcon());
        vo.setDiscount(lv.getDiscount());
        vo.setPointsRate(lv.getPointsRate());
        vo.setIsPaid(um.getIsPaid());
        vo.setExpireTime(um.getExpireTime());
        vo.setSignContinuous(um.getSignContinuous());
        vo.setTodaySigned(LocalDate.now().equals(um.getLastSignDate()));
        // 距下一级
        MemberLevel next = levelMapper.selectOne(new LambdaQueryWrapper<MemberLevel>()
                .gt(MemberLevel::getLevel, lv.getLevel())
                .orderByAsc(MemberLevel::getLevel).last("limit 1"));
        vo.setNextLevelGrowth(next == null ? 0 : Math.max(0, next.getGrowthMin() - um.getGrowth()));
        return vo;
    }

    /** 开通/续费会员 */
    @Transactional
    public MemberOrder open(Long userId, MemberOpenDTO dto) {
        MemberPackage pkg = packageMapper.selectById(dto.getPackageId());
        if (pkg == null || pkg.getStatus() == 0) throw new RuntimeException("套餐不存在");

        MemberOrder order = new MemberOrder();
        order.setOrderNo("M" + System.currentTimeMillis() + userId);
        order.setUserId(userId);
        order.setPackageId(pkg.getId());
        order.setPackageName(pkg.getName());
        order.setAmount(pkg.getPrice());
        order.setPayType(dto.getPayType());
        order.setStatus(0);
        orderMapper.insert(order);
        return order; // 前端拿 orderNo 跳支付，回调里调用 markPaid
    }

    /** 支付回调：标记订单已支付并发放权益 */
    @Transactional
    public void markPaid(String orderNo) {
        MemberOrder order = orderMapper.selectOne(
                new LambdaQueryWrapper<MemberOrder>().eq(MemberOrder::getOrderNo, orderNo));
        if (order == null || order.getStatus() == 1) return;
        MemberPackage pkg = packageMapper.selectById(order.getPackageId());
        UserMember um = getOrCreate(order.getUserId());

        LocalDateTime start = LocalDateTime.now();
        // 续费叠加
        if (um.getIsPaid() == 1 && um.getExpireTime() != null && um.getExpireTime().isAfter(start)) {
            start = um.getExpireTime();
        }
        LocalDateTime end = start.plusDays(pkg.getDurationDays());

        order.setStatus(1);
        order.setPayTime(LocalDateTime.now());
        order.setStartTime(start);
        order.setEndTime(end);
        orderMapper.updateById(order);

        um.setIsPaid(1);
        um.setExpireTime(end);
        userMemberMapper.updateById(um);

        if (pkg.getGiveGrowth() > 0) addGrowth(order.getUserId(), pkg.getGiveGrowth(), 5, orderNo, "开通会员赠送");
        // 积分发放复用现有 points_record 逻辑
    }

    /** 增加成长值并自动升级 */
    @Transactional
    public void addGrowth(Long userId, int value, int source, String bizId, String remark) {
        UserMember um = getOrCreate(userId);
        int after = um.getGrowth() + value;
        if (after < 0) after = 0;
        um.setGrowth(after);
        // 重新计算等级
        List<MemberLevel> levels = levelMapper.selectList(new LambdaQueryWrapper<MemberLevel>()
                .eq(MemberLevel::getStatus, 1).orderByDesc(MemberLevel::getGrowthMin));
        for (MemberLevel lv : levels) {
            if (after >= lv.getGrowthMin()) { um.setLevelId(lv.getId()); break; }
        }
        userMemberMapper.updateById(um);

        MemberGrowthRecord rec = new MemberGrowthRecord();
        rec.setUserId(userId);
        rec.setChangeValue(value);
        rec.setAfterValue(after);
        rec.setSource(source);
        rec.setBizId(bizId);
        rec.setRemark(remark);
        growthMapper.insert(rec);
    }

    /** 每日签到 */
    @Transactional
    public MemberInfoVO sign(Long userId) {
        UserMember um = getOrCreate(userId);
        LocalDate today = LocalDate.now();
        if (today.equals(um.getLastSignDate())) throw new RuntimeException("今日已签到");

        int continuous = 1;
        if (um.getLastSignDate() != null && um.getLastSignDate().plusDays(1).equals(today)) {
            continuous = um.getSignContinuous() + 1;
        }
        int giveGrowth = 10 + Math.min(continuous, 7) * 5;   // 连续越高越多
        int givePoints = 5 + Math.min(continuous, 7) * 2;

        um.setLastSignDate(today);
        um.setSignContinuous(continuous);
        userMemberMapper.updateById(um);

        MemberSignRecord sr = new MemberSignRecord();
        sr.setUserId(userId);
        sr.setSignDate(today);
        sr.setContinuous(continuous);
        sr.setGiveGrowth(giveGrowth);
        sr.setGivePoints(givePoints);
        signMapper.insert(sr);

        addGrowth(userId, giveGrowth, 2, null, "每日签到");
        return getInfo(userId);
    }

    /** 计算订单折扣价（供订单服务调用） */
    public BigDecimal calcDiscountPrice(Long userId, BigDecimal original) {
        UserMember um = getOrCreate(userId);
        MemberLevel lv = levelMapper.selectById(um.getLevelId());
        return original.multiply(lv.getDiscount()).setScale(2, BigDecimal.ROUND_HALF_UP);
    }
}
```

### 5. Controller

```java
package com.minecraft.controller;

import com.minecraft.dto.MemberOpenDTO;
import com.minecraft.service.MemberService;
import com.minecraft.common.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    /** 会员中心信息 */
    @GetMapping("/info")
    public Result<?> info(@RequestAttribute("userId") Long userId) {
        return Result.success(memberService.getInfo(userId));
    }

    /** 套餐列表 */
    @GetMapping("/packages")
    public Result<?> packages() {
        return Result.success(memberService.listPackages());
    }

    /** 开通会员（生成订单） */
    @PostMapping("/open")
    public Result<?> open(@RequestAttribute("userId") Long userId,
                          @RequestBody @Valid MemberOpenDTO dto) {
        return Result.success(memberService.open(userId, dto));
    }

    /** 签到 */
    @PostMapping("/sign")
    public Result<?> sign(@RequestAttribute("userId") Long userId) {
        return Result.success(memberService.sign(userId));
    }

    /** 成长值流水 */
    @GetMapping("/growth/records")
    public Result<?> growthRecords(@RequestAttribute("userId") Long userId,
                                   @RequestParam(defaultValue = "1") Integer page,
                                   @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(memberService.pageGrowth(userId, page, size));
    }

    /** 我的会员订单 */
    @GetMapping("/orders")
    public Result<?> orders(@RequestAttribute("userId") Long userId) {
        return Result.success(memberService.listOrders(userId));
    }

    /** 支付回调（由支付网关调用） */
    @PostMapping("/pay/callback")
    public Result<?> payCallback(@RequestParam String orderNo) {
        memberService.markPaid(orderNo);
        return Result.success();
    }
}
```

> **注意**：`@RequestAttribute("userId")` 需配合你现有的 JWT 拦截器；若项目里是从 `SecurityContext` 或 `UserContext` 取，按现有写法替换即可。

### 6. 与现有业务打通

- **订单折扣**：在下单 Service（酒店/票务/商品）计算金额处调用 `memberService.calcDiscountPrice(userId, total)`。
- **积分倍率**：发放积分时乘以 `memberInfo.getPointsRate()`。
- **成长值**：支付成功回调中调用 `memberService.addGrowth(userId, amount.intValue(), 1, orderNo, "消费")`。
- **定时任务**：每日凌晨扫描付费会员过期，`isPaid` 置 0；生日当天发放礼包（写 `member_benefit_record`）。

---

## 四、用户端前端（minecraft-ui）

### 1. API

`src/api/member.js`：

```js
import request from '@/utils/request'

export const getMemberInfo   = () => request.get('/api/member/info')
export const getPackages     = () => request.get('/api/member/packages')
export const openMember      = (data) => request.post('/api/member/open', data)
export const signIn          = () => request.post('/api/member/sign')
export const getGrowthRecords= (params) => request.get('/api/member/growth/records', { params })
export const getMyOrders     = () => request.get('/api/member/orders')
```

### 2. 会员中心页 `src/views/member/MemberCenter.vue`

```vue
<template>
  <div class="member-center">
    <!-- 会员卡 -->
    <div class="member-card" :class="`lv-${info.level}`">
      <div class="lv-name">{{ info.levelName }}</div>
      <div class="growth">
        成长值 {{ info.growth }}
        <span v-if="info.nextLevelGrowth > 0">
          (距升级还需 {{ info.nextLevelGrowth }})
        </span>
      </div>
      <el-progress
        :percentage="progress"
        :stroke-width="10"
        :show-text="false"
        color="#ffd700" />
      <div v-if="info.isPaid" class="expire">
        付费会员有效期至 {{ formatDate(info.expireTime) }}
      </div>
    </div>

    <!-- 权益 -->
    <div class="benefits">
      <div class="benefit-item">
        <div class="val">{{ info.discount * 10 }}折</div>
        <div class="label">下单折扣</div>
      </div>
      <div class="benefit-item">
        <div class="val">{{ info.pointsRate }}x</div>
        <div class="label">积分倍率</div>
      </div>
      <div class="benefit-item">
        <div class="val">{{ info.signContinuous }}天</div>
        <div class="label">连续签到</div>
      </div>
    </div>

    <!-- 签到 -->
    <el-button
      type="primary"
      :disabled="info.todaySigned"
      @click="handleSign">
      {{ info.todaySigned ? '今日已签到' : '立即签到' }}
    </el-button>

    <!-- 套餐 -->
    <h3>开通会员</h3>
    <div class="packages">
      <div
        v-for="p in packages"
        :key="p.id"
        class="pkg"
        @click="handleOpen(p)">
        <div class="pkg-name">{{ p.name }}</div>
        <div class="pkg-price">¥{{ p.price }}</div>
        <div class="pkg-desc">{{ p.description }}</div>
      </div>
    </div>

    <!-- 成长值明细 -->
    <h3>成长值明细</h3>
    <el-table :data="records" size="small">
      <el-table-column prop="changeValue" label="变化" width="80">
        <template #default="{ row }">
          <span :class="row.changeValue > 0 ? 'plus' : 'minus'">
            {{ row.changeValue > 0 ? '+' : '' }}{{ row.changeValue }}
          </span>
        </template>
      </el-table-column>
      <el-table-column prop="remark" label="说明" />
      <el-table-column prop="createTime" label="时间" width="180" />
    </el-table>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMemberInfo, getPackages, openMember, signIn, getGrowthRecords } from '@/api/member'

const info = ref({ level: 0, discount: 1, pointsRate: 1, growth: 0, nextLevelGrowth: 0, todaySigned: false, signContinuous: 0 })
const packages = ref([])
const records = ref([])

const progress = computed(() => {
  const total = info.value.growth + info.value.nextLevelGrowth
  if (!total) return 100
  return Math.floor((info.value.growth / total) * 100)
})

const formatDate = (d) => d ? new Date(d).toLocaleDateString() : ''

async function load() {
  info.value = await getMemberInfo()
  packages.value = await getPackages()
  const res = await getGrowthRecords({ page: 1, size: 20 })
  records.value = res.records || res
}

async function handleSign() {
  try {
    info.value = await signIn()
    ElMessage.success('签到成功')
    load()
  } catch (e) {
    ElMessage.error(e.message || '签到失败')
  }
}

async function handleOpen(p) {
  await ElMessageBox.confirm(`确认开通【${p.name}】需支付 ¥${p.price}？`, '开通会员')
  await openMember({ packageId: p.id, payType: 1 })
  ElMessage.success('订单已生成，请完成支付')
}
onMounted(load)
</script>

<style scoped>
.member-card {
  border-radius: 12px;
  padding: 24px;
  color: #fff;
  background: linear-gradient(135deg, #888, #555);
}
.lv-1 { background: linear-gradient(135deg, #c0c0c0, #909090); }
.lv-2 { background: linear-gradient(135deg, #ffd700, #d4a017); }
.lv-3 { background: linear-gradient(135deg, #00c6ff, #0072ff); }
.lv-4 { background: linear-gradient(135deg, #a259ff, #f24e1e); }
.lv-name { font-size: 22px; font-weight: bold; margin-bottom: 8px; }
.growth { margin-bottom: 8px; }
.expire { margin-top: 8px; font-size: 13px; opacity: .9; }
.benefits { display: flex; gap: 16px; margin: 20px 0; }
.benefit-item { flex: 1; text-align: center; background: #f5f7fa; border-radius: 8px; padding: 16px; }
.benefit-item .val { font-size: 20px; font-weight: bold; color: #409eff; }
.packages { display: flex; gap: 16px; margin: 16px 0; }
.pkg { flex: 1; border: 1px solid #e4e7ed; border-radius: 8px; padding: 16px; cursor: pointer; transition: .2s; }
.pkg:hover { border-color: #409eff; box-shadow: 0 2px 12px rgba(64,158,255,.2); }
.pkg-name { font-weight: bold; }
.pkg-price { color: #f56c6c; font-size: 20px; margin: 8px 0; }
.plus { color: #67c23a; }
.minus { color: #f56c6c; }
</style>
```

### 3. 路由

`src/router/index.js` 中注册：

```js
{ path: '/member', name: 'MemberCenter', component: () => import('@/views/member/MemberCenter.vue'), meta: { title: '会员中心', requiresAuth: true } }
```

---

## 五、管理端（minecraft-admin-main）

在 `apps/web-antdv-next/src/views/travel/` 下新增 `member/`：

| 页面 | 功能 |
|------|------|
| `level/index.vue` | 会员等级 CRUD（门槛、折扣、积分倍率、权益） |
| `package/index.vue` | 套餐 CRUD（价格、时长、赠送） |
| `order/index.vue` | 会员订单查询、退款 |
| `user/index.vue` | 会员列表、手动调整成长值/等级、发放权益 |
| `record/index.vue` | 成长值流水、签到记录查询 |

对应后端管理接口放在 `controller/admin/MemberAdminController.java`，路径 `/api/admin/member/**`。

---

## 六、关键业务注意事项

1. **等级自动升降级**：`addGrowth` 内按 `growthMin` 倒序匹配，降级时也走同一逻辑。
2. **付费会员过期**：定时任务每日 0 点执行，`expireTime < now` 且 `isPaid=1` → `isPaid=0`（成长值等级保留）。
3. **续费叠加**：`open` 时若未过期从 `expireTime` 起算，已过期从当前时间起算。
4. **并发签到**：`member_sign_record` 上 `uk_user_date` 唯一索引兜底，配合事务。
5. **折扣与积分**：务必在**订单支付成功**时计算最终金额/积分，避免下单后等级变化导致金额不一致。
6. **权限**：会员接口走 JWT，管理端接口走角色权限（沿用现有 `@PreAuthorize` 或拦截器体系）。

---

## 七、开发排期建议

| 阶段 | 内容 | 工期 |
|------|------|------|
| 1 | 建表 + 实体/Mapper + 会员信息/等级 Service | 1天 |
| 2 | 开通订单 + 支付回调 + 折扣/积分打通 | 1.5天 |
| 3 | 签到 + 成长值流水 + 定时任务 | 1天 |
| 4 | 用户端会员中心页 + 接口联调 | 1.5天 |
| 5 | 管理端 5 个页面 + 管理接口 | 2天 |
| 6 | 测试（并发签到/续费/升降级/折扣） | 1天 |

---

以上方案与现有 `points_record`、`orders`、JWT 拦截器、Druid/MyBatis Plus 完全兼容，可平滑接入。需要我继续补充**管理端 Vue 页面完整代码**、**支付对接细节**或**推荐系统联动（会员优先推荐）**，可以告诉我具体模块。