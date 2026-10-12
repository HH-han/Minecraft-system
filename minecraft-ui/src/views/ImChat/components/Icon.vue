<template>
  <!-- 单色线性图标（Lucide 风格），颜色随 currentColor -->
  <svg v-if="name === 'close'" class="icon" viewBox="0 0 24 24" :width="size" :height="size">
    <path d="M18 6 6 18" />
    <path d="m6 6 12 12" />
  </svg>
  <svg v-else-if="name === 'check'" class="icon" viewBox="0 0 24 24" :width="size" :height="size">
    <path d="M20 6 9 17l-5-5" />
  </svg>
  <svg v-else-if="name === 'at-sign'" class="icon" viewBox="0 0 24 24" :width="size" :height="size">
    <circle cx="12" cy="12" r="4" />
    <path d="M16 8v5a3 3 0 0 0 6 0v-1a10 10 0 1 0-4 8" />
  </svg>
  <svg v-else-if="name === 'chevron-right'" class="icon" viewBox="0 0 24 24" :width="size" :height="size">
    <path d="m9 18 6-6-6-6" />
  </svg>
  <svg v-else-if="name === 'more'" class="icon" viewBox="0 0 24 24" :width="size" :height="size">
    <circle cx="12" cy="12" r="1" />
    <circle cx="19" cy="12" r="1" />
    <circle cx="5" cy="12" r="1" />
  </svg>
  <svg v-else-if="name === 'plus'" class="icon" viewBox="0 0 24 24" :width="size" :height="size">
    <path d="M5 12h14" />
    <path d="M12 5v14" />
  </svg>
  <svg v-else-if="name === 'send'" class="icon" viewBox="0 0 24 24" :width="size" :height="size">
    <path
      d="M14.536 21.686a.5.5 0 0 0 .937-.024l6.5-19a.496.496 0 0 0-.635-.635l-19 6.5a.5.5 0 0 0-.024.937l7.93 3.18a2 2 0 0 1 1.112 1.11z" />
    <path d="m21.854 2.147-10.94 10.939" />
  </svg>
  <svg v-else-if="name === 'upload'" class="icon" viewBox="0 0 24 24" :width="size" :height="size">
    <path d="M12 3v12" />
    <path d="m17 8-5-5-5 5" />
    <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4" />
  </svg>
  <svg v-else-if="name === 'edit'" class="icon" viewBox="0 0 24 24" :width="size" :height="size">
    <path
      d="M21.174 6.812a1 1 0 0 0-3.986-3.987L3.842 16.174a2 2 0 0 0-.5.83l-1.321 4.352a.5.5 0 0 0 .623.622l4.353-1.32a2 2 0 0 0 .83-.497z" />
    <path d="m15 5 4 4" />
  </svg>
  <svg v-else-if="name === 'trash'" class="icon" viewBox="0 0 24 24" :width="size" :height="size">
    <path d="M10 11v6" />
    <path d="M14 11v6" />
    <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6" />
    <path d="M3 6h18" />
    <path d="M8 6V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2" />
  </svg>
  <svg v-else-if="name === 'log-out'" class="icon" viewBox="0 0 24 24" :width="size" :height="size">
    <path d="m16 17 5-5-5-5" />
    <path d="M21 12H9" />
    <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4" />
  </svg>
  <svg v-else-if="name === 'refresh-cw'" class="icon" viewBox="0 0 24 24" :width="size" :height="size">
    <path d="M3 12a9 9 0 0 1 9-9 9.75 9.75 0 0 1 6.74 2.74L21 8" />
    <path d="M21 3v5h-5" />
    <path d="M21 12a9 9 0 0 1-9 9 9.75 9.75 0 0 1-6.74-2.74L3 16" />
    <path d="M8 16H3v5" />
  </svg>
  <svg v-else-if="name === 'scan'" class="icon" viewBox="0 0 24 24" :width="size" :height="size">
    <path d="M3 7V5a2 2 0 0 1 2-2h2" />
    <path d="M17 3h2a2 2 0 0 1 2 2v2" />
    <path d="M21 17v2a2 2 0 0 1-2 2h-2" />
    <path d="M7 21H5a2 2 0 0 1-2-2v-2" />
  </svg>
  <svg v-else-if="name === 'user'" class="icon" viewBox="0 0 24 24" :width="size" :height="size">
    <path d="M19 21v-2a4 4 0 0 0-4-4H9a4 4 0 0 0-4 4v2" />
    <circle cx="12" cy="7" r="4" />
  </svg>
  <svg v-else-if="name === 'users'" class="icon" viewBox="0 0 24 24" :width="size" :height="size">
    <path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2" />
    <path d="M16 3.128a4 4 0 0 1 0 7.744" />
    <path d="M22 21v-2a4 4 0 0 0-3-3.87" />
    <circle cx="9" cy="7" r="4" />
  </svg>
  <svg v-else-if="name === 'user-plus'" class="icon" viewBox="0 0 24 24" :width="size" :height="size">
    <path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2" />
    <circle cx="9" cy="7" r="4" />
    <line x1="19" x2="19" y1="8" y2="14" />
    <line x1="22" x2="16" y1="11" y2="11" />
  </svg>
  <svg v-else-if="name === 'user-circle'" class="icon" viewBox="0 0 24 24" :width="size" :height="size">
    <circle cx="12" cy="12" r="10" />
    <circle cx="12" cy="10" r="3" />
    <path d="M7 20.662V19a2 2 0 0 1 2-2h6a2 2 0 0 1 2 2v1.662" />
  </svg>
  <svg v-else-if="name === 'message'" class="icon" viewBox="0 0 24 24" :width="size" :height="size">
    <path
      d="M2.992 16.342a2 2 0 0 1 .094 1.167l-1.065 3.29a1 1 0 0 0 1.236 1.168l3.413-.998a2 2 0 0 1 1.099.092 10 10 0 1 0-4.777-4.719" />
  </svg>
  <svg v-else-if="name === 'chat-round'" class="icon" viewBox="0 0 24 24" :width="size" :height="size">
    <path
      d="M2.992 16.342a2 2 0 0 1 .094 1.167l-1.065 3.29a1 1 0 0 0 1.236 1.168l3.413-.998a2 2 0 0 1 1.099.092 10 10 0 1 0-4.777-4.719" />
  </svg>
  <svg v-else-if="name === 'sms'" class="icon" viewBox="0 0 24 24" :width="size" :height="size">
    <path
      d="M22 17a2 2 0 0 1-2 2H6.828a2 2 0 0 0-1.414.586l-2.202 2.202A.71.71 0 0 1 2 21.286V5a2 2 0 0 1 2-2h16a2 2 0 0 1 2 2z" />
  </svg>
  <svg v-else-if="name === 'smile'" class="icon" viewBox="0 0 24 24" :width="size" :height="size">
    <path d="M15 10V9" />
    <path d="M16.472 15a6 6 0 0 1-8.943 0" />
    <path d="M9 10V9" />
    <circle cx="12" cy="12" r="10" />
  </svg>
  <svg v-else-if="name === 'phone'" class="icon" viewBox="0 0 24 24" :width="size" :height="size">
    <path
      d="M13.832 16.568a1 1 0 0 0 1.213-.303l.355-.465A2 2 0 0 1 17 15h3a2 2 0 0 1 2 2v3a2 2 0 0 1-2 2A18 18 0 0 1 2 4a2 2 0 0 1 2-2h3a2 2 0 0 1 2 2v3a2 2 0 0 1-.8 1.6l-.468.351a1 1 0 0 0-.292 1.233 14 14 0 0 0 6.392 6.384" />
  </svg>
  <svg v-else-if="name === 'phone-off'" class="icon" viewBox="0 0 24 24" :width="size" :height="size">
    <path
      d="M10.1 13.9a14 14 0 0 0 3.732 2.668 1 1 0 0 0 1.213-.303l.355-.465A2 2 0 0 1 17 15h3a2 2 0 0 1 2 2v3a2 2 0 0 1-2 2 18 18 0 0 1-12.728-5.272" />
    <path d="M22 2 2 22" />
    <path
      d="M4.76 13.582A18 18 0 0 1 2 4a2 2 0 0 1 2-2h3a2 2 0 0 1 2 2v3a2 2 0 0 1-.8 1.6l-.468.351a1 1 0 0 0-.292 1.233 14 14 0 0 0 .244.473" />
  </svg>
  <svg v-else-if="name === 'video-camera'" class="icon" viewBox="0 0 24 24" :width="size" :height="size">
    <path d="m16 13 5.223 3.482a.5.5 0 0 0 .777-.416V7.87a.5.5 0 0 0-.752-.432L16 10.5" />
    <rect x="2" y="6" width="14" height="12" rx="2" />
  </svg>
  <svg v-else-if="name === 'video-camera-off'" class="icon" viewBox="0 0 24 24" :width="size" :height="size">
    <path d="M10.66 6H14a2 2 0 0 1 2 2v2.5l5.248-3.062A.5.5 0 0 1 22 7.87v8.196" />
    <path d="M16 16a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h2" />
    <path d="m2 2 20 20" />
  </svg>
  <svg v-else-if="name === 'mic'" class="icon" viewBox="0 0 24 24" :width="size" :height="size">
    <path d="M12 19v3" />
    <path d="M19 10v2a7 7 0 0 1-14 0v-2" />
    <rect x="9" y="2" width="6" height="13" rx="3" />
  </svg>
  <svg v-else-if="name === 'mic-off'" class="icon" viewBox="0 0 24 24" :width="size" :height="size">
    <path d="M12 19v3" />
    <path d="M15 9.34V5a3 3 0 0 0-5.68-1.33" />
    <path d="M16.95 16.95A7 7 0 0 1 5 12v-2" />
    <path d="M18.89 13.23A7 7 0 0 0 19 12v-2" />
    <path d="m2 2 20 20" />
    <path d="M9 9v3a3 3 0 0 0 5.12 2.12" />
  </svg>
  <svg v-else-if="name === 'volume-2'" class="icon" viewBox="0 0 24 24" :width="size" :height="size">
    <path
      d="M11 4.702a.705.705 0 0 0-1.203-.498L6.413 7.587A1.4 1.4 0 0 1 5.416 8H3a1 1 0 0 0-1 1v6a1 1 0 0 0 1 1h2.416a1.4 1.4 0 0 1 .997.413l3.383 3.384A.705.705 0 0 0 11 19.298z" />
    <path d="M16 9a5 5 0 0 1 0 6" />
    <path d="M19.364 18.364a9 9 0 0 0 0-12.728" />
  </svg>
  <svg v-else-if="name === 'volume-x'" class="icon" viewBox="0 0 24 24" :width="size" :height="size">
    <path
      d="M11 4.702a.7.7 0 0 0-1.203-.498L6.413 7.587A1.4 1.4 0 0 1 5.416 8H3a1 1 0 0 0-1 1v6a1 1 0 0 0 1 1h2.416a1.4 1.4 0 0 1 .997.413l3.383 3.384A.7.7 0 0 0 11 19.298z" />
    <path d="m16.5 14.5 5-5" />
    <path d="m16.5 9.5 5 5" />
  </svg>
  <svg v-else-if="name === 'camera'" class="icon" viewBox="0 0 24 24" :width="size" :height="size">
    <path
      d="M13.997 4a2 2 0 0 1 1.76 1.05l.486.9A2 2 0 0 0 18.003 7H20a2 2 0 0 1 2 2v9a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V9a2 2 0 0 1 2-2h1.997a2 2 0 0 0 1.759-1.048l.489-.904A2 2 0 0 1 10.004 4z" />
    <circle cx="12" cy="13" r="3" />
  </svg>
  <svg v-else-if="name === 'bell'" class="icon" viewBox="0 0 24 24" :width="size" :height="size">
    <path d="M10.268 21a2 2 0 0 0 3.464 0" />
    <path
      d="M3.262 15.326A1 1 0 0 0 4 17h16a1 1 0 0 0 .74-1.673C19.41 13.956 18 12.499 18 8A6 6 0 0 0 6 8c0 4.499-1.411 5.956-2.738 7.326" />
  </svg>
  <svg v-else-if="name === 'paperclip'" class="icon" viewBox="0 0 24 24" :width="size" :height="size">
    <path
      d="m16 6-8.414 8.586a2 2 0 0 0 2.829 2.829l8.414-8.586a4 4 0 1 0-5.657-5.657l-8.379 8.551a6 6 0 1 0 8.485 8.485l8.379-8.551" />
  </svg>
  <svg v-else-if="name === 'picture'" class="icon" viewBox="0 0 24 24" :width="size" :height="size">
    <rect width="18" height="18" x="3" y="3" rx="2" ry="2" />
    <circle cx="9" cy="9" r="2" />
    <path d="m21 15-3.086-3.086a2 2 0 0 0-2.828 0L6 21" />
  </svg>
  <svg v-else-if="name === 'image'" class="icon" viewBox="0 0 24 24" :width="size" :height="size">
    <rect width="18" height="18" x="3" y="3" rx="2" ry="2" />
    <circle cx="9" cy="9" r="2" />
    <path d="m21 15-3.086-3.086a2 2 0 0 0-2.828 0L6 21" />
  </svg>
  <svg v-else-if="name === 'file'" class="icon" viewBox="0 0 24 24" :width="size" :height="size">
    <path
      d="M6 22a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2h8a2.4 2.4 0 0 1 1.704.706l3.588 3.588A2.4 2.4 0 0 1 20 8v12a2 2 0 0 1-2 2z" />
    <path d="M14 2v5a1 1 0 0 0 1 1h5" />
  </svg>
  <svg v-else-if="name === 'compass'" class="icon" viewBox="0 0 24 24" :width="size" :height="size">
    <circle cx="12" cy="12" r="10" />
    <path
      d="m16.24 7.76-1.804 5.411a2 2 0 0 1-1.265 1.265L7.76 16.24l1.804-5.411a2 2 0 0 1 1.265-1.265z" />
  </svg>
  <svg v-else-if="name === 'map-pin'" class="icon" viewBox="0 0 24 24" :width="size" :height="size">
    <path
      d="M20 10c0 4.993-5.539 10.193-7.399 11.799a1 1 0 0 1-1.202 0C9.539 20.193 4 14.993 4 10a8 8 0 0 1 16 0" />
    <circle cx="12" cy="10" r="3" />
  </svg>
  <svg v-else-if="name === 'building'" class="icon" viewBox="0 0 24 24" :width="size" :height="size">
    <path d="M12 10h.01" />
    <path d="M12 14h.01" />
    <path d="M12 6h.01" />
    <path d="M16 10h.01" />
    <path d="M16 14h.01" />
    <path d="M16 6h.01" />
    <path d="M8 10h.01" />
    <path d="M8 14h.01" />
    <path d="M8 6h.01" />
    <path d="M9 22v-3a1 1 0 0 1 1-1h4a1 1 0 0 1 1 1v3" />
    <rect x="4" y="2" width="16" height="20" rx="2" />
  </svg>
  <svg v-else-if="name === 'tag'" class="icon" viewBox="0 0 24 24" :width="size" :height="size">
    <path
      d="M12.586 2.586A2 2 0 0 0 11.172 2H4a2 2 0 0 0-2 2v7.172a2 2 0 0 0 .586 1.414l8.704 8.704a2.426 2.426 0 0 0 3.42 0l6.58-6.58a2.426 2.426 0 0 0 0-3.42z" />
    <circle cx="7.5" cy="7.5" r=".5" fill="currentColor" />
  </svg>
  <svg v-else-if="name === 'star'" class="icon" viewBox="0 0 24 24" :width="size" :height="size">
    <path
      d="M11.525 2.295a.53.53 0 0 1 .95 0l2.31 4.679a2.123 2.123 0 0 0 1.595 1.16l5.166.756a.53.53 0 0 1 .294.904l-3.736 3.638a2.123 2.123 0 0 0-.611 1.878l.882 5.14a.53.53 0 0 1-.771.56l-4.618-2.428a2.122 2.122 0 0 0-1.973 0L6.396 21.01a.53.53 0 0 1-.77-.56l.881-5.139a2.122 2.122 0 0 0-.611-1.879L2.16 9.795a.53.53 0 0 1 .294-.906l5.165-.755a2.122 2.122 0 0 0 1.597-1.16z" />
  </svg>
  <svg v-else-if="name === 'credit-card'" class="icon" viewBox="0 0 24 24" :width="size" :height="size">
    <rect width="20" height="14" x="2" y="5" rx="2" />
    <line x1="2" x2="22" y1="10" y2="10" />
    <path d="M6 14h2" />
  </svg>
  <svg v-else-if="name === 'settings'" class="icon" viewBox="0 0 24 24" :width="size" :height="size">
    <path
      d="M9.671 4.136a2.34 2.34 0 0 1 4.659 0 2.34 2.34 0 0 0 3.319 1.915 2.34 2.34 0 0 1 2.33 4.033 2.34 2.34 0 0 0 0 3.831 2.34 2.34 0 0 1-2.33 4.033 2.34 2.34 0 0 0-3.319 1.915 2.34 2.34 0 0 1-4.659 0 2.34 2.34 0 0 0-3.32-1.915 2.34 2.34 0 0 1-2.33-4.033 2.34 2.34 0 0 0 0-3.831A2.34 2.34 0 0 1 6.35 6.051a2.34 2.34 0 0 0 3.319-1.915" />
    <circle cx="12" cy="12" r="3" />
  </svg>
  <svg v-else-if="name === 'info'" class="icon" viewBox="0 0 24 24" :width="size" :height="size">
    <circle cx="12" cy="12" r="10" />
    <path d="M12 16v-4" />
    <path d="M12 8h.01" />
  </svg>
  <svg v-else-if="name === 'clock'" class="icon" viewBox="0 0 24 24" :width="size" :height="size">
    <circle cx="12" cy="12" r="10" />
    <polyline points="12 6 12 12 16 14" />
  </svg>
  <svg v-else-if="name === 'search'" class="icon" viewBox="0 0 24 24" :width="size" :height="size">
    <circle cx="11" cy="11" r="8" />
    <line x1="21" y1="21" x2="16.65" y2="16.65" />
  </svg>
  <svg v-else-if="name === 'link'" class="icon" viewBox="0 0 24 24" :width="size" :height="size">
    <path d="M10 13a5 5 0 0 0 7.54.54l3-3a5 5 0 0 0-7.07-7.07l-1.72 1.71" />
    <path d="M14 11a5 5 0 0 0-7.54-.54l-3 3a5 5 0 0 0 7.07 7.07l1.71-1.71" />
  </svg>
  <svg v-else-if="name === 'arrow-left'" class="icon" viewBox="0 0 24 24" :width="size" :height="size">
    <path d="m12 19-7-7 7-7" />
    <path d="M19 12H5" />
  </svg>
  <svg v-else-if="name === 'keyboard'" class="icon" viewBox="0 0 24 24" :width="size" :height="size">
    <path d="M10 8h.01" />
    <path d="M12 12h.01" />
    <path d="M14 8h.01" />
    <path d="M16 12h.01" />
    <path d="M18 8h.01" />
    <path d="M6 8h.01" />
    <path d="M7 16h10" />
    <path d="M8 12h.01" />
    <rect x="2" y="5" width="20" height="14" rx="2" />
  </svg>
  <svg v-else-if="name === 'puzzle'" class="icon" viewBox="0 0 24 24" :width="size" :height="size">
    <path
      d="M15.39 4.39a1 1 0 0 0 1.68-.474 2.5 2.5 0 1 1 3.014 3.015 1 1 0 0 0-.474 1.68l1.683 1.682a2.414 2.414 0 0 1 0 3.414L19.61 15.39a1 1 0 0 1-1.68-.474 2.5 2.5 0 1 0-3.014 3.015 1 1 0 0 1 .474 1.68l-1.683 1.682a2.414 2.414 0 0 1-3.414 0L8.61 19.61a1 1 0 0 0-1.68.474 2.5 2.5 0 1 1-3.014-3.015 1 1 0 0 0 .474-1.68l-1.683-1.682a2.414 2.414 0 0 1 0-3.414L4.39 8.61a1 1 0 0 1 1.68.474 2.5 2.5 0 1 0 3.014-3.015 1 1 0 0 1-.474-1.68l1.683-1.682a2.414 2.414 0 0 1 3.414 0z" />
  </svg>
  <!-- 兼容旧图标名 -->
  <svg v-else-if="name === 'speakerEnabled'" class="icon" viewBox="0 0 24 24" :width="size" :height="size">
    <path
      d="M11 4.702a.705.705 0 0 0-1.203-.498L6.413 7.587A1.4 1.4 0 0 1 5.416 8H3a1 1 0 0 0-1 1v6a1 1 0 0 0 1 1h2.416a1.4 1.4 0 0 1 .997.413l3.383 3.384A.705.705 0 0 0 11 19.298z" />
    <path d="M16 9a5 5 0 0 1 0 6" />
    <path d="M19.364 18.364a9 9 0 0 0 0-12.728" />
  </svg>
  <svg v-else-if="name === 'micEnabled'" class="icon" viewBox="0 0 24 24" :width="size" :height="size">
    <path d="M12 19v3" />
    <path d="M19 10v2a7 7 0 0 1-14 0v-2" />
    <rect x="9" y="2" width="6" height="13" rx="3" />
  </svg>
  <svg v-else-if="name === 'isConnected'" class="icon" viewBox="0 0 24 24" :width="size" :height="size">
    <path d="M20 6 9 17l-5-5" />
  </svg>
</template>

<script setup>
defineProps({
  name: {
    type: String,
    required: true
  },
  size: {
    type: String,
    default: '24px'
  }
})
</script>

<style scoped>
.icon {
  fill: none;
  stroke: currentColor;
  stroke-width: 2;
  stroke-linecap: round;
  stroke-linejoin: round;
}
</style>
