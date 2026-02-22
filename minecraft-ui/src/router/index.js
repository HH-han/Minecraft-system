// router/index.js
import { createRouter, createWebHistory } from "vue-router";

const routes = [
  // 导航
  {
    path: "/",
    name: "Navigation",
    component: () => import("@/components/NavigationComponent/Header.vue"),
    meta: {
      title: "导航",
      requiresAuth: false,
    },
    children: [
      {
        path: "/hotel",
        name: "Hotel",
        component: () => import("@/views/Hotel/index.vue"),
        meta: {
          title: "酒店",
          requiresAuth: false,
        },
      },
      {
        path: "/food",
        name: "Food",
        component: () => import("@/views/Food/index.vue"),
        meta: {
          title: "美食",
          requiresAuth: false,
        },
      },
      {
        path: "/scenicspot",
        name: "ScenicSpot",
        component: () => import("@/views/Scenicspot/index.vue"),
        meta: {
          title: "景点",
          requiresAuth: false,
        },
      },
      {
        path: "/souvenir",
        name: "Souvenir",
        component: () => import("@/views/Souvenir/index.vue"),
        meta: {
          title: "纪念品",
          requiresAuth: false,
        },
      },
      {
        path: "/strategy",
        name: "Strategy",
        component: () => import("@/views/Strategy/index.vue"),
        meta: {
          title: "攻略",
          requiresAuth: false,
        },
      },
      {
        path: "/community",
        name: "Community",
        component: () => import("@/views/Community/index.vue"),
        meta: {
          title: "社区",
          requiresAuth: true,
        },
        children: [
          {
            path: "",
            name: "CommunityMain",
            component: () => import("@/views/Community/components/main.vue"),
            meta: {
              title: "社区",
              requiresAuth: true,
            },
          },
          {
            path: "detail/:id",
            name: "CommunityDetail",
            component: () => import("@/views/Community/components/details.vue"),
            meta: {
              title: "帖子详情",
              requiresAuth: true,
            },
          },
        ],
      },
      {
        path: "/forum",
        name: "Forum",
        component: () => import("@/views/Forum/index.vue"),
        meta: {
          title: "论坛",
          requiresAuth: true,
        },
      },
      {
        path: "/worldtravel",
        name: "WorldTravel",
        component: () => import("@/views/WorldTravel/index.vue"),
        meta: {
          title: "世界旅行",
          requiresAuth: true,
        },
      },
    ],
  },

  {
    path: "/personalcenter",
    name: "PersonalCenter",
    component: () => import("@/views/PersonalCenter/index.vue"),
    meta: {
      title: "个人中心",
      requiresAuth: true,
    },
  },
  {
    path: "/accountsettings",
    name: "accountsettings",
    component: () => import("@/views/PersonalCenter/components/accountsettings.vue"),
    meta: {
      title: "账户设置",
      requiresAuth: true,
    },
  },
  {
    path: "/aboutwebsite",
    name: "AboutWebsite",
    component: () => import("@/views/Aboutwebsite/index.vue"),
    meta: {
      title: "关于",
      requiresAuth: false,
    },
    children: [
      {
        path: "/aboutweb",
        name: "AboutWeb",
        component: () => import("@/views/Aboutwebsite/components/Aboutweb.vue"),
        meta: {
          title: "关于我们",
          requiresAuth: false,
        },
      },
      {
        path: "/websiteintroduction",
        name: "WebsiteIntroduction",
        component: () => import("@/views/Aboutwebsite/components/WebsiteIntroduction.vue"),
        meta: {
          title: "网站介绍",
          requiresAuth: false,
        },
      },
    ]
  },
  // 首页
  {
    path: "/home",
    name: "Home",
    component: () => import("@/views/index/index.vue"),
    meta: {
      title: "首页",
      requiresAuth: false,
    },
    children: [
      {
        path: "/header",
        name: "HomeHeader",
        component: () => import("@/views/index/components/Header.vue"),
        meta: {
          title: "导航栏",
          requiresAuth: false,
        },
      },
      {
        path: "/footer",
        name: "HomeFooter",
        component: () => import("@/views/index/components/Footer.vue"),
        meta: {
          title: "页脚",
          requiresAuth: false,
        },
      },
    ],
  },
  // 登录
  {
    path: "/login",
    name: "Login",
    component: () => import("@/views/login/LoginName.vue"),
    meta: {
      title: "登录/注册",
      requiresAuth: false,
    },
    children: [
      {
        path: "enrolfirst",
        name: "EnrolFirst",
        component: () => import("@/views/login/components/EnrolFirst.vue"),
        meta: {
          title: "注册",
          requiresAuth: false,
        },
      },
      {
        path: "emaillogin",
        name: "EmailLogin",
        component: () => import("@/views/login/components/EmailLogin.vue"),
        meta: {
          title: "邮箱登录",
          requiresAuth: false,
        },
      },
      {
        path: "ForgotPassword",
        name: "ForgotPassword",
        component: () => import("@/views/login/components/ForgotPassword.vue"),
        meta: {
          title: "忘记密码",
          requiresAuth: false,
        },
      },
      {
        path: "Fanginter",
        name: "Fanginter",
        component: () =>
          import("@/views/login/components/FanginternationalContainer.vue"),
        meta: {
          title: "国际登录",
          requiresAuth: false,
        },
      },
    ],
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
