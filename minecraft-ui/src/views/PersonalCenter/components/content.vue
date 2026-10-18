<template>
  <div class="pc-page">
    <!-- 环境光背景 -->
    <div class="pc-aurora" aria-hidden="true">
      <span class="pc-aurora__blob pc-aurora__blob--1"></span>
      <span class="pc-aurora__blob pc-aurora__blob--2"></span>
      <span class="pc-aurora__blob pc-aurora__blob--3"></span>
      <span class="pc-grid"></span>
    </div>

    <div class="pc-shell" v-loading="loading" element-loading-text="加载中...">
      <!-- 个人名片 -->
      <section class="pc-hero theme-transition">
        <div class="pc-hero__cover">
          <span class="pc-hero__cover-pattern"></span>
          <div class="pc-hero__actions">
            <el-button type="primary" :icon="Edit" round @click="openEditModal">修改信息</el-button>
            <el-button :icon="Lock" round @click="openPasswordModal">更改密码</el-button>
          </div>
        </div>

        <div class="pc-hero__body">
          <div class="pc-avatar-wrap">
            <div class="pc-avatar" role="button" tabindex="0" aria-label="上传照片" @click="triggerFileInput"
              @keydown.enter="triggerFileInput">
              <img v-if="avatarPreview" :src="avatarPreview" alt="用户头像" />
              <span v-else class="pc-avatar__ph"><i class="fas fa-user"></i></span>
              <span class="pc-avatar__hover">
                <i class="fas fa-camera"></i>
                <em>更换头像</em>
              </span>
            </div>
            <input type="file" ref="fileInput" @change="handleAvatarUpload" accept="image/*" style="display: none;" />
          </div>

          <div class="pc-hero__info">
            <div class="pc-hero__name-row">
              <h1 class="pc-hero__name">{{ userInfo.username || '未设置用户名' }}</h1>
              <span class="pc-chip pc-chip--online" v-if="userInfo.online !== undefined">
                <span class="pc-dot" :class="{ 'is-on': userInfo.online === 0 }"></span>
                {{ userInfo.online === 0 ? '当前在线' : '离线' }}
              </span>
            </div>
            <p class="pc-hero__sub" v-if="userInfo.nickname">{{ userInfo.nickname }}</p>
            <div class="pc-hero__meta">
              <span class="pc-chip" v-if="userInfo.account">
                <i class="fas fa-at"></i>{{ userInfo.account }}
              </span>
              <span class="pc-chip" v-if="userInfo.email">
                <i class="fas fa-envelope"></i>{{ userInfo.email }}
              </span>
              <span class="pc-chip" v-if="userInfo.phone">
                <i class="fas fa-phone-alt"></i>{{ userInfo.phone }}
              </span>
            </div>
          </div>
        </div>
      </section>

      <!-- 数据概览 -->
      <section class="pc-stats">
        <article v-for="item in statCards" :key="item.key" class="pc-stat theme-transition" :class="`tone-${item.tone}`">
          <span class="pc-stat__icon"><i :class="item.icon"></i></span>
          <span class="pc-stat__label">{{ item.label }}</span>
          <strong class="pc-stat__value">{{ item.value }}</strong>
        </article>
      </section>

      <!-- 会员积分与会员等级 -->
      <section class="pc-member">
        <article class="pc-member-card theme-transition">
          <header class="pc-member-card__head">
            <span class="pc-member-card__icon pc-member-card__icon--level"><i class="fas fa-crown"></i></span>
            <div class="pc-member-card__title">
              <h2>{{ $t('member.level') }}</h2>
              <p>{{ levelName || $t('member.levelNames.0') }}</p>
            </div>
            <span class="pc-member-card__tag" :class="{ 'is-on': memberInfo.isPaid === 1 }">
              {{ memberInfo.isPaid === 1 ? $t('member.paid') : $t('member.notPaid') }}
            </span>
          </header>

          <div class="pc-level">
            <div class="pc-level__row">
              <span>{{ $t('member.growth') }}</span>
              <strong>{{ memberInfo.growth || 0 }}</strong>
            </div>
            <div class="pc-level__bar" role="progressbar" aria-valuemin="0" aria-valuemax="100"
              :aria-valuenow="levelProgress">
              <span :style="{ width: `${levelProgress}%` }"></span>
            </div>
            <p class="pc-level__hint">
              {{ Number(memberInfo.nextLevelGrowth) > 0
                ? $t('member.nextLevel', { value: memberInfo.nextLevelGrowth })
                : $t('member.maxLevel') }}
            </p>
            <div class="pc-mini-chips">
              <span class="pc-mini-chip">
                <i class="fas fa-bolt"></i>{{ $t('member.stats.pointsRate') }} {{ pointsRateText }}
              </span>
              <span class="pc-mini-chip" v-if="memberInfo.isPaid === 1 && memberInfo.expireTime">
                <i class="fas fa-hourglass-half"></i>{{ $t('member.expireTime', { date: formatDay(memberInfo.expireTime) }) }}
              </span>
            </div>
          </div>

          <button class="pc-member-card__link" type="button" @click="goToMember">
            {{ $t('member.title') }}
            <i class="fas fa-chevron-right"></i>
          </button>
        </article>

        <article class="pc-member-card theme-transition">
          <header class="pc-member-card__head">
            <span class="pc-member-card__icon pc-member-card__icon--points"><i class="fas fa-coins"></i></span>
            <div class="pc-member-card__title">
              <h2>{{ $t('member.pointsTitle') }}</h2>
              <p>{{ $t('member.pointsDesc') }}</p>
            </div>
          </header>

          <div class="pc-points">
            <div class="pc-points__value">
              <strong>{{ userPoints ?? 0 }}</strong>
              <span>{{ $t('member.pointsBalance') }}</span>
            </div>
            <div class="pc-mini-chips">
              <span class="pc-mini-chip">
                <i class="fas fa-gem"></i>{{ $t('member.stats.pointsRate') }} {{ pointsRateText }}
              </span>
              <span class="pc-mini-chip" v-if="Number(memberInfo.signContinuous) > 0">
                <i class="fas fa-calendar-check"></i>{{ $t('member.signDaysValue', { value: memberInfo.signContinuous }) }}
              </span>
            </div>
          </div>

          <button class="pc-member-card__link" type="button" @click="goToPoints">
            {{ $t('member.pointsMall') }}
            <i class="fas fa-chevron-right"></i>
          </button>
        </article>
      </section>

      <!-- 快捷入口 -->
      <section class="pc-entries">
        <button v-for="entry in quickEntries" :key="entry.title" type="button" class="pc-entry theme-transition"
          @click="entry.action">
          <span class="pc-entry__icon"><i :class="entry.icon"></i></span>
          <span class="pc-entry__text">
            <strong>{{ entry.title }}</strong>
            <em>{{ entry.desc }}</em>
          </span>
          <i class="fas fa-chevron-right pc-entry__arrow"></i>
        </button>
      </section>

      <!-- 资料详情 -->
      <section class="pc-details">
        <article v-if="hasBasicInfo" class="pc-panel theme-transition">
          <header class="pc-panel__head">
            <span class="pc-panel__icon"><i class="fas fa-id-card"></i></span>
            <div>
              <h2>基本信息</h2>
              <p>你的基础个人资料</p>
            </div>
          </header>
          <div class="pc-rows">
            <div class="pc-row" v-if="userInfo.gender !== null">
              <span class="pc-row__k">性别</span>
              <span class="pc-row__v">{{ genderText }}</span>
            </div>
            <div class="pc-row" v-if="userInfo.age">
              <span class="pc-row__k">年龄</span>
              <span class="pc-row__v">{{ userInfo.age }} 岁</span>
            </div>
            <div class="pc-row" v-if="userInfo.occupation">
              <span class="pc-row__k">职业</span>
              <span class="pc-row__v">{{ userInfo.occupation }}</span>
            </div>
            <div class="pc-row" v-if="userInfo.hobbies">
              <span class="pc-row__k">爱好</span>
              <span class="pc-row__v">{{ userInfo.hobbies }}</span>
            </div>
          </div>
        </article>

        <article v-if="hasAboutInfo" class="pc-panel theme-transition">
          <header class="pc-panel__head">
            <span class="pc-panel__icon pc-panel__icon--alt"><i class="fas fa-feather-alt"></i></span>
            <div>
              <h2>关于我</h2>
              <p>让别人更了解你</p>
            </div>
          </header>
          <div class="pc-rows">
            <div class="pc-row pc-row--block" v-if="userInfo.signature">
              <span class="pc-row__k">个性签名</span>
              <span class="pc-row__v">{{ userInfo.signature }}</span>
            </div>
            <div class="pc-row pc-row--block" v-if="userInfo.bio">
              <span class="pc-row__k">个人简介</span>
              <span class="pc-row__v">{{ userInfo.bio }}</span>
            </div>
            <div class="pc-row pc-row--block" v-if="userInfo.experience">
              <span class="pc-row__k">经验</span>
              <span class="pc-row__v">{{ userInfo.experience }}</span>
            </div>
          </div>
        </article>
      </section>
    </div>

    <!-- 修改信息模态框 (Apple 风格玻璃拟态) -->
    <div v-if="showEditModal" class="modal-overlay" @click.self="">
      <div class="modal-dialog modal-edit" role="dialog" aria-modal="true" aria-labelledby="edit-modal-title">
        <header class="modal-header">
          <div class="modal-heading">
            <span class="modal-heading__badge"><i class="fas fa-user-pen"></i></span>
            <div class="modal-heading__text">
              <h3 id="edit-modal-title" class="modal-title">修改个人信息</h3>
              <p class="modal-subtitle">完善资料，让别人更了解你</p>
            </div>
          </div>
          <button class="modal-close" type="button" aria-label="关闭" @click="closeEditModal">
            <svg viewBox="0 0 24 24" width="18" height="18" aria-hidden="true">
              <path d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z" fill="currentColor"/>
            </svg>
          </button>
        </header>
        <div class="modal-body theme-scrollbar">
          <!-- 账号信息 -->
          <section class="form-section">
            <div class="form-section__head">
              <i class="fas fa-id-card"></i>
              <span>账号信息</span>
            </div>
            <div class="form-grid">
              <div class="form-item">
                <label class="form-label" for="edit-username">用户名</label>
                <div class="form-control">
                  <i class="fas fa-user control-icon"></i>
                  <input id="edit-username" class="form-input" type="text" v-model="editForm.username" placeholder="请输入用户名" />
                </div>
              </div>
              <div class="form-item">
                <label class="form-label" for="edit-nickname">昵称</label>
                <div class="form-control">
                  <i class="fas fa-id-badge control-icon"></i>
                  <input id="edit-nickname" class="form-input" type="text" v-model="editForm.nickname" placeholder="请输入昵称" />
                </div>
              </div>
              <div class="form-item">
                <label class="form-label" for="edit-email">邮箱</label>
                <div class="form-control">
                  <i class="fas fa-envelope control-icon"></i>
                  <input id="edit-email" class="form-input" type="email" v-model="editForm.email" placeholder="请输入邮箱" />
                </div>
              </div>
              <div class="form-item">
                <label class="form-label" for="edit-phone">手机号</label>
                <div class="form-control">
                  <i class="fas fa-phone-alt control-icon"></i>
                  <input id="edit-phone" class="form-input" type="tel" v-model="editForm.phone" placeholder="请输入手机号" />
                </div>
              </div>
            </div>
          </section>

          <!-- 个人资料 -->
          <section class="form-section">
            <div class="form-section__head">
              <i class="fas fa-user-circle"></i>
              <span>个人资料</span>
            </div>
            <div class="form-grid">
              <div class="form-item">
                <label class="form-label">性别</label>
                <div class="segmented" role="radiogroup" aria-label="性别">
                  <button type="button" class="segmented__item" :class="{ 'is-active': genderOption === 'secret' }" @click="editForm.gender = null">保密</button>
                  <button type="button" class="segmented__item" :class="{ 'is-active': genderOption === 'male' }" @click="editForm.gender = 1">男</button>
                  <button type="button" class="segmented__item" :class="{ 'is-active': genderOption === 'female' }" @click="editForm.gender = 0">女</button>
                </div>
              </div>
              <div class="form-item">
                <label class="form-label" for="edit-age">年龄</label>
                <div class="form-control">
                  <i class="fas fa-birthday-cake control-icon"></i>
                  <input id="edit-age" class="form-input" type="number" min="1" max="120" v-model.number="editForm.age" placeholder="请输入年龄" />
                </div>
              </div>
              <div class="form-item">
                <label class="form-label" for="edit-occupation">职业</label>
                <div class="form-control">
                  <i class="fas fa-briefcase control-icon"></i>
                  <input id="edit-occupation" class="form-input" type="text" v-model="editForm.occupation" placeholder="请输入职业" />
                </div>
              </div>
              <div class="form-item">
                <label class="form-label" for="edit-hobbies">爱好</label>
                <div class="form-control">
                  <i class="fas fa-heart control-icon"></i>
                  <input id="edit-hobbies" class="form-input" type="text" v-model="editForm.hobbies" placeholder="请输入爱好" />
                </div>
              </div>
            </div>
          </section>

          <!-- 关于我 -->
          <section class="form-section">
            <div class="form-section__head">
              <i class="fas fa-feather-alt"></i>
              <span>关于我</span>
            </div>
            <div class="form-grid">
              <div class="form-item form-item--block">
                <label class="form-label" for="edit-bio">个人简介</label>
                <textarea id="edit-bio" class="form-input form-textarea" rows="3" v-model="editForm.bio" placeholder="介绍一下自己"></textarea>
              </div>
              <div class="form-item form-item--block">
                <label class="form-label" for="edit-signature">个性签名</label>
                <div class="form-control">
                  <i class="fas fa-pen-nib control-icon"></i>
                  <input id="edit-signature" class="form-input" type="text" v-model="editForm.signature" placeholder="请输入签名" />
                </div>
              </div>
              <div class="form-item form-item--block">
                <label class="form-label" for="edit-experience">经验</label>
                <textarea id="edit-experience" class="form-input form-textarea" rows="3" v-model="editForm.experience" placeholder="请输入经验"></textarea>
              </div>
            </div>
          </section>
        </div>
        <footer class="modal-footer">
          <button class="btn btn-secondary" type="button" @click="closeEditModal">取消</button>
          <button class="btn btn-primary" type="button" :disabled="loading" @click="handleUpdateUserInfo">
            <span v-if="loading" class="btn-spinner" aria-hidden="true"></span>
            {{ loading ? '保存中…' : '保存修改' }}
          </button>
        </footer>
      </div>
    </div>

    <!-- 更改密码模态框 (Apple 风格玻璃拟态) -->
    <div v-if="showPasswordModal" class="modal-overlay" @click.self="">
      <div class="modal-dialog modal-password" role="dialog" aria-modal="true" aria-labelledby="pwd-modal-title">
        <header class="modal-header">
          <div class="modal-heading">
            <span class="modal-heading__badge modal-heading__badge--lock"><i class="fas fa-shield-halved"></i></span>
            <div class="modal-heading__text">
              <h3 id="pwd-modal-title" class="modal-title">更改密码</h3>
              <p class="modal-subtitle">定期更换密码，保障账号安全</p>
            </div>
          </div>
          <button class="modal-close" type="button" aria-label="关闭" @click="closePasswordModal">
            <svg viewBox="0 0 24 24" width="18" height="18" aria-hidden="true">
              <path d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z" fill="currentColor"/>
            </svg>
          </button>
        </header>
        <div class="modal-body theme-scrollbar">
          <div class="form-grid">
            <div class="form-item form-item--block">
              <label class="form-label" for="pwd-old">原密码</label>
              <div class="form-control form-control--pwd">
                <i class="fas fa-lock control-icon"></i>
                <input id="pwd-old" class="form-input" :type="showOldPwd ? 'text' : 'password'" v-model="passwordForm.oldPassword" placeholder="请输入原密码" />
                <button type="button" class="pwd-toggle" :aria-label="showOldPwd ? '隐藏密码' : '显示密码'" @click="showOldPwd = !showOldPwd">
                  <svg v-if="showOldPwd" viewBox="0 0 24 24" width="18" height="18" aria-hidden="true">
                    <path d="M12 4.5C7 4.5 2.73 7.61 1 12c1.73 4.39 6 7.5 11 7.5s9.27-3.11 11-7.5c-1.73-4.39-6-7.5-11-7.5zM12 17c-2.76 0-5-2.24-5-5s2.24-5 5-5 5 2.24 5 5-2.24 5-5 5zm0-8c-1.66 0-3 1.34-3 3s1.34 3 3 3 3-1.34 3-3-1.34-3-3-3z" fill="currentColor"/>
                  </svg>
                  <svg v-else viewBox="0 0 24 24" width="18" height="18" aria-hidden="true">
                    <path d="M12 7c2.76 0 5 2.24 5 5 0 .65-.13 1.26-.36 1.83l2.92 2.92c1.51-1.26 2.7-2.89 3.43-4.75-1.73-4.39-6-7.5-11-7.5-1.4 0-2.74.25-3.98.7l2.16 2.16C10.74 7.13 11.35 7 12 7zM2 4.27l2.28 2.28.46.46A11.804 11.804 0 001 12c1.73 4.39 6 7.5 11 7.5 1.55 0 3.03-.3 4.38-.84l.42.42L19.73 22 21 20.73 3.27 3 2 4.27zM7.53 9.8l1.55 1.55c-.05.21-.08.43-.08.65 0 1.66 1.34 3 3 3 .22 0 .44-.03.65-.08l1.55 1.55c-.67.33-1.41.53-2.2.53-2.76 0-5-2.24-5-5 0-.79.2-1.53.53-2.2zm4.31-.78l3.15 3.15.02-.16c0-1.66-1.34-3-3-3l-.17.01z" fill="currentColor"/>
                  </svg>
                </button>
              </div>
            </div>
            <div class="form-item form-item--block">
              <label class="form-label" for="pwd-new">新密码</label>
              <div class="form-control form-control--pwd">
                <i class="fas fa-key control-icon"></i>
                <input id="pwd-new" class="form-input" :type="showNewPwd ? 'text' : 'password'" v-model="passwordForm.newPassword" placeholder="请输入新密码" />
                <button type="button" class="pwd-toggle" :aria-label="showNewPwd ? '隐藏密码' : '显示密码'" @click="showNewPwd = !showNewPwd">
                  <svg v-if="showNewPwd" viewBox="0 0 24 24" width="18" height="18" aria-hidden="true">
                    <path d="M12 4.5C7 4.5 2.73 7.61 1 12c1.73 4.39 6 7.5 11 7.5s9.27-3.11 11-7.5c-1.73-4.39-6-7.5-11-7.5zM12 17c-2.76 0-5-2.24-5-5s2.24-5 5-5 5 2.24 5 5-2.24 5-5 5zm0-8c-1.66 0-3 1.34-3 3s1.34 3 3 3 3-1.34 3-3-1.34-3-3-3z" fill="currentColor"/>
                  </svg>
                  <svg v-else viewBox="0 0 24 24" width="18" height="18" aria-hidden="true">
                    <path d="M12 7c2.76 0 5 2.24 5 5 0 .65-.13 1.26-.36 1.83l2.92 2.92c1.51-1.26 2.7-2.89 3.43-4.75-1.73-4.39-6-7.5-11-7.5-1.4 0-2.74.25-3.98.7l2.16 2.16C10.74 7.13 11.35 7 12 7zM2 4.27l2.28 2.28.46.46A11.804 11.804 0 001 12c1.73 4.39 6 7.5 11 7.5 1.55 0 3.03-.3 4.38-.84l.42.42L19.73 22 21 20.73 3.27 3 2 4.27zM7.53 9.8l1.55 1.55c-.05.21-.08.43-.08.65 0 1.66 1.34 3 3 3 .22 0 .44-.03.65-.08l1.55 1.55c-.67.33-1.41.53-2.2.53-2.76 0-5-2.24-5-5 0-.79.2-1.53.53-2.2zm4.31-.78l3.15 3.15.02-.16c0-1.66-1.34-3-3-3l-.17.01z" fill="currentColor"/>
                  </svg>
                </button>
              </div>
            </div>
            <div class="form-item form-item--block">
              <label class="form-label" for="pwd-confirm">确认密码</label>
              <div class="form-control form-control--pwd">
                <i class="fas fa-lock control-icon"></i>
                <input id="pwd-confirm" class="form-input" :type="showConfirmPwd ? 'text' : 'password'" v-model="passwordForm.confirmPassword" placeholder="请再次输入新密码" />
                <button type="button" class="pwd-toggle" :aria-label="showConfirmPwd ? '隐藏密码' : '显示密码'" @click="showConfirmPwd = !showConfirmPwd">
                  <svg v-if="showConfirmPwd" viewBox="0 0 24 24" width="18" height="18" aria-hidden="true">
                    <path d="M12 4.5C7 4.5 2.73 7.61 1 12c1.73 4.39 6 7.5 11 7.5s9.27-3.11 11-7.5c-1.73-4.39-6-7.5-11-7.5zM12 17c-2.76 0-5-2.24-5-5s2.24-5 5-5 5 2.24 5 5-2.24 5-5 5zm0-8c-1.66 0-3 1.34-3 3s1.34 3 3 3 3-1.34 3-3-1.34-3-3-3z" fill="currentColor"/>
                  </svg>
                  <svg v-else viewBox="0 0 24 24" width="18" height="18" aria-hidden="true">
                    <path d="M12 7c2.76 0 5 2.24 5 5 0 .65-.13 1.26-.36 1.83l2.92 2.92c1.51-1.26 2.7-2.89 3.43-4.75-1.73-4.39-6-7.5-11-7.5-1.4 0-2.74.25-3.98.7l2.16 2.16C10.74 7.13 11.35 7 12 7zM2 4.27l2.28 2.28.46.46A11.804 11.804 0 001 12c1.73 4.39 6 7.5 11 7.5 1.55 0 3.03-.3 4.38-.84l.42.42L19.73 22 21 20.73 3.27 3 2 4.27zM7.53 9.8l1.55 1.55c-.05.21-.08.43-.08.65 0 1.66 1.34 3 3 3 .22 0 .44-.03.65-.08l1.55 1.55c-.67.33-1.41.53-2.2.53-2.76 0-5-2.24-5-5 0-.79.2-1.53.53-2.2zm4.31-.78l3.15 3.15.02-.16c0-1.66-1.34-3-3-3l-.17.01z" fill="currentColor"/>
                  </svg>
                </button>
              </div>
            </div>
          </div>
        </div>
        <footer class="modal-footer">
          <button class="btn btn-secondary" type="button" @click="closePasswordModal">取消</button>
          <button class="btn btn-primary" type="button" :disabled="passwordLoading" @click="handleUpdatePassword">
            <span v-if="passwordLoading" class="btn-spinner" aria-hidden="true"></span>
            {{ passwordLoading ? '更改中…' : '确认更改' }}
          </button>
        </footer>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed } from 'vue';
import { ElMessage } from 'element-plus';
import { Edit, Lock } from '@element-plus/icons-vue';
import { useRouter } from 'vue-router';
import { useI18n } from 'vue-i18n';
import { getUserInfo, updateUserInfo, updatePassword, uploadAvatar } from '@/api/user';
import { getUserPoints } from '@/api/points';
import { getMemberInfo } from '@/api/member';
import { useAutoRefresh } from '@/hooks/useAutoRefresh';

// 用户信息 (根据后端返回完整字段)
const userInfo = ref({
  id: '',
  account: '',
  username: '',
  email: '',
  phone: '',
  status: '',
  createTime: '',
  updateTime: '',
  avatar: '',
  gender: null,
  age: null,
  occupation: '',
  hobbies: '',
  bio: '',
  online: 0,
  password: '',
  nickname:'',
  signature:'',
  experience:''
});

// 积分信息
const userPoints = ref(0);

// 会员信息（等级 / 成长值 / 积分倍率）
const memberInfo = ref({
  growth: 0,
  level: 0,
  levelName: '',
  levelMinGrowth: 0,
  nextLevelGrowth: 0,
  discount: 1,
  pointsRate: 1,
  isPaid: 0,
  expireTime: null,
  signContinuous: 0
});

// 加载状态
const loading = ref(false);
const passwordLoading = ref(false);

// 路由实例
const router = useRouter();

// 国际化
const { t, te } = useI18n();

// 模态框状态
const showEditModal = ref(false);
const showPasswordModal = ref(false);

// 修改信息表单
const editForm = ref({
  username: '',
  email: '',
  phone: '',
  gender: null,
  age: null,
  occupation: '',
  hobbies: '',
  bio: '',
  nickname:'',
  signature:'',
  experience:''
});

// 更改密码表单
const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
});

// 密码显示开关
const showOldPwd = ref(false);
const showNewPwd = ref(false);
const showConfirmPwd = ref(false);

// 头像预览
const avatarPreview = ref('');

// 触发文件输入
const fileInput = ref(null);
const triggerFileInput = () => {
  fileInput.value.click();
};

// 性别文案
const genderText = computed(() => {
  if (userInfo.value.gender === 1 || userInfo.value.gender === '1') return '男';
  if (userInfo.value.gender === 0 || userInfo.value.gender === '0') return '女';
  return '未设置';
});

// 性别分段控件选中项
const genderOption = computed(() => {
  const g = editForm.value.gender;
  if (g === 1 || g === '1') return 'male';
  if (g === 0 || g === '0') return 'female';
  return 'secret';
});

// 分区是否有内容
const hasBasicInfo = computed(() =>
  userInfo.value.gender !== null ||
  !!userInfo.value.age ||
  !!userInfo.value.occupation ||
  !!userInfo.value.hobbies
);
const hasAboutInfo = computed(() =>
  !!userInfo.value.signature ||
  !!userInfo.value.bio ||
  !!userInfo.value.experience
);

// 顶部数据概览
const statCards = computed(() => [
  { key: 'points', label: '积分余额', value: userPoints.value ?? 0, icon: 'fas fa-coins', tone: 'gold' },
  { key: 'status', label: '账号状态', value: userInfo.value.status === 1 ? '正常' : '禁用', icon: 'fas fa-shield-alt', tone: 'green' },
  { key: 'createTime', label: '注册时间', value: formatDay(userInfo.value.createTime) || '—', icon: 'fas fa-calendar-plus', tone: 'blue' },
  { key: 'updateTime', label: '最近更新', value: formatDay(userInfo.value.updateTime) || '—', icon: 'fas fa-clock', tone: 'violet' }
]);

// 会员等级名称（按等级序号取国际化文案，兜底后端返回名称）
const levelName = computed(() => {
  const key = `member.levelNames.${memberInfo.value.level}`;
  return te(key) ? t(key) : memberInfo.value.levelName || '';
});

// 积分倍率文案
const pointsRateText = computed(() => t('member.pointsRate', {
  value: Number(memberInfo.value.pointsRate ?? 1)
}));

// 当前等级成长进度
const levelProgress = computed(() => {
  const growth = Number(memberInfo.value.growth) || 0;
  const min = Number(memberInfo.value.levelMinGrowth) || 0;
  const next = Number(memberInfo.value.nextLevelGrowth) || 0;
  if (!next) return 100;
  const span = growth - min + next;
  if (span <= 0) return 0;
  return Math.min(100, Math.max(0, Math.round(((growth - min) / span) * 100)));
});

// 初始化获取用户信息
const onEscKey = (e) => {
  if (e.key !== 'Escape') return;
  if (showEditModal.value)     closeEditModal();
  if (showPasswordModal.value) closePasswordModal();
};

onMounted(() => {
  fetchUserInfo();
  fetchUserPoints();
  fetchMemberInfo();
  window.addEventListener('keydown', onEscKey);
});

onUnmounted(() => {
  window.removeEventListener('keydown', onEscKey);
});

// 获取用户信息 (从localStorage获取token)
const fetchUserInfo = async () => {
  try {
    loading.value = true;
    const token = localStorage.getItem('token');
    const response = await getUserInfo(token);
    if (response.code === 200) {
      userInfo.value = response.data;
      // 设置头像预览
      if (response.data.avatar) {
        avatarPreview.value = response.data.avatar;
      }
      console.log('用户信息:', response.data);
    } else {
      ElMessage.error(response.message || '获取用户信息失败');
    }
  } catch (error) {
    ElMessage.error('获取用户信息失败，请检查网络');
    console.error('获取用户信息失败:', error);
  } finally {
    loading.value = false;
  }
};

// 获取用户积分
const fetchUserPoints = async () => {
  try {
    const response = await getUserPoints();
    if (response.code === 200) {
      userPoints.value = response.data;
      console.log('用户积分:', response.data);
    } else {
      ElMessage.error(response.message || '获取用户积分失败');
    }
  } catch (error) {
    console.error('获取用户积分失败:', error);
  }
};

// 获取会员信息（等级 / 成长值 / 积分倍率）
const fetchMemberInfo = async () => {
  try {
    const response = await getMemberInfo();
    if (response?.code === 200 && response.data) {
      memberInfo.value = { ...memberInfo.value, ...response.data };
    }
  } catch (error) {
    console.error('获取会员信息失败:', error);
  }
};

// 数据变更后自动刷新
useAutoRefresh(fetchUserInfo);
useAutoRefresh(fetchUserPoints);
useAutoRefresh(fetchMemberInfo);

// 跳转到积分商城
const goToPoints = () => {
  router.push('/points');
};

// 跳转到会员中心
const goToMember = () => {
  router.push('/member');
};

// 跳转到物流详情
const goToLogistics = () => {
  router.push('/logistics');
};

// 跳转到售后服务
const goToAfterSale = () => {
  router.push('/aftersale');
};

// 跳转到我的举报
const goToMyReport = () => {
  router.push('/myreport');
};

// 快捷入口
const quickEntries = [
  { title: '积分商城', desc: '用积分兑换心仪好物', icon: 'fas fa-gift', action: goToPoints },
  { title: '物流详情', desc: '查看订单配送进度', icon: 'fas fa-truck', action: goToLogistics },
  { title: '售后服务', desc: '退换维修一站处理', icon: 'fas fa-headset', action: goToAfterSale },
  { title: '我的举报', desc: '查看举报记录与进度', icon: 'fas fa-flag', action: goToMyReport }
];

// 处理头像上传
const handleAvatarUpload = async (event) => {
  const file = event.target.files[0];
  if (!file) return;

  // 验证文件类型
  const validTypes = ['image/jpeg', 'image/png', 'image/gif', 'image/webp'];
  if (!validTypes.includes(file.type)) {
    ElMessage.error('请选择有效的图片文件');
    return;
  }

  // 验证文件大小 (2MB)
  const maxSize = 2 * 1024 * 1024;
  if (file.size > maxSize) {
    ElMessage.error('图片大小不能超过2MB');
    return;
  }

  try {
    loading.value = true;
    const formData = new FormData();
    formData.append('avatar', file);

    const response = await uploadAvatar(formData);
    if (response.code === 200) {
      ElMessage.success('头像上传成功');
      // 更新头像预览
      avatarPreview.value = response.data;
      // 重新获取用户信息
      await fetchUserInfo();
    } else {
      ElMessage.error(response.message || '头像上传失败');
    }
  } catch (error) {
    ElMessage.error('头像上传失败，请检查网络');
    console.error('头像上传失败:', error);
  } finally {
    loading.value = false;
    // 重置文件输入
    event.target.value = '';
  }
};

// 打开修改信息模态框
const openEditModal = () => {
  // 填充表单数据
  editForm.value = {
    username: userInfo.value.username || '',
    email: userInfo.value.email || '',
    phone: userInfo.value.phone || '',
    gender: userInfo.value.gender,
    age: userInfo.value.age,
    occupation: userInfo.value.occupation || '',
    hobbies: userInfo.value.hobbies || '',
    bio: userInfo.value.bio || '',
    nickname: userInfo.value.nickname || '',
    signature: userInfo.value.signature || '',
    experience: userInfo.value.experience || ''
  };
  showEditModal.value = true;
};

// 关闭修改信息模态框
const closeEditModal = () => {
  showEditModal.value = false;
};

// 打开更改密码模态框
const openPasswordModal = () => {
  // 重置表单
  passwordForm.value = {
    oldPassword: '',
    newPassword: '',
    confirmPassword: ''
  };
  showPasswordModal.value = true;
};

// 关闭更改密码模态框
const closePasswordModal = () => {
  showPasswordModal.value = false;
};

// 更新用户信息
const handleUpdateUserInfo = async () => {
  try {
    loading.value = true;

    // 验证表单
    if (!editForm.value.username) {
      ElMessage.warning('请输入用户名');
      return;
    }
    if (!editForm.value.email) {
      ElMessage.warning('请输入邮箱');
      return;
    }
    if (!editForm.value.phone) {
      ElMessage.warning('请输入手机号');
      return;
    }

    // 构建更新数据
    const updateData = {
      id: userInfo.value.id,
      username: editForm.value.username,
      email: editForm.value.email,
      phone: editForm.value.phone,
      gender: editForm.value.gender,
      age: editForm.value.age,
      occupation: editForm.value.occupation,
      hobbies: editForm.value.hobbies,
      bio: editForm.value.bio,
      nickname: editForm.value.nickname,
      signature: editForm.value.signature,
      experience: editForm.value.experience
    };

    const response = await updateUserInfo(updateData);
    if (response.code === 200) {
      ElMessage.success('修改成功');
      // 重新获取用户信息
      await fetchUserInfo();
      // 关闭模态框
      closeEditModal();
    } else {
      ElMessage.error(response.message || '修改失败');
    }
  } catch (error) {
    ElMessage.error('修改失败，请检查网络');
    console.error('修改用户信息失败:', error);
  } finally {
    loading.value = false;
  }
};

// 更新密码
const handleUpdatePassword = async () => {
  try {
    passwordLoading.value = true;

    // 验证表单
    if (!passwordForm.value.oldPassword) {
      ElMessage.warning('请输入原密码');
      return;
    }
    if (!passwordForm.value.newPassword) {
      ElMessage.warning('请输入新密码');
      return;
    }
    if (passwordForm.value.newPassword !== passwordForm.value.confirmPassword) {
      ElMessage.warning('两次输入的密码不一致');
      return;
    }

    const response = await updatePassword(passwordForm.value.oldPassword, passwordForm.value.newPassword);
    if (response.code === 200) {
      ElMessage.success('密码更改成功');
      // 关闭模态框
      closePasswordModal();
    } else {
      ElMessage.error(response.message || '密码更改失败');
    }
  } catch (error) {
    ElMessage.error('密码更改失败，请检查网络');
    console.error('更改密码失败:', error);
  } finally {
    passwordLoading.value = false;
  }
};

// 格式化日期（用于概览卡片）
const formatDay = (dateString) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  if (Number.isNaN(date.getTime())) return '';
  return date.toLocaleDateString('zh-CN', { year: 'numeric', month: '2-digit', day: '2-digit' });
};
</script>

<style scoped>
/* ========== 页面外壳 ========== */
.pc-page {
  position: relative;
  min-height: 100vh;
  padding: 40px 24px 64px;
  background: var(--theme-bg-app);
  color: var(--theme-text-1);
  overflow: hidden;
}

/* 环境光装饰 */
.pc-aurora {
  position: absolute;
  inset: 0;
  z-index: 0;
  pointer-events: none;
}

.pc-aurora__blob {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.55;
}

.pc-aurora__blob--1 {
  width: 460px;
  height: 460px;
  top: -180px;
  right: -120px;
  background: var(--theme-primary-soft);
}

.pc-aurora__blob--2 {
  width: 380px;
  height: 380px;
  bottom: -160px;
  left: -140px;
  background: color-mix(in srgb, var(--theme-accent-2) 22%, transparent);
}

.pc-aurora__blob--3 {
  width: 300px;
  height: 300px;
  top: 42%;
  left: 58%;
  background: color-mix(in srgb, var(--theme-accent-3) 16%, transparent);
}

.pc-grid {
  position: absolute;
  inset: 0;
  background-image:
    linear-gradient(to right, var(--theme-hairline) 1px, transparent 1px),
    linear-gradient(to bottom, var(--theme-hairline) 1px, transparent 1px);
  background-size: 58px 58px;
  -webkit-mask-image: radial-gradient(ellipse 78% 58% at 50% 0%, #000 18%, transparent 76%);
  mask-image: radial-gradient(ellipse 78% 58% at 50% 0%, #000 18%, transparent 76%);
  opacity: 0.6;
}

.pc-shell {
  position: relative;
  z-index: 1;
  max-width: 1120px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

/* ========== 个人名片 ========== */
.pc-hero {
  position: relative;
  border-radius: var(--theme-radius-xl);
  background: var(--theme-bg-surface);
  border: 1px solid var(--theme-border);
  box-shadow: var(--theme-shadow-md);
  overflow: hidden;
}

.pc-hero__cover {
  position: relative;
  height: 140px;
  background: var(--theme-gradient);
  overflow: hidden;
}

.pc-hero__cover-pattern {
  position: absolute;
  inset: 0;
  background-image:
    radial-gradient(circle at 18% 130%, rgba(255, 255, 255, 0.42), transparent 58%),
    repeating-linear-gradient(115deg, rgba(255, 255, 255, 0.16) 0 2px, transparent 2px 14px);
  opacity: 0.7;
}

.pc-hero__actions {
  position: absolute;
  top: 16px;
  right: 16px;
  display: flex;
  gap: 10px;
  z-index: 2;
}

.pc-hero__actions :deep(.el-button) {
  background: rgba(255, 255, 255, 0.18) !important;
  border: 1px solid rgba(255, 255, 255, 0.38) !important;
  color: #fff !important;
  font-weight: 500;
  backdrop-filter: blur(6px);
  -webkit-backdrop-filter: blur(6px);
  transition: background-color var(--theme-duration) var(--theme-ease),
    transform var(--theme-duration) var(--theme-ease);
}

.pc-hero__actions :deep(.el-button:hover) {
  background: rgba(255, 255, 255, 0.32) !important;
  transform: translateY(-1px);
}

.pc-hero__body {
  display: flex;
  align-items: flex-end;
  gap: 22px;
  padding: 0 28px 26px;
}

.pc-avatar-wrap {
  flex: 0 0 auto;
  margin-top: -54px;
}

.pc-avatar {
  position: relative;
  width: 112px;
  height: 112px;
  border-radius: 30px;
  border: 4px solid var(--theme-bg-surface);
  background: var(--theme-bg-surface-2);
  box-shadow: var(--theme-shadow-md);
  overflow: hidden;
  cursor: pointer;
  transition: transform var(--theme-duration) var(--theme-ease),
    box-shadow var(--theme-duration) var(--theme-ease);
}

.pc-avatar:hover {
  transform: translateY(-3px);
  box-shadow: var(--theme-shadow-lg);
}

.pc-avatar:focus-visible {
  outline: none;
  box-shadow: var(--theme-ring), var(--theme-shadow-md);
}

.pc-avatar img {
  display: block;
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.pc-avatar__ph {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
  font-size: 42px;
  color: var(--theme-text-3);
}

.pc-avatar__hover {
  position: absolute;
  inset: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 6px;
  background: var(--theme-overlay);
  color: #fff;
  font-size: 20px;
  opacity: 0;
  transition: opacity var(--theme-duration) var(--theme-ease);
}

.pc-avatar__hover em {
  font-style: normal;
  font-size: 12px;
  letter-spacing: 0.04em;
}

.pc-avatar:hover .pc-avatar__hover {
  opacity: 1;
}

.pc-hero__info {
  flex: 1;
  min-width: 0;
  padding-bottom: 2px;
}

.pc-hero__name-row {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 12px;
}

.pc-hero__name {
  margin: 0;
  font-size: 30px;
  font-weight: 700;
  line-height: 1.15;
  letter-spacing: -0.02em;
  color: var(--theme-text-1);
}

.pc-hero__sub {
  margin: 6px 0 0;
  font-size: 14px;
  color: var(--theme-text-2);
}

.pc-hero__meta {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 14px;
}

/* 胶囊标签 */
.pc-chip {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  border-radius: var(--theme-radius-pill);
  background: var(--theme-bg-surface-2);
  color: var(--theme-text-2);
  font-size: 12.5px;
  white-space: nowrap;
}

.pc-chip i {
  font-size: 11px;
  opacity: 0.85;
}

.pc-dot {
  width: 7px;
  height: 7px;
  border-radius: 50%;
  background: var(--theme-text-3);
}

.pc-dot.is-on {
  background: var(--theme-success);
  box-shadow: 0 0 0 3px var(--theme-success-soft);
}

/* ========== 数据概览 ========== */
.pc-stats {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 16px;
}

.pc-stat {
  display: flex;
  flex-direction: column;
  gap: 4px;
  padding: 18px 20px;
  border-radius: var(--theme-radius-lg);
  background: var(--theme-bg-surface);
  border: 1px solid var(--theme-border);
  box-shadow: var(--theme-shadow-sm);
  transition: transform var(--theme-duration) var(--theme-ease),
    box-shadow var(--theme-duration) var(--theme-ease),
    border-color var(--theme-duration) var(--theme-ease);
}

.pc-stat:hover {
  transform: translateY(-3px);
  border-color: var(--theme-border-strong);
  box-shadow: var(--theme-shadow-md);
}

.pc-stat__icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  margin-bottom: 6px;
  border-radius: var(--theme-radius-sm);
  font-size: 16px;
}

.pc-stat__label {
  font-size: 11.5px;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  color: var(--theme-text-3);
}

.pc-stat__value {
  font-size: 20px;
  font-weight: 700;
  letter-spacing: -0.01em;
  color: var(--theme-text-1);
}

.tone-gold .pc-stat__icon {
  background: var(--theme-warning-soft);
  color: var(--theme-warning);
}

.tone-green .pc-stat__icon {
  background: var(--theme-success-soft);
  color: var(--theme-success);
}

.tone-blue .pc-stat__icon {
  background: var(--theme-info-soft);
  color: var(--theme-info);
}

.tone-violet .pc-stat__icon {
  background: color-mix(in srgb, var(--theme-accent-2) 16%, transparent);
  color: var(--theme-accent-2);
}

/* ========== 会员积分与会员等级 ========== */
.pc-member {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
  gap: 16px;
}

.pc-member-card {
  display: flex;
  flex-direction: column;
  gap: 18px;
  padding: 22px 24px;
  border-radius: var(--theme-radius-lg);
  background: var(--theme-bg-surface);
  border: 1px solid var(--theme-border);
  box-shadow: var(--theme-shadow-sm);
  transition: transform var(--theme-duration) var(--theme-ease),
    box-shadow var(--theme-duration) var(--theme-ease),
    border-color var(--theme-duration) var(--theme-ease);
}

.pc-member-card:hover {
  transform: translateY(-3px);
  border-color: var(--theme-border-strong);
  box-shadow: var(--theme-shadow-md);
}

.pc-member-card__head {
  display: flex;
  align-items: center;
  gap: 12px;
}

.pc-member-card__icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  flex: 0 0 auto;
  width: 40px;
  height: 40px;
  border-radius: var(--theme-radius-sm);
  font-size: 16px;
}

.pc-member-card__icon--level {
  background: var(--theme-warning-soft);
  color: var(--theme-warning);
}

.pc-member-card__icon--points {
  background: var(--theme-info-soft);
  color: var(--theme-info);
}

.pc-member-card__title {
  flex: 1;
  min-width: 0;
}

.pc-member-card__title h2 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: var(--theme-text-1);
}

.pc-member-card__title p {
  margin: 2px 0 0;
  font-size: 12.5px;
  color: var(--theme-text-3);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.pc-member-card__tag {
  flex: 0 0 auto;
  padding: 4px 10px;
  border-radius: var(--theme-radius-pill);
  background: var(--theme-bg-surface-2);
  color: var(--theme-text-2);
  font-size: 12px;
  font-weight: 500;
}

.pc-member-card__tag.is-on {
  background: var(--theme-warning-soft);
  color: var(--theme-warning);
}

.pc-level {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.pc-level__row {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  font-size: 13px;
  color: var(--theme-text-2);
}

.pc-level__row strong {
  font-size: 22px;
  font-weight: 700;
  letter-spacing: -0.01em;
  color: var(--theme-text-1);
}

.pc-level__bar {
  height: 8px;
  border-radius: var(--theme-radius-pill);
  background: var(--theme-bg-surface-2);
  overflow: hidden;
}

.pc-level__bar span {
  display: block;
  height: 100%;
  border-radius: inherit;
  background: linear-gradient(90deg, var(--theme-warning), var(--theme-primary));
  transition: width 0.5s var(--theme-ease);
}

.pc-level__hint {
  margin: 0;
  font-size: 12.5px;
  color: var(--theme-text-3);
}

.pc-mini-chips {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.pc-mini-chip {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 5px 10px;
  border-radius: var(--theme-radius-pill);
  background: var(--theme-bg-surface-2);
  color: var(--theme-text-2);
  font-size: 12px;
}

.pc-mini-chip i {
  font-size: 11px;
  opacity: 0.85;
}

.pc-points {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.pc-points__value {
  display: flex;
  align-items: baseline;
  gap: 10px;
}

.pc-points__value strong {
  font-size: 34px;
  font-weight: 700;
  letter-spacing: -0.02em;
  line-height: 1.1;
  color: var(--theme-text-1);
}

.pc-points__value span {
  font-size: 12.5px;
  color: var(--theme-text-3);
}

.pc-member-card__link {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  align-self: flex-start;
  padding: 8px 14px;
  border: 1px solid var(--theme-border);
  border-radius: var(--theme-radius-pill);
  background: var(--theme-bg-surface);
  color: var(--theme-primary);
  font: inherit;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: background var(--theme-duration) var(--theme-ease),
    border-color var(--theme-duration) var(--theme-ease),
    color var(--theme-duration) var(--theme-ease);
}

.pc-member-card__link:hover {
  background: var(--theme-primary);
  border-color: var(--theme-primary);
  color: var(--theme-primary-text);
}

.pc-member-card__link:focus-visible {
  outline: none;
  box-shadow: var(--theme-ring);
}

.pc-member-card__link i {
  font-size: 11px;
}

/* ========== 快捷入口 ========== */
.pc-entries {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(232px, 1fr));
  gap: 16px;
}

.pc-entry {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 18px 20px;
  border-radius: var(--theme-radius-lg);
  background: var(--theme-bg-surface);
  border: 1px solid var(--theme-border);
  box-shadow: var(--theme-shadow-sm);
  color: var(--theme-text-1);
  font: inherit;
  text-align: left;
  cursor: pointer;
  transition: transform var(--theme-duration) var(--theme-ease),
    box-shadow var(--theme-duration) var(--theme-ease),
    border-color var(--theme-duration) var(--theme-ease);
}

.pc-entry:hover {
  transform: translateY(-3px);
  border-color: color-mix(in srgb, var(--theme-primary) 45%, transparent);
  box-shadow: var(--theme-shadow-md);
}

.pc-entry:focus-visible {
  outline: none;
  box-shadow: var(--theme-ring);
}

.pc-entry__icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  flex: 0 0 auto;
  width: 44px;
  height: 44px;
  border-radius: var(--theme-radius-md);
  background: var(--theme-primary-soft);
  color: var(--theme-primary);
  font-size: 18px;
}

.pc-entry__text {
  display: flex;
  flex: 1;
  min-width: 0;
  flex-direction: column;
  gap: 3px;
}

.pc-entry__text strong {
  font-size: 15px;
  font-weight: 600;
}

.pc-entry__text em {
  font-style: normal;
  font-size: 12.5px;
  color: var(--theme-text-2);
}

.pc-entry__arrow {
  flex: 0 0 auto;
  font-size: 12px;
  color: var(--theme-text-3);
  transition: transform var(--theme-duration) var(--theme-ease),
    color var(--theme-duration) var(--theme-ease);
}

.pc-entry:hover .pc-entry__arrow {
  color: var(--theme-primary);
  transform: translateX(3px);
}

/* ========== 资料详情 ========== */
.pc-details {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
  gap: 16px;
  align-items: start;
}

.pc-panel {
  padding: 22px 24px 10px;
  border-radius: var(--theme-radius-lg);
  background: var(--theme-bg-surface);
  border: 1px solid var(--theme-border);
  box-shadow: var(--theme-shadow-sm);
}

.pc-panel__head {
  display: flex;
  align-items: center;
  gap: 12px;
  padding-bottom: 16px;
  border-bottom: 1px solid var(--theme-hairline);
}

.pc-panel__icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  flex: 0 0 auto;
  width: 40px;
  height: 40px;
  border-radius: var(--theme-radius-sm);
  background: var(--theme-primary-soft);
  color: var(--theme-primary);
  font-size: 16px;
}

.pc-panel__icon--alt {
  background: color-mix(in srgb, var(--theme-accent-3) 16%, transparent);
  color: var(--theme-accent-3);
}

.pc-panel__head h2 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: var(--theme-text-1);
}

.pc-panel__head p {
  margin: 2px 0 0;
  font-size: 12.5px;
  color: var(--theme-text-3);
}

.pc-rows {
  display: flex;
  flex-direction: column;
}

.pc-row {
  display: flex;
  align-items: baseline;
  gap: 16px;
  padding: 13px 0;
  border-bottom: 1px dashed var(--theme-hairline);
}

.pc-row:last-child {
  border-bottom: none;
}

.pc-row__k {
  flex: 0 0 84px;
  font-size: 12.5px;
  letter-spacing: 0.02em;
  color: var(--theme-text-3);
}

.pc-row__v {
  flex: 1;
  min-width: 0;
  font-size: 14.5px;
  font-weight: 500;
  line-height: 1.6;
  color: var(--theme-text-1);
  word-break: break-word;
}

.pc-row--block {
  flex-direction: column;
  align-items: stretch;
  gap: 6px;
}

.pc-row--block .pc-row__k {
  flex: none;
}

.pc-row--block .pc-row__v {
  font-weight: 400;
  color: var(--theme-text-2);
}

/* ========== 模态框 (Apple 风格玻璃拟态) ========== */
.modal-overlay {
  position: fixed;
  inset: 0;
  z-index: 1000;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 24px;
  background: var(--theme-overlay);
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
  animation: modal-fade 0.2s ease;
}

@keyframes modal-fade {
  from { opacity: 0; }
  to   { opacity: 1; }
}

.modal-dialog {
  position: relative;
  width: 100%;
  max-height: calc(100vh - 48px);
  display: flex;
  flex-direction: column;
  /* Apple 导航栏玻璃色: 玻璃底 + blur(20px) */
  background: var(--theme-bg-glass-strong);
  backdrop-filter: blur(20px) saturate(180%);
  -webkit-backdrop-filter: blur(20px) saturate(180%);
  border: 1px solid var(--theme-border-strong);
  border-radius: 28px;                              /* Apple 卡片圆角 24~28px */
  box-shadow:
    var(--theme-shadow-lg),
    0 0 0 0.5px var(--theme-hairline);
  overflow: hidden;
  color: var(--theme-text-1);                        /* Apple 主文本色 */
  font-family: system-ui, -apple-system, 'PingFang SC', 'SF Pro Display', 'Microsoft YaHei', sans-serif;
  animation: modal-pop 0.25s cubic-bezier(0.22, 1, 0.36, 1);
}

/* 顶部渐变强调线 */
.modal-dialog::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: var(--theme-gradient);
  opacity: 0.9;
  z-index: 2;
  pointer-events: none;
}

@keyframes modal-pop {
  from { opacity: 0; transform: translateY(12px) scale(0.98); }
  to   { opacity: 1; transform: translateY(0) scale(1); }
}

.modal-edit      { max-width: 620px; }
.modal-password  { max-width: 460px; }

/* --- 头部 --- */
.modal-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
  padding: 22px 24px 18px;
  border-bottom: 1px solid var(--theme-border);                /* Apple 分割线色 */
  flex-shrink: 0;
}

.modal-heading {
  display: flex;
  align-items: center;
  gap: 12px;
  min-width: 0;
}

.modal-heading__badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  flex: 0 0 auto;
  width: 42px;
  height: 42px;
  border-radius: 14px;
  background: var(--theme-primary-soft);
  color: var(--theme-primary);
  font-size: 16px;
}

.modal-heading__badge--lock {
  background: color-mix(in srgb, var(--theme-accent-3) 16%, transparent);
  color: var(--theme-accent-3);
}

.modal-heading__text {
  min-width: 0;
}

.modal-title {
  margin: 0;
  font-size: 17px;
  font-weight: 600;                                 /* Apple H3 600~700 */
  line-height: 1.25;
  color: var(--theme-text-1);
  letter-spacing: -0.01em;
}

.modal-subtitle {
  margin: 3px 0 0;
  font-size: 12.5px;
  color: var(--theme-text-3);
}

.modal-close {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  flex: 0 0 auto;
  width: 32px;
  height: 32px;
  border: none;
  border-radius: 50%;
  background: transparent;
  color: var(--theme-text-2);                                   /* Apple 次要文本色 */
  cursor: pointer;
  transition: background 0.2s ease, color 0.2s ease;
}
.modal-close:hover {
  background: var(--theme-bg-hover);                              /* Apple 背景辅色 */
  color: var(--theme-text-1);
}
.modal-close:focus-visible {
  outline: 2px solid var(--theme-primary);
  outline-offset: 2px;
}

/* --- 主体 --- */
.modal-body {
  display: flex;
  flex-direction: column;
  gap: 22px;
  padding: 22px 24px 24px;
  overflow-y: auto;
  flex: 1 1 auto;
}

/* --- 表单分区 --- */
.form-section {
  display: flex;
  flex-direction: column;
}

.form-section__head {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 14px;
  font-size: 12px;
  font-weight: 600;
  letter-spacing: 0.06em;
  color: var(--theme-text-3);
}
.form-section__head i {
  font-size: 12px;
  color: var(--theme-primary);
}
.form-section__head::after {
  content: '';
  flex: 1;
  height: 1px;
  background: var(--theme-hairline);
}

/* --- 表单网格 (Apple 风格) --- */
.form-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px 20px;                                   /* 8px 网格基准 */
}
.form-item--block {
  grid-column: 1 / -1;
}

.form-label {
  display: block;
  margin-bottom: 7px;
  font-size: 13px;
  font-weight: 500;
  color: var(--theme-text-2);                                   /* Apple 次要文本: 标签 */
  letter-spacing: 0.01em;
}

/* 输入容器（承载前置图标 / 后置按钮） */
.form-control {
  position: relative;
}

.form-control .control-icon {
  position: absolute;
  left: 14px;
  top: 50%;
  transform: translateY(-50%);
  font-size: 14px;
  color: var(--theme-text-3);
  pointer-events: none;
  transition: color 0.2s ease;
}

.form-control > .control-icon + .form-input {
  padding-left: 40px;
}

.form-control:focus-within .control-icon {
  color: var(--theme-primary);
}

.form-input {
  display: block;
  width: 100%;
  box-sizing: border-box;
  padding: 10px 14px;
  font-size: 15px;                                  /* Apple 正文 16~18px, 输入稍小 */
  line-height: 1.4;
  font-family: inherit;
  color: var(--theme-text-1);                                   /* 输入主文本 */
  background: var(--theme-bg-surface-2);                        /* 填充式输入底 */
  border: 1px solid var(--theme-border);                        /* Apple 边框 */
  border-radius: 12px;                              /* 输入圆角, 与 Apple 表单匹配 */
  outline: none;
  transition: border-color 0.2s ease, box-shadow 0.2s ease, background 0.2s ease;
  -webkit-appearance: none;
  appearance: none;
}

.form-input::placeholder {
  color: var(--theme-text-3);
}

.form-input:hover {
  border-color: var(--theme-border-strong);
}

.form-input:focus {
  border-color: var(--theme-primary);                            /* Apple 强调色 */
  box-shadow: var(--theme-ring);  /* 蓝色焦点光晕 */
}

/* textarea */
.form-textarea {
  min-height: 84px;
  padding: 11px 14px;
  resize: vertical;
  font: inherit;
  line-height: 1.55;
}

/* 原生数字输入去箭头 (视觉统一) */
.form-input[type="number"]::-webkit-outer-spin-button,
.form-input[type="number"]::-webkit-inner-spin-button {
  -webkit-appearance: none;
  margin: 0;
}
.form-input[type="number"] {
  -moz-appearance: textfield;
}

/* --- 性别分段控件 --- */
.segmented {
  display: flex;
  gap: 4px;
  padding: 4px;
  border-radius: 12px;
  background: var(--theme-bg-surface-2);
  border: 1px solid var(--theme-border);
}
.segmented__item {
  flex: 1;
  padding: 8px 10px;
  border: none;
  border-radius: 9px;
  background: transparent;
  color: var(--theme-text-2);
  font: inherit;
  font-size: 14px;
  font-weight: 500;
  line-height: 1.3;
  cursor: pointer;
  transition: background 0.2s ease, color 0.2s ease, box-shadow 0.2s ease;
}
.segmented__item:hover {
  color: var(--theme-text-1);
  background: var(--theme-bg-hover);
}
.segmented__item.is-active {
  background: var(--theme-bg-surface);
  color: var(--theme-primary);
  box-shadow: var(--theme-shadow-sm);
}
.segmented__item:focus-visible {
  outline: none;
  box-shadow: var(--theme-ring);
}

/* --- 密码显示按钮 --- */
.form-control--pwd > .form-input {
  padding-right: 48px;
}

.pwd-toggle {
  position: absolute;
  top: 50%;
  right: 8px;
  transform: translateY(-50%);
  width: 34px;
  height: 34px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border: none;
  border-radius: 9px;
  background: transparent;
  color: var(--theme-text-3);
  cursor: pointer;
  transition: background 0.2s ease, color 0.2s ease;
}
.pwd-toggle:hover {
  background: var(--theme-bg-hover);
  color: var(--theme-text-1);
}
.pwd-toggle:focus-visible {
  outline: 2px solid var(--theme-primary);
  outline-offset: 1px;
}

/* --- 底部 --- */
.modal-footer {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 12px;
  padding: 16px 24px;
  border-top: 1px solid var(--theme-border);
  flex-shrink: 0;
  background: var(--theme-bg-surface-2);
}

/* --- 按钮 (Apple 风格) --- */
.btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  min-width: 104px;
  padding: 12px 28px;                               /* Apple 按钮内边距 */
  border-radius: 40px;                              /* Apple 按钮圆角 40px */
  font-size: 15px;
  font-weight: 500;
  line-height: 1;
  font-family: inherit;
  border: 1px solid transparent;
  cursor: pointer;
  transition: background 0.2s ease, color 0.2s ease,
              border-color 0.2s ease, transform 0.2s ease,
              box-shadow 0.2s ease;
  user-select: none;
}
.btn:focus-visible {
  outline: 2px solid var(--theme-primary);
  outline-offset: 2px;
}
.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-secondary {
  background: var(--theme-bg-surface);
  color: var(--theme-text-1);
  border-color: var(--theme-border);
}
.btn-secondary:hover:not(:disabled) {
  background: var(--theme-bg-hover);
  border-color: var(--theme-border-strong);
}

.btn-primary {
  background: var(--theme-primary);                              /* Apple 强调色 */
  color: var(--theme-primary-text);
  border-color: var(--theme-primary);
  box-shadow: 0 4px 12px var(--theme-primary-soft);
}
.btn-primary:hover:not(:disabled) {
  background: var(--theme-primary-hover);                              /* Apple 悬停加深 */
  border-color: var(--theme-primary-hover);
  transform: translateY(-1px);
  box-shadow: var(--theme-shadow-md);
}
.btn-primary:active:not(:disabled) {
  transform: translateY(0);
}

/* --- 按钮加载 spinner --- */
.btn-spinner {
  display: inline-block;
  width: 16px;
  height: 16px;
  border: 2px solid color-mix(in srgb, var(--theme-primary-text) 50%, transparent);
  border-top-color: var(--theme-primary-text);
  border-radius: 50%;
  animation: spin 0.7s linear infinite;
}
.btn-secondary .btn-spinner {
  border-color: color-mix(in srgb, var(--theme-text-1) 30%, transparent);
  border-top-color: var(--theme-text-1);
}
@keyframes spin {
  to { transform: rotate(360deg); }
}

/* ========== 响应式 ========== */
@media (max-width: 900px) {
  .pc-stats {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .pc-entries {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 640px) {
  .pc-page {
    padding: 18px 14px 48px;
  }

  .pc-shell {
    gap: 14px;
  }

  .pc-hero__cover {
    height: 116px;
  }

  .pc-hero__actions {
    top: 12px;
    right: 12px;
    gap: 8px;
  }

  .pc-hero__actions :deep(.el-button) {
    padding: 8px 12px;
    font-size: 12px;
  }

  .pc-hero__body {
    flex-direction: column;
    align-items: center;
    padding: 0 18px 22px;
    text-align: center;
  }

  .pc-hero__name-row {
    justify-content: center;
  }

  .pc-hero__name {
    font-size: 24px;
  }

  .pc-hero__meta {
    justify-content: center;
  }

  .pc-stats {
    gap: 12px;
  }

  .pc-stat {
    padding: 14px 16px;
  }

  .pc-stat__value {
    font-size: 17px;
  }

  .pc-member {
    grid-template-columns: 1fr;
    gap: 12px;
  }

  .pc-member-card {
    padding: 18px;
  }

  .pc-points__value strong {
    font-size: 28px;
  }

  .pc-entry {
    padding: 14px 16px;
  }

  .pc-panel {
    padding: 18px 18px 6px;
  }

  .pc-row {
    flex-direction: column;
    gap: 4px;
  }

  .pc-row__k {
    flex: none;
  }

  /* 模态框移动端适配 */
  .modal-overlay {
    padding: 12px;
    align-items: flex-end;
  }
  .modal-dialog {
    border-radius: 24px 24px 28px 28px;
    max-height: 92vh;
    animation: modal-pop-mobile 0.28s cubic-bezier(0.22, 1, 0.36, 1);
  }
  @keyframes modal-pop-mobile {
    from { opacity: 0; transform: translateY(24px); }
    to   { opacity: 1; transform: translateY(0); }
  }
  .modal-header  { padding: 18px 20px 14px; }
  .modal-heading__badge { width: 38px; height: 38px; border-radius: 12px; font-size: 15px; }
  .modal-body    { padding: 18px 20px; gap: 18px; }
  .modal-footer  { padding: 12px 20px; gap: 8px; }
  .modal-title   { font-size: 16px; }
  .form-grid     { grid-template-columns: 1fr; gap: 14px; }
  .btn           { min-width: 92px; padding: 11px 20px; font-size: 14px; }
}
</style>
