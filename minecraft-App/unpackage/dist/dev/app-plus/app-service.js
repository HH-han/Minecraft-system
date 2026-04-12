if (typeof Promise !== "undefined" && !Promise.prototype.finally) {
  Promise.prototype.finally = function(callback) {
    const promise = this.constructor;
    return this.then(
      (value) => promise.resolve(callback()).then(() => value),
      (reason) => promise.resolve(callback()).then(() => {
        throw reason;
      })
    );
  };
}
;
if (typeof uni !== "undefined" && uni && uni.requireGlobal) {
  const global = uni.requireGlobal();
  ArrayBuffer = global.ArrayBuffer;
  Int8Array = global.Int8Array;
  Uint8Array = global.Uint8Array;
  Uint8ClampedArray = global.Uint8ClampedArray;
  Int16Array = global.Int16Array;
  Uint16Array = global.Uint16Array;
  Int32Array = global.Int32Array;
  Uint32Array = global.Uint32Array;
  Float32Array = global.Float32Array;
  Float64Array = global.Float64Array;
  BigInt64Array = global.BigInt64Array;
  BigUint64Array = global.BigUint64Array;
}
;
if (uni.restoreGlobal) {
  uni.restoreGlobal(Vue, weex, plus, setTimeout, clearTimeout, setInterval, clearInterval);
}
(function(vue) {
  "use strict";
  function formatAppLog(type, filename, ...args) {
    if (uni.__log__) {
      uni.__log__(type, filename, ...args);
    } else {
      console[type].apply(console, [...args, filename]);
    }
  }
  const BASE_URL = "http://localhost:8080/api";
  const requestInterceptor = (config) => {
    const token = uni.getStorageSync("token");
    if (token) {
      config.header = config.header || {};
      config.header["Authorization"] = `Bearer ${token}`;
    }
    if (!config.url.startsWith("http")) {
      config.url = BASE_URL + config.url;
    }
    return config;
  };
  const responseInterceptor = (response) => {
    const { statusCode, data } = response;
    if (statusCode === 200) {
      return data;
    } else if (statusCode === 401) {
      uni.removeStorageSync("token");
      uni.showToast({
        title: "登录已过期，请重新登录",
        icon: "none"
      });
      uni.navigateTo({
        url: "/pages/login/login"
      });
      return Promise.reject(new Error("登录已过期"));
    } else {
      return Promise.reject(new Error(`请求失败: ${statusCode}`));
    }
  };
  const errorInterceptor = (error) => {
    formatAppLog("error", "at utils/request.js:44", "请求错误:", error);
    uni.showToast({
      title: "网络请求失败",
      icon: "none"
    });
    return Promise.reject(error);
  };
  const request = (options) => {
    const config = requestInterceptor(options);
    return new Promise((resolve, reject) => {
      uni.request({
        ...config,
        success: (res) => {
          try {
            const result = responseInterceptor(res);
            resolve(result);
          } catch (error) {
            reject(error);
          }
        },
        fail: (error) => {
          const handledError = errorInterceptor(error);
          reject(handledError);
        }
      });
    });
  };
  const get = (url, data = {}, config = {}) => {
    return request({
      url,
      data,
      method: "GET",
      ...config
    });
  };
  const post = (url, data = {}, config = {}) => {
    return request({
      url,
      data,
      method: "POST",
      header: {
        "Content-Type": "application/json",
        ...config.header
      },
      ...config
    });
  };
  const put = (url, data = {}, config = {}) => {
    return request({
      url,
      data,
      method: "PUT",
      header: {
        "Content-Type": "application/json",
        ...config.header
      },
      ...config
    });
  };
  const del = (url, data = {}, config = {}) => {
    return request({
      url,
      data,
      method: "DELETE",
      ...config
    });
  };
  const upload = (url, filePath, formData = {}, config = {}) => {
    return new Promise((resolve, reject) => {
      const uploadConfig = requestInterceptor({
        url,
        method: "POST",
        ...config
      });
      uni.uploadFile({
        url: uploadConfig.url,
        filePath,
        name: "file",
        formData,
        header: uploadConfig.header,
        success: (res) => {
          try {
            const result = responseInterceptor({
              statusCode: res.statusCode,
              data: res.data ? JSON.parse(res.data) : res.data
            });
            resolve(result);
          } catch (error) {
            reject(error);
          }
        },
        fail: (error) => {
          const handledError = errorInterceptor(error);
          reject(handledError);
        }
      });
    });
  };
  const request$1 = {
    request,
    get,
    post,
    put,
    del,
    upload
  };
  const getAttractionList = (params) => {
    return request$1.get("/attraction/list", { params });
  };
  const getAttractionDetail = (id) => {
    return request$1.get(`/attraction/${id}`);
  };
  const getHotAttractions = (limit = 10) => {
    return request$1.get("/attraction/hot", {
      params: {
        limit
      }
    });
  };
  const getHotelList = (params) => {
    return request$1.get("/hotel/list", { params });
  };
  const getHotelDetail = (id) => {
    return request$1.get(`/hotel/${id}`);
  };
  const getRecommendHotels = (city, limit = 10) => {
    return request$1.get("/hotel/recommend", {
      params: {
        city,
        limit
      }
    });
  };
  const getFoodList = (params) => {
    return request$1.get("/food/list", { params });
  };
  const getFoodDetail = (id) => {
    return request$1.get(`/food/${id}`);
  };
  const getRecommendFoods = (city, limit = 10) => {
    return request$1.get("/food/recommend", {
      params: {
        city,
        limit
      }
    });
  };
  const getNewsList = (params) => {
    return request$1.get("/news/list", { params });
  };
  const getNewsDetail = (id) => {
    return request$1.get(`/news/${id}`);
  };
  const getHotNews = (params) => {
    return request$1.get("/news/hot", { params });
  };
  const carouselApi = {
    // 获取指定位置的轮播图
    getCarouselsByPosition: (position) => {
      return request$1.get("/carousel/position", {
        params: { position }
      });
    },
    // 获取指定分组的轮播图
    getCarouselsByGroup: (groupCode) => {
      return request$1.get("/carousel/group", {
        params: { groupCode }
      });
    },
    // 获取首页顶部轮播图
    getHomeTopCarousels: () => {
      return request$1.get("/carousel/home/top");
    },
    // 获取首页中部轮播图
    getHomeMiddleCarousels: () => {
      return request$1.get("/carousel/home/middle");
    },
    // 获取目的地轮播图
    getDestinationCarousels: () => {
      return request$1.get("/carousel/destination");
    },
    // 获取景点轮播图
    getAttractionCarousels: () => {
      return request$1.get("/carousel/attraction");
    },
    // 获取酒店轮播图
    getHotelCarousels: () => {
      return request$1.get("/carousel/hotel");
    },
    // 获取美食轮播图
    getFoodCarousels: () => {
      return request$1.get("/carousel/food");
    },
    // 获取纪念品轮播图
    getSouvenirCarousels: () => {
      return request$1.get("/carousel/souvenir");
    },
    // 获取攻略群轮播图
    getStrategyCarousels: () => {
      return request$1.get("/carousel/strategy");
    },
    // 获取社区轮播图
    getCommunityCarousels: () => {
      return request$1.get("/carousel/community");
    },
    // 获取所有轮播图
    getAllCarousels: () => {
      return request$1.get("/carousel/categories");
    },
    // 获取轮播图详情
    getCarouselById: (id) => {
      return request$1.get(`/carousel/${id}`);
    }
  };
  const _export_sfc = (sfc, props) => {
    const target = sfc.__vccOpts || sfc;
    for (const [key, val] of props) {
      target[key] = val;
    }
    return target;
  };
  const _sfc_main$q = {
    __name: "Carousel",
    props: {
      items: {
        type: Array,
        default: () => [],
        validator: (value) => value.every((item) => "imageUrl" in item)
      },
      interval: {
        type: Number,
        default: 3e3
      },
      transitionDuration: {
        type: Number,
        default: 600
      },
      showArrows: {
        type: Boolean,
        default: true
      },
      showIndicators: {
        type: Boolean,
        default: true
      },
      aspectRatio: {
        type: String,
        default: "16/9"
      }
    },
    setup(__props, { expose: __expose }) {
      __expose();
      vue.useCssVars((_ctx) => ({
        "d710d644-aspectRatio": __props.aspectRatio
      }));
      const props = __props;
      const currentIndex = vue.ref(0);
      const autoPlayTimer = vue.ref(null);
      const isTransitioning = vue.ref(false);
      const touchStartX = vue.ref(0);
      const touchEndX = vue.ref(0);
      const trackStyle = vue.computed(() => {
        const offset = props.items.length > 1 ? -(currentIndex.value + 1) * 100 : -currentIndex.value * 100;
        return {
          transform: `translateX(${offset}%)`,
          transition: isTransitioning.value ? `transform ${props.transitionDuration}ms cubic-bezier(0.16, 1, 0.3, 1)` : "none"
        };
      });
      const startAutoPlay = () => {
        if (props.items.length <= 1)
          return;
        stopAutoPlay();
        autoPlayTimer.value = setInterval(() => {
          next();
        }, props.interval);
        startProgressBar();
      };
      const pauseAutoPlay = () => {
        stopAutoPlay();
        clearProgressBar();
      };
      const stopAutoPlay = () => {
        if (autoPlayTimer.value) {
          clearInterval(autoPlayTimer.value);
          autoPlayTimer.value = null;
        }
      };
      const startProgressBar = () => {
        clearProgressBar();
        const progressBars = document.querySelectorAll(".progress-bar");
        if (progressBars[currentIndex.value]) {
          progressBars[currentIndex.value].style.transition = `width ${props.interval}ms linear`;
          progressBars[currentIndex.value].style.width = "100%";
        }
      };
      const clearProgressBar = () => {
        const progressBars = document.querySelectorAll(".progress-bar");
        progressBars.forEach((bar) => {
          bar.style.transition = "none";
          bar.style.width = "0%";
        });
      };
      const goTo = (index) => {
        if (isTransitioning.value || index === currentIndex.value)
          return;
        isTransitioning.value = true;
        currentIndex.value = index;
        setTimeout(() => {
          isTransitioning.value = false;
          startAutoPlay();
        }, props.transitionDuration);
      };
      const next = () => {
        if (isTransitioning.value)
          return;
        isTransitioning.value = true;
        currentIndex.value = (currentIndex.value + 1) % props.items.length;
        setTimeout(() => {
          isTransitioning.value = false;
          if (currentIndex.value === props.items.length - 1) {
            setTimeout(() => {
              isTransitioning.value = false;
              currentIndex.value = 0;
            }, 50);
          }
        }, props.transitionDuration);
      };
      const prev = () => {
        if (isTransitioning.value)
          return;
        isTransitioning.value = true;
        currentIndex.value = (currentIndex.value - 1 + props.items.length) % props.items.length;
        setTimeout(() => {
          isTransitioning.value = false;
          if (currentIndex.value === 0) {
            setTimeout(() => {
              isTransitioning.value = false;
              currentIndex.value = props.items.length - 1;
            }, 50);
          }
        }, props.transitionDuration);
      };
      const handleTouchStart = (e) => {
        touchStartX.value = e.touches[0].clientX;
        pauseAutoPlay();
      };
      const handleTouchMove = (e) => {
        touchEndX.value = e.touches[0].clientX;
      };
      const handleTouchEnd = () => {
        const diff = touchStartX.value - touchEndX.value;
        if (Math.abs(diff) > 50) {
          if (diff > 0) {
            next();
          } else {
            prev();
          }
        } else {
          startAutoPlay();
        }
      };
      vue.onMounted(() => {
        startAutoPlay();
      });
      vue.onUnmounted(() => {
        stopAutoPlay();
        clearProgressBar();
      });
      const __returned__ = { props, currentIndex, autoPlayTimer, isTransitioning, touchStartX, touchEndX, trackStyle, startAutoPlay, pauseAutoPlay, stopAutoPlay, startProgressBar, clearProgressBar, goTo, next, prev, handleTouchStart, handleTouchMove, handleTouchEnd, ref: vue.ref, computed: vue.computed, onMounted: vue.onMounted, onUnmounted: vue.onUnmounted };
      Object.defineProperty(__returned__, "__isScriptSetup", { enumerable: false, value: true });
      return __returned__;
    }
  };
  function _sfc_render$p(_ctx, _cache, $props, $setup, $data, $options) {
    return vue.openBlock(), vue.createElementBlock(
      "div",
      {
        class: "carousel",
        onMouseenter: $setup.pauseAutoPlay,
        onMouseleave: $setup.startAutoPlay
      },
      [
        vue.createElementVNode("div", { class: "carousel-viewport" }, [
          vue.createElementVNode(
            "div",
            {
              class: "carousel-track",
              style: vue.normalizeStyle($setup.trackStyle),
              onTouchstart: $setup.handleTouchStart,
              onTouchmove: $setup.handleTouchMove,
              onTouchend: $setup.handleTouchEnd
            },
            [
              $props.items.length > 1 ? (vue.openBlock(), vue.createElementBlock("div", {
                key: 0,
                class: "carousel-slide"
              }, [
                vue.createElementVNode("div", { class: "slide-content" }, [
                  vue.createElementVNode("img", {
                    src: $props.items[$props.items.length - 1].imageUrl,
                    class: "slide-image"
                  }, null, 8, ["src"]),
                  vue.createElementVNode("div", { class: "slide-overlay" }),
                  $props.items[$props.items.length - 1].title ? (vue.openBlock(), vue.createElementBlock("div", {
                    key: 0,
                    class: "slide-caption"
                  }, [
                    vue.createElementVNode(
                      "h3",
                      null,
                      vue.toDisplayString($props.items[$props.items.length - 1].title),
                      1
                      /* TEXT */
                    ),
                    $props.items[$props.items.length - 1].subtitle ? (vue.openBlock(), vue.createElementBlock(
                      "p",
                      { key: 0 },
                      vue.toDisplayString($props.items[$props.items.length - 1].subtitle),
                      1
                      /* TEXT */
                    )) : vue.createCommentVNode("v-if", true)
                  ])) : vue.createCommentVNode("v-if", true)
                ])
              ])) : vue.createCommentVNode("v-if", true),
              (vue.openBlock(true), vue.createElementBlock(
                vue.Fragment,
                null,
                vue.renderList($props.items, (item, index) => {
                  return vue.openBlock(), vue.createElementBlock("div", {
                    class: "carousel-slide",
                    key: index
                  }, [
                    vue.createElementVNode("div", { class: "slide-content" }, [
                      vue.createElementVNode("img", {
                        src: item.imageUrl,
                        class: "slide-image"
                      }, null, 8, ["src"]),
                      vue.createElementVNode("div", { class: "slide-overlay" }),
                      item.title ? (vue.openBlock(), vue.createElementBlock("div", {
                        key: 0,
                        class: "slide-caption"
                      }, [
                        vue.createElementVNode(
                          "h3",
                          null,
                          vue.toDisplayString(item.title),
                          1
                          /* TEXT */
                        ),
                        item.subtitle ? (vue.openBlock(), vue.createElementBlock(
                          "p",
                          { key: 0 },
                          vue.toDisplayString(item.subtitle),
                          1
                          /* TEXT */
                        )) : vue.createCommentVNode("v-if", true)
                      ])) : vue.createCommentVNode("v-if", true)
                    ])
                  ]);
                }),
                128
                /* KEYED_FRAGMENT */
              )),
              $props.items.length > 1 ? (vue.openBlock(), vue.createElementBlock("div", {
                key: 1,
                class: "carousel-slide"
              }, [
                vue.createElementVNode("div", { class: "slide-content" }, [
                  vue.createElementVNode("img", {
                    src: $props.items[0].imageUrl,
                    class: "slide-image"
                  }, null, 8, ["src"]),
                  vue.createElementVNode("div", { class: "slide-overlay" }),
                  $props.items[0].title ? (vue.openBlock(), vue.createElementBlock("div", {
                    key: 0,
                    class: "slide-caption"
                  }, [
                    vue.createElementVNode(
                      "h3",
                      null,
                      vue.toDisplayString($props.items[0].title),
                      1
                      /* TEXT */
                    ),
                    $props.items[0].subtitle ? (vue.openBlock(), vue.createElementBlock(
                      "p",
                      { key: 0 },
                      vue.toDisplayString($props.items[0].subtitle),
                      1
                      /* TEXT */
                    )) : vue.createCommentVNode("v-if", true)
                  ])) : vue.createCommentVNode("v-if", true)
                ])
              ])) : vue.createCommentVNode("v-if", true)
            ],
            36
            /* STYLE, NEED_HYDRATION */
          )
        ]),
        $props.showIndicators && $props.items.length > 1 ? (vue.openBlock(), vue.createElementBlock("div", {
          key: 0,
          class: "carousel-pagination"
        }, [
          (vue.openBlock(true), vue.createElementBlock(
            vue.Fragment,
            null,
            vue.renderList($props.items, (item, index) => {
              return vue.openBlock(), vue.createElementBlock("button", {
                key: index,
                class: vue.normalizeClass({ "active": $setup.currentIndex === index }),
                onClick: ($event) => $setup.goTo(index),
                "aria-label": `Go to slide ${index + 1}`
              }, [
                $setup.currentIndex === index ? (vue.openBlock(), vue.createElementBlock("span", {
                  key: 0,
                  class: "progress-bar"
                })) : vue.createCommentVNode("v-if", true)
              ], 10, ["onClick", "aria-label"]);
            }),
            128
            /* KEYED_FRAGMENT */
          ))
        ])) : vue.createCommentVNode("v-if", true),
        $props.showArrows && $props.items.length > 1 ? (vue.openBlock(), vue.createElementBlock("button", {
          key: 1,
          class: "carousel-nav prev",
          onClick: $setup.prev,
          "aria-label": "Previous slide"
        }, [
          (vue.openBlock(), vue.createElementBlock("svg", {
            width: "24",
            height: "24",
            viewBox: "0 0 24 24",
            fill: "none",
            xmlns: "http://www.w3.org/2000/svg"
          }, [
            vue.createElementVNode("path", {
              d: "M15 18L9 12L15 6",
              stroke: "currentColor",
              "stroke-width": "2",
              "stroke-linecap": "round",
              "stroke-linejoin": "round"
            })
          ]))
        ])) : vue.createCommentVNode("v-if", true),
        $props.showArrows && $props.items.length > 1 ? (vue.openBlock(), vue.createElementBlock("button", {
          key: 2,
          class: "carousel-nav next",
          onClick: $setup.next,
          "aria-label": "Next slide"
        }, [
          (vue.openBlock(), vue.createElementBlock("svg", {
            width: "24",
            height: "24",
            viewBox: "0 0 24 24",
            fill: "none",
            xmlns: "http://www.w3.org/2000/svg"
          }, [
            vue.createElementVNode("path", {
              d: "M9 6L15 12L9 18",
              stroke: "currentColor",
              "stroke-width": "2",
              "stroke-linecap": "round",
              "stroke-linejoin": "round"
            })
          ]))
        ])) : vue.createCommentVNode("v-if", true)
      ],
      32
      /* NEED_HYDRATION */
    );
  }
  const Carousel = /* @__PURE__ */ _export_sfc(_sfc_main$q, [["render", _sfc_render$p], ["__scopeId", "data-v-d710d644"], ["__file", "D:/IDEA/work/Minecraft-system/minecraft-App/pages/carousel/Carousel.vue"]]);
  const _sfc_main$p = {
    __name: "index",
    setup(__props, { expose: __expose }) {
      __expose();
      const carouselList = vue.ref([]);
      const recommendAttractions = vue.ref([]);
      const recommendHotels = vue.ref([]);
      const recommendFoods = vue.ref([]);
      const hotNews = vue.ref([]);
      const getRecommendAttractions = async () => {
        try {
          const response = await getHotAttractions(4);
          recommendAttractions.value = response.data;
        } catch (error) {
          formatAppLog("error", "at pages/index/index.vue:287", "获取推荐景点失败:", error);
        }
      };
      const getRecommendHotelsList = async () => {
        try {
          const response = await getRecommendHotels("", 4);
          recommendHotels.value = response.data;
        } catch (error) {
          formatAppLog("error", "at pages/index/index.vue:297", "获取推荐酒店失败:", error);
        }
      };
      const getRecommendFoodsList = async () => {
        try {
          const response = await getRecommendFoods("", 4);
          recommendFoods.value = response.data;
        } catch (error) {
          formatAppLog("error", "at pages/index/index.vue:307", "获取推荐美食失败:", error);
        }
      };
      const getHotNewsList = async () => {
        try {
          const response = await getHotNews({ limit: 4 });
          hotNews.value = response.data;
        } catch (error) {
          formatAppLog("error", "at pages/index/index.vue:317", "获取热门新闻失败:", error);
        }
      };
      const getCarouselData = async () => {
        try {
          const response = await carouselApi.getHomeTopCarousels();
          carouselList.value = response.data;
        } catch (error) {
          formatAppLog("error", "at pages/index/index.vue:327", "获取轮播图数据失败:", error);
        }
      };
      const navigateTo = (url) => {
        const pagePath = url.split("?")[0];
        const tabbarPages = [
          "/pages/index/index",
          "/pages/discover/index",
          "/pages/community/index",
          "/pages/news/index",
          "/pages/profile/index"
        ];
        if (tabbarPages.includes(pagePath)) {
          uni.switchTab({
            url: pagePath
          });
        } else {
          uni.navigateTo({
            url
          });
        }
      };
      vue.onMounted(() => {
        getCarouselData();
        getRecommendAttractions();
        getRecommendHotelsList();
        getRecommendFoodsList();
        getHotNewsList();
      });
      vue.onUnmounted(() => {
      });
      const __returned__ = { carouselList, recommendAttractions, recommendHotels, recommendFoods, hotNews, getRecommendAttractions, getRecommendHotelsList, getRecommendFoodsList, getHotNewsList, getCarouselData, navigateTo, ref: vue.ref, onMounted: vue.onMounted, onUnmounted: vue.onUnmounted, get getHotAttractions() {
        return getHotAttractions;
      }, get getRecommendHotels() {
        return getRecommendHotels;
      }, get getRecommendFoods() {
        return getRecommendFoods;
      }, get getHotNews() {
        return getHotNews;
      }, get carouselApi() {
        return carouselApi;
      }, Carousel };
      Object.defineProperty(__returned__, "__isScriptSetup", { enumerable: false, value: true });
      return __returned__;
    }
  };
  function _sfc_render$o(_ctx, _cache, $props, $setup, $data, $options) {
    return vue.openBlock(), vue.createElementBlock("div", { class: "page-container" }, [
      vue.createVNode($setup["Carousel"], {
        items: $setup.carouselList,
        interval: 3e3,
        transitionDuration: 600,
        showArrows: true,
        showIndicators: true,
        aspectRatio: "16/9"
      }, null, 8, ["items"]),
      vue.createElementVNode("div", { class: "glass-morphism nav-container" }, [
        vue.createElementVNode("div", {
          class: "nav-item",
          onClick: _cache[0] || (_cache[0] = ($event) => $setup.navigateTo("/pages/attraction/index"))
        }, [
          vue.createElementVNode("div", { class: "nav-icon" }, [
            (vue.openBlock(), vue.createElementBlock("svg", {
              t: "1774967500532",
              class: "icon",
              viewBox: "0 0 1024 1024",
              version: "1.1",
              xmlns: "http://www.w3.org/2000/svg",
              "p-id": "6031",
              width: "256",
              height: "256"
            }, [
              vue.createElementVNode("path", {
                d: "M568.074 954.878l-5.845-23.73c0 0-66.445-217.909-150.15-389.469 59.057-165.185 139.96-384.977 163.93-417.841C615.053 70.292 725.533-16.31 822.863 277.232c121.78 367.142 187.22 677.646 187.22 677.646L568.074 954.878zM747.328 232.189c-39.68-97.332-74.965-124.877-112.8-102.695-37.84 22.227-95.445 248.594-95.445 248.594s34.265-28.56 64.52-30.88c30.25-2.32 71.905 53.932 71.905 53.932s23.87-69.542 52.145-66.885c28.32 2.61 75.64 37.697 75.64 37.697S786.998 329.519 747.328 232.189zM510.814 954.878l-486.659-2.035c0 0 104.097-262.609 135.555-351.674 31.462-89.067 92.015-227.959 174.37-99.555 82.302 128.41 157.402 374.639 157.402 374.639L510.814 954.878zM313.637 582.083c-21.457-51.955-40.405-66.6-60.607-54.615-20.2 11.985-50.452 133.29-50.452 133.29s18.27-15.365 34.455-16.725c16.147-1.3 38.617 28.66 38.617 28.66s12.565-37.26 27.692-35.905c15.175 1.31 40.592 19.91 40.592 19.91S335.094 634.078 313.637 582.083z",
                fill: "#0e932e",
                "p-id": "6032"
              })
            ])),
            vue.createTextVNode(" ️ ")
          ]),
          vue.createElementVNode("div", { class: "nav-text" }, "景点")
        ]),
        vue.createElementVNode("div", {
          class: "nav-item",
          onClick: _cache[1] || (_cache[1] = ($event) => $setup.navigateTo("/pages/hotel/index"))
        }, [
          vue.createElementVNode("div", { class: "nav-icon" }, [
            (vue.openBlock(), vue.createElementBlock("svg", {
              t: "1774967533900",
              class: "icon",
              viewBox: "0 0 1024 1024",
              version: "1.1",
              xmlns: "http://www.w3.org/2000/svg",
              "p-id": "8849",
              width: "256",
              height: "256"
            }, [
              vue.createElementVNode("path", {
                d: "M513.024 510.0544m-453.2224 0a453.2224 453.2224 0 1 0 906.4448 0 453.2224 453.2224 0 1 0-906.4448 0Z",
                fill: "#58A2F8",
                "p-id": "8850"
              }),
              vue.createElementVNode("path", {
                d: "M513.024 56.832c-250.3168 0-453.2224 202.9056-453.2224 453.2224 0 99.7888 32.256 192 86.8864 266.8544 74.8544 54.6304 167.0656 86.8864 266.8544 86.8864 250.3168 0 453.2224-202.9056 453.2224-453.2224 0-99.7888-32.256-192-86.8864-266.8544C704.9728 89.088 612.7616 56.832 513.024 56.832z",
                fill: "#57ADFA",
                "p-id": "8851"
              }),
              vue.createElementVNode("path", {
                d: "M756.6848 303.5136c0-72.6016-17.152-141.2096-47.5136-202.0352-59.3408-28.5696-125.8496-44.5952-196.096-44.5952-250.3168 0-453.2224 202.9056-453.2224 453.2224 0 72.6016 17.152 141.2096 47.5136 202.0352 59.3408 28.5696 125.8496 44.5952 196.096 44.5952 250.2656 0 453.2224-202.9056 453.2224-453.2224z",
                fill: "#55C2FD",
                "p-id": "8852"
              }),
              vue.createElementVNode("path", {
                d: "M59.8016 510.0544c0 38.912 4.9664 76.6464 14.2336 112.64a443.5968 443.5968 0 0 0 114.688 15.0528c245.7088 0 444.928-199.2192 444.928-444.928 0-43.1616-6.2464-84.7872-17.7152-124.2112-33.024-7.68-67.4816-11.776-102.8608-11.776-250.368 0-453.2736 202.9056-453.2736 453.2224z",
                fill: "#54D2FF",
                "p-id": "8853"
              }),
              vue.createElementVNode("path", {
                d: "M712.704 583.68l49.2544-19.2512v158.5152h29.0816s20.3776 0.768 20.3776 20.9408v34.8672s0.512 20.48-20.9408 20.48h-264.8576l10.752-60.3648 130.0992-78.9504s15.104-3.7888 20.224-20.224c5.0688-16.4352 26.0096-56.0128 26.0096-56.0128z",
                fill: "#BFE0FF",
                "p-id": "8854"
              }),
              vue.createElementVNode("path", {
                d: "M346.7776 422.7584V363.8272c0-15.3088 12.3904-27.6992 27.6992-27.6992h58.9312l141.4144-141.4144h-235.52c-43.3664 0-78.5408 35.1744-78.5408 78.5408v235.4688l86.016-85.9648z",
                fill: "#FFFFFF",
                "p-id": "8855"
              }),
              vue.createElementVNode("path", {
                d: "M683.3664 194.7136h-108.5952L433.408 336.128h18.1248c15.3088 0 27.6992 12.3904 27.6992 27.6992v77.056c0 15.3088-12.3904 27.6992-27.6992 27.6992H374.4768c-15.3088 0-27.6992-12.3904-27.6992-27.6992v-18.1248l-86.016 86.016v214.1696h-28.4672c-11.5712 0-20.9408 9.3696-20.9408 20.9408v34.4576c0 11.5712 9.3696 20.9408 20.9408 20.9408h16.4352l138.3936-138.3936h-12.6464c-15.3088 0-27.6992-12.3904-27.6992-27.6992v-77.056c0-15.3088 12.3904-27.6992 27.6992-27.6992h77.056c15.3088 0 27.6992 12.3904 27.6992 27.6992v12.6464l100.5056-100.5056c-13.824-1.536-24.576-13.1584-24.576-27.392V363.8272c0-15.3088 12.3904-27.6992 27.6992-27.6992h77.056c14.2336 0 25.8048 10.8032 27.392 24.6272l74.752-74.752v-12.6976c-0.1024-43.4176-35.2768-78.592-78.6944-78.592z",
                fill: "#EAF5FF",
                "p-id": "8856"
              }),
              vue.createElementVNode("path", {
                d: "M687.2064 360.7552c0.1024 1.024 0.3072 2.048 0.3072 3.072v77.056c0 15.3088-12.3904 27.6992-27.6992 27.6992h-77.056c-1.0752 0-2.048-0.2048-3.072-0.3072l-100.5056 100.5056v64.4096c0 15.3088-12.3904 27.6992-27.6992 27.6992H387.1232L248.7296 799.232h278.4256l139.264-139.264c-2.0992 0.512-4.3008 0.8704-6.5536 0.8704h-77.056c-15.3088 0-27.6992-12.3904-27.6992-27.6992v-77.056c0-15.3088 12.3904-27.6992 27.6992-27.6992h77.056c15.3088 0 27.6992 12.3904 27.6992 27.6992v77.056c0 2.2528-0.3584 4.4544-0.8704 6.5536l75.264-75.264V286.0032l-74.752 74.752z",
                fill: "#D6EBFF",
                "p-id": "8857"
              })
            ]))
          ]),
          vue.createElementVNode("div", { class: "nav-text" }, "酒店")
        ]),
        vue.createElementVNode("div", {
          class: "nav-item",
          onClick: _cache[2] || (_cache[2] = ($event) => $setup.navigateTo("/pages/ticket/index"))
        }, [
          vue.createElementVNode("div", { class: "nav-icon" }, [
            (vue.openBlock(), vue.createElementBlock("svg", {
              t: "1774967621564",
              class: "icon",
              viewBox: "0 0 1024 1024",
              version: "1.1",
              xmlns: "http://www.w3.org/2000/svg",
              "p-id": "10876",
              width: "256",
              height: "256"
            }, [
              vue.createElementVNode("path", {
                d: "M512 512m-512 0a512 512 0 1 0 1024 0 512 512 0 1 0-1024 0Z",
                fill: "#FFFFFF",
                "p-id": "10877"
              }),
              vue.createElementVNode("path", {
                d: "M884.696 519.76c-41.32 9.536-82.576-16.24-92.112-57.552s16.24-82.576 57.56-92.112l-37.44-162.136a12.64 12.64 0 0 0-15.344-9.592L123.872 353.856a12.64 12.64 0 0 0-9.592 15.352l37.432 162.136c41.312-9.536 82.568 16.24 92.104 57.552 9.544 41.312-16.24 82.568-57.552 92.112l31.68 137.192a12.64 12.64 0 0 0 15.344 9.592l673.488-155.488a12.64 12.64 0 0 0 9.592-15.352l-31.68-137.192z",
                fill: "#FF7400",
                "p-id": "10878"
              }),
              vue.createElementVNode("path", {
                d: "M230.528 840.744c-4.856 0.296-9.288-1.144-13.312-4.32-6.352-2.64-10.128-8.336-11.752-15.344l-34.552-149.664 12.48-2.88c34.288-7.92 55.872-42.464 47.952-76.76-7.92-34.296-42.456-55.88-76.752-47.96l-12.48 2.88-40.304-174.608c-1.616-7.016-0.72-13.792 2.88-19.552 3.6-5.76 9.288-9.536 16.304-11.152L794.48 185.896c14.032-3.24 27.464 5.16 30.704 19.184l40.304 174.608-12.472 2.88c-34.296 7.92-55.88 42.464-47.96 76.76 3.784 16.368 13.616 30.52 28.184 40.288 14.4 8.992 31.424 11.632 48.576 7.68l12.472-2.88 34.552 149.656c3.24 14.032-5.16 27.464-19.184 30.704L236.16 840.264c-1.56 0.36-3.896 0.896-5.64 0.48zM201.44 689.816l28.976 125.504 673.488-155.488-28.976-125.496a82.96 82.96 0 0 1-54.864-13.608c-20.152-12.592-34.544-32.256-39.944-55.64-10.08-43.656 14.2-87.848 54.856-103.8l-34.728-150.448-673.488 155.488 34.736 150.44c43.528-3.48 84.72 25.6 94.8 69.248 10.08 43.656-14.2 87.848-54.856 103.8z",
                fill: "#333333",
                "p-id": "10879"
              }),
              vue.createElementVNode("path", {
                d: "M729.28 700.144l-11.512-49.888 24.944-5.76 11.52 49.888-24.944 5.76z m-17.272-74.832l-17.28-74.832 24.952-5.76 17.28 74.832-24.952 5.76z m-23.032-99.776l-17.28-74.832 24.944-5.76 17.28 74.832-24.944 5.76z m-23.04-99.776l-17.272-74.832 24.944-5.76 17.28 74.832-24.944 5.76z m-23.032-99.776l-17.28-74.832 24.952-5.76 17.28 74.832-24.952 5.76z",
                fill: "#2C2C2C",
                "p-id": "10880"
              }),
              vue.createElementVNode("path", {
                d: "M421.6 427.16l-3.424-14.808 16.368-3.776 3.416 14.8 91.2-21.048 3.24 14.032-91.2 21.056 2.88 12.472 70.936-16.376 15.12 65.472-10.92 2.52c15.776 7.856 28.24 15.648 37.24 22.608l-9.712 11.272c-4.976-3.776-9.952-7.552-15.712-11.152a4128.952 4128.952 0 0 1-165.192 45.52l-5.576-13.488c31-7.976 59.84-14.64 85.384-21.352l-3.24-14.032-70.936 16.376-15.12-65.472 70.944-16.376-2.88-12.48-91.2 21.056-3.24-14.024 91.616-22.8z m-25.776 147.992c-1.136 19.968-5.152 38.136-11.272 54.32l-15.224-5.504c7.072-15.6 10.664-32.024 11.744-48.696l14.752-0.12z m38.184-94.208l-3.416-14.8-55.344 12.768 3.416 14.816 55.344-12.784z m2.52 10.92l-55.344 12.776 3.424 14.808 55.344-12.776-3.424-14.808z m7.392 128.016c-13.256 3.056-20.928-1.736-23.808-14.208l-9-38.976 15.592-3.6 8.28 35.856c1.08 4.68 4.912 7.08 10.368 5.816l51.448-11.88c3.896-0.896 6.84-2.4 8.64-5.28 1.792-2.872 1.736-10.248-0.368-22.896l15.896 1.256c2.216 16.72 1.44 27.584-1.56 32.376-3.176 4.016-8.272 6.84-16.072 8.64l-59.416 12.896z m5.68-142.528l55.344-12.784-3.416-14.8-55.344 12.768 3.416 14.816z m41.56 94.672l-10.488 11.456c-8.216-7.136-19.728-14.328-33.16-22.72l10.672-10.68c13.432 8.4 24.944 15.592 32.976 21.944z m17.08-96.72l-55.344 12.776 3.424 14.808 55.344-12.776-3.424-14.808z m-2.928 33.52l4.736-4.376-51.448 11.872 3.24 14.032a7960.72 7960.72 0 0 0 54.8-15.12c-3.84-2.392-7.68-4.792-11.328-6.408z m65.6 63.68l-10.488 11.44c-11.64-11.264-25.968-23.552-43.776-36.688l10.672-10.672a526.592 526.592 0 0 1 43.6 35.92z",
                fill: "#434343",
                "p-id": "10881"
              }),
              vue.createElementVNode("path", {
                d: "M873.6 601.6c-42.4 0-76.8-34.4-76.8-76.8s34.4-76.8 76.8-76.8V281.6c0-7.2-5.6-12.8-12.8-12.8H169.6c-7.2 0-12.8 5.6-12.8 12.8V448c42.4 0 76.8 34.4 76.8 76.8s-34.4 76.8-76.8 76.8v140.8c0 7.2 5.6 12.8 12.8 12.8h691.2c7.2 0 12.8-5.6 12.8-12.8V601.6z",
                fill: "#FFB900",
                "p-id": "10882"
              }),
              vue.createElementVNode("path", {
                d: "M860.8 768H169.6c-14.4 0-25.6-11.2-25.6-25.6V588.8h12.8c35.2 0 64-28.8 64-64s-28.8-64-64-64H144V281.6c0-14.4 11.2-25.6 25.6-25.6h691.2c14.4 0 25.6 11.2 25.6 25.6v179.2h-12.8c-35.2 0-64 28.8-64 64s28.8 64 64 64h12.8v153.6c0 14.4-11.2 25.6-25.6 25.6zM169.6 613.6v128.8h691.2V613.6c-43.2-6.4-76.8-44-76.8-88.8s33.6-82.4 76.8-88.8V281.6H169.6v154.4c43.2 6.4 76.8 44 76.8 88.8s-33.6 82.4-76.8 88.8z",
                fill: "#333333",
                "p-id": "10883"
              }),
              vue.createElementVNode("path", {
                d: "M707.2 742.4h-25.6v-51.2h25.6v51.2z m0-76.8h-25.6v-76.8h25.6v76.8z m0-102.4h-25.6v-76.8h25.6v76.8z m0-102.4h-25.6V384h25.6v76.8z m0-102.4h-25.6v-76.8h25.6v76.8z",
                fill: "#2C2C2C",
                "p-id": "10884"
              }),
              vue.createElementVNode("path", {
                d: "M553.6 401.6V416h-69.6v18.4h52.8v51.2H365.6v-51.2h52.8V416h-69.6v-14.4h204.8z m-4 132v14.4h-88V584c0 12.8-7.2 19.2-20.8 19.2h-20.8l-3.2-14.4c6.4 0.8 12.8 0.8 18.4 0.8 6.4 0 9.6-3.2 9.6-8.8v-32H352.8v-14.4h196.8z m-130.4 32.8c-15.2 13.6-34.4 24.8-56.8 34.4l-9.6-12.8c22.4-9.6 40.8-20 56-32l10.4 10.4z m108.8-64v13.6H375.2v-13.6H528zM418.4 448h-36.8v24h36.8v-24z m50.4-32h-34.4v18.4h34.4V416z m0 32h-34.4v24h34.4v-24z m52 0h-36.8v24h36.8v-24zM552 589.6l-11.2 11.2c-12.8-11.2-30.4-22.4-52.8-35.2l11.2-10.4c20 12 38.4 23.2 52.8 34.4z",
                fill: "#434343",
                "p-id": "10885"
              })
            ]))
          ]),
          vue.createElementVNode("div", { class: "nav-text" }, "票务")
        ]),
        vue.createElementVNode("div", {
          class: "nav-item",
          onClick: _cache[3] || (_cache[3] = ($event) => $setup.navigateTo("/pages/food/index"))
        }, [
          vue.createElementVNode("div", { class: "nav-icon" }, [
            (vue.openBlock(), vue.createElementBlock("svg", {
              t: "1774967652713",
              class: "icon",
              viewBox: "0 0 1024 1024",
              version: "1.1",
              xmlns: "http://www.w3.org/2000/svg",
              "p-id": "13396",
              width: "256",
              height: "256"
            }, [
              vue.createElementVNode("path", {
                d: "M921.344 867.4816H101.0688c-21.248 0-38.4512-17.2032-38.4512-38.4512v-33.3312c0-21.248 17.2032-38.4512 38.4512-38.4512h820.2752c21.248 0 38.4512 17.2032 38.4512 38.4512v33.3312c0 21.248-17.2032 38.4512-38.4512 38.4512zM904.704 591.9744h-5.6832c-21.0944-156.8256-134.912-284.16-284.5696-324.9664 0-0.512 0.0512-0.9728 0.0512-1.4848 0-57.0368-46.2336-103.3216-103.3216-103.3216s-103.3216 46.2336-103.3216 103.3216c0 0.512 0.0512 0.9728 0.0512 1.4848-149.6576 40.8064-263.5264 168.1408-284.5696 324.9664h-5.6832c-30.4128 0-55.0912 24.6784-55.0912 55.0912 0 30.4128 24.6784 55.0912 55.0912 55.0912h786.944c30.4128 0 55.0912-24.6784 55.0912-55.0912 0.1024-30.4128-24.576-55.0912-54.9888-55.0912zM447.5392 436.7872c-3.84 0.5632-86.6816 13.8752-121.1904 96.4096a37.06368 37.06368 0 0 1-34.2528 22.784 37.12 37.12 0 0 1-34.2016-51.456c51.3536-122.7264 174.3872-140.5952 179.5584-141.312 20.224-2.7648 39.0144 11.52 41.728 31.8464a37.2224 37.2224 0 0 1-31.6416 41.728z",
                fill: "#FF3369",
                "p-id": "13397"
              }),
              vue.createElementVNode("path", {
                d: "M889.6 544.6656C854.1184 409.9072 748.6976 303.616 614.4 267.008c0-0.512 0.0512-0.9728 0.0512-1.4848 0-57.0368-46.2336-103.3216-103.3216-103.3216-57.0368 0-103.3216 46.2336-103.3216 103.3216 0 0.512 0.0512 0.9728 0.0512 1.4848-149.6576 40.8064-263.5264 168.1408-284.5696 324.9664h-5.6832c-30.4128 0-55.0912 24.6784-55.0912 55.0912s24.6784 55.0912 55.0912 55.0912h689.152c34.9184-47.7184 62.976-100.6592 82.8416-157.4912zM447.5392 436.7872c-3.84 0.5632-86.6816 13.8752-121.1904 96.4096a37.06368 37.06368 0 0 1-34.2528 22.784 37.12 37.12 0 0 1-34.2016-51.456c51.3536-122.7264 174.3872-140.5952 179.6096-141.312 20.224-2.7648 39.0144 11.52 41.728 31.8464 2.6112 20.224-11.52 38.8608-31.6928 41.728zM114.5856 757.2992c-28.7232 0-51.968 23.2448-51.968 51.968v6.2976c0 28.7232 23.2448 51.968 51.968 51.968h510.9248c50.5344-30.1056 96.3584-67.2768 136.0896-110.2336H114.5856z",
                fill: "#FF4D7C",
                "p-id": "13398"
              }),
              vue.createElementVNode("path", {
                d: "M726.4256 317.7472c-33.9968-22.4256-71.68-39.7824-111.9744-50.7392 0-0.512 0.0512-0.9728 0.0512-1.4848 0-57.0368-46.2336-103.3216-103.3216-103.3216-57.0368 0-103.3216 46.2336-103.3216 103.3216 0 0.512 0.0512 0.9728 0.0512 1.4848-149.6576 40.8064-263.5264 168.1408-284.5696 324.9664h-5.6832c-30.4128 0-55.0912 24.6784-55.0912 55.0912s24.6784 55.0912 55.0912 55.0912h231.7824c177.8688-62.464 317.952-205.0048 376.9856-384.4096z m-434.3808 238.2336a37.12 37.12 0 0 1-34.2016-51.456c51.3536-122.7264 174.3872-140.5952 179.6096-141.312 20.224-2.7648 39.0144 11.52 41.728 31.8464a37.0944 37.0944 0 0 1-31.6416 41.6768c-3.84 0.5632-86.6816 13.8752-121.1904 96.4096a37.15072 37.15072 0 0 1-34.304 22.8352z",
                fill: "#FF5F89",
                "p-id": "13399"
              }),
              vue.createElementVNode("path", {
                d: "M146.8928 501.5552c53.8624-13.9264 104.8576-35.0208 151.8592-62.1568 27.9552-31.232 60.4672-49.9712 86.9888-61.0304 67.8912-57.0368 123.136-128.512 161.024-209.7664-11.1104-4.096-23.04-6.4-35.584-6.4-57.0368 0-103.3216 46.2336-103.3216 103.3216 0 0.512 0.0512 0.9728 0.0512 1.4848-119.552 32.6144-216.2176 120.5248-261.0176 234.5472z",
                fill: "#FF7196",
                "p-id": "13400"
              }),
              vue.createElementVNode("path", {
                d: "M206.6432 757.2992H114.5856c-24.2688 0-44.5952 16.64-50.3296 39.168 51.3536-11.0592 100.1472-25.2416 142.3872-39.168z",
                fill: "#FF5F89",
                "p-id": "13401"
              })
            ]))
          ]),
          vue.createElementVNode("div", { class: "nav-text" }, "美食")
        ]),
        vue.createElementVNode("div", {
          class: "nav-item",
          onClick: _cache[4] || (_cache[4] = ($event) => $setup.navigateTo("/pages/product/index"))
        }, [
          vue.createElementVNode("div", { class: "nav-icon" }, "🛍️"),
          vue.createElementVNode("div", { class: "nav-text" }, "特产")
        ]),
        vue.createElementVNode("div", {
          class: "nav-item",
          onClick: _cache[5] || (_cache[5] = ($event) => $setup.navigateTo("/pages/travelPlan/index"))
        }, [
          vue.createElementVNode("div", { class: "nav-icon" }, "📅"),
          vue.createElementVNode("div", { class: "nav-text" }, "出行计划")
        ]),
        vue.createElementVNode("div", {
          class: "nav-item",
          onClick: _cache[6] || (_cache[6] = ($event) => $setup.navigateTo("/pages/groupTravel/index"))
        }, [
          vue.createElementVNode("div", { class: "nav-icon" }, "👥"),
          vue.createElementVNode("div", { class: "nav-text" }, "跟团游")
        ]),
        vue.createElementVNode("div", {
          class: "nav-item",
          onClick: _cache[7] || (_cache[7] = ($event) => $setup.navigateTo("/pages/news/index"))
        }, [
          vue.createElementVNode("div", { class: "nav-icon" }, "📰"),
          vue.createElementVNode("div", { class: "nav-text" }, "新闻")
        ])
      ]),
      vue.createElementVNode("div", { class: "section" }, [
        vue.createElementVNode("div", { class: "section-header" }, [
          vue.createElementVNode("h2", null, "推荐景点"),
          vue.createElementVNode("button", {
            class: "more-btn",
            onClick: _cache[8] || (_cache[8] = ($event) => $setup.navigateTo("/pages/attraction/index"))
          }, "查看更多")
        ]),
        vue.createElementVNode("div", { class: "recommend-list" }, [
          (vue.openBlock(true), vue.createElementBlock(
            vue.Fragment,
            null,
            vue.renderList($setup.recommendAttractions, (attraction) => {
              return vue.openBlock(), vue.createElementBlock("div", {
                key: attraction.id,
                class: "glass-card attraction-item",
                onClick: ($event) => $setup.navigateTo(`/pages/attraction/detail?id=${attraction.id}`)
              }, [
                vue.createElementVNode("img", {
                  src: attraction.coverImage,
                  alt: attraction.name,
                  class: "item-image"
                }, null, 8, ["src", "alt"]),
                vue.createElementVNode("div", { class: "item-info" }, [
                  vue.createElementVNode(
                    "h3",
                    null,
                    vue.toDisplayString(attraction.name),
                    1
                    /* TEXT */
                  ),
                  vue.createElementVNode(
                    "p",
                    { class: "text-secondary" },
                    vue.toDisplayString(attraction.city),
                    1
                    /* TEXT */
                  ),
                  attraction.price > 0 ? (vue.openBlock(), vue.createElementBlock(
                    "div",
                    {
                      key: 0,
                      class: "attraction-price"
                    },
                    "¥" + vue.toDisplayString(attraction.price),
                    1
                    /* TEXT */
                  )) : (vue.openBlock(), vue.createElementBlock("div", {
                    key: 1,
                    class: "attraction-price free"
                  }, "免费")),
                  vue.createElementVNode("div", { class: "rating" }, [
                    (vue.openBlock(true), vue.createElementBlock(
                      vue.Fragment,
                      null,
                      vue.renderList(attraction.rating, (i) => {
                        return vue.openBlock(), vue.createElementBlock("span", {
                          key: i,
                          class: "star"
                        }, "★");
                      }),
                      128
                      /* KEYED_FRAGMENT */
                    ))
                  ]),
                  vue.createElementVNode(
                    "div",
                    { class: "attraction-address" },
                    vue.toDisplayString(attraction.address),
                    1
                    /* TEXT */
                  ),
                  attraction.season ? (vue.openBlock(), vue.createElementBlock(
                    "div",
                    {
                      key: 2,
                      class: "attraction-season"
                    },
                    vue.toDisplayString(attraction.season),
                    1
                    /* TEXT */
                  )) : vue.createCommentVNode("v-if", true),
                  vue.createElementVNode("div", { class: "attraction-stats stats" }, [
                    vue.createElementVNode("div", { class: "attraction-stat" }, [
                      vue.createElementVNode(
                        "span",
                        null,
                        vue.toDisplayString(attraction.likeCount) + " 点赞",
                        1
                        /* TEXT */
                      ),
                      vue.createElementVNode(
                        "span",
                        null,
                        vue.toDisplayString(attraction.commentCount) + " 评论",
                        1
                        /* TEXT */
                      ),
                      vue.createElementVNode(
                        "span",
                        null,
                        vue.toDisplayString(attraction.collectCount) + " 收藏",
                        1
                        /* TEXT */
                      )
                    ])
                  ])
                ])
              ], 8, ["onClick"]);
            }),
            128
            /* KEYED_FRAGMENT */
          ))
        ])
      ]),
      vue.createElementVNode("div", { class: "section" }, [
        vue.createElementVNode("div", { class: "section-header" }, [
          vue.createElementVNode("h2", null, "推荐酒店"),
          vue.createElementVNode("button", {
            class: "more-btn",
            onClick: _cache[9] || (_cache[9] = ($event) => $setup.navigateTo("/pages/hotel/index"))
          }, "查看更多")
        ]),
        vue.createElementVNode("div", { class: "recommend-list" }, [
          (vue.openBlock(true), vue.createElementBlock(
            vue.Fragment,
            null,
            vue.renderList($setup.recommendHotels, (hotel) => {
              return vue.openBlock(), vue.createElementBlock("div", {
                key: hotel.id,
                class: "glass-card hotel-item",
                onClick: ($event) => $setup.navigateTo(`/pages/hotel/detail?id=${hotel.id}`)
              }, [
                vue.createElementVNode("img", {
                  src: hotel.coverImage,
                  alt: hotel.name,
                  class: "item-image"
                }, null, 8, ["src", "alt"]),
                vue.createElementVNode("div", { class: "item-info" }, [
                  vue.createElementVNode(
                    "h3",
                    null,
                    vue.toDisplayString(hotel.name),
                    1
                    /* TEXT */
                  ),
                  vue.createElementVNode(
                    "p",
                    { class: "text-secondary" },
                    vue.toDisplayString(hotel.city),
                    1
                    /* TEXT */
                  ),
                  vue.createElementVNode(
                    "div",
                    { class: "price" },
                    "¥" + vue.toDisplayString(hotel.price) + "/晚",
                    1
                    /* TEXT */
                  ),
                  vue.createElementVNode("div", { class: "hotel-stars" }, [
                    (vue.openBlock(true), vue.createElementBlock(
                      vue.Fragment,
                      null,
                      vue.renderList(hotel.starLevel, (i) => {
                        return vue.openBlock(), vue.createElementBlock("span", {
                          key: i,
                          class: "star"
                        }, "★");
                      }),
                      128
                      /* KEYED_FRAGMENT */
                    ))
                  ]),
                  vue.createElementVNode(
                    "div",
                    { class: "hotel-address" },
                    vue.toDisplayString(hotel.address),
                    1
                    /* TEXT */
                  ),
                  vue.createElementVNode("div", { class: "hotel-facilities" }, [
                    (vue.openBlock(true), vue.createElementBlock(
                      vue.Fragment,
                      null,
                      vue.renderList(hotel.facilities ? hotel.facilities.split(",") : [], (facility) => {
                        return vue.openBlock(), vue.createElementBlock(
                          "span",
                          {
                            key: facility,
                            class: "hotel-facility"
                          },
                          vue.toDisplayString(facility),
                          1
                          /* TEXT */
                        );
                      }),
                      128
                      /* KEYED_FRAGMENT */
                    ))
                  ]),
                  vue.createElementVNode("div", { class: "hotel-stats stats" }, [
                    vue.createElementVNode("div", { class: "hotel-stat" }, [
                      vue.createElementVNode(
                        "span",
                        null,
                        vue.toDisplayString(hotel.likeCount) + " 点赞",
                        1
                        /* TEXT */
                      ),
                      vue.createElementVNode(
                        "span",
                        null,
                        vue.toDisplayString(hotel.commentCount) + " 评论",
                        1
                        /* TEXT */
                      ),
                      vue.createElementVNode(
                        "span",
                        null,
                        vue.toDisplayString(hotel.collectCount) + " 收藏",
                        1
                        /* TEXT */
                      )
                    ])
                  ])
                ])
              ], 8, ["onClick"]);
            }),
            128
            /* KEYED_FRAGMENT */
          ))
        ])
      ]),
      vue.createElementVNode("div", { class: "section" }, [
        vue.createElementVNode("div", { class: "section-header" }, [
          vue.createElementVNode("h2", null, "推荐美食"),
          vue.createElementVNode("button", {
            class: "more-btn",
            onClick: _cache[10] || (_cache[10] = ($event) => $setup.navigateTo("/pages/food/index"))
          }, "查看更多")
        ]),
        vue.createElementVNode("div", { class: "recommend-list" }, [
          (vue.openBlock(true), vue.createElementBlock(
            vue.Fragment,
            null,
            vue.renderList($setup.recommendFoods, (food) => {
              return vue.openBlock(), vue.createElementBlock("div", {
                key: food.id,
                class: "glass-card food-item",
                onClick: ($event) => $setup.navigateTo(`/pages/food/detail?id=${food.id}`)
              }, [
                vue.createElementVNode("img", {
                  src: food.coverImage,
                  alt: food.name,
                  class: "item-image"
                }, null, 8, ["src", "alt"]),
                vue.createElementVNode("div", { class: "item-info" }, [
                  vue.createElementVNode(
                    "h3",
                    null,
                    vue.toDisplayString(food.name),
                    1
                    /* TEXT */
                  ),
                  vue.createElementVNode(
                    "p",
                    { class: "text-secondary" },
                    vue.toDisplayString(food.city) + " - " + vue.toDisplayString(food.cuisineType),
                    1
                    /* TEXT */
                  ),
                  vue.createElementVNode(
                    "div",
                    { class: "food-price" },
                    "¥" + vue.toDisplayString(food.price),
                    1
                    /* TEXT */
                  ),
                  vue.createElementVNode("div", { class: "rating" }, [
                    (vue.openBlock(true), vue.createElementBlock(
                      vue.Fragment,
                      null,
                      vue.renderList(food.rating, (i) => {
                        return vue.openBlock(), vue.createElementBlock("span", {
                          key: i,
                          class: "star"
                        }, "★");
                      }),
                      128
                      /* KEYED_FRAGMENT */
                    ))
                  ]),
                  vue.createElementVNode(
                    "div",
                    { class: "food-address" },
                    vue.toDisplayString(food.address),
                    1
                    /* TEXT */
                  ),
                  vue.createElementVNode("div", { class: "food-tags" }, [
                    (vue.openBlock(true), vue.createElementBlock(
                      vue.Fragment,
                      null,
                      vue.renderList(food.tags ? food.tags.split(",") : [], (tag) => {
                        return vue.openBlock(), vue.createElementBlock(
                          "span",
                          {
                            key: tag,
                            class: "food-tag"
                          },
                          vue.toDisplayString(tag),
                          1
                          /* TEXT */
                        );
                      }),
                      128
                      /* KEYED_FRAGMENT */
                    ))
                  ]),
                  vue.createElementVNode("div", { class: "food-stats stats" }, [
                    vue.createElementVNode("div", { class: "food-stat" }, [
                      vue.createElementVNode(
                        "span",
                        null,
                        vue.toDisplayString(food.likeCount) + " 点赞",
                        1
                        /* TEXT */
                      ),
                      vue.createElementVNode(
                        "span",
                        null,
                        vue.toDisplayString(food.commentCount) + " 评论",
                        1
                        /* TEXT */
                      ),
                      vue.createElementVNode(
                        "span",
                        null,
                        vue.toDisplayString(food.collectCount) + " 收藏",
                        1
                        /* TEXT */
                      )
                    ])
                  ])
                ])
              ], 8, ["onClick"]);
            }),
            128
            /* KEYED_FRAGMENT */
          ))
        ])
      ]),
      vue.createElementVNode("div", { class: "section" }, [
        vue.createElementVNode("div", { class: "section-header" }, [
          vue.createElementVNode("h2", null, "热门新闻"),
          vue.createElementVNode("button", {
            class: "more-btn",
            onClick: _cache[11] || (_cache[11] = ($event) => $setup.navigateTo("/pages/news/index"))
          }, "查看更多")
        ]),
        vue.createElementVNode("div", { class: "news-list" }, [
          (vue.openBlock(true), vue.createElementBlock(
            vue.Fragment,
            null,
            vue.renderList($setup.hotNews, (news) => {
              return vue.openBlock(), vue.createElementBlock("div", {
                key: news.id,
                class: "glass-card news-item",
                onClick: ($event) => $setup.navigateTo(`/pages/news/detail?id=${news.id}`)
              }, [
                vue.createElementVNode("div", { class: "news-content" }, [
                  news.coverImage ? (vue.openBlock(), vue.createElementBlock("div", {
                    key: 0,
                    class: "news-image"
                  }, [
                    vue.createElementVNode("img", {
                      src: news.coverImage,
                      alt: news.title
                    }, null, 8, ["src", "alt"])
                  ])) : vue.createCommentVNode("v-if", true),
                  vue.createElementVNode("div", { class: "news-info" }, [
                    vue.createElementVNode(
                      "h3",
                      null,
                      vue.toDisplayString(news.title),
                      1
                      /* TEXT */
                    ),
                    vue.createElementVNode(
                      "p",
                      { class: "news-desc" },
                      vue.toDisplayString(news.description),
                      1
                      /* TEXT */
                    ),
                    vue.createElementVNode("div", { class: "news-meta" }, [
                      vue.createElementVNode(
                        "span",
                        { class: "news-source" },
                        vue.toDisplayString(news.source),
                        1
                        /* TEXT */
                      ),
                      vue.createElementVNode(
                        "span",
                        { class: "news-date" },
                        vue.toDisplayString(news.createTime),
                        1
                        /* TEXT */
                      )
                    ])
                  ])
                ]),
                vue.createElementVNode("div", { class: "news-stats" }, [
                  vue.createElementVNode(
                    "span",
                    { class: "news-stat" },
                    vue.toDisplayString(news.viewCount) + " 浏览",
                    1
                    /* TEXT */
                  ),
                  vue.createElementVNode(
                    "span",
                    { class: "news-stat" },
                    vue.toDisplayString(news.likeCount) + " 点赞",
                    1
                    /* TEXT */
                  ),
                  vue.createElementVNode(
                    "span",
                    { class: "news-stat" },
                    vue.toDisplayString(news.commentCount) + " 评论",
                    1
                    /* TEXT */
                  ),
                  vue.createElementVNode(
                    "span",
                    { class: "news-stat" },
                    vue.toDisplayString(news.collectCount) + " 收藏",
                    1
                    /* TEXT */
                  )
                ])
              ], 8, ["onClick"]);
            }),
            128
            /* KEYED_FRAGMENT */
          ))
        ])
      ])
    ]);
  }
  const PagesIndexIndex = /* @__PURE__ */ _export_sfc(_sfc_main$p, [["render", _sfc_render$o], ["__scopeId", "data-v-1cf27b2a"], ["__file", "D:/IDEA/work/Minecraft-system/minecraft-App/pages/index/index.vue"]]);
  const getWorldCharacteristicsPage = (current, size) => {
    return request$1.get("/world-characteristics/page", {
      params: {
        current,
        size
      }
    });
  };
  const getWorldCharacteristicsByCategory = (category) => {
    return request$1.get(`/world-characteristics/by-category/${category}`);
  };
  const _sfc_main$o = {
    __name: "index",
    setup(__props, { expose: __expose }) {
      __expose();
      const categories = vue.ref(["全部", "自然景观", "文化遗产", "历史建筑", "特色美食", "传统习俗"]);
      const selectedCategory = vue.ref("全部");
      const currentPage = vue.ref(1);
      const pageSize = vue.ref(10);
      const totalPages = vue.ref(1);
      const characteristics = vue.ref([]);
      const calculateTotalPages = (total) => {
        return Math.ceil(total / pageSize.value);
      };
      const fetchCharacteristics = async () => {
        try {
          if (selectedCategory.value === "全部") {
            const response = await getWorldCharacteristicsPage(currentPage.value, pageSize.value);
            characteristics.value = response.data.records || [];
            totalPages.value = calculateTotalPages(response.data.total || 0);
          } else {
            const response = await getWorldCharacteristicsByCategory(selectedCategory.value);
            characteristics.value = response.data || [];
            totalPages.value = 1;
          }
        } catch (error) {
          formatAppLog("error", "at pages/discover/index.vue:89", "获取世界特征失败:", error);
        }
      };
      const selectCategory = (category) => {
        selectedCategory.value = category;
        currentPage.value = 1;
        fetchCharacteristics();
      };
      const changePage = (page) => {
        currentPage.value = page;
        fetchCharacteristics();
      };
      vue.onMounted(() => {
        fetchCharacteristics();
      });
      const __returned__ = { categories, selectedCategory, currentPage, pageSize, totalPages, characteristics, calculateTotalPages, fetchCharacteristics, selectCategory, changePage, ref: vue.ref, onMounted: vue.onMounted, get getWorldCharacteristicsPage() {
        return getWorldCharacteristicsPage;
      }, get getWorldCharacteristicsByCategory() {
        return getWorldCharacteristicsByCategory;
      } };
      Object.defineProperty(__returned__, "__isScriptSetup", { enumerable: false, value: true });
      return __returned__;
    }
  };
  function _sfc_render$n(_ctx, _cache, $props, $setup, $data, $options) {
    return vue.openBlock(), vue.createElementBlock("div", { class: "discover-container" }, [
      vue.createElementVNode("div", { class: "page-header" }, [
        vue.createElementVNode("h1", null, "发现世界"),
        vue.createElementVNode("p", null, "探索世界各地的独特特征和文化")
      ]),
      vue.createElementVNode("div", { class: "category-filter" }, [
        (vue.openBlock(true), vue.createElementBlock(
          vue.Fragment,
          null,
          vue.renderList($setup.categories, (category) => {
            return vue.openBlock(), vue.createElementBlock("div", {
              key: category,
              class: vue.normalizeClass(["category-item", { "active": $setup.selectedCategory === category }]),
              onClick: ($event) => $setup.selectCategory(category)
            }, vue.toDisplayString(category), 11, ["onClick"]);
          }),
          128
          /* KEYED_FRAGMENT */
        ))
      ]),
      vue.createElementVNode("div", { class: "characteristics-list" }, [
        (vue.openBlock(true), vue.createElementBlock(
          vue.Fragment,
          null,
          vue.renderList($setup.characteristics, (item) => {
            return vue.openBlock(), vue.createElementBlock("div", {
              key: item.id,
              class: "characteristic-item"
            }, [
              vue.createElementVNode("img", {
                src: item.image,
                alt: item.name,
                class: "characteristic-image"
              }, null, 8, ["src", "alt"]),
              vue.createElementVNode("div", { class: "characteristic-info" }, [
                vue.createElementVNode(
                  "h3",
                  null,
                  vue.toDisplayString(item.name),
                  1
                  /* TEXT */
                ),
                vue.createElementVNode(
                  "p",
                  { class: "characteristic-country" },
                  vue.toDisplayString(item.country),
                  1
                  /* TEXT */
                ),
                vue.createElementVNode(
                  "p",
                  { class: "characteristic-desc" },
                  vue.toDisplayString(item.description),
                  1
                  /* TEXT */
                ),
                vue.createElementVNode("div", { class: "characteristic-category" }, [
                  vue.createElementVNode(
                    "span",
                    { class: "category-tag" },
                    vue.toDisplayString(item.category),
                    1
                    /* TEXT */
                  )
                ])
              ])
            ]);
          }),
          128
          /* KEYED_FRAGMENT */
        ))
      ]),
      $setup.characteristics.length === 0 ? (vue.openBlock(), vue.createElementBlock("div", {
        key: 0,
        class: "empty-tip"
      }, " 暂无世界特征数据 ")) : vue.createCommentVNode("v-if", true),
      $setup.characteristics.length > 0 ? (vue.openBlock(), vue.createElementBlock("div", {
        key: 1,
        class: "pagination"
      }, [
        vue.createElementVNode("button", {
          class: "page-btn",
          disabled: $setup.currentPage === 1,
          onClick: _cache[0] || (_cache[0] = ($event) => $setup.changePage($setup.currentPage - 1))
        }, "上一页", 8, ["disabled"]),
        vue.createElementVNode(
          "span",
          { class: "page-info" },
          vue.toDisplayString($setup.currentPage) + " / " + vue.toDisplayString($setup.totalPages),
          1
          /* TEXT */
        ),
        vue.createElementVNode("button", {
          class: "page-btn",
          disabled: $setup.currentPage === $setup.totalPages,
          onClick: _cache[1] || (_cache[1] = ($event) => $setup.changePage($setup.currentPage + 1))
        }, "下一页", 8, ["disabled"])
      ])) : vue.createCommentVNode("v-if", true)
    ]);
  }
  const PagesDiscoverIndex = /* @__PURE__ */ _export_sfc(_sfc_main$o, [["render", _sfc_render$n], ["__scopeId", "data-v-157e4766"], ["__file", "D:/IDEA/work/Minecraft-system/minecraft-App/pages/discover/index.vue"]]);
  const getPostList = (params) => {
    return request$1.get("/community/list", { params });
  };
  const _sfc_main$n = {
    __name: "index",
    setup(__props, { expose: __expose }) {
      __expose();
      const searchQuery = vue.ref("");
      const currentPage = vue.ref(1);
      const pageSize = vue.ref(10);
      const totalPages = vue.ref(1);
      const posts = vue.ref([]);
      const calculateTotalPages = (total) => {
        return Math.ceil(total / pageSize.value);
      };
      const fetchPosts = async () => {
        try {
          const response = await getPostList({
            page: currentPage.value,
            pageSize: pageSize.value,
            keyword: searchQuery.value
          });
          posts.value = response.data.records || [];
          totalPages.value = calculateTotalPages(response.data.total || 0);
        } catch (error) {
          formatAppLog("error", "at pages/community/index.vue:108", "获取帖子列表失败:", error);
        }
      };
      const navigateToDetail = (id) => {
        uni.navigateTo({
          url: `/pages/community/detail?id=${id}`
        });
      };
      const changePage = (page) => {
        currentPage.value = page;
        fetchPosts();
      };
      const createPost = () => {
        uni.navigateTo({
          url: "/pages/community/create"
        });
      };
      vue.onMounted(() => {
        fetchPosts();
      });
      const __returned__ = { searchQuery, currentPage, pageSize, totalPages, posts, calculateTotalPages, fetchPosts, navigateToDetail, changePage, createPost, ref: vue.ref, onMounted: vue.onMounted, get getPostList() {
        return getPostList;
      } };
      Object.defineProperty(__returned__, "__isScriptSetup", { enumerable: false, value: true });
      return __returned__;
    }
  };
  function _sfc_render$m(_ctx, _cache, $props, $setup, $data, $options) {
    return vue.openBlock(), vue.createElementBlock("div", { class: "community-container" }, [
      vue.createElementVNode("div", { class: "page-header" }, [
        vue.createElementVNode("h1", null, "社区"),
        vue.createElementVNode("p", null, "分享你的旅行经历和故事")
      ]),
      vue.createElementVNode("div", { class: "search-bar" }, [
        vue.withDirectives(vue.createElementVNode(
          "input",
          {
            type: "text",
            "onUpdate:modelValue": _cache[0] || (_cache[0] = ($event) => $setup.searchQuery = $event),
            placeholder: "搜索帖子",
            class: "search-input"
          },
          null,
          512
          /* NEED_PATCH */
        ), [
          [vue.vModelText, $setup.searchQuery]
        ]),
        vue.createElementVNode("button", { class: "search-btn" }, "搜索")
      ]),
      vue.createElementVNode("div", { class: "post-list" }, [
        (vue.openBlock(true), vue.createElementBlock(
          vue.Fragment,
          null,
          vue.renderList($setup.posts, (post2) => {
            return vue.openBlock(), vue.createElementBlock("div", {
              key: post2.id,
              class: "post-item",
              onClick: ($event) => $setup.navigateToDetail(post2.id)
            }, [
              vue.createElementVNode("div", { class: "post-header" }, [
                vue.createElementVNode("div", { class: "user-info" }, [
                  vue.createElementVNode("div", { class: "user-avatar" }, [
                    vue.createElementVNode("img", {
                      src: post2.userAvatar || "https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=user%20avatar%20profile%20picture&image_size=square",
                      alt: post2.userName
                    }, null, 8, ["src", "alt"])
                  ]),
                  vue.createElementVNode("div", { class: "user-details" }, [
                    vue.createElementVNode(
                      "h4",
                      null,
                      vue.toDisplayString(post2.userName),
                      1
                      /* TEXT */
                    ),
                    vue.createElementVNode(
                      "p",
                      { class: "post-time" },
                      vue.toDisplayString(post2.createTime),
                      1
                      /* TEXT */
                    )
                  ])
                ])
              ]),
              vue.createElementVNode("div", { class: "post-content" }, [
                vue.createElementVNode(
                  "h3",
                  null,
                  vue.toDisplayString(post2.title),
                  1
                  /* TEXT */
                ),
                vue.createElementVNode(
                  "p",
                  { class: "post-desc" },
                  vue.toDisplayString(post2.content),
                  1
                  /* TEXT */
                ),
                post2.images && post2.images.length > 0 ? (vue.openBlock(), vue.createElementBlock("img", {
                  key: 0,
                  src: post2.images[0],
                  alt: post2.title,
                  class: "post-image"
                }, null, 8, ["src", "alt"])) : vue.createCommentVNode("v-if", true)
              ]),
              vue.createElementVNode("div", { class: "post-footer" }, [
                vue.createElementVNode("div", { class: "post-stats" }, [
                  vue.createElementVNode("span", { class: "stat-item" }, [
                    vue.createElementVNode("span", { class: "stat-icon" }, "❤️"),
                    vue.createElementVNode(
                      "span",
                      null,
                      vue.toDisplayString(post2.likeCount || 0),
                      1
                      /* TEXT */
                    )
                  ]),
                  vue.createElementVNode("span", { class: "stat-item" }, [
                    vue.createElementVNode("span", { class: "stat-icon" }, "💬"),
                    vue.createElementVNode(
                      "span",
                      null,
                      vue.toDisplayString(post2.commentCount || 0),
                      1
                      /* TEXT */
                    )
                  ]),
                  vue.createElementVNode("span", { class: "stat-item" }, [
                    vue.createElementVNode("span", { class: "stat-icon" }, "👁️"),
                    vue.createElementVNode(
                      "span",
                      null,
                      vue.toDisplayString(post2.viewCount || 0),
                      1
                      /* TEXT */
                    )
                  ])
                ])
              ])
            ], 8, ["onClick"]);
          }),
          128
          /* KEYED_FRAGMENT */
        ))
      ]),
      $setup.posts.length === 0 ? (vue.openBlock(), vue.createElementBlock("div", {
        key: 0,
        class: "empty-tip"
      }, " 暂无帖子数据 ")) : vue.createCommentVNode("v-if", true),
      $setup.posts.length > 0 ? (vue.openBlock(), vue.createElementBlock("div", {
        key: 1,
        class: "pagination"
      }, [
        vue.createElementVNode("button", {
          class: "page-btn",
          disabled: $setup.currentPage === 1,
          onClick: _cache[1] || (_cache[1] = ($event) => $setup.changePage($setup.currentPage - 1))
        }, "上一页", 8, ["disabled"]),
        vue.createElementVNode(
          "span",
          { class: "page-info" },
          vue.toDisplayString($setup.currentPage) + " / " + vue.toDisplayString($setup.totalPages),
          1
          /* TEXT */
        ),
        vue.createElementVNode("button", {
          class: "page-btn",
          disabled: $setup.currentPage === $setup.totalPages,
          onClick: _cache[2] || (_cache[2] = ($event) => $setup.changePage($setup.currentPage + 1))
        }, "下一页", 8, ["disabled"])
      ])) : vue.createCommentVNode("v-if", true),
      vue.createElementVNode("div", { class: "create-post-btn" }, [
        vue.createElementVNode("button", { onClick: $setup.createPost }, "+")
      ])
    ]);
  }
  const PagesCommunityIndex = /* @__PURE__ */ _export_sfc(_sfc_main$n, [["render", _sfc_render$m], ["__scopeId", "data-v-35ff9bcb"], ["__file", "D:/IDEA/work/Minecraft-system/minecraft-App/pages/community/index.vue"]]);
  const _sfc_main$m = {
    __name: "index",
    setup(__props, { expose: __expose }) {
      __expose();
      const searchQuery = vue.ref("");
      const currentPage = vue.ref(1);
      const pageSize = vue.ref(10);
      const totalPages = vue.ref(1);
      const newsList = vue.ref([]);
      const calculateTotalPages = (total) => {
        return Math.ceil(total / pageSize.value);
      };
      const fetchNewsList = async () => {
        try {
          const response = await getNewsList({
            page: currentPage.value,
            pageSize: pageSize.value,
            keyword: searchQuery.value
          });
          newsList.value = response.data.records || [];
          totalPages.value = calculateTotalPages(response.data.total || 0);
        } catch (error) {
          formatAppLog("error", "at pages/news/index.vue:79", "获取新闻列表失败:", error);
        }
      };
      const navigateToDetail = (id) => {
        uni.navigateTo({
          url: `/pages/news/detail?id=${id}`
        });
      };
      const changePage = (page) => {
        currentPage.value = page;
        fetchNewsList();
      };
      vue.onMounted(() => {
        fetchNewsList();
      });
      const __returned__ = { searchQuery, currentPage, pageSize, totalPages, newsList, calculateTotalPages, fetchNewsList, navigateToDetail, changePage, ref: vue.ref, onMounted: vue.onMounted, get getNewsList() {
        return getNewsList;
      } };
      Object.defineProperty(__returned__, "__isScriptSetup", { enumerable: false, value: true });
      return __returned__;
    }
  };
  function _sfc_render$l(_ctx, _cache, $props, $setup, $data, $options) {
    return vue.openBlock(), vue.createElementBlock("div", { class: "news-container" }, [
      vue.createElementVNode("div", { class: "search-bar" }, [
        vue.withDirectives(vue.createElementVNode(
          "input",
          {
            type: "text",
            "onUpdate:modelValue": _cache[0] || (_cache[0] = ($event) => $setup.searchQuery = $event),
            placeholder: "搜索新闻",
            class: "search-input"
          },
          null,
          512
          /* NEED_PATCH */
        ), [
          [vue.vModelText, $setup.searchQuery]
        ]),
        vue.createElementVNode("button", { class: "search-btn" }, "搜索")
      ]),
      vue.createElementVNode("div", { class: "news-list" }, [
        (vue.openBlock(true), vue.createElementBlock(
          vue.Fragment,
          null,
          vue.renderList($setup.newsList, (news) => {
            return vue.openBlock(), vue.createElementBlock("div", {
              key: news.id,
              class: "news-item",
              onClick: ($event) => $setup.navigateToDetail(news.id)
            }, [
              vue.createElementVNode("div", { class: "news-content" }, [
                news.coverImage ? (vue.openBlock(), vue.createElementBlock("div", {
                  key: 0,
                  class: "news-image"
                }, [
                  vue.createElementVNode("img", {
                    src: news.coverImage,
                    alt: news.title
                  }, null, 8, ["src", "alt"])
                ])) : vue.createCommentVNode("v-if", true),
                vue.createElementVNode("div", { class: "news-info" }, [
                  vue.createElementVNode(
                    "h3",
                    null,
                    vue.toDisplayString(news.title),
                    1
                    /* TEXT */
                  ),
                  vue.createElementVNode(
                    "p",
                    { class: "news-desc" },
                    vue.toDisplayString(news.description),
                    1
                    /* TEXT */
                  ),
                  vue.createElementVNode("div", { class: "news-meta" }, [
                    vue.createElementVNode(
                      "span",
                      { class: "news-source" },
                      vue.toDisplayString(news.source),
                      1
                      /* TEXT */
                    ),
                    vue.createElementVNode(
                      "span",
                      { class: "news-date" },
                      vue.toDisplayString(news.createTime),
                      1
                      /* TEXT */
                    )
                  ])
                ])
              ]),
              vue.createElementVNode("div", { class: "news-stats" }, [
                vue.createElementVNode(
                  "span",
                  { class: "news-stat" },
                  vue.toDisplayString(news.viewCount) + " 浏览",
                  1
                  /* TEXT */
                ),
                vue.createElementVNode(
                  "span",
                  { class: "news-stat" },
                  vue.toDisplayString(news.likeCount) + " 点赞",
                  1
                  /* TEXT */
                ),
                vue.createElementVNode(
                  "span",
                  { class: "news-stat" },
                  vue.toDisplayString(news.commentCount) + " 评论",
                  1
                  /* TEXT */
                ),
                vue.createElementVNode(
                  "span",
                  { class: "news-stat" },
                  vue.toDisplayString(news.collectCount) + " 收藏",
                  1
                  /* TEXT */
                )
              ])
            ], 8, ["onClick"]);
          }),
          128
          /* KEYED_FRAGMENT */
        ))
      ]),
      $setup.newsList.length === 0 ? (vue.openBlock(), vue.createElementBlock("div", {
        key: 0,
        class: "empty-tip"
      }, " 暂无新闻数据 ")) : vue.createCommentVNode("v-if", true),
      $setup.newsList.length > 0 ? (vue.openBlock(), vue.createElementBlock("div", {
        key: 1,
        class: "pagination"
      }, [
        vue.createElementVNode("button", {
          class: "page-btn",
          disabled: $setup.currentPage === 1,
          onClick: _cache[1] || (_cache[1] = ($event) => $setup.changePage($setup.currentPage - 1))
        }, "上一页", 8, ["disabled"]),
        vue.createElementVNode(
          "span",
          { class: "page-info" },
          vue.toDisplayString($setup.currentPage) + " / " + vue.toDisplayString($setup.totalPages),
          1
          /* TEXT */
        ),
        vue.createElementVNode("button", {
          class: "page-btn",
          disabled: $setup.currentPage === $setup.totalPages,
          onClick: _cache[2] || (_cache[2] = ($event) => $setup.changePage($setup.currentPage + 1))
        }, "下一页", 8, ["disabled"])
      ])) : vue.createCommentVNode("v-if", true)
    ]);
  }
  const PagesNewsIndex = /* @__PURE__ */ _export_sfc(_sfc_main$m, [["render", _sfc_render$l], ["__scopeId", "data-v-52cd24e9"], ["__file", "D:/IDEA/work/Minecraft-system/minecraft-App/pages/news/index.vue"]]);
  const _sfc_main$l = {
    __name: "detail",
    setup(__props, { expose: __expose }) {
      __expose();
      const news = vue.ref({});
      vue.onMounted(async () => {
        const pages = getCurrentPages();
        const currentPage = pages[pages.length - 1];
        const { id } = currentPage.options || {};
        if (id) {
          try {
            const response = await getNewsDetail(id);
            news.value = response.data;
          } catch (error) {
            formatAppLog("error", "at pages/news/detail.vue:51", "获取新闻详情失败:", error);
          }
        }
      });
      const __returned__ = { news, ref: vue.ref, onMounted: vue.onMounted, get getNewsDetail() {
        return getNewsDetail;
      } };
      Object.defineProperty(__returned__, "__isScriptSetup", { enumerable: false, value: true });
      return __returned__;
    }
  };
  function _sfc_render$k(_ctx, _cache, $props, $setup, $data, $options) {
    return vue.openBlock(), vue.createElementBlock("div", { class: "detail-container" }, [
      vue.createElementVNode("view", { class: "detail-header" }, [
        vue.createElementVNode("view", { class: "detail-title" }, [
          vue.createElementVNode(
            "h1",
            null,
            vue.toDisplayString($setup.news.title),
            1
            /* TEXT */
          ),
          vue.createElementVNode("div", { class: "news-meta" }, [
            vue.createElementVNode(
              "span",
              { class: "news-source" },
              vue.toDisplayString($setup.news.source),
              1
              /* TEXT */
            ),
            vue.createElementVNode(
              "span",
              { class: "news-date" },
              vue.toDisplayString($setup.news.createTime),
              1
              /* TEXT */
            )
          ]),
          vue.createElementVNode("div", { class: "news-stats" }, [
            vue.createElementVNode(
              "span",
              { class: "news-stat" },
              vue.toDisplayString($setup.news.viewCount) + " 浏览",
              1
              /* TEXT */
            ),
            vue.createElementVNode(
              "span",
              { class: "news-stat" },
              vue.toDisplayString($setup.news.likeCount) + " 点赞",
              1
              /* TEXT */
            ),
            vue.createElementVNode(
              "span",
              { class: "news-stat" },
              vue.toDisplayString($setup.news.commentCount) + " 评论",
              1
              /* TEXT */
            ),
            vue.createElementVNode(
              "span",
              { class: "news-stat" },
              vue.toDisplayString($setup.news.collectCount) + " 收藏",
              1
              /* TEXT */
            )
          ])
        ]),
        $setup.news.coverImage ? (vue.openBlock(), vue.createElementBlock("div", {
          key: 0,
          class: "detail-image"
        }, [
          vue.createElementVNode("img", {
            src: $setup.news.coverImage,
            alt: $setup.news.title
          }, null, 8, ["src", "alt"])
        ])) : vue.createCommentVNode("v-if", true)
      ]),
      vue.createElementVNode("view", { class: "detail-content" }, [
        vue.createElementVNode("view", { class: "section" }, [
          vue.createElementVNode("h2", null, "新闻内容"),
          vue.createElementVNode(
            "div",
            { class: "content-text" },
            vue.toDisplayString($setup.news.content),
            1
            /* TEXT */
          )
        ]),
        vue.createElementVNode("view", { class: "section" }, [
          vue.createElementVNode("h2", null, "新闻摘要"),
          vue.createElementVNode(
            "p",
            null,
            vue.toDisplayString($setup.news.description),
            1
            /* TEXT */
          )
        ])
      ])
    ]);
  }
  const PagesNewsDetail = /* @__PURE__ */ _export_sfc(_sfc_main$l, [["render", _sfc_render$k], ["__scopeId", "data-v-4a324f21"], ["__file", "D:/IDEA/work/Minecraft-system/minecraft-App/pages/news/detail.vue"]]);
  const _sfc_main$k = {
    name: "GroupTravelIndex"
  };
  function _sfc_render$j(_ctx, _cache, $props, $setup, $data, $options) {
    return vue.openBlock(), vue.createElementBlock("view", { class: "groupTravel-container" }, [
      vue.createElementVNode("text", null, "跟团游页面")
    ]);
  }
  const PagesGroupTravelIndex = /* @__PURE__ */ _export_sfc(_sfc_main$k, [["render", _sfc_render$j], ["__scopeId", "data-v-5708a7b1"], ["__file", "D:/IDEA/work/Minecraft-system/minecraft-App/pages/groupTravel/index.vue"]]);
  const _sfc_main$j = {
    name: "TravelPlanIndex"
  };
  function _sfc_render$i(_ctx, _cache, $props, $setup, $data, $options) {
    return vue.openBlock(), vue.createElementBlock("view", { class: "travelPlan-container" }, [
      vue.createElementVNode("text", null, "出行计划页面")
    ]);
  }
  const PagesTravelPlanIndex = /* @__PURE__ */ _export_sfc(_sfc_main$j, [["render", _sfc_render$i], ["__scopeId", "data-v-29bda855"], ["__file", "D:/IDEA/work/Minecraft-system/minecraft-App/pages/travelPlan/index.vue"]]);
  const _sfc_main$i = {
    name: "ProductIndex"
  };
  function _sfc_render$h(_ctx, _cache, $props, $setup, $data, $options) {
    return vue.openBlock(), vue.createElementBlock("view", { class: "product-container" }, [
      vue.createElementVNode("text", null, "特产商城页面")
    ]);
  }
  const PagesProductIndex = /* @__PURE__ */ _export_sfc(_sfc_main$i, [["render", _sfc_render$h], ["__scopeId", "data-v-a911e391"], ["__file", "D:/IDEA/work/Minecraft-system/minecraft-App/pages/product/index.vue"]]);
  const _sfc_main$h = {
    __name: "index",
    setup(__props, { expose: __expose }) {
      __expose();
      const searchQuery = vue.ref("");
      const filterCity = vue.ref("");
      const filterSeason = vue.ref("");
      const currentPage = vue.ref(1);
      const pageSize = vue.ref(10);
      const totalPages = vue.ref(1);
      const attractions = vue.ref([]);
      const calculateTotalPages = (total) => {
        return Math.ceil(total / pageSize.value);
      };
      const fetchAttractions = async () => {
        try {
          const response = await getAttractionList({
            page: currentPage.value,
            pageSize: pageSize.value,
            city: filterCity.value,
            season: filterSeason.value,
            keyword: searchQuery.value
          });
          attractions.value = response.data.records || [];
          totalPages.value = calculateTotalPages(response.data.total || 0);
        } catch (error) {
          formatAppLog("error", "at pages/attraction/index.vue:107", "获取景点列表失败:", error);
        }
      };
      const navigateToDetail = (id) => {
        uni.navigateTo({
          url: `/pages/attraction/detail?id=${id}`
        });
      };
      const changePage = (page) => {
        currentPage.value = page;
        fetchAttractions();
      };
      vue.onMounted(() => {
        fetchAttractions();
      });
      const __returned__ = { searchQuery, filterCity, filterSeason, currentPage, pageSize, totalPages, attractions, calculateTotalPages, fetchAttractions, navigateToDetail, changePage, ref: vue.ref, onMounted: vue.onMounted, computed: vue.computed, get getAttractionList() {
        return getAttractionList;
      } };
      Object.defineProperty(__returned__, "__isScriptSetup", { enumerable: false, value: true });
      return __returned__;
    }
  };
  function _sfc_render$g(_ctx, _cache, $props, $setup, $data, $options) {
    return vue.openBlock(), vue.createElementBlock("div", { class: "attraction-container" }, [
      vue.createElementVNode("div", { class: "search-bar" }, [
        vue.withDirectives(vue.createElementVNode(
          "input",
          {
            type: "text",
            "onUpdate:modelValue": _cache[0] || (_cache[0] = ($event) => $setup.searchQuery = $event),
            placeholder: "搜索景点",
            class: "search-input"
          },
          null,
          512
          /* NEED_PATCH */
        ), [
          [vue.vModelText, $setup.searchQuery]
        ]),
        vue.createElementVNode("button", { class: "search-btn" }, "搜索")
      ]),
      vue.createElementVNode("div", { class: "filter-bar" }, [
        vue.withDirectives(vue.createElementVNode(
          "select",
          {
            "onUpdate:modelValue": _cache[1] || (_cache[1] = ($event) => $setup.filterCity = $event),
            class: "filter-select"
          },
          [
            vue.createElementVNode("option", { value: "" }, "全部城市"),
            vue.createElementVNode("option", { value: "北京" }, "北京"),
            vue.createElementVNode("option", { value: "上海" }, "上海"),
            vue.createElementVNode("option", { value: "广州" }, "广州"),
            vue.createElementVNode("option", { value: "深圳" }, "深圳")
          ],
          512
          /* NEED_PATCH */
        ), [
          [vue.vModelSelect, $setup.filterCity]
        ]),
        vue.withDirectives(vue.createElementVNode(
          "select",
          {
            "onUpdate:modelValue": _cache[2] || (_cache[2] = ($event) => $setup.filterSeason = $event),
            class: "filter-select"
          },
          [
            vue.createElementVNode("option", { value: "" }, "全部季节"),
            vue.createElementVNode("option", { value: "spring" }, "春季"),
            vue.createElementVNode("option", { value: "summer" }, "夏季"),
            vue.createElementVNode("option", { value: "autumn" }, "秋季"),
            vue.createElementVNode("option", { value: "winter" }, "冬季")
          ],
          512
          /* NEED_PATCH */
        ), [
          [vue.vModelSelect, $setup.filterSeason]
        ])
      ]),
      vue.createElementVNode("div", { class: "attraction-list" }, [
        (vue.openBlock(true), vue.createElementBlock(
          vue.Fragment,
          null,
          vue.renderList($setup.attractions, (attraction) => {
            return vue.openBlock(), vue.createElementBlock("div", {
              key: attraction.id,
              class: "attraction-item",
              onClick: ($event) => $setup.navigateToDetail(attraction.id)
            }, [
              vue.createElementVNode("img", {
                src: attraction.coverImage,
                alt: attraction.name,
                class: "attraction-image"
              }, null, 8, ["src", "alt"]),
              vue.createElementVNode("div", { class: "attraction-info" }, [
                vue.createElementVNode(
                  "h3",
                  null,
                  vue.toDisplayString(attraction.name),
                  1
                  /* TEXT */
                ),
                vue.createElementVNode(
                  "p",
                  { class: "attraction-city" },
                  vue.toDisplayString(attraction.city),
                  1
                  /* TEXT */
                ),
                attraction.price > 0 ? (vue.openBlock(), vue.createElementBlock(
                  "div",
                  {
                    key: 0,
                    class: "attraction-price"
                  },
                  "¥" + vue.toDisplayString(attraction.price),
                  1
                  /* TEXT */
                )) : (vue.openBlock(), vue.createElementBlock("div", {
                  key: 1,
                  class: "attraction-price free"
                }, "免费")),
                vue.createElementVNode(
                  "div",
                  { class: "attraction-address" },
                  vue.toDisplayString(attraction.address),
                  1
                  /* TEXT */
                ),
                attraction.season ? (vue.openBlock(), vue.createElementBlock(
                  "div",
                  {
                    key: 2,
                    class: "attraction-season"
                  },
                  vue.toDisplayString(attraction.season),
                  1
                  /* TEXT */
                )) : vue.createCommentVNode("v-if", true),
                vue.createElementVNode("div", { class: "attraction-stats" }, [
                  vue.createElementVNode("div", { class: "attraction-rating" }, [
                    vue.createElementVNode("span", { class: "star" }, "★"),
                    vue.createElementVNode(
                      "span",
                      null,
                      vue.toDisplayString(attraction.rating),
                      1
                      /* TEXT */
                    )
                  ]),
                  vue.createElementVNode("div", { class: "attraction-stat" }, [
                    vue.createElementVNode(
                      "span",
                      null,
                      vue.toDisplayString(attraction.likeCount) + " 点赞",
                      1
                      /* TEXT */
                    ),
                    vue.createElementVNode(
                      "span",
                      null,
                      vue.toDisplayString(attraction.commentCount) + " 评论",
                      1
                      /* TEXT */
                    ),
                    vue.createElementVNode(
                      "span",
                      null,
                      vue.toDisplayString(attraction.collectCount) + " 收藏",
                      1
                      /* TEXT */
                    )
                  ])
                ])
              ])
            ], 8, ["onClick"]);
          }),
          128
          /* KEYED_FRAGMENT */
        ))
      ]),
      $setup.attractions.length === 0 ? (vue.openBlock(), vue.createElementBlock("div", {
        key: 0,
        class: "empty-tip"
      }, " 暂无景点数据 ")) : vue.createCommentVNode("v-if", true),
      $setup.attractions.length > 0 ? (vue.openBlock(), vue.createElementBlock("div", {
        key: 1,
        class: "pagination"
      }, [
        vue.createElementVNode("button", {
          class: "page-btn",
          disabled: $setup.currentPage === 1,
          onClick: _cache[3] || (_cache[3] = ($event) => $setup.changePage($setup.currentPage - 1))
        }, "上一页", 8, ["disabled"]),
        vue.createElementVNode(
          "span",
          { class: "page-info" },
          vue.toDisplayString($setup.currentPage) + " / " + vue.toDisplayString($setup.totalPages),
          1
          /* TEXT */
        ),
        vue.createElementVNode("button", {
          class: "page-btn",
          disabled: $setup.currentPage === $setup.totalPages,
          onClick: _cache[4] || (_cache[4] = ($event) => $setup.changePage($setup.currentPage + 1))
        }, "下一页", 8, ["disabled"])
      ])) : vue.createCommentVNode("v-if", true)
    ]);
  }
  const PagesAttractionIndex = /* @__PURE__ */ _export_sfc(_sfc_main$h, [["render", _sfc_render$g], ["__scopeId", "data-v-471183cd"], ["__file", "D:/IDEA/work/Minecraft-system/minecraft-App/pages/attraction/index.vue"]]);
  const _sfc_main$g = {
    __name: "detail",
    setup(__props, { expose: __expose }) {
      __expose();
      const attraction = vue.ref({});
      vue.onMounted(async () => {
        const pages = getCurrentPages();
        const currentPage = pages[pages.length - 1];
        const { id } = currentPage.options || {};
        if (id) {
          try {
            const response = await getAttractionDetail(id);
            attraction.value = response.data;
          } catch (error) {
            formatAppLog("error", "at pages/attraction/detail.vue:61", "获取景点详情失败:", error);
          }
        }
      });
      const __returned__ = { attraction, ref: vue.ref, onMounted: vue.onMounted, get getAttractionDetail() {
        return getAttractionDetail;
      } };
      Object.defineProperty(__returned__, "__isScriptSetup", { enumerable: false, value: true });
      return __returned__;
    }
  };
  function _sfc_render$f(_ctx, _cache, $props, $setup, $data, $options) {
    return vue.openBlock(), vue.createElementBlock("div", { class: "detail-container" }, [
      vue.createElementVNode("view", { class: "detail-header" }, [
        vue.createElementVNode("image", {
          src: $setup.attraction.coverImage,
          alt: $setup.attraction.name,
          class: "detail-image"
        }, null, 8, ["src", "alt"]),
        vue.createElementVNode("view", { class: "detail-title" }, [
          vue.createElementVNode(
            "h1",
            null,
            vue.toDisplayString($setup.attraction.name),
            1
            /* TEXT */
          ),
          vue.createElementVNode(
            "p",
            { class: "location" },
            vue.toDisplayString($setup.attraction.city),
            1
            /* TEXT */
          ),
          $setup.attraction.price > 0 ? (vue.openBlock(), vue.createElementBlock(
            "div",
            {
              key: 0,
              class: "attraction-price"
            },
            "¥" + vue.toDisplayString($setup.attraction.price),
            1
            /* TEXT */
          )) : (vue.openBlock(), vue.createElementBlock("div", {
            key: 1,
            class: "attraction-price free"
          }, "免费")),
          $setup.attraction.season ? (vue.openBlock(), vue.createElementBlock(
            "div",
            {
              key: 2,
              class: "attraction-season"
            },
            vue.toDisplayString($setup.attraction.season),
            1
            /* TEXT */
          )) : vue.createCommentVNode("v-if", true),
          vue.createElementVNode("div", { class: "attraction-stats" }, [
            vue.createElementVNode(
              "span",
              { class: "attraction-stat" },
              vue.toDisplayString($setup.attraction.likeCount) + " 点赞",
              1
              /* TEXT */
            ),
            vue.createElementVNode(
              "span",
              { class: "attraction-stat" },
              vue.toDisplayString($setup.attraction.commentCount) + " 评论",
              1
              /* TEXT */
            ),
            vue.createElementVNode(
              "span",
              { class: "attraction-stat" },
              vue.toDisplayString($setup.attraction.collectCount) + " 收藏",
              1
              /* TEXT */
            )
          ])
        ])
      ]),
      vue.createElementVNode("view", { class: "detail-content" }, [
        vue.createElementVNode("view", { class: "section" }, [
          vue.createElementVNode("h2", null, "景点介绍"),
          vue.createElementVNode(
            "p",
            null,
            vue.toDisplayString($setup.attraction.description),
            1
            /* TEXT */
          )
        ]),
        vue.createElementVNode("view", { class: "section" }, [
          vue.createElementVNode("h2", null, "评分"),
          vue.createElementVNode("div", { class: "rating" }, [
            vue.createElementVNode("span", { class: "star" }, "★"),
            vue.createElementVNode(
              "span",
              null,
              vue.toDisplayString($setup.attraction.rating),
              1
              /* TEXT */
            )
          ])
        ]),
        vue.createElementVNode("view", { class: "section" }, [
          vue.createElementVNode("h2", null, "地址"),
          vue.createElementVNode(
            "p",
            null,
            vue.toDisplayString($setup.attraction.address),
            1
            /* TEXT */
          )
        ]),
        vue.createElementVNode("view", { class: "section" }, [
          vue.createElementVNode("h2", null, "开放时间"),
          vue.createElementVNode(
            "p",
            null,
            vue.toDisplayString($setup.attraction.openTime),
            1
            /* TEXT */
          )
        ])
      ])
    ]);
  }
  const PagesAttractionDetail = /* @__PURE__ */ _export_sfc(_sfc_main$g, [["render", _sfc_render$f], ["__scopeId", "data-v-b05ef446"], ["__file", "D:/IDEA/work/Minecraft-system/minecraft-App/pages/attraction/detail.vue"]]);
  const _sfc_main$f = {
    __name: "index",
    setup(__props, { expose: __expose }) {
      __expose();
      const searchQuery = vue.ref("");
      const filterCity = vue.ref("");
      const filterPrice = vue.ref("");
      const currentPage = vue.ref(1);
      const pageSize = vue.ref(10);
      const totalPages = vue.ref(1);
      const hotels = vue.ref([]);
      const calculateTotalPages = (total) => {
        return Math.ceil(total / pageSize.value);
      };
      const fetchHotels = async () => {
        try {
          const response = await getHotelList({
            page: currentPage.value,
            pageSize: pageSize.value,
            city: filterCity.value,
            priceRange: filterPrice.value,
            keyword: searchQuery.value
          });
          hotels.value = response.data.records || [];
          totalPages.value = calculateTotalPages(response.data.total || 0);
        } catch (error) {
          formatAppLog("error", "at pages/hotel/index.vue:107", "获取酒店列表失败:", error);
        }
      };
      const navigateToDetail = (id) => {
        uni.navigateTo({
          url: `/pages/hotel/detail?id=${id}`
        });
      };
      const changePage = (page) => {
        currentPage.value = page;
        fetchHotels();
      };
      vue.onMounted(() => {
        fetchHotels();
      });
      const __returned__ = { searchQuery, filterCity, filterPrice, currentPage, pageSize, totalPages, hotels, calculateTotalPages, fetchHotels, navigateToDetail, changePage, ref: vue.ref, onMounted: vue.onMounted, get getHotelList() {
        return getHotelList;
      } };
      Object.defineProperty(__returned__, "__isScriptSetup", { enumerable: false, value: true });
      return __returned__;
    }
  };
  function _sfc_render$e(_ctx, _cache, $props, $setup, $data, $options) {
    return vue.openBlock(), vue.createElementBlock("div", { class: "hotel-container" }, [
      vue.createElementVNode("div", { class: "search-bar" }, [
        vue.withDirectives(vue.createElementVNode(
          "input",
          {
            type: "text",
            "onUpdate:modelValue": _cache[0] || (_cache[0] = ($event) => $setup.searchQuery = $event),
            placeholder: "搜索酒店",
            class: "search-input"
          },
          null,
          512
          /* NEED_PATCH */
        ), [
          [vue.vModelText, $setup.searchQuery]
        ]),
        vue.createElementVNode("button", { class: "search-btn" }, "搜索")
      ]),
      vue.createElementVNode("div", { class: "filter-bar" }, [
        vue.withDirectives(vue.createElementVNode(
          "select",
          {
            "onUpdate:modelValue": _cache[1] || (_cache[1] = ($event) => $setup.filterCity = $event),
            class: "filter-select"
          },
          [
            vue.createElementVNode("option", { value: "" }, "全部城市"),
            vue.createElementVNode("option", { value: "北京" }, "北京"),
            vue.createElementVNode("option", { value: "上海" }, "上海"),
            vue.createElementVNode("option", { value: "广州" }, "广州"),
            vue.createElementVNode("option", { value: "深圳" }, "深圳")
          ],
          512
          /* NEED_PATCH */
        ), [
          [vue.vModelSelect, $setup.filterCity]
        ]),
        vue.withDirectives(vue.createElementVNode(
          "select",
          {
            "onUpdate:modelValue": _cache[2] || (_cache[2] = ($event) => $setup.filterPrice = $event),
            class: "filter-select"
          },
          [
            vue.createElementVNode("option", { value: "" }, "价格区间"),
            vue.createElementVNode("option", { value: "0-200" }, "0-200"),
            vue.createElementVNode("option", { value: "200-500" }, "200-500"),
            vue.createElementVNode("option", { value: "500-1000" }, "500-1000"),
            vue.createElementVNode("option", { value: "1000+" }, "1000+")
          ],
          512
          /* NEED_PATCH */
        ), [
          [vue.vModelSelect, $setup.filterPrice]
        ])
      ]),
      vue.createElementVNode("div", { class: "hotel-list" }, [
        (vue.openBlock(true), vue.createElementBlock(
          vue.Fragment,
          null,
          vue.renderList($setup.hotels, (hotel) => {
            return vue.openBlock(), vue.createElementBlock("div", {
              key: hotel.id,
              class: "hotel-item",
              onClick: ($event) => $setup.navigateToDetail(hotel.id)
            }, [
              vue.createElementVNode("img", {
                src: hotel.coverImage,
                alt: hotel.name,
                class: "hotel-image"
              }, null, 8, ["src", "alt"]),
              vue.createElementVNode("div", { class: "hotel-info" }, [
                vue.createElementVNode(
                  "h3",
                  null,
                  vue.toDisplayString(hotel.name),
                  1
                  /* TEXT */
                ),
                vue.createElementVNode(
                  "p",
                  { class: "hotel-city" },
                  vue.toDisplayString(hotel.city),
                  1
                  /* TEXT */
                ),
                vue.createElementVNode("div", { class: "hotel-stars" }, [
                  (vue.openBlock(true), vue.createElementBlock(
                    vue.Fragment,
                    null,
                    vue.renderList(hotel.starLevel, (i) => {
                      return vue.openBlock(), vue.createElementBlock("span", {
                        key: i,
                        class: "star"
                      }, "★");
                    }),
                    128
                    /* KEYED_FRAGMENT */
                  ))
                ]),
                vue.createElementVNode(
                  "div",
                  { class: "hotel-address" },
                  vue.toDisplayString(hotel.address),
                  1
                  /* TEXT */
                ),
                vue.createElementVNode("div", { class: "hotel-facilities" }, [
                  (vue.openBlock(true), vue.createElementBlock(
                    vue.Fragment,
                    null,
                    vue.renderList(hotel.facilities ? hotel.facilities.split(",") : [], (facility) => {
                      return vue.openBlock(), vue.createElementBlock(
                        "span",
                        {
                          key: facility,
                          class: "hotel-facility"
                        },
                        vue.toDisplayString(facility),
                        1
                        /* TEXT */
                      );
                    }),
                    128
                    /* KEYED_FRAGMENT */
                  ))
                ]),
                vue.createElementVNode("div", { class: "hotel-stats" }, [
                  vue.createElementVNode(
                    "div",
                    { class: "hotel-price" },
                    "¥" + vue.toDisplayString(hotel.price) + "/晚",
                    1
                    /* TEXT */
                  ),
                  vue.createElementVNode("div", { class: "hotel-stat" }, [
                    vue.createElementVNode(
                      "span",
                      null,
                      vue.toDisplayString(hotel.likeCount) + " 点赞",
                      1
                      /* TEXT */
                    ),
                    vue.createElementVNode(
                      "span",
                      null,
                      vue.toDisplayString(hotel.commentCount) + " 评论",
                      1
                      /* TEXT */
                    ),
                    vue.createElementVNode(
                      "span",
                      null,
                      vue.toDisplayString(hotel.collectCount) + " 收藏",
                      1
                      /* TEXT */
                    )
                  ])
                ])
              ])
            ], 8, ["onClick"]);
          }),
          128
          /* KEYED_FRAGMENT */
        ))
      ]),
      $setup.hotels.length === 0 ? (vue.openBlock(), vue.createElementBlock("div", {
        key: 0,
        class: "empty-tip"
      }, " 暂无酒店数据 ")) : vue.createCommentVNode("v-if", true),
      $setup.hotels.length > 0 ? (vue.openBlock(), vue.createElementBlock("div", {
        key: 1,
        class: "pagination"
      }, [
        vue.createElementVNode("button", {
          class: "page-btn",
          disabled: $setup.currentPage === 1,
          onClick: _cache[3] || (_cache[3] = ($event) => $setup.changePage($setup.currentPage - 1))
        }, "上一页", 8, ["disabled"]),
        vue.createElementVNode(
          "span",
          { class: "page-info" },
          vue.toDisplayString($setup.currentPage) + " / " + vue.toDisplayString($setup.totalPages),
          1
          /* TEXT */
        ),
        vue.createElementVNode("button", {
          class: "page-btn",
          disabled: $setup.currentPage === $setup.totalPages,
          onClick: _cache[4] || (_cache[4] = ($event) => $setup.changePage($setup.currentPage + 1))
        }, "下一页", 8, ["disabled"])
      ])) : vue.createCommentVNode("v-if", true)
    ]);
  }
  const PagesHotelIndex = /* @__PURE__ */ _export_sfc(_sfc_main$f, [["render", _sfc_render$e], ["__scopeId", "data-v-c0ede7ae"], ["__file", "D:/IDEA/work/Minecraft-system/minecraft-App/pages/hotel/index.vue"]]);
  const _sfc_main$e = {
    __name: "detail",
    setup(__props, { expose: __expose }) {
      __expose();
      const hotel = vue.ref({});
      const parseImages = (imagesStr) => {
        try {
          return JSON.parse(imagesStr);
        } catch (error) {
          return [];
        }
      };
      vue.onMounted(async () => {
        const pages = getCurrentPages();
        const currentPage = pages[pages.length - 1];
        const { id } = currentPage.options || {};
        if (id) {
          try {
            const response = await getHotelDetail(id);
            hotel.value = response.data;
          } catch (error) {
            formatAppLog("error", "at pages/hotel/detail.vue:73", "获取酒店详情失败:", error);
          }
        }
      });
      const __returned__ = { hotel, parseImages, ref: vue.ref, onMounted: vue.onMounted, get getHotelDetail() {
        return getHotelDetail;
      } };
      Object.defineProperty(__returned__, "__isScriptSetup", { enumerable: false, value: true });
      return __returned__;
    }
  };
  function _sfc_render$d(_ctx, _cache, $props, $setup, $data, $options) {
    return vue.openBlock(), vue.createElementBlock("div", { class: "detail-container" }, [
      vue.createElementVNode("view", { class: "detail-header" }, [
        vue.createElementVNode("image", {
          src: $setup.hotel.coverImage,
          alt: $setup.hotel.name,
          class: "detail-image"
        }, null, 8, ["src", "alt"]),
        vue.createElementVNode("view", { class: "detail-title" }, [
          vue.createElementVNode(
            "h1",
            null,
            vue.toDisplayString($setup.hotel.name),
            1
            /* TEXT */
          ),
          vue.createElementVNode(
            "p",
            { class: "location" },
            vue.toDisplayString($setup.hotel.city),
            1
            /* TEXT */
          ),
          vue.createElementVNode("div", { class: "hotel-stars" }, [
            (vue.openBlock(true), vue.createElementBlock(
              vue.Fragment,
              null,
              vue.renderList($setup.hotel.starLevel, (i) => {
                return vue.openBlock(), vue.createElementBlock("span", {
                  key: i,
                  class: "star"
                }, "★");
              }),
              128
              /* KEYED_FRAGMENT */
            ))
          ]),
          vue.createElementVNode(
            "div",
            { class: "hotel-price" },
            "¥" + vue.toDisplayString($setup.hotel.price) + "/晚",
            1
            /* TEXT */
          ),
          vue.createElementVNode("div", { class: "hotel-facilities" }, [
            (vue.openBlock(true), vue.createElementBlock(
              vue.Fragment,
              null,
              vue.renderList($setup.hotel.facilities ? $setup.hotel.facilities.split(",") : [], (facility) => {
                return vue.openBlock(), vue.createElementBlock(
                  "span",
                  {
                    key: facility,
                    class: "hotel-facility"
                  },
                  vue.toDisplayString(facility),
                  1
                  /* TEXT */
                );
              }),
              128
              /* KEYED_FRAGMENT */
            ))
          ]),
          vue.createElementVNode("div", { class: "hotel-stats" }, [
            vue.createElementVNode(
              "span",
              { class: "hotel-stat" },
              vue.toDisplayString($setup.hotel.likeCount) + " 点赞",
              1
              /* TEXT */
            ),
            vue.createElementVNode(
              "span",
              { class: "hotel-stat" },
              vue.toDisplayString($setup.hotel.commentCount) + " 评论",
              1
              /* TEXT */
            ),
            vue.createElementVNode(
              "span",
              { class: "hotel-stat" },
              vue.toDisplayString($setup.hotel.collectCount) + " 收藏",
              1
              /* TEXT */
            )
          ])
        ])
      ]),
      vue.createElementVNode("view", { class: "detail-content" }, [
        vue.createElementVNode("view", { class: "section" }, [
          vue.createElementVNode("h2", null, "酒店介绍"),
          vue.createElementVNode(
            "p",
            null,
            vue.toDisplayString($setup.hotel.description),
            1
            /* TEXT */
          )
        ]),
        vue.createElementVNode("view", { class: "section" }, [
          vue.createElementVNode("h2", null, "地址"),
          vue.createElementVNode(
            "p",
            null,
            vue.toDisplayString($setup.hotel.address),
            1
            /* TEXT */
          )
        ]),
        vue.createElementVNode("view", { class: "section" }, [
          vue.createElementVNode("h2", null, "联系电话"),
          vue.createElementVNode(
            "p",
            null,
            vue.toDisplayString($setup.hotel.phone),
            1
            /* TEXT */
          )
        ]),
        vue.createElementVNode("view", { class: "section" }, [
          vue.createElementVNode("h2", null, "图片展示"),
          vue.createElementVNode("div", { class: "image-gallery" }, [
            (vue.openBlock(true), vue.createElementBlock(
              vue.Fragment,
              null,
              vue.renderList($setup.hotel.images ? $setup.parseImages($setup.hotel.images) : [], (img, index) => {
                return vue.openBlock(), vue.createElementBlock("image", {
                  key: index,
                  src: img.trim(),
                  alt: $setup.hotel.name + index,
                  class: "gallery-image"
                }, null, 8, ["src", "alt"]);
              }),
              128
              /* KEYED_FRAGMENT */
            ))
          ])
        ])
      ])
    ]);
  }
  const PagesHotelDetail = /* @__PURE__ */ _export_sfc(_sfc_main$e, [["render", _sfc_render$d], ["__scopeId", "data-v-b335e5bc"], ["__file", "D:/IDEA/work/Minecraft-system/minecraft-App/pages/hotel/detail.vue"]]);
  const _sfc_main$d = {
    __name: "index",
    setup(__props, { expose: __expose }) {
      __expose();
      const searchQuery = vue.ref("");
      const filterCity = vue.ref("");
      const filterCuisine = vue.ref("");
      const currentPage = vue.ref(1);
      const pageSize = vue.ref(10);
      const totalPages = vue.ref(1);
      const foods = vue.ref([]);
      const calculateTotalPages = (total) => {
        return Math.ceil(total / pageSize.value);
      };
      const fetchFoods = async () => {
        try {
          const response = await getFoodList({
            page: currentPage.value,
            pageSize: pageSize.value,
            city: filterCity.value,
            cuisine: filterCuisine.value,
            keyword: searchQuery.value
          });
          foods.value = response.data.records || [];
          totalPages.value = calculateTotalPages(response.data.total || 0);
        } catch (error) {
          formatAppLog("error", "at pages/food/index.vue:108", "获取美食列表失败:", error);
        }
      };
      const navigateToDetail = (id) => {
        uni.navigateTo({
          url: `/pages/food/detail?id=${id}`
        });
      };
      const changePage = (page) => {
        currentPage.value = page;
        fetchFoods();
      };
      vue.onMounted(() => {
        fetchFoods();
      });
      const __returned__ = { searchQuery, filterCity, filterCuisine, currentPage, pageSize, totalPages, foods, calculateTotalPages, fetchFoods, navigateToDetail, changePage, ref: vue.ref, onMounted: vue.onMounted, get getFoodList() {
        return getFoodList;
      } };
      Object.defineProperty(__returned__, "__isScriptSetup", { enumerable: false, value: true });
      return __returned__;
    }
  };
  function _sfc_render$c(_ctx, _cache, $props, $setup, $data, $options) {
    return vue.openBlock(), vue.createElementBlock("div", { class: "food-container" }, [
      vue.createElementVNode("div", { class: "search-bar" }, [
        vue.withDirectives(vue.createElementVNode(
          "input",
          {
            type: "text",
            "onUpdate:modelValue": _cache[0] || (_cache[0] = ($event) => $setup.searchQuery = $event),
            placeholder: "搜索美食",
            class: "search-input"
          },
          null,
          512
          /* NEED_PATCH */
        ), [
          [vue.vModelText, $setup.searchQuery]
        ]),
        vue.createElementVNode("button", { class: "search-btn" }, "搜索")
      ]),
      vue.createElementVNode("div", { class: "filter-bar" }, [
        vue.withDirectives(vue.createElementVNode(
          "select",
          {
            "onUpdate:modelValue": _cache[1] || (_cache[1] = ($event) => $setup.filterCity = $event),
            class: "filter-select"
          },
          [
            vue.createElementVNode("option", { value: "" }, "全部城市"),
            vue.createElementVNode("option", { value: "北京" }, "北京"),
            vue.createElementVNode("option", { value: "上海" }, "上海"),
            vue.createElementVNode("option", { value: "广州" }, "广州"),
            vue.createElementVNode("option", { value: "深圳" }, "深圳")
          ],
          512
          /* NEED_PATCH */
        ), [
          [vue.vModelSelect, $setup.filterCity]
        ]),
        vue.withDirectives(vue.createElementVNode(
          "select",
          {
            "onUpdate:modelValue": _cache[2] || (_cache[2] = ($event) => $setup.filterCuisine = $event),
            class: "filter-select"
          },
          [
            vue.createElementVNode("option", { value: "" }, "全部菜系"),
            vue.createElementVNode("option", { value: "川菜" }, "川菜"),
            vue.createElementVNode("option", { value: "粤菜" }, "粤菜"),
            vue.createElementVNode("option", { value: "鲁菜" }, "鲁菜"),
            vue.createElementVNode("option", { value: "淮扬菜" }, "淮扬菜")
          ],
          512
          /* NEED_PATCH */
        ), [
          [vue.vModelSelect, $setup.filterCuisine]
        ])
      ]),
      vue.createElementVNode("div", { class: "food-list" }, [
        (vue.openBlock(true), vue.createElementBlock(
          vue.Fragment,
          null,
          vue.renderList($setup.foods, (food) => {
            return vue.openBlock(), vue.createElementBlock("div", {
              key: food.id,
              class: "food-item",
              onClick: ($event) => $setup.navigateToDetail(food.id)
            }, [
              vue.createElementVNode("img", {
                src: food.coverImage,
                alt: food.name,
                class: "food-image"
              }, null, 8, ["src", "alt"]),
              vue.createElementVNode("div", { class: "food-info" }, [
                vue.createElementVNode(
                  "h3",
                  null,
                  vue.toDisplayString(food.name),
                  1
                  /* TEXT */
                ),
                vue.createElementVNode(
                  "p",
                  { class: "food-meta" },
                  vue.toDisplayString(food.city) + " - " + vue.toDisplayString(food.cuisineType),
                  1
                  /* TEXT */
                ),
                vue.createElementVNode(
                  "div",
                  { class: "food-price" },
                  "¥" + vue.toDisplayString(food.price),
                  1
                  /* TEXT */
                ),
                vue.createElementVNode(
                  "div",
                  { class: "food-address" },
                  vue.toDisplayString(food.address),
                  1
                  /* TEXT */
                ),
                vue.createElementVNode("div", { class: "food-tags" }, [
                  (vue.openBlock(true), vue.createElementBlock(
                    vue.Fragment,
                    null,
                    vue.renderList(food.tags ? food.tags.split(",") : [], (tag) => {
                      return vue.openBlock(), vue.createElementBlock(
                        "span",
                        {
                          key: tag,
                          class: "food-tag"
                        },
                        vue.toDisplayString(tag),
                        1
                        /* TEXT */
                      );
                    }),
                    128
                    /* KEYED_FRAGMENT */
                  ))
                ]),
                vue.createElementVNode("div", { class: "food-stats" }, [
                  vue.createElementVNode("div", { class: "food-rating" }, [
                    vue.createElementVNode("span", { class: "star" }, "★"),
                    vue.createElementVNode(
                      "span",
                      null,
                      vue.toDisplayString(food.rating),
                      1
                      /* TEXT */
                    )
                  ]),
                  vue.createElementVNode("div", { class: "food-stat" }, [
                    vue.createElementVNode(
                      "span",
                      null,
                      vue.toDisplayString(food.likeCount) + " 点赞",
                      1
                      /* TEXT */
                    ),
                    vue.createElementVNode(
                      "span",
                      null,
                      vue.toDisplayString(food.commentCount) + " 评论",
                      1
                      /* TEXT */
                    ),
                    vue.createElementVNode(
                      "span",
                      null,
                      vue.toDisplayString(food.collectCount) + " 收藏",
                      1
                      /* TEXT */
                    )
                  ])
                ])
              ])
            ], 8, ["onClick"]);
          }),
          128
          /* KEYED_FRAGMENT */
        ))
      ]),
      $setup.foods.length === 0 ? (vue.openBlock(), vue.createElementBlock("div", {
        key: 0,
        class: "empty-tip"
      }, " 暂无美食数据 ")) : vue.createCommentVNode("v-if", true),
      $setup.foods.length > 0 ? (vue.openBlock(), vue.createElementBlock("div", {
        key: 1,
        class: "pagination"
      }, [
        vue.createElementVNode("button", {
          class: "page-btn",
          disabled: $setup.currentPage === 1,
          onClick: _cache[3] || (_cache[3] = ($event) => $setup.changePage($setup.currentPage - 1))
        }, "上一页", 8, ["disabled"]),
        vue.createElementVNode(
          "span",
          { class: "page-info" },
          vue.toDisplayString($setup.currentPage) + " / " + vue.toDisplayString($setup.totalPages),
          1
          /* TEXT */
        ),
        vue.createElementVNode("button", {
          class: "page-btn",
          disabled: $setup.currentPage === $setup.totalPages,
          onClick: _cache[4] || (_cache[4] = ($event) => $setup.changePage($setup.currentPage + 1))
        }, "下一页", 8, ["disabled"])
      ])) : vue.createCommentVNode("v-if", true)
    ]);
  }
  const PagesFoodIndex = /* @__PURE__ */ _export_sfc(_sfc_main$d, [["render", _sfc_render$c], ["__scopeId", "data-v-f255b95b"], ["__file", "D:/IDEA/work/Minecraft-system/minecraft-App/pages/food/index.vue"]]);
  const _sfc_main$c = {
    __name: "detail",
    setup(__props, { expose: __expose }) {
      __expose();
      const food = vue.ref({});
      vue.onMounted(async () => {
        const pages = getCurrentPages();
        const currentPage = pages[pages.length - 1];
        const { id } = currentPage.options || {};
        if (id) {
          try {
            const response = await getFoodDetail(id);
            food.value = response.data;
          } catch (error) {
            formatAppLog("error", "at pages/food/detail.vue:64", "获取美食详情失败:", error);
          }
        }
      });
      const __returned__ = { food, ref: vue.ref, onMounted: vue.onMounted, get getFoodDetail() {
        return getFoodDetail;
      } };
      Object.defineProperty(__returned__, "__isScriptSetup", { enumerable: false, value: true });
      return __returned__;
    }
  };
  function _sfc_render$b(_ctx, _cache, $props, $setup, $data, $options) {
    return vue.openBlock(), vue.createElementBlock("div", { class: "detail-container" }, [
      vue.createElementVNode("view", { class: "detail-header" }, [
        vue.createElementVNode("image", {
          src: $setup.food.coverImage,
          alt: $setup.food.name,
          class: "detail-image"
        }, null, 8, ["src", "alt"]),
        vue.createElementVNode("view", { class: "detail-title" }, [
          vue.createElementVNode(
            "h1",
            null,
            vue.toDisplayString($setup.food.name),
            1
            /* TEXT */
          ),
          vue.createElementVNode(
            "p",
            { class: "location" },
            vue.toDisplayString($setup.food.city) + " - " + vue.toDisplayString($setup.food.cuisineType),
            1
            /* TEXT */
          ),
          vue.createElementVNode(
            "div",
            { class: "food-price" },
            "¥" + vue.toDisplayString($setup.food.price),
            1
            /* TEXT */
          ),
          vue.createElementVNode("div", { class: "food-tags" }, [
            (vue.openBlock(true), vue.createElementBlock(
              vue.Fragment,
              null,
              vue.renderList($setup.food.tags.split(","), (tag) => {
                return vue.openBlock(), vue.createElementBlock(
                  "span",
                  {
                    key: tag,
                    class: "food-tag"
                  },
                  vue.toDisplayString(tag),
                  1
                  /* TEXT */
                );
              }),
              128
              /* KEYED_FRAGMENT */
            ))
          ]),
          vue.createElementVNode("div", { class: "food-stats" }, [
            vue.createElementVNode(
              "span",
              { class: "food-stat" },
              vue.toDisplayString($setup.food.likeCount) + " 点赞",
              1
              /* TEXT */
            ),
            vue.createElementVNode(
              "span",
              { class: "food-stat" },
              vue.toDisplayString($setup.food.commentCount) + " 评论",
              1
              /* TEXT */
            ),
            vue.createElementVNode(
              "span",
              { class: "food-stat" },
              vue.toDisplayString($setup.food.collectCount) + " 收藏",
              1
              /* TEXT */
            )
          ])
        ])
      ]),
      vue.createElementVNode("view", { class: "detail-content" }, [
        vue.createElementVNode("view", { class: "section" }, [
          vue.createElementVNode("h2", null, "美食介绍"),
          vue.createElementVNode(
            "p",
            null,
            vue.toDisplayString($setup.food.description),
            1
            /* TEXT */
          )
        ]),
        vue.createElementVNode("view", { class: "section" }, [
          vue.createElementVNode("h2", null, "评分"),
          vue.createElementVNode("div", { class: "rating" }, [
            vue.createElementVNode("span", { class: "star" }, "★"),
            vue.createElementVNode(
              "span",
              null,
              vue.toDisplayString($setup.food.rating),
              1
              /* TEXT */
            )
          ])
        ]),
        vue.createElementVNode("view", { class: "section" }, [
          vue.createElementVNode("h2", null, "地址"),
          vue.createElementVNode(
            "p",
            null,
            vue.toDisplayString($setup.food.address),
            1
            /* TEXT */
          )
        ]),
        vue.createElementVNode("view", { class: "section" }, [
          vue.createElementVNode("h2", null, "图片展示"),
          vue.createElementVNode("div", { class: "image-gallery" }, [
            (vue.openBlock(true), vue.createElementBlock(
              vue.Fragment,
              null,
              vue.renderList($setup.food.images.split(","), (img, index) => {
                return vue.openBlock(), vue.createElementBlock("image", {
                  key: index,
                  src: img.trim(),
                  alt: $setup.food.name + index,
                  class: "gallery-image"
                }, null, 8, ["src", "alt"]);
              }),
              128
              /* KEYED_FRAGMENT */
            ))
          ])
        ])
      ])
    ]);
  }
  const PagesFoodDetail = /* @__PURE__ */ _export_sfc(_sfc_main$c, [["render", _sfc_render$b], ["__scopeId", "data-v-b49a7fa1"], ["__file", "D:/IDEA/work/Minecraft-system/minecraft-App/pages/food/detail.vue"]]);
  const _sfc_main$b = {
    name: "TicketIndex"
  };
  function _sfc_render$a(_ctx, _cache, $props, $setup, $data, $options) {
    return vue.openBlock(), vue.createElementBlock("view", { class: "ticket-container" }, [
      vue.createElementVNode("text", null, "票务预订页面")
    ]);
  }
  const PagesTicketIndex = /* @__PURE__ */ _export_sfc(_sfc_main$b, [["render", _sfc_render$a], ["__scopeId", "data-v-2fbcbfbd"], ["__file", "D:/IDEA/work/Minecraft-system/minecraft-App/pages/ticket/index.vue"]]);
  const _sfc_main$a = {
    __name: "index",
    setup(__props, { expose: __expose }) {
      __expose();
      const navigateTo = (url) => {
        uni.navigateTo({
          url
        });
      };
      const logout = () => {
        uni.showModal({
          title: "退出登录",
          content: "确定要退出登录吗？",
          success: function(res) {
            if (res.confirm) {
              uni.removeStorageSync("userInfo");
              uni.removeStorageSync("token");
              uni.navigateTo({
                url: "/pages/login/index"
              });
            }
          }
        });
      };
      const __returned__ = { navigateTo, logout, ref: vue.ref };
      Object.defineProperty(__returned__, "__isScriptSetup", { enumerable: false, value: true });
      return __returned__;
    }
  };
  function _sfc_render$9(_ctx, _cache, $props, $setup, $data, $options) {
    return vue.openBlock(), vue.createElementBlock("div", { class: "profile-container" }, [
      vue.createElementVNode("div", { class: "user-info" }, [
        vue.createElementVNode("div", { class: "avatar" }, [
          vue.createElementVNode("img", {
            src: "https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=user%20avatar%20profile%20picture&image_size=square",
            alt: "用户头像"
          })
        ]),
        vue.createElementVNode("div", { class: "user-details" }, [
          vue.createElementVNode("h2", null, "用户名"),
          vue.createElementVNode("p", null, "点击登录")
        ])
      ]),
      vue.createElementVNode("div", { class: "menu-section" }, [
        vue.createElementVNode("div", {
          class: "menu-item",
          onClick: _cache[0] || (_cache[0] = ($event) => $setup.navigateTo("/pages/order/index"))
        }, [
          vue.createElementVNode("div", { class: "menu-icon" }, "📋"),
          vue.createElementVNode("div", { class: "menu-text" }, "我的订单"),
          vue.createElementVNode("div", { class: "menu-arrow" }, "›")
        ]),
        vue.createElementVNode("div", {
          class: "menu-item",
          onClick: _cache[1] || (_cache[1] = ($event) => $setup.navigateTo("/pages/collection/index"))
        }, [
          vue.createElementVNode("div", { class: "menu-icon" }, "❤️"),
          vue.createElementVNode("div", { class: "menu-text" }, "我的收藏"),
          vue.createElementVNode("div", { class: "menu-arrow" }, "›")
        ]),
        vue.createElementVNode("div", {
          class: "menu-item",
          onClick: _cache[2] || (_cache[2] = ($event) => $setup.navigateTo("/pages/comment/index"))
        }, [
          vue.createElementVNode("div", { class: "menu-icon" }, "💬"),
          vue.createElementVNode("div", { class: "menu-text" }, "我的评论"),
          vue.createElementVNode("div", { class: "menu-arrow" }, "›")
        ]),
        vue.createElementVNode("div", {
          class: "menu-item",
          onClick: _cache[3] || (_cache[3] = ($event) => $setup.navigateTo("/pages/chat/index"))
        }, [
          vue.createElementVNode("div", { class: "menu-icon" }, "💭"),
          vue.createElementVNode("div", { class: "menu-text" }, "在线客服"),
          vue.createElementVNode("div", { class: "menu-arrow" }, "›")
        ])
      ]),
      vue.createElementVNode("div", { class: "menu-section" }, [
        vue.createElementVNode("div", {
          class: "menu-item",
          onClick: _cache[4] || (_cache[4] = ($event) => $setup.navigateTo("/pages/profile/settings"))
        }, [
          vue.createElementVNode("div", { class: "menu-icon" }, "⚙️"),
          vue.createElementVNode("div", { class: "menu-text" }, "设置"),
          vue.createElementVNode("div", { class: "menu-arrow" }, "›")
        ]),
        vue.createElementVNode("div", {
          class: "menu-item",
          onClick: _cache[5] || (_cache[5] = ($event) => $setup.navigateTo("/pages/profile/about"))
        }, [
          vue.createElementVNode("div", { class: "menu-icon" }, "ℹ️"),
          vue.createElementVNode("div", { class: "menu-text" }, "关于我们"),
          vue.createElementVNode("div", { class: "menu-arrow" }, "›")
        ])
      ]),
      vue.createElementVNode("button", {
        class: "logout-btn",
        onClick: $setup.logout
      }, "退出登录")
    ]);
  }
  const PagesProfileIndex = /* @__PURE__ */ _export_sfc(_sfc_main$a, [["render", _sfc_render$9], ["__scopeId", "data-v-201c0da5"], ["__file", "D:/IDEA/work/Minecraft-system/minecraft-App/pages/profile/index.vue"]]);
  const _sfc_main$9 = {
    __name: "settings",
    setup(__props, { expose: __expose }) {
      __expose();
      const navigateTo = (url) => {
        uni.navigateTo({
          url
        });
      };
      const __returned__ = { navigateTo, ref: vue.ref };
      Object.defineProperty(__returned__, "__isScriptSetup", { enumerable: false, value: true });
      return __returned__;
    }
  };
  function _sfc_render$8(_ctx, _cache, $props, $setup, $data, $options) {
    return vue.openBlock(), vue.createElementBlock("div", { class: "settings-container" }, [
      vue.createElementVNode("div", { class: "settings-header" }, [
        vue.createElementVNode("h1", null, "设置")
      ]),
      vue.createElementVNode("div", { class: "settings-list" }, [
        vue.createElementVNode("div", { class: "settings-item" }, [
          vue.createElementVNode("div", { class: "settings-label" }, "账号与安全"),
          vue.createElementVNode("div", { class: "settings-action" }, [
            vue.createElementVNode("span", { class: "settings-desc" }, "修改密码、绑定手机"),
            vue.createElementVNode("span", { class: "settings-arrow" }, "›")
          ])
        ]),
        vue.createElementVNode("div", { class: "settings-item" }, [
          vue.createElementVNode("div", { class: "settings-label" }, "隐私设置"),
          vue.createElementVNode("div", { class: "settings-action" }, [
            vue.createElementVNode("span", { class: "settings-desc" }, "消息通知、隐私保护"),
            vue.createElementVNode("span", { class: "settings-arrow" }, "›")
          ])
        ]),
        vue.createElementVNode("div", { class: "settings-item" }, [
          vue.createElementVNode("div", { class: "settings-label" }, "通用设置"),
          vue.createElementVNode("div", { class: "settings-action" }, [
            vue.createElementVNode("span", { class: "settings-desc" }, "语言、主题"),
            vue.createElementVNode("span", { class: "settings-arrow" }, "›")
          ])
        ]),
        vue.createElementVNode("div", { class: "settings-item" }, [
          vue.createElementVNode("div", { class: "settings-label" }, "清除缓存"),
          vue.createElementVNode("div", { class: "settings-action" }, [
            vue.createElementVNode("span", { class: "settings-desc" }, "12.3MB"),
            vue.createElementVNode("span", { class: "settings-arrow" }, "›")
          ])
        ]),
        vue.createElementVNode("div", { class: "settings-item" }, [
          vue.createElementVNode("div", { class: "settings-label" }, "检查更新"),
          vue.createElementVNode("div", { class: "settings-action" }, [
            vue.createElementVNode("span", { class: "settings-desc" }, "当前版本：v1.0.0"),
            vue.createElementVNode("span", { class: "settings-arrow" }, "›")
          ])
        ]),
        vue.createElementVNode("div", { class: "settings-item" }, [
          vue.createElementVNode("div", { class: "settings-label" }, "意见反馈"),
          vue.createElementVNode("div", { class: "settings-action" }, [
            vue.createElementVNode("span", { class: "settings-desc" }, "提交问题与建议"),
            vue.createElementVNode("span", { class: "settings-arrow" }, "›")
          ])
        ])
      ])
    ]);
  }
  const PagesProfileSettings = /* @__PURE__ */ _export_sfc(_sfc_main$9, [["render", _sfc_render$8], ["__scopeId", "data-v-eeefe5cd"], ["__file", "D:/IDEA/work/Minecraft-system/minecraft-App/pages/profile/settings.vue"]]);
  const _sfc_main$8 = {
    __name: "about",
    setup(__props, { expose: __expose }) {
      __expose();
      const navigateTo = (url) => {
        uni.navigateTo({
          url
        });
      };
      const __returned__ = { navigateTo, ref: vue.ref };
      Object.defineProperty(__returned__, "__isScriptSetup", { enumerable: false, value: true });
      return __returned__;
    }
  };
  function _sfc_render$7(_ctx, _cache, $props, $setup, $data, $options) {
    return vue.openBlock(), vue.createElementBlock("div", { class: "about-container" }, [
      vue.createElementVNode("div", { class: "about-header" }, [
        vue.createElementVNode("h1", null, "关于我们")
      ]),
      vue.createElementVNode("div", { class: "about-content" }, [
        vue.createElementVNode("div", { class: "about-logo" }, [
          vue.createElementVNode("img", {
            src: "https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=travel%20app%20logo%20modern%20design&image_size=square",
            alt: "应用logo"
          })
        ]),
        vue.createElementVNode("div", { class: "about-info" }, [
          vue.createElementVNode("h2", null, "旅行助手"),
          vue.createElementVNode("p", { class: "version" }, "版本：v1.0.0"),
          vue.createElementVNode("p", { class: "description" }, " 旅行助手是一款专注于为用户提供全方位旅行服务的应用，包括景点推荐、酒店预订、美食探索、票务预订等功能。我们致力于为用户打造便捷、舒适的旅行体验，让每一次出行都充满乐趣。 ")
        ]),
        vue.createElementVNode("div", { class: "about-section" }, [
          vue.createElementVNode("h3", null, "功能特点"),
          vue.createElementVNode("ul", { class: "feature-list" }, [
            vue.createElementVNode("li", null, "• 景点推荐与详情"),
            vue.createElementVNode("li", null, "• 酒店预订与比较"),
            vue.createElementVNode("li", null, "• 美食探索与推荐"),
            vue.createElementVNode("li", null, "• 票务预订与管理"),
            vue.createElementVNode("li", null, "• 旅行计划制定"),
            vue.createElementVNode("li", null, "• 在线客服支持")
          ])
        ]),
        vue.createElementVNode("div", { class: "about-section" }, [
          vue.createElementVNode("h3", null, "联系我们"),
          vue.createElementVNode("p", null, "邮箱：support@travelassistant.com"),
          vue.createElementVNode("p", null, "电话：400-123-4567"),
          vue.createElementVNode("p", null, "地址：北京市朝阳区旅行大厦1001室")
        ]),
        vue.createElementVNode("div", { class: "about-section" }, [
          vue.createElementVNode("h3", null, "用户协议"),
          vue.createElementVNode("p", null, [
            vue.createElementVNode("a", { href: "#" }, "《用户服务协议》")
          ]),
          vue.createElementVNode("p", null, [
            vue.createElementVNode("a", { href: "#" }, "《隐私政策》")
          ])
        ])
      ])
    ]);
  }
  const PagesProfileAbout = /* @__PURE__ */ _export_sfc(_sfc_main$8, [["render", _sfc_render$7], ["__scopeId", "data-v-c0d14513"], ["__file", "D:/IDEA/work/Minecraft-system/minecraft-App/pages/profile/about.vue"]]);
  const _sfc_main$7 = {
    __name: "index",
    setup(__props, { expose: __expose }) {
      __expose();
      const navigateTo = (url) => {
        uni.navigateTo({
          url
        });
      };
      const __returned__ = { navigateTo, ref: vue.ref };
      Object.defineProperty(__returned__, "__isScriptSetup", { enumerable: false, value: true });
      return __returned__;
    }
  };
  function _sfc_render$6(_ctx, _cache, $props, $setup, $data, $options) {
    return vue.openBlock(), vue.createElementBlock("div", { class: "order-container" }, [
      vue.createElementVNode("div", { class: "order-header" }, [
        vue.createElementVNode("h1", null, "我的订单")
      ]),
      vue.createElementVNode("div", { class: "order-tabs" }, [
        vue.createElementVNode("div", { class: "tab-item active" }, "全部"),
        vue.createElementVNode("div", { class: "tab-item" }, "待支付"),
        vue.createElementVNode("div", { class: "tab-item" }, "待使用"),
        vue.createElementVNode("div", { class: "tab-item" }, "已完成"),
        vue.createElementVNode("div", { class: "tab-item" }, "已取消")
      ]),
      vue.createElementVNode("div", { class: "order-list" }, [
        vue.createElementVNode("div", { class: "order-item" }, [
          vue.createElementVNode("div", { class: "order-header-info" }, [
            vue.createElementVNode("span", { class: "order-id" }, "订单号：20260329001"),
            vue.createElementVNode("span", { class: "order-status" }, "待支付")
          ]),
          vue.createElementVNode("div", { class: "order-content" }, [
            vue.createElementVNode("div", { class: "order-product" }, [
              vue.createElementVNode("img", {
                src: "https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=travel%20ticket%20icon&image_size=square",
                alt: "订单商品",
                class: "product-image"
              }),
              vue.createElementVNode("div", { class: "product-info" }, [
                vue.createElementVNode("h3", null, "景点门票"),
                vue.createElementVNode("p", null, "数量：2")
              ])
            ]),
            vue.createElementVNode("div", { class: "order-price" }, "¥198.00")
          ]),
          vue.createElementVNode("div", { class: "order-actions" }, [
            vue.createElementVNode("button", { class: "action-btn secondary" }, "取消订单"),
            vue.createElementVNode("button", { class: "action-btn primary" }, "立即支付")
          ])
        ]),
        vue.createElementVNode("div", { class: "order-item" }, [
          vue.createElementVNode("div", { class: "order-header-info" }, [
            vue.createElementVNode("span", { class: "order-id" }, "订单号：20260328002"),
            vue.createElementVNode("span", { class: "order-status" }, "已完成")
          ]),
          vue.createElementVNode("div", { class: "order-content" }, [
            vue.createElementVNode("div", { class: "order-product" }, [
              vue.createElementVNode("img", {
                src: "https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=hotel%20booking%20icon&image_size=square",
                alt: "订单商品",
                class: "product-image"
              }),
              vue.createElementVNode("div", { class: "product-info" }, [
                vue.createElementVNode("h3", null, "酒店预订"),
                vue.createElementVNode("p", null, "数量：1")
              ])
            ]),
            vue.createElementVNode("div", { class: "order-price" }, "¥398.00")
          ]),
          vue.createElementVNode("div", { class: "order-actions" }, [
            vue.createElementVNode("button", { class: "action-btn secondary" }, "查看详情")
          ])
        ])
      ])
    ]);
  }
  const PagesOrderIndex = /* @__PURE__ */ _export_sfc(_sfc_main$7, [["render", _sfc_render$6], ["__scopeId", "data-v-17a44f9d"], ["__file", "D:/IDEA/work/Minecraft-system/minecraft-App/pages/order/index.vue"]]);
  const _sfc_main$6 = {
    __name: "index",
    setup(__props, { expose: __expose }) {
      __expose();
      const removeCollection = () => {
        uni.showModal({
          title: "移除收藏",
          content: "确定要移除这个收藏吗？",
          success: function(res) {
            if (res.confirm) {
              uni.showToast({
                title: "已移除收藏",
                icon: "success"
              });
            }
          }
        });
      };
      const __returned__ = { removeCollection, ref: vue.ref };
      Object.defineProperty(__returned__, "__isScriptSetup", { enumerable: false, value: true });
      return __returned__;
    }
  };
  function _sfc_render$5(_ctx, _cache, $props, $setup, $data, $options) {
    return vue.openBlock(), vue.createElementBlock("div", { class: "collection-container" }, [
      vue.createElementVNode("div", { class: "collection-header" }, [
        vue.createElementVNode("h1", null, "我的收藏")
      ]),
      vue.createElementVNode("div", { class: "collection-tabs" }, [
        vue.createElementVNode("div", { class: "tab-item active" }, "全部"),
        vue.createElementVNode("div", { class: "tab-item" }, "景点"),
        vue.createElementVNode("div", { class: "tab-item" }, "酒店"),
        vue.createElementVNode("div", { class: "tab-item" }, "美食")
      ]),
      vue.createElementVNode("div", { class: "collection-list" }, [
        vue.createElementVNode("div", { class: "collection-item" }, [
          vue.createElementVNode("img", {
            src: "https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=beautiful%20scenic%20landscape&image_size=landscape_4_3",
            alt: "收藏景点",
            class: "collection-image"
          }),
          vue.createElementVNode("div", { class: "collection-info" }, [
            vue.createElementVNode("h3", null, "长城"),
            vue.createElementVNode("p", { class: "collection-desc" }, "中国著名景点，历史悠久"),
            vue.createElementVNode("div", { class: "collection-meta" }, [
              vue.createElementVNode("span", { class: "collection-type" }, "景点"),
              vue.createElementVNode("span", { class: "collection-time" }, "2026-03-28")
            ])
          ]),
          vue.createElementVNode("div", { class: "collection-action" }, [
            vue.createElementVNode("button", {
              class: "action-btn",
              onClick: $setup.removeCollection
            }, [
              vue.createElementVNode("span", { class: "icon" }, "❤️")
            ])
          ])
        ]),
        vue.createElementVNode("div", { class: "collection-item" }, [
          vue.createElementVNode("img", {
            src: "https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=luxury%20hotel%20exterior&image_size=landscape_4_3",
            alt: "收藏酒店",
            class: "collection-image"
          }),
          vue.createElementVNode("div", { class: "collection-info" }, [
            vue.createElementVNode("h3", null, "豪华酒店"),
            vue.createElementVNode("p", { class: "collection-desc" }, "五星级酒店，舒适体验"),
            vue.createElementVNode("div", { class: "collection-meta" }, [
              vue.createElementVNode("span", { class: "collection-type" }, "酒店"),
              vue.createElementVNode("span", { class: "collection-time" }, "2026-03-27")
            ])
          ]),
          vue.createElementVNode("div", { class: "collection-action" }, [
            vue.createElementVNode("button", {
              class: "action-btn",
              onClick: $setup.removeCollection
            }, [
              vue.createElementVNode("span", { class: "icon" }, "❤️")
            ])
          ])
        ]),
        vue.createElementVNode("div", { class: "collection-item" }, [
          vue.createElementVNode("img", {
            src: "https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=chinese%20food%20dish&image_size=landscape_4_3",
            alt: "收藏美食",
            class: "collection-image"
          }),
          vue.createElementVNode("div", { class: "collection-info" }, [
            vue.createElementVNode("h3", null, "北京烤鸭"),
            vue.createElementVNode("p", { class: "collection-desc" }, "传统名菜，口感独特"),
            vue.createElementVNode("div", { class: "collection-meta" }, [
              vue.createElementVNode("span", { class: "collection-type" }, "美食"),
              vue.createElementVNode("span", { class: "collection-time" }, "2026-03-26")
            ])
          ]),
          vue.createElementVNode("div", { class: "collection-action" }, [
            vue.createElementVNode("button", {
              class: "action-btn",
              onClick: $setup.removeCollection
            }, [
              vue.createElementVNode("span", { class: "icon" }, "❤️")
            ])
          ])
        ])
      ])
    ]);
  }
  const PagesCollectionIndex = /* @__PURE__ */ _export_sfc(_sfc_main$6, [["render", _sfc_render$5], ["__scopeId", "data-v-7e535635"], ["__file", "D:/IDEA/work/Minecraft-system/minecraft-App/pages/collection/index.vue"]]);
  const _sfc_main$5 = {
    __name: "index",
    setup(__props, { expose: __expose }) {
      __expose();
      const deleteComment = () => {
        uni.showModal({
          title: "删除评论",
          content: "确定要删除这条评论吗？",
          success: function(res) {
            if (res.confirm) {
              uni.showToast({
                title: "评论已删除",
                icon: "success"
              });
            }
          }
        });
      };
      const __returned__ = { deleteComment, ref: vue.ref };
      Object.defineProperty(__returned__, "__isScriptSetup", { enumerable: false, value: true });
      return __returned__;
    }
  };
  function _sfc_render$4(_ctx, _cache, $props, $setup, $data, $options) {
    return vue.openBlock(), vue.createElementBlock("div", { class: "comment-container" }, [
      vue.createElementVNode("div", { class: "comment-header" }, [
        vue.createElementVNode("h1", null, "我的评论")
      ]),
      vue.createElementVNode("div", { class: "comment-list" }, [
        vue.createElementVNode("div", { class: "comment-item" }, [
          vue.createElementVNode("div", { class: "comment-user" }, [
            vue.createElementVNode("img", {
              src: "https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=user%20avatar%20profile%20picture&image_size=square",
              alt: "用户头像",
              class: "user-avatar"
            }),
            vue.createElementVNode("div", { class: "user-info" }, [
              vue.createElementVNode("h3", null, "用户名"),
              vue.createElementVNode("p", { class: "comment-time" }, "2026-03-28 14:30")
            ])
          ]),
          vue.createElementVNode("div", { class: "comment-content" }, [
            vue.createElementVNode("div", { class: "comment-rating" }, [
              vue.createElementVNode("span", { class: "star" }, "★★★★★")
            ]),
            vue.createElementVNode("p", { class: "comment-text" }, "这个景点非常棒，风景优美，服务周到，下次还会再来！"),
            vue.createElementVNode("div", { class: "comment-target" }, [
              vue.createElementVNode("span", { class: "target-label" }, "评论对象："),
              vue.createElementVNode("span", { class: "target-name" }, "长城")
            ])
          ]),
          vue.createElementVNode("div", { class: "comment-actions" }, [
            vue.createElementVNode("button", { class: "action-btn" }, "编辑"),
            vue.createElementVNode("button", { class: "action-btn" }, "删除")
          ])
        ]),
        vue.createElementVNode("div", { class: "comment-item" }, [
          vue.createElementVNode("div", { class: "comment-user" }, [
            vue.createElementVNode("img", {
              src: "https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=user%20avatar%20profile%20picture&image_size=square",
              alt: "用户头像",
              class: "user-avatar"
            }),
            vue.createElementVNode("div", { class: "user-info" }, [
              vue.createElementVNode("h3", null, "用户名"),
              vue.createElementVNode("p", { class: "comment-time" }, "2026-03-27 10:15")
            ])
          ]),
          vue.createElementVNode("div", { class: "comment-content" }, [
            vue.createElementVNode("div", { class: "comment-rating" }, [
              vue.createElementVNode("span", { class: "star" }, "★★★★☆")
            ]),
            vue.createElementVNode("p", { class: "comment-text" }, "酒店环境不错，房间干净整洁，就是价格有点贵。"),
            vue.createElementVNode("div", { class: "comment-target" }, [
              vue.createElementVNode("span", { class: "target-label" }, "评论对象："),
              vue.createElementVNode("span", { class: "target-name" }, "豪华酒店")
            ])
          ]),
          vue.createElementVNode("div", { class: "comment-actions" }, [
            vue.createElementVNode("button", { class: "action-btn" }, "编辑"),
            vue.createElementVNode("button", { class: "action-btn" }, "删除")
          ])
        ]),
        vue.createElementVNode("div", { class: "comment-item" }, [
          vue.createElementVNode("div", { class: "comment-user" }, [
            vue.createElementVNode("img", {
              src: "https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=user%20avatar%20profile%20picture&image_size=square",
              alt: "用户头像",
              class: "user-avatar"
            }),
            vue.createElementVNode("div", { class: "user-info" }, [
              vue.createElementVNode("h3", null, "用户名"),
              vue.createElementVNode("p", { class: "comment-time" }, "2026-03-26 18:45")
            ])
          ]),
          vue.createElementVNode("div", { class: "comment-content" }, [
            vue.createElementVNode("div", { class: "comment-rating" }, [
              vue.createElementVNode("span", { class: "star" }, "★★★★★")
            ]),
            vue.createElementVNode("p", { class: "comment-text" }, "北京烤鸭真的很好吃，皮脆肉嫩，非常推荐！"),
            vue.createElementVNode("div", { class: "comment-target" }, [
              vue.createElementVNode("span", { class: "target-label" }, "评论对象："),
              vue.createElementVNode("span", { class: "target-name" }, "北京烤鸭")
            ])
          ]),
          vue.createElementVNode("div", { class: "comment-actions" }, [
            vue.createElementVNode("button", { class: "action-btn" }, "编辑"),
            vue.createElementVNode("button", { class: "action-btn" }, "删除")
          ])
        ])
      ])
    ]);
  }
  const PagesCommentIndex = /* @__PURE__ */ _export_sfc(_sfc_main$5, [["render", _sfc_render$4], ["__scopeId", "data-v-ba484d75"], ["__file", "D:/IDEA/work/Minecraft-system/minecraft-App/pages/comment/index.vue"]]);
  const _sfc_main$4 = {
    __name: "index",
    setup(__props, { expose: __expose }) {
      __expose();
      const message = vue.ref("");
      const sendMessage = () => {
        if (message.value.trim()) {
          formatAppLog("log", "at pages/chat/index.vue:60", "发送消息:", message.value);
          message.value = "";
          setTimeout(() => {
          }, 1e3);
        }
      };
      const __returned__ = { message, sendMessage, ref: vue.ref };
      Object.defineProperty(__returned__, "__isScriptSetup", { enumerable: false, value: true });
      return __returned__;
    }
  };
  function _sfc_render$3(_ctx, _cache, $props, $setup, $data, $options) {
    return vue.openBlock(), vue.createElementBlock("div", { class: "chat-container" }, [
      vue.createElementVNode("div", { class: "chat-header" }, [
        vue.createElementVNode("h1", null, "在线客服")
      ]),
      vue.createElementVNode("div", { class: "chat-messages" }, [
        vue.createElementVNode("div", { class: "message-item bot" }, [
          vue.createElementVNode("div", { class: "message-avatar" }, [
            vue.createElementVNode("img", {
              src: "https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=customer%20service%20robot%20avatar&image_size=square",
              alt: "客服头像"
            })
          ]),
          vue.createElementVNode("div", { class: "message-content" }, [
            vue.createElementVNode("p", null, "您好！欢迎使用在线客服，有什么可以帮助您的吗？"),
            vue.createElementVNode("span", { class: "message-time" }, "10:00")
          ])
        ]),
        vue.createElementVNode("div", { class: "message-item user" }, [
          vue.createElementVNode("div", { class: "message-content" }, [
            vue.createElementVNode("p", null, "我想了解一下景点门票的预订流程"),
            vue.createElementVNode("span", { class: "message-time" }, "10:01")
          ]),
          vue.createElementVNode("div", { class: "message-avatar" }, [
            vue.createElementVNode("img", {
              src: "https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=user%20avatar%20profile%20picture&image_size=square",
              alt: "用户头像"
            })
          ])
        ]),
        vue.createElementVNode("div", { class: "message-item bot" }, [
          vue.createElementVNode("div", { class: "message-avatar" }, [
            vue.createElementVNode("img", {
              src: "https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=customer%20service%20robot%20avatar&image_size=square",
              alt: "客服头像"
            })
          ]),
          vue.createElementVNode("div", { class: "message-content" }, [
            vue.createElementVNode("p", null, "您好，预订景点门票的流程如下："),
            vue.createElementVNode("p", null, '1. 在首页选择"景点"进入景点列表'),
            vue.createElementVNode("p", null, "2. 选择您感兴趣的景点，点击进入详情页"),
            vue.createElementVNode("p", null, '3. 在详情页点击"立即预订"按钮'),
            vue.createElementVNode("p", null, '4. 选择日期和人数，然后点击"提交订单"'),
            vue.createElementVNode("p", null, "5. 完成支付后，您将收到电子门票"),
            vue.createElementVNode("span", { class: "message-time" }, "10:02")
          ])
        ])
      ]),
      vue.createElementVNode("div", { class: "chat-input" }, [
        vue.withDirectives(vue.createElementVNode(
          "input",
          {
            type: "text",
            "onUpdate:modelValue": _cache[0] || (_cache[0] = ($event) => $setup.message = $event),
            placeholder: "请输入您的问题...",
            class: "input-field"
          },
          null,
          512
          /* NEED_PATCH */
        ), [
          [vue.vModelText, $setup.message]
        ]),
        vue.createElementVNode("button", {
          onClick: $setup.sendMessage,
          class: "send-btn"
        }, "发送")
      ])
    ]);
  }
  const PagesChatIndex = /* @__PURE__ */ _export_sfc(_sfc_main$4, [["render", _sfc_render$3], ["__scopeId", "data-v-5a559478"], ["__file", "D:/IDEA/work/Minecraft-system/minecraft-App/pages/chat/index.vue"]]);
  const login = (data) => {
    return request$1.post("/auth/login", data);
  };
  const register = (data) => {
    return request$1.post("/auth/register", data);
  };
  const defaultAvatar$1 = "https://img.icons8.com/ios-filled/50/000000/user.png";
  const _sfc_main$3 = {
    __name: "login",
    setup(__props, { expose: __expose }) {
      __expose();
      const loading = vue.ref(false);
      const remember = vue.ref(false);
      const avatarUrl = vue.ref(defaultAvatar$1);
      const formData = vue.reactive({
        account: "",
        password: "",
        hasInput: false
      });
      const errors = vue.reactive({
        account: "",
        password: ""
      });
      const handleAccountInput = async () => {
        const account = formData.account.trim();
        formData.hasInput = account !== "";
        if (account) {
          avatarUrl.value = defaultAvatar$1;
        } else {
          avatarUrl.value = defaultAvatar$1;
        }
      };
      const validateForm = () => {
        let isValid = true;
        errors.account = "";
        errors.password = "";
        if (!formData.account.trim()) {
          errors.account = "请输入账号";
          isValid = false;
        }
        if (!formData.password.trim()) {
          errors.password = "请输入密码";
          isValid = false;
        }
        return isValid;
      };
      const submitForm = async () => {
        if (validateForm()) {
          loading.value = true;
          try {
            const response = await login(formData);
            if (response.code === 200) {
              uni.setStorageSync("token", response.data.token);
              uni.setStorageSync("userInfo", response.data);
              uni.showToast({
                title: "登录成功",
                icon: "success"
              });
              uni.switchTab({
                url: "/pages/index/index"
              });
            } else {
              uni.showToast({
                title: response.message || "登录失败",
                icon: "none"
              });
            }
          } catch (error) {
            uni.showToast({
              title: "网络错误，请稍后重试",
              icon: "none"
            });
          } finally {
            loading.value = false;
          }
        }
      };
      const forgotPassword = () => {
        uni.showToast({
          title: "忘记密码功能暂未实现",
          icon: "none"
        });
      };
      const goRegister = () => {
        uni.navigateTo({
          url: "/pages/login/register"
        });
      };
      const __returned__ = { loading, remember, defaultAvatar: defaultAvatar$1, avatarUrl, formData, errors, handleAccountInput, validateForm, submitForm, forgotPassword, goRegister, ref: vue.ref, reactive: vue.reactive, get login() {
        return login;
      } };
      Object.defineProperty(__returned__, "__isScriptSetup", { enumerable: false, value: true });
      return __returned__;
    }
  };
  function _sfc_render$2(_ctx, _cache, $props, $setup, $data, $options) {
    return vue.openBlock(), vue.createElementBlock("view", { class: "login-container" }, [
      vue.createElementVNode("view", { class: "login-background" }, [
        vue.createElementVNode("view", { class: "block" }),
        vue.createElementVNode("view", { class: "block" }),
        vue.createElementVNode("view", { class: "block" }),
        vue.createElementVNode("view", { class: "block" }),
        vue.createElementVNode("view", { class: "block" })
      ]),
      vue.createElementVNode("view", { class: "login-form" }, [
        vue.createElementVNode("view", { class: "logo" }, [
          vue.createElementVNode("text", { class: "logo-text" }, "Minecraft"),
          vue.createElementVNode("text", { class: "logo-subtext" }, "登录您的账户")
        ]),
        vue.createElementVNode("view", { class: "avatar-container" }, [
          vue.createElementVNode("view", { class: "avatar" }, [
            vue.createElementVNode("image", {
              src: $setup.avatarUrl,
              mode: "aspectFill",
              class: "avatar-img"
            }, null, 8, ["src"])
          ])
        ]),
        vue.createElementVNode("view", { class: "form-item" }, [
          vue.createElementVNode("view", { class: "input-wrapper" }, [
            vue.createElementVNode("text", { class: "input-icon" }, [
              (vue.openBlock(), vue.createElementBlock("svg", {
                t: "1772207429069",
                class: "icon",
                viewBox: "0 0 1024 1024",
                version: "1.1",
                xmlns: "http://www.w3.org/2000/svg",
                "p-id": "7501",
                width: "256",
                height: "256"
              }, [
                vue.createElementVNode("path", {
                  d: "M502.28224 338.432m-234.496 0a234.496 234.496 0 1 0 468.992 0 234.496 234.496 0 1 0-468.992 0Z",
                  fill: "#CAE4FF",
                  "p-id": "7502"
                }),
                vue.createElementVNode("path", {
                  d: "M492.288 609.792c-160.256-0.512-289.792-130.048-289.792-289.792 0-160.768 129.024-291.84 289.792-294.4 160.256 0.512 289.792 130.048 289.792 289.792 0.512 161.28-129.024 291.84-289.792 294.4z m0-500.224c-113.664 0-205.824 92.16-205.824 205.824 0 113.664 92.16 205.824 205.824 205.824s205.824-92.16 205.824-205.824c0-113.664-92.16-205.824-205.824-205.824z",
                  fill: "#0972E7",
                  "p-id": "7503"
                }),
                vue.createElementVNode("path", {
                  d: "M109.824 1013.248c-23.04 0-41.984-18.944-41.984-41.984v-16.896c1.024-236.544 192.512-427.52 428.544-428.544 23.04 0 41.984 18.944 41.984 41.984 0 23.04-18.944 41.984-41.984 41.984-189.952 1.024-343.552 154.624-344.576 344.576v12.8c0.512 24.064-17.92 44.032-41.984 46.08z",
                  fill: "#0972E7",
                  "p-id": "7504"
                }),
                vue.createElementVNode("path", {
                  d: "M874.752 1009.152c-22.016 1.536-40.448-15.36-41.984-37.376v-17.408c-1.024-189.952-154.624-343.552-344.576-344.576-23.04 0-41.984-18.944-41.984-41.984 0-23.04 18.944-41.984 41.984-41.984 236.544 1.024 427.52 192.512 428.544 428.544v12.8c1.536 22.016-15.36 40.448-37.376 41.984h-4.608z",
                  fill: "#0972E7",
                  "p-id": "7505"
                })
              ]))
            ]),
            vue.withDirectives(vue.createElementVNode(
              "input",
              {
                "onUpdate:modelValue": _cache[0] || (_cache[0] = ($event) => $setup.formData.account = $event),
                placeholder: "请输入账号",
                class: vue.normalizeClass(["form-input", { "error": $setup.errors.account }]),
                onInput: $setup.handleAccountInput
              },
              null,
              34
              /* CLASS, NEED_HYDRATION */
            ), [
              [vue.vModelText, $setup.formData.account]
            ])
          ]),
          $setup.errors.account ? (vue.openBlock(), vue.createElementBlock(
            "text",
            {
              key: 0,
              class: "error-message"
            },
            vue.toDisplayString($setup.errors.account),
            1
            /* TEXT */
          )) : vue.createCommentVNode("v-if", true)
        ]),
        vue.createElementVNode("view", { class: "form-item" }, [
          vue.createElementVNode("view", { class: "input-wrapper" }, [
            vue.createElementVNode("text", { class: "input-icon" }, [
              (vue.openBlock(), vue.createElementBlock("svg", {
                t: "1775143734296",
                class: "icon",
                viewBox: "0 0 1024 1024",
                version: "1.1",
                xmlns: "http://www.w3.org/2000/svg",
                "p-id": "12595",
                width: "256",
                height: "256"
              }, [
                vue.createElementVNode("path", {
                  d: "M511.2832 509.696m-432.3328 0a432.3328 432.3328 0 1 0 864.6656 0 432.3328 432.3328 0 1 0-864.6656 0Z",
                  fill: "#FF579C",
                  "p-id": "12596"
                }),
                vue.createElementVNode("path", {
                  d: "M286.3104 415.5904c-85.0432 0-160.4096 41.3696-207.104 105.0624 4.5568 182.7328 122.368 337.3056 285.952 396.032 103.2192-33.28 177.92-130.048 177.92-244.3776-0.0512-141.7728-114.9952-256.7168-256.768-256.7168z",
                  fill: "#FF80BA",
                  "p-id": "12597"
                }),
                vue.createElementVNode("path", {
                  d: "M408.7808 422.8096V358.7584c0-27.9552 22.7328-50.6368 50.6368-50.6368h103.68c27.9552 0 50.6368 22.7328 50.6368 50.6368v64.0512h61.44V358.7584c0-61.7984-50.2784-112.0768-112.0768-112.0768H459.4176c-61.7984 0-112.0768 50.2784-112.0768 112.0768v64.0512h61.44z",
                  fill: "#FFC7E3",
                  "p-id": "12598"
                }),
                vue.createElementVNode("path", {
                  d: "M668.8768 395.2128H353.6384c-48.9472 0-88.6272 39.68-88.6272 88.6272v186.0608c0 48.9472 39.68 88.6272 88.6272 88.6272h315.2384c48.9472 0 88.6272-39.68 88.6272-88.6272V483.84c0-48.9472-39.68-88.6272-88.6272-88.6272z m-130.1504 188.2112v64.512a27.4432 27.4432 0 0 1-54.8864 0v-64.512a55.31648 55.31648 0 0 1-27.9552-48.0768c0-30.6176 24.7808-55.3984 55.3984-55.3984s55.3984 24.7808 55.3984 55.3984c0 20.6336-11.264 38.5536-27.9552 48.0768z",
                  fill: "#FFFFFF",
                  "p-id": "12599"
                })
              ]))
            ]),
            vue.withDirectives(vue.createElementVNode(
              "input",
              {
                "onUpdate:modelValue": _cache[1] || (_cache[1] = ($event) => $setup.formData.password = $event),
                type: "password",
                placeholder: "请输入密码",
                class: vue.normalizeClass(["form-input", { "error": $setup.errors.password }])
              },
              null,
              2
              /* CLASS */
            ), [
              [vue.vModelText, $setup.formData.password]
            ])
          ]),
          $setup.errors.password ? (vue.openBlock(), vue.createElementBlock(
            "text",
            {
              key: 0,
              class: "error-message"
            },
            vue.toDisplayString($setup.errors.password),
            1
            /* TEXT */
          )) : vue.createCommentVNode("v-if", true)
        ]),
        vue.createElementVNode("view", { class: "form-item" }, [
          vue.createElementVNode("view", { class: "checkbox-group" }, [
            vue.createElementVNode(
              "view",
              {
                class: vue.normalizeClass(["custom-checkbox", { "checked": $setup.remember }]),
                onClick: _cache[2] || (_cache[2] = ($event) => $setup.remember = !$setup.remember)
              },
              [
                $setup.remember ? (vue.openBlock(), vue.createElementBlock("text", {
                  key: 0,
                  class: "checkbox-check"
                }, "✓")) : vue.createCommentVNode("v-if", true)
              ],
              2
              /* CLASS */
            ),
            vue.createElementVNode("text", { class: "checkbox-label" }, "记住我")
          ]),
          vue.createElementVNode("text", {
            class: "forgot-password",
            onClick: $setup.forgotPassword
          }, "忘记密码？")
        ]),
        vue.createElementVNode("view", { class: "form-item" }, [
          vue.createElementVNode("button", {
            loading: $setup.loading,
            onClick: $setup.submitForm,
            class: "login-button"
          }, [
            vue.createElementVNode("text", { class: "button-text" }, "登录")
          ], 8, ["loading"])
        ]),
        vue.createElementVNode("view", { class: "register-link" }, [
          vue.createElementVNode("text", null, "还没有账号？"),
          vue.createElementVNode("text", {
            class: "link",
            onClick: $setup.goRegister
          }, "立即注册")
        ])
      ])
    ]);
  }
  const PagesLoginLogin = /* @__PURE__ */ _export_sfc(_sfc_main$3, [["render", _sfc_render$2], ["__scopeId", "data-v-e4e4508d"], ["__file", "D:/IDEA/work/Minecraft-system/minecraft-App/pages/login/login.vue"]]);
  const defaultAvatar = "https://img.icons8.com/ios-filled/50/000000/user.png";
  const _sfc_main$2 = {
    __name: "register",
    setup(__props, { expose: __expose }) {
      __expose();
      const loading = vue.ref(false);
      const formData = vue.reactive({
        username: "",
        password: "",
        confirmPassword: "",
        email: "",
        phone: "",
        avatar: ""
      });
      const errors = vue.reactive({
        username: "",
        password: "",
        confirmPassword: "",
        email: "",
        phone: "",
        avatar: ""
      });
      const handleAvatarUpload = (event) => {
        const file = event.target.files[0];
        if (file) {
          const reader = new FileReader();
          reader.onload = (e) => {
            formData.avatar = e.target.result;
          };
          reader.readAsDataURL(file);
        }
      };
      const validateForm = () => {
        let isValid = true;
        errors.username = "";
        errors.password = "";
        errors.confirmPassword = "";
        errors.email = "";
        errors.phone = "";
        errors.avatar = "";
        if (!formData.username.trim()) {
          errors.username = "请输入用户名";
          isValid = false;
        } else if (formData.username.length < 3 || formData.username.length > 20) {
          errors.username = "用户名长度必须在3-20位之间";
          isValid = false;
        }
        if (!formData.password.trim()) {
          errors.password = "请输入密码";
          isValid = false;
        } else if (formData.password.length < 6 || formData.password.length > 20) {
          errors.password = "密码长度必须在6-20位之间";
          isValid = false;
        }
        if (!formData.confirmPassword.trim()) {
          errors.confirmPassword = "请确认密码";
          isValid = false;
        } else if (formData.confirmPassword !== formData.password) {
          errors.confirmPassword = "两次输入密码不一致";
          isValid = false;
        }
        if (!formData.email.trim()) {
          errors.email = "请输入邮箱";
          isValid = false;
        } else {
          const emailRegex = /^[^@]+@[^@]+\.[^@]+$/;
          if (!emailRegex.test(formData.email)) {
            errors.email = "请输入正确的邮箱格式";
            isValid = false;
          }
        }
        if (!formData.phone.trim()) {
          errors.phone = "请输入手机号";
          isValid = false;
        }
        if (!formData.avatar) {
          errors.avatar = "请上传头像";
          isValid = false;
        }
        return isValid;
      };
      const submitForm = async () => {
        if (validateForm()) {
          loading.value = true;
          try {
            const registerData = {
              username: formData.username,
              password: formData.password,
              email: formData.email,
              phone: formData.phone,
              avatar: formData.avatar
            };
            const response = await register(registerData);
            if (response.code === 200) {
              uni.showToast({
                title: "注册成功",
                icon: "success"
              });
              uni.navigateTo({
                url: "/pages/login/login"
              });
            } else {
              uni.showToast({
                title: response.message || "注册失败",
                icon: "none"
              });
            }
          } catch (error) {
            uni.showToast({
              title: "网络错误，请稍后重试",
              icon: "none"
            });
          } finally {
            loading.value = false;
          }
        }
      };
      const goLogin = () => {
        uni.navigateTo({
          url: "/pages/login/login"
        });
      };
      const __returned__ = { loading, defaultAvatar, formData, errors, handleAvatarUpload, validateForm, submitForm, goLogin, ref: vue.ref, reactive: vue.reactive, get register() {
        return register;
      } };
      Object.defineProperty(__returned__, "__isScriptSetup", { enumerable: false, value: true });
      return __returned__;
    }
  };
  function _sfc_render$1(_ctx, _cache, $props, $setup, $data, $options) {
    return vue.openBlock(), vue.createElementBlock("view", { class: "register-container" }, [
      vue.createElementVNode("view", { class: "login-background" }, [
        vue.createElementVNode("view", { class: "block" }),
        vue.createElementVNode("view", { class: "block" }),
        vue.createElementVNode("view", { class: "block" }),
        vue.createElementVNode("view", { class: "block" }),
        vue.createElementVNode("view", { class: "block" })
      ]),
      vue.createElementVNode("view", { class: "register-form" }, [
        vue.createElementVNode("view", { class: "logo" }, [
          vue.createElementVNode("text", { class: "logo-text" }, "Minecraft"),
          vue.createElementVNode("text", { class: "logo-subtext" }, "创建新账户")
        ]),
        vue.createElementVNode("view", { class: "avatar-upload-container" }, [
          vue.createElementVNode(
            "view",
            {
              class: "avatar-preview",
              style: vue.normalizeStyle({ backgroundImage: $setup.formData.avatar ? `url(${$setup.formData.avatar})` : `url(${$setup.defaultAvatar})` })
            },
            [
              vue.createElementVNode("label", {
                for: "avatar-upload",
                class: "avatar-upload-label"
              }, [
                !$setup.formData.avatar ? (vue.openBlock(), vue.createElementBlock("text", { key: 0 }, "点击上传头像")) : (vue.openBlock(), vue.createElementBlock("text", { key: 1 }, "更换头像")),
                vue.createElementVNode(
                  "input",
                  {
                    id: "avatar-upload",
                    type: "file",
                    accept: "image/*",
                    onChange: $setup.handleAvatarUpload,
                    class: "avatar-upload"
                  },
                  null,
                  32
                  /* NEED_HYDRATION */
                )
              ])
            ],
            4
            /* STYLE */
          ),
          $setup.errors.avatar ? (vue.openBlock(), vue.createElementBlock(
            "text",
            {
              key: 0,
              class: "error-message"
            },
            vue.toDisplayString($setup.errors.avatar),
            1
            /* TEXT */
          )) : vue.createCommentVNode("v-if", true)
        ]),
        vue.createElementVNode("view", { class: "form-item" }, [
          vue.createElementVNode("view", { class: "input-wrapper" }, [
            vue.createElementVNode("text", { class: "input-icon" }, [
              (vue.openBlock(), vue.createElementBlock("svg", {
                t: "1772207429069",
                class: "icon",
                viewBox: "0 0 1024 1024",
                version: "1.1",
                xmlns: "http://www.w3.org/2000/svg",
                "p-id": "7501",
                width: "256",
                height: "256"
              }, [
                vue.createElementVNode("path", {
                  d: "M502.28224 338.432m-234.496 0a234.496 234.496 0 1 0 468.992 0 234.496 234.496 0 1 0-468.992 0Z",
                  fill: "#CAE4FF",
                  "p-id": "7502"
                }),
                vue.createElementVNode("path", {
                  d: "M492.288 609.792c-160.256-0.512-289.792-130.048-289.792-289.792 0-160.768 129.024-291.84 289.792-294.4 160.256 0.512 289.792 130.048 289.792 289.792 0.512 161.28-129.024 291.84-289.792 294.4z m0-500.224c-113.664 0-205.824 92.16-205.824 205.824 0 113.664 92.16 205.824 205.824 205.824s205.824-92.16 205.824-205.824c0-113.664-92.16-205.824-205.824-205.824z",
                  fill: "#0972E7",
                  "p-id": "7503"
                }),
                vue.createElementVNode("path", {
                  d: "M109.824 1013.248c-23.04 0-41.984-18.944-41.984-41.984v-16.896c1.024-236.544 192.512-427.52 428.544-428.544 23.04 0 41.984 18.944 41.984 41.984 0 23.04-18.944 41.984-41.984 41.984-189.952 1.024-343.552 154.624-344.576 344.576v12.8c0.512 24.064-17.92 44.032-41.984 46.08z",
                  fill: "#0972E7",
                  "p-id": "7504"
                }),
                vue.createElementVNode("path", {
                  d: "M874.752 1009.152c-22.016 1.536-40.448-15.36-41.984-37.376v-17.408c-1.024-189.952-154.624-343.552-344.576-344.576-23.04 0-41.984-18.944-41.984-41.984 0-23.04 18.944-41.984 41.984-41.984 236.544 1.024 427.52 192.512 428.544 428.544v12.8c1.536 22.016-15.36 40.448-37.376 41.984h-4.608z",
                  fill: "#0972E7",
                  "p-id": "7505"
                })
              ]))
            ]),
            vue.withDirectives(vue.createElementVNode(
              "input",
              {
                "onUpdate:modelValue": _cache[0] || (_cache[0] = ($event) => $setup.formData.username = $event),
                placeholder: "请输入用户名",
                class: vue.normalizeClass(["form-input", { "error": $setup.errors.username }])
              },
              null,
              2
              /* CLASS */
            ), [
              [vue.vModelText, $setup.formData.username]
            ])
          ]),
          $setup.errors.username ? (vue.openBlock(), vue.createElementBlock(
            "text",
            {
              key: 0,
              class: "error-message"
            },
            vue.toDisplayString($setup.errors.username),
            1
            /* TEXT */
          )) : vue.createCommentVNode("v-if", true)
        ]),
        vue.createElementVNode("view", { class: "form-item" }, [
          vue.createElementVNode("view", { class: "input-wrapper" }, [
            vue.createElementVNode("text", { class: "input-icon" }, [
              (vue.openBlock(), vue.createElementBlock("svg", {
                t: "1775143734296",
                class: "icon",
                viewBox: "0 0 1024 1024",
                version: "1.1",
                xmlns: "http://www.w3.org/2000/svg",
                "p-id": "12595",
                width: "256",
                height: "256"
              }, [
                vue.createElementVNode("path", {
                  d: "M511.2832 509.696m-432.3328 0a432.3328 432.3328 0 1 0 864.6656 0 432.3328 432.3328 0 1 0-864.6656 0Z",
                  fill: "#FF579C",
                  "p-id": "12596"
                }),
                vue.createElementVNode("path", {
                  d: "M286.3104 415.5904c-85.0432 0-160.4096 41.3696-207.104 105.0624 4.5568 182.7328 122.368 337.3056 285.952 396.032 103.2192-33.28 177.92-130.048 177.92-244.3776-0.0512-141.7728-114.9952-256.7168-256.768-256.7168z",
                  fill: "#FF80BA",
                  "p-id": "12597"
                }),
                vue.createElementVNode("path", {
                  d: "M408.7808 422.8096V358.7584c0-27.9552 22.7328-50.6368 50.6368-50.6368h103.68c27.9552 0 50.6368 22.7328 50.6368 50.6368v64.0512h61.44V358.7584c0-61.7984-50.2784-112.0768-112.0768-112.0768H459.4176c-61.7984 0-112.0768 50.2784-112.0768 112.0768v64.0512h61.44z",
                  fill: "#FFC7E3",
                  "p-id": "12598"
                }),
                vue.createElementVNode("path", {
                  d: "M668.8768 395.2128H353.6384c-48.9472 0-88.6272 39.68-88.6272 88.6272v186.0608c0 48.9472 39.68 88.6272 88.6272 88.6272h315.2384c48.9472 0 88.6272-39.68 88.6272-88.6272V483.84c0-48.9472-39.68-88.6272-88.6272-88.6272z m-130.1504 188.2112v64.512a27.4432 27.4432 0 0 1-54.8864 0v-64.512a55.31648 55.31648 0 0 1-27.9552-48.0768c0-30.6176 24.7808-55.3984 55.3984-55.3984s55.3984 24.7808 55.3984 55.3984c0 20.6336-11.264 38.5536-27.9552 48.0768z",
                  fill: "#FFFFFF",
                  "p-id": "12599"
                })
              ]))
            ]),
            vue.withDirectives(vue.createElementVNode(
              "input",
              {
                "onUpdate:modelValue": _cache[1] || (_cache[1] = ($event) => $setup.formData.password = $event),
                type: "password",
                placeholder: "请输入密码",
                class: vue.normalizeClass(["form-input", { "error": $setup.errors.password }])
              },
              null,
              2
              /* CLASS */
            ), [
              [vue.vModelText, $setup.formData.password]
            ])
          ]),
          $setup.errors.password ? (vue.openBlock(), vue.createElementBlock(
            "text",
            {
              key: 0,
              class: "error-message"
            },
            vue.toDisplayString($setup.errors.password),
            1
            /* TEXT */
          )) : vue.createCommentVNode("v-if", true)
        ]),
        vue.createElementVNode("view", { class: "form-item" }, [
          vue.createElementVNode("view", { class: "input-wrapper" }, [
            vue.createElementVNode("text", { class: "input-icon" }, [
              (vue.openBlock(), vue.createElementBlock("svg", {
                t: "1775143734296",
                class: "icon",
                viewBox: "0 0 1024 1024",
                version: "1.1",
                xmlns: "http://www.w3.org/2000/svg",
                "p-id": "12595",
                width: "256",
                height: "256"
              }, [
                vue.createElementVNode("path", {
                  d: "M511.2832 509.696m-432.3328 0a432.3328 432.3328 0 1 0 864.6656 0 432.3328 432.3328 0 1 0-864.6656 0Z",
                  fill: "#FF579C",
                  "p-id": "12596"
                }),
                vue.createElementVNode("path", {
                  d: "M286.3104 415.5904c-85.0432 0-160.4096 41.3696-207.104 105.0624 4.5568 182.7328 122.368 337.3056 285.952 396.032 103.2192-33.28 177.92-130.048 177.92-244.3776-0.0512-141.7728-114.9952-256.7168-256.768-256.7168z",
                  fill: "#FF80BA",
                  "p-id": "12597"
                }),
                vue.createElementVNode("path", {
                  d: "M408.7808 422.8096V358.7584c0-27.9552 22.7328-50.6368 50.6368-50.6368h103.68c27.9552 0 50.6368 22.7328 50.6368 50.6368v64.0512h61.44V358.7584c0-61.7984-50.2784-112.0768-112.0768-112.0768H459.4176c-61.7984 0-112.0768 50.2784-112.0768 112.0768v64.0512h61.44z",
                  fill: "#FFC7E3",
                  "p-id": "12598"
                }),
                vue.createElementVNode("path", {
                  d: "M668.8768 395.2128H353.6384c-48.9472 0-88.6272 39.68-88.6272 88.6272v186.0608c0 48.9472 39.68 88.6272 88.6272 88.6272h315.2384c48.9472 0 88.6272-39.68 88.6272-88.6272V483.84c0-48.9472-39.68-88.6272-88.6272-88.6272z m-130.1504 188.2112v64.512a27.4432 27.4432 0 0 1-54.8864 0v-64.512a55.31648 55.31648 0 0 1-27.9552-48.0768c0-30.6176 24.7808-55.3984 55.3984-55.3984s55.3984 24.7808 55.3984 55.3984c0 20.6336-11.264 38.5536-27.9552 48.0768z",
                  fill: "#FFFFFF",
                  "p-id": "12599"
                })
              ]))
            ]),
            vue.withDirectives(vue.createElementVNode(
              "input",
              {
                "onUpdate:modelValue": _cache[2] || (_cache[2] = ($event) => $setup.formData.confirmPassword = $event),
                type: "password",
                placeholder: "请确认密码",
                class: vue.normalizeClass(["form-input", { "error": $setup.errors.confirmPassword }])
              },
              null,
              2
              /* CLASS */
            ), [
              [vue.vModelText, $setup.formData.confirmPassword]
            ])
          ]),
          $setup.errors.confirmPassword ? (vue.openBlock(), vue.createElementBlock(
            "text",
            {
              key: 0,
              class: "error-message"
            },
            vue.toDisplayString($setup.errors.confirmPassword),
            1
            /* TEXT */
          )) : vue.createCommentVNode("v-if", true)
        ]),
        vue.createElementVNode("view", { class: "form-item" }, [
          vue.createElementVNode("view", { class: "input-wrapper" }, [
            vue.createElementVNode("text", { class: "input-icon" }, [
              (vue.openBlock(), vue.createElementBlock("svg", {
                t: "1775143701530",
                class: "icon",
                viewBox: "0 0 1024 1024",
                version: "1.1",
                xmlns: "http://www.w3.org/2000/svg",
                "p-id": "10618",
                width: "256",
                height: "256"
              }, [
                vue.createElementVNode("path", {
                  d: "M407 170h358.6c64.6 0 117.4 52.8 117.4 117.4v362.7c0 64.6-52.8 117.4-117.4 117.4H407c-64.6 0-117.4-52.8-117.4-117.4V287.4c0-64.6 52.9-117.4 117.4-117.4z",
                  fill: "#22B229",
                  "p-id": "10619"
                }),
                vue.createElementVNode("path", {
                  d: "M363.9 170h288.9c64.6 0 117.4 52.8 117.4 117.4v362.7c0 64.6-52.8 117.4-117.4 117.4H363.9c-64.6 0-117.4-52.8-117.4-117.4V287.4c0-64.6 52.9-117.4 117.4-117.4z",
                  fill: "#42DA56",
                  "p-id": "10620"
                }),
                vue.createElementVNode("path", {
                  d: "M304.3 311h-0.1c-8.3 0-15-6.7-15-15s6.7-15 15-15h0.1c8.3 0 15 6.7 15 15s-6.7 15-15 15zM327 270c-2.8 0-5.6-0.8-8.1-2.4-7-4.5-9-13.8-4.5-20.7 12.8-19.9 33.8-31.7 34.7-32.2 7.2-4 16.4-1.4 20.4 5.9 4 7.2 1.4 16.3-5.8 20.4-0.3 0.2-15.4 8.8-24 22.1-2.9 4.5-7.7 6.9-12.7 6.9z",
                  fill: "#FFFFFF",
                  "p-id": "10621"
                }),
                vue.createElementVNode("path", {
                  d: "M708.7 782.5h-418c-73 0-132.4-59.4-132.4-132.4V557c0-8.3 6.7-15 15-15s15 6.7 15 15v93.1c0 56.5 45.9 102.4 102.4 102.4h417.9c56.5 0 102.4-45.9 102.4-102.4 0-8.3 6.7-15 15-15s15 6.7 15 15c0.1 73-59.3 132.4-132.3 132.4zM826.1 595c-8.3 0-15-6.7-15-15V397c0-8.3 6.7-15 15-15s15 6.7 15 15v183c0 8.3-6.8 15-15 15zM826.1 372c-8.3 0-15-6.7-15-15v-18c0-8.3 6.7-15 15-15s15 6.7 15 15v18c0 8.3-6.8 15-15 15zM173.3 512c-8.3 0-15-6.7-15-15V287.4c0-73 59.4-132.4 132.4-132.4h417.9c73 0 132.4 59.4 132.4 132.4V301c0 8.3-6.7 15-15 15s-15-6.7-15-15v-13.6c0-56.5-45.9-102.4-102.4-102.4H290.7c-56.5 0-102.4 45.9-102.4 102.4V497c0 8.3-6.7 15-15 15z",
                  fill: "#1A1A1A",
                  "p-id": "10622"
                }),
                vue.createElementVNode("path", {
                  d: "M562.3 595c-8.3 0-15-6.7-15-15v-70c0-8.3 6.7-15 15-15s15 6.7 15 15v70c0 8.3-6.7 15-15 15zM377.3 557c-8.3 0-15-6.7-15-15v-16c0-8.3 6.7-15 15-15s15 6.7 15 15v16c0 8.3-6.7 15-15 15zM377.3 502c-8.3 0-15-6.7-15-15v-88c0-14.3 5.6-27.6 15.7-37.7 10-9.9 23.2-15.3 37.3-15.3h133c8.3 0 15 6.7 15 15s-6.7 15-15 15h-133c-6.1 0-11.8 2.4-16.2 6.7-4.4 4.4-6.8 10.2-6.8 16.3v88c0 8.3-6.7 15-15 15z",
                  fill: "#FFFFFF",
                  "p-id": "10623"
                }),
                vue.createElementVNode("path", {
                  d: "M593.3 596H563c-8.3 0-15-6.7-15-15s6.7-15 15-15h30.4c12.7 0 23-10.3 23-23V399c0-12.7-10.3-23-23-23-8.3 0-15-6.7-15-15s6.7-15 15-15c29.2 0 53 23.8 53 53v144c-0.1 29.2-23.8 53-53.1 53z",
                  fill: "#FFFFFF",
                  "p-id": "10624"
                }),
                vue.createElementVNode("path", {
                  d: "M538.8 553.9H472c-21.2 0-38.5-17.3-38.5-38.5v-66.9c0-21.2 17.3-38.5 38.5-38.5h66.9c21.2 0 38.5 17.3 38.5 38.5v66.9c-0.1 21.2-17.3 38.5-38.6 38.5zM472 440c-4.6 0-8.5 3.9-8.5 8.5v66.9c0 4.6 3.9 8.5 8.5 8.5h66.9c4.6 0 8.5-3.9 8.5-8.5v-66.9c0-4.6-3.9-8.5-8.5-8.5H472z",
                  fill: "#FFFFFF",
                  "p-id": "10625"
                }),
                vue.createElementVNode("path", {
                  d: "M995 869h-20.4c-8.3 0-15-6.7-15-15s6.7-15 15-15H995c8.3 0 15 6.7 15 15s-6.7 15-15 15zM925 869H491.8c-8.3 0-15-6.7-15-15s6.7-15 15-15H925c8.3 0 15 6.7 15 15s-6.7 15-15 15z",
                  fill: "#1A1A1A",
                  "p-id": "10626"
                }),
                vue.createElementVNode("path", {
                  d: "M523.4 869H261c-8.3 0-15-6.7-15-15s6.7-15 15-15h262.4c8.3 0 15 6.7 15 15s-6.7 15-15 15zM162 869H44.3c-8.3 0-15-6.7-15-15s6.7-15 15-15H162c8.3 0 15 6.7 15 15s-6.8 15-15 15z",
                  fill: "#1A1A1A",
                  "p-id": "10627"
                }),
                vue.createElementVNode("path", {
                  d: "M40.8 298.7c-0.8 0-1.5-0.7-1.5-1.5v-38.7c0-0.8 0.7-1.5 1.5-1.5s1.5 0.7 1.5 1.5v38.7c0 0.8-0.7 1.5-1.5 1.5z",
                  fill: "#F74E00",
                  "p-id": "10628"
                }),
                vue.createElementVNode("path", {
                  d: "M28.7 293.3c-0.4 0-0.7-0.1-1-0.4-0.6-0.6-0.7-1.5-0.1-2.1l25.1-27.4c0.6-0.6 1.5-0.7 2.1-0.1 0.6 0.6 0.7 1.5 0.1 2.1l-25.1 27.4c-0.3 0.3-0.7 0.5-1.1 0.5z",
                  fill: "#F74E00",
                  "p-id": "10629"
                }),
                vue.createElementVNode("path", {
                  d: "M53.9 293c-0.4 0-0.8-0.2-1.1-0.5l-25.1-27.4c-0.6-0.6-0.5-1.6 0.1-2.1 0.6-0.6 1.6-0.5 2.1 0.1L55 290.5c0.6 0.6 0.5 1.6-0.1 2.1-0.2 0.3-0.6 0.4-1 0.4z",
                  fill: "#F74E00",
                  "p-id": "10630"
                }),
                vue.createElementVNode("path", {
                  d: "M58.6 279.3H23c-0.8 0-1.5-0.7-1.5-1.5s0.7-1.5 1.5-1.5h35.6c0.8 0 1.5 0.7 1.5 1.5s-0.7 1.5-1.5 1.5z",
                  fill: "#F74E00",
                  "p-id": "10631"
                }),
                vue.createElementVNode("path", {
                  d: "M107.1 460.5h-7.4c-1.4 0-2.5-1.1-2.5-2.5s1.1-2.5 2.5-2.5h7.4c1.4 0 2.5 1.1 2.5 2.5s-1.2 2.5-2.5 2.5zM83.3 460.5h-7.4c-1.4 0-2.5-1.1-2.5-2.5s1.1-2.5 2.5-2.5h7.4c1.4 0 2.5 1.1 2.5 2.5s-1.1 2.5-2.5 2.5zM85.6 454.4c-0.7 0-1.3-0.3-1.8-0.8l-5.3-5.7c-0.9-1-0.9-2.6 0.2-3.5 1-0.9 2.6-0.9 3.5 0.2l5.3 5.7c0.9 1 0.9 2.6-0.2 3.5-0.5 0.3-1.1 0.6-1.7 0.6zM103.6 472.7c-0.7 0-1.3-0.3-1.8-0.8l-5.3-5.7c-0.9-1-0.9-2.6 0.2-3.5 1-0.9 2.6-0.9 3.5 0.2l5.3 5.7c0.9 1 0.9 2.6-0.2 3.5-0.5 0.4-1.1 0.6-1.7 0.6zM98.3 453.8c-0.6 0-1.2-0.2-1.7-0.7-1-0.9-1.1-2.5-0.2-3.5l5.3-5.7c0.9-1 2.5-1.1 3.5-0.2 1 0.9 1.1 2.5 0.2 3.5l-5.3 5.7c-0.4 0.6-1.1 0.9-1.8 0.9zM91.7 453.6c-1.4 0-2.5-1.1-2.5-2.5V443c0-1.4 1.1-2.5 2.5-2.5s2.5 1.1 2.5 2.5v8.1c0 1.4-1.1 2.5-2.5 2.5zM91.7 476.7c-1.4 0-2.5-1.1-2.5-2.5v-8.1c0-1.4 1.1-2.5 2.5-2.5s2.5 1.1 2.5 2.5v8.1c0 1.4-1.1 2.5-2.5 2.5zM80.5 472.6c-0.6 0-1.2-0.2-1.7-0.7-1-0.9-1.1-2.5-0.2-3.5l5.3-5.7c0.9-1 2.5-1.1 3.5-0.2 1 0.9 1.1 2.5 0.2 3.5l-5.3 5.7c-0.5 0.6-1.1 0.9-1.8 0.9z",
                  fill: "#42DA56",
                  "p-id": "10632"
                }),
                vue.createElementVNode("path", {
                  d: "M118.7 259h-4.2c-3.1 0-5.7-2.5-5.7-5.7v-1c0-3.1 2.5-5.7 5.7-5.7h4.2c3.1 0 5.7 2.5 5.7 5.7v1c-0.1 3.2-2.6 5.7-5.7 5.7z",
                  fill: "#3268CF",
                  "p-id": "10633"
                }),
                vue.createElementVNode("path", {
                  d: "M77.1 581c-2.5 0-4.5-2-4.5-4.5v-0.8c0-2.5 2-4.5 4.5-4.5s4.5 2 4.5 4.5v0.8c-0.1 2.5-2.1 4.5-4.5 4.5z",
                  fill: "#F74E00",
                  "p-id": "10634"
                }),
                vue.createElementVNode("path", {
                  d: "M43.6 389c-7.3 0-13.2-6-13.2-13.2v-2.4c0-7.3 6-13.2 13.2-13.2 7.3 0 13.2 6 13.2 13.2v2.4c0 7.2-5.9 13.2-13.2 13.2z",
                  fill: "#42DA56",
                  "p-id": "10635"
                }),
                vue.createElementVNode("path", {
                  d: "M993.7 434.8h-6.2c-5.4 0-9.8-4.4-9.8-9.8v-1.3c0-5.4 4.4-9.8 9.8-9.8h6.2c5.4 0 9.8 4.4 9.8 9.8v1.3c0 5.4-4.4 9.8-9.8 9.8z m-6.2-15.9c-2.6 0-4.8 2.2-4.8 4.8v1.3c0 2.6 2.2 4.8 4.8 4.8h6.2c2.6 0 4.8-2.2 4.8-4.8v-1.3c0-2.6-2.2-4.8-4.8-4.8h-6.2z",
                  fill: "#42DA56",
                  "p-id": "10636"
                }),
                vue.createElementVNode("path", {
                  d: "M944 364.9c-0.8 0-1.5-0.7-1.5-1.5V340c0-0.8 0.7-1.5 1.5-1.5s1.5 0.7 1.5 1.5v23.4c0 0.8-0.6 1.5-1.5 1.5z",
                  fill: "#0000FF",
                  "p-id": "10637"
                }),
                vue.createElementVNode("path", {
                  d: "M936.8 361.7c-0.4 0-0.7-0.1-1-0.4-0.6-0.6-0.7-1.5-0.1-2.1l15.2-16.6c0.6-0.6 1.5-0.7 2.1-0.1 0.6 0.6 0.7 1.5 0.1 2.1l-15.2 16.6c-0.3 0.3-0.7 0.5-1.1 0.5z",
                  fill: "#0000FF",
                  "p-id": "10638"
                }),
                vue.createElementVNode("path", {
                  d: "M952 361.5c-0.4 0-0.8-0.2-1.1-0.5l-15.2-16.6c-0.6-0.6-0.5-1.6 0.1-2.1 0.6-0.6 1.6-0.5 2.1 0.1l15.2 16.6c0.6 0.6 0.5 1.6-0.1 2.1-0.3 0.3-0.6 0.4-1 0.4z",
                  fill: "#0000FF",
                  "p-id": "10639"
                }),
                vue.createElementVNode("path", {
                  d: "M954.8 353.2h-21.5c-0.8 0-1.5-0.7-1.5-1.5s0.7-1.5 1.5-1.5h21.5c0.8 0 1.5 0.7 1.5 1.5s-0.7 1.5-1.5 1.5z",
                  fill: "#0000FF",
                  "p-id": "10640"
                }),
                vue.createElementVNode("path", {
                  d: "M996.2 521.2h-7.4c-1.4 0-2.5-1.1-2.5-2.5s1.1-2.5 2.5-2.5h7.4c1.4 0 2.5 1.1 2.5 2.5s-1.1 2.5-2.5 2.5zM972.4 521.2H965c-1.4 0-2.5-1.1-2.5-2.5s1.1-2.5 2.5-2.5h7.4c1.4 0 2.5 1.1 2.5 2.5s-1.1 2.5-2.5 2.5zM974.7 515c-0.7 0-1.3-0.3-1.8-0.8l-5.3-5.7c-0.9-1-0.9-2.6 0.2-3.5 1-0.9 2.6-0.9 3.5 0.2l5.3 5.7c0.9 1 0.9 2.6-0.2 3.5-0.5 0.4-1.1 0.6-1.7 0.6zM992.8 533.4c-0.7 0-1.3-0.3-1.8-0.8l-5.3-5.7c-0.9-1-0.9-2.6 0.2-3.5 1-0.9 2.6-0.9 3.5 0.2l5.3 5.7c0.9 1 0.9 2.6-0.2 3.5-0.5 0.4-1.1 0.6-1.7 0.6zM987.5 514.5c-0.6 0-1.2-0.2-1.7-0.7-1-0.9-1.1-2.5-0.2-3.5l5.3-5.7c0.9-1 2.5-1.1 3.5-0.2 1 0.9 1.1 2.5 0.2 3.5l-5.3 5.7c-0.4 0.6-1.1 0.9-1.8 0.9zM980.9 514.2c-1.4 0-2.5-1.1-2.5-2.5v-8.1c0-1.4 1.1-2.5 2.5-2.5s2.5 1.1 2.5 2.5v8.1c0 1.4-1.2 2.5-2.5 2.5zM980.9 537.4c-1.4 0-2.5-1.1-2.5-2.5v-8.1c0-1.4 1.1-2.5 2.5-2.5s2.5 1.1 2.5 2.5v8.1c0 1.3-1.2 2.5-2.5 2.5zM969.7 533.3c-0.6 0-1.2-0.2-1.7-0.7-1-0.9-1.1-2.5-0.2-3.5l5.3-5.7c0.9-1 2.5-1.1 3.5-0.2s1.1 2.5 0.2 3.5l-5.3 5.7c-0.5 0.6-1.2 0.9-1.8 0.9z",
                  fill: "#F74E00",
                  "p-id": "10641"
                }),
                vue.createElementVNode("path", {
                  d: "M932.7 603h-4.3c-1.2 0-2.1-1-2.1-2.1v-0.4c0-1.2 1-2.1 2.1-2.1h4.3c1.2 0 2.1 1 2.1 2.1v0.4c0 1.1-1 2.1-2.1 2.1z",
                  fill: "#3268CF",
                  "p-id": "10642"
                }),
                vue.createElementVNode("path", {
                  d: "M933.9 487.7h-9.3c-4 0-7.2-3.2-7.2-7.2v-0.8c0-4 3.2-7.2 7.2-7.2h9.3c4 0 7.2 3.2 7.2 7.2v0.8c0 4-3.3 7.2-7.2 7.2z m-9.4-10.2c-1.2 0-2.2 1-2.2 2.2v0.8c0 1.2 1 2.2 2.2 2.2h9.3c1.2 0 2.2-1 2.2-2.2v-0.8c0-1.2-1-2.2-2.2-2.2h-9.3z",
                  fill: "#F74E00",
                  "p-id": "10643"
                }),
                vue.createElementVNode("path", {
                  d: "M922.3 275.3a5.7 6.3 0 1 0 11.4 0 5.7 6.3 0 1 0-11.4 0Z",
                  fill: "#3268CF",
                  "p-id": "10644"
                })
              ]))
            ]),
            vue.withDirectives(vue.createElementVNode(
              "input",
              {
                "onUpdate:modelValue": _cache[3] || (_cache[3] = ($event) => $setup.formData.email = $event),
                placeholder: "请输入邮箱",
                class: vue.normalizeClass(["form-input", { "error": $setup.errors.email }])
              },
              null,
              2
              /* CLASS */
            ), [
              [vue.vModelText, $setup.formData.email]
            ])
          ]),
          $setup.errors.email ? (vue.openBlock(), vue.createElementBlock(
            "text",
            {
              key: 0,
              class: "error-message"
            },
            vue.toDisplayString($setup.errors.email),
            1
            /* TEXT */
          )) : vue.createCommentVNode("v-if", true)
        ]),
        vue.createElementVNode("view", { class: "form-item" }, [
          vue.createElementVNode("view", { class: "input-wrapper" }, [
            vue.createElementVNode("text", { class: "input-icon" }, [
              (vue.openBlock(), vue.createElementBlock("svg", {
                t: "1775143669773",
                class: "icon",
                viewBox: "0 0 1024 1024",
                version: "1.1",
                xmlns: "http://www.w3.org/2000/svg",
                "p-id": "9464",
                width: "256",
                height: "256"
              }, [
                vue.createElementVNode("path", {
                  d: "M191.6 83.4c-47.4 22.4-85.8 60.8-108.2 108.2L191.6 83.4z",
                  fill: "#4ADF9E",
                  "p-id": "9465"
                }),
                vue.createElementVNode("path", {
                  d: "M228.8 69.7C151.6 90.5 90.5 151.6 69.7 228.8L228.8 69.7z",
                  fill: "#49DF9E",
                  "p-id": "9466"
                }),
                vue.createElementVNode("path", {
                  d: "M258.2 63.9c-100.8 13-181.3 93.5-194.4 194.4L258.2 63.9z",
                  fill: "#48DF9F",
                  "p-id": "9467"
                }),
                vue.createElementVNode("path", {
                  d: "M283.6 62C162.5 63.9 63.9 162.5 62 283.6L283.6 62z",
                  fill: "#47E09F",
                  "p-id": "9468"
                }),
                vue.createElementVNode("path", {
                  d: "M307.2 62H287C163.3 62 62 163.3 62 287v20.2L307.2 62z",
                  fill: "#46E09F",
                  "p-id": "9469"
                }),
                vue.createElementVNode("path", {
                  d: "M330.7 62H287C163.3 62 62 163.3 62 287v43.7L330.7 62z",
                  fill: "#45E09F",
                  "p-id": "9470"
                }),
                vue.createElementVNode("path", {
                  d: "M354.2 62H287C163.3 62 62 163.3 62 287v67.2L354.2 62z",
                  fill: "#44E0A0",
                  "p-id": "9471"
                }),
                vue.createElementVNode("path", {
                  d: "M377.8 62H287C163.3 62 62 163.3 62 287v90.8L377.8 62z",
                  fill: "#43E0A0",
                  "p-id": "9472"
                }),
                vue.createElementVNode("path", {
                  d: "M401.3 62H287C163.3 62 62 163.3 62 287v114.3L401.3 62z",
                  fill: "#42E0A0",
                  "p-id": "9473"
                }),
                vue.createElementVNode("path", {
                  d: "M424.9 62H287C163.3 62 62 163.3 62 287v137.9L424.9 62z",
                  fill: "#41E0A0",
                  "p-id": "9474"
                }),
                vue.createElementVNode("path", {
                  d: "M448.4 62H287c-39.8 0-77.3 10.5-109.8 28.8l-86.4 86.4C72.5 209.7 62 247.2 62 287v161.4L448.4 62z",
                  fill: "#40E1A1",
                  "p-id": "9475"
                }),
                vue.createElementVNode("path", {
                  d: "M472 62H287c-23.7 0-46.5 3.7-68 10.6L72.6 219C65.7 240.5 62 263.3 62 287v185L472 62z",
                  fill: "#3FE1A1",
                  "p-id": "9476"
                }),
                vue.createElementVNode("path", {
                  d: "M495.5 62H287c-12.6 0-24.9 1.1-37 3.1l-185 185c-2 12-3.1 24.4-3.1 37v208.5L495.5 62z",
                  fill: "#3EE1A1",
                  "p-id": "9477"
                }),
                vue.createElementVNode("path", {
                  d: "M519.1 62H287c-3.6 0-7.1 0.1-10.6 0.3L62.3 276.4c-0.2 3.5-0.3 7-0.3 10.6v232.1L519.1 62z",
                  fill: "#3DE1A1",
                  "p-id": "9478"
                }),
                vue.createElementVNode("path", {
                  d: "M62 542.6L542.6 62H300.2L62 300.2z",
                  fill: "#3CE1A2",
                  "p-id": "9479"
                }),
                vue.createElementVNode("path", {
                  d: "M62 566.2L566.2 62H323.8L62 323.8z",
                  fill: "#3BE1A2",
                  "p-id": "9480"
                }),
                vue.createElementVNode("path", {
                  d: "M62 589.7L589.7 62H347.3L62 347.3z",
                  fill: "#3AE1A2",
                  "p-id": "9481"
                }),
                vue.createElementVNode("path", {
                  d: "M62 613.3L613.3 62H370.8L62 370.8z",
                  fill: "#39E2A2",
                  "p-id": "9482"
                }),
                vue.createElementVNode("path", {
                  d: "M62 636.8L636.8 62H394.4L62 394.4z",
                  fill: "#38E2A3",
                  "p-id": "9483"
                }),
                vue.createElementVNode("path", {
                  d: "M62 417.9v242.5L660.4 62H417.9z",
                  fill: "#37E2A3",
                  "p-id": "9484"
                }),
                vue.createElementVNode("path", {
                  d: "M62 441.5v242.4L683.9 62H441.5z",
                  fill: "#36E2A3",
                  "p-id": "9485"
                }),
                vue.createElementVNode("path", {
                  d: "M62 465v242.5L707.5 62H465z",
                  fill: "#35E2A3",
                  "p-id": "9486"
                }),
                vue.createElementVNode("path", {
                  d: "M62 488.6V731L731 62H488.6z",
                  fill: "#34E2A3",
                  "p-id": "9487"
                }),
                vue.createElementVNode("path", {
                  d: "M737 62H512.1L62 512.1V737c0 5.7 0.2 11.3 0.6 16.9L753.9 62.6c-5.6-0.4-11.2-0.6-16.9-0.6z",
                  fill: "#33E2A4",
                  "p-id": "9488"
                }),
                vue.createElementVNode("path", {
                  d: "M737 62H535.7L62 535.7V737c0 12.9 1.1 25.5 3.2 37.9L774.9 65.2c-12.4-2.1-25-3.2-37.9-3.2z",
                  fill: "#32E3A4",
                  "p-id": "9489"
                }),
                vue.createElementVNode("path", {
                  d: "M737 62H559.2L62 559.2V737c0 19.8 2.6 38.9 7.4 57.2L794.2 69.4C775.9 64.6 756.8 62 737 62z",
                  fill: "#31E3A4",
                  "p-id": "9490"
                }),
                vue.createElementVNode("path", {
                  d: "M737 62H582.8L62 582.8V737c0 26.3 4.6 51.6 13 75.2L812.2 75c-23.6-8.4-48.9-13-75.2-13z",
                  fill: "#30E3A4",
                  "p-id": "9491"
                }),
                vue.createElementVNode("path", {
                  d: "M737 62H606.3L62 606.3V737c0 32.7 7.1 63.8 19.8 92L829 81.8C800.8 69.1 769.7 62 737 62z",
                  fill: "#2FE3A5",
                  "p-id": "9492"
                }),
                vue.createElementVNode("path", {
                  d: "M844.7 89.6C812.6 72 775.9 62 737 62H629.9L62 629.9V737c0 38.9 10 75.6 27.6 107.7L844.7 89.6z",
                  fill: "#2EE3A5",
                  "p-id": "9493"
                }),
                vue.createElementVNode("path", {
                  d: "M859.4 98.5C824.1 75.4 782 62 737 62h-83.6L62 653.4V737c0 45 13.4 87.1 36.5 122.4L859.4 98.5z",
                  fill: "#2DE3A5",
                  "p-id": "9494"
                }),
                vue.createElementVNode("path", {
                  d: "M873.2 108.2C835.3 79.3 788.1 62 737 62h-60L62 677v60c0 51.1 17.3 98.3 46.2 136.2l765-765z",
                  fill: "#2CE3A5",
                  "p-id": "9495"
                }),
                vue.createElementVNode("path", {
                  d: "M886.1 118.8C846.3 83.5 794 62 737 62h-36.5L62 700.5V737c0 57 21.5 109.3 56.8 149.1l767.3-767.3z",
                  fill: "#2BE4A6",
                  "p-id": "9496"
                }),
                vue.createElementVNode("path", {
                  d: "M737 62h-12.9L62 724.1V737c0 63 26.3 120.2 68.4 161.1l767.8-767.8C857.2 88.3 800 62 737 62z",
                  fill: "#2AE4A6",
                  "p-id": "9497"
                }),
                vue.createElementVNode("path", {
                  d: "M62.2 747.4c3 64.8 33.7 122.7 80.5 161.9l766.6-766.6C870 95.9 812.2 65.2 747.4 62.2L62.2 747.4z",
                  fill: "#29E4A6",
                  "p-id": "9498"
                }),
                vue.createElementVNode("path", {
                  d: "M64.3 768.9c8.9 61.8 43.1 115.7 91.7 150.7L919.6 156c-35-48.6-88.9-82.8-150.7-91.7L64.3 768.9z",
                  fill: "#28E4A6",
                  "p-id": "9499"
                }),
                vue.createElementVNode("path", {
                  d: "M68 788.7c14 59.1 51.5 109.4 102.1 140.4l758.9-759c-30.9-50.5-81.2-88-140.4-102.1L68 788.7z",
                  fill: "#27E4A7",
                  "p-id": "9500"
                }),
                vue.createElementVNode("path", {
                  d: "M73.2 807c18.7 56.7 59.4 103.6 111.9 130.5l752.3-752.3c-26.8-52.5-73.7-93.3-130.4-112L73.2 807z",
                  fill: "#26E4A7",
                  "p-id": "9501"
                }),
                vue.createElementVNode("path", {
                  d: "M79.7 824.1c23.1 54.4 67 98.1 121.6 120.8l743.6-743.6c-22.7-54.6-66.3-98.6-120.8-121.6L79.7 824.1z",
                  fill: "#25E5A7",
                  "p-id": "9502"
                }),
                vue.createElementVNode("path", {
                  d: "M87.2 840.1c27.2 52.3 74.4 92.8 131.3 111.1l732.8-732.8C933 161.5 892.5 114.3 840.2 87.1l-753 753z",
                  fill: "#25E5A7",
                  "p-id": "9503"
                }),
                vue.createElementVNode("path", {
                  d: "M95.8 855.1c31.3 50.3 81.9 87.6 141.2 101.2L956.3 237c-13.6-59.4-50.8-110-101.2-141.2L95.8 855.1z",
                  fill: "#24E5A7",
                  "p-id": "9504"
                }),
                vue.createElementVNode("path", {
                  d: "M256.9 960L960 256.9c-8.4-62-42.3-116.3-90.8-151.6l-764 764c35.4 48.3 89.6 82.3 151.7 90.7z",
                  fill: "#23E5A8",
                  "p-id": "9505"
                }),
                vue.createElementVNode("path", {
                  d: "M278.6 961.8l683.3-683.3c-2.4-65.1-32.8-123.3-79.5-162.9L115.6 882.4c39.7 46.6 97.9 77 163 79.4z",
                  fill: "#22E5A8",
                  "p-id": "9506"
                }),
                vue.createElementVNode("path", {
                  d: "M962 287c0-62.5-25.8-119.3-67.3-160.1L126.9 894.7C167.7 936.2 224.5 962 287 962h14.9L962 301.9V287z",
                  fill: "#21E5A8",
                  "p-id": "9507"
                }),
                vue.createElementVNode("path", {
                  d: "M962 287c0-56.5-21.1-108.4-55.9-148L139 906.1c39.6 34.8 91.5 55.9 148 55.9h38.5L962 325.5V287z",
                  fill: "#20E5A8",
                  "p-id": "9508"
                }),
                vue.createElementVNode("path", {
                  d: "M962 287c0-50.6-16.9-97.4-45.3-135L152 916.7c37.7 28.4 84.5 45.3 135 45.3h62l613-613v-62z",
                  fill: "#1FE6A9",
                  "p-id": "9509"
                }),
                vue.createElementVNode("path", {
                  d: "M962 287c0-44.5-13.1-86.1-35.7-121.2L165.8 926.3c35 22.5 76.6 35.7 121.2 35.7h85.6L962 372.6V287z",
                  fill: "#1EE6A9",
                  "p-id": "9510"
                }),
                vue.createElementVNode("path", {
                  d: "M962 287c0-38.4-9.8-74.6-26.9-106.4L180.6 935.1c31.7 17.2 68 26.9 106.4 26.9h109.1L962 396.1V287z",
                  fill: "#1DE6A9",
                  "p-id": "9511"
                }),
                vue.createElementVNode("path", {
                  d: "M287 962h132.7L962 419.7V287c0-32.2-6.9-62.8-19.2-90.6L196.4 942.8c27.8 12.3 58.4 19.2 90.6 19.2z",
                  fill: "#1CE6A9",
                  "p-id": "9512"
                }),
                vue.createElementVNode("path", {
                  d: "M287 962h156.2L962 443.2V287c0-25.8-4.4-50.6-12.5-73.7L213.3 949.5c23.1 8.1 47.9 12.5 73.7 12.5z",
                  fill: "#1BE6AA",
                  "p-id": "9513"
                }),
                vue.createElementVNode("path", {
                  d: "M287 962h179.8L962 466.8V287c0-19.2-2.4-37.8-7-55.6L231.4 955c17.8 4.6 36.4 7 55.6 7z",
                  fill: "#1AE6AA",
                  "p-id": "9514"
                }),
                vue.createElementVNode("path", {
                  d: "M287 962h203.3L962 490.3V287c0-12.3-1-24.4-2.9-36.2L250.8 959.1c11.8 1.9 23.9 2.9 36.2 2.9z",
                  fill: "#19E6AA",
                  "p-id": "9515"
                }),
                vue.createElementVNode("path", {
                  d: "M287 962h226.9L962 513.9V287c0-5.1-0.2-10.1-0.5-15L272 961.5c4.9 0.3 9.9 0.5 15 0.5z",
                  fill: "#18E7AA",
                  "p-id": "9516"
                }),
                vue.createElementVNode("path", {
                  d: "M537.4 962L962 537.4V295L295 962z",
                  fill: "#17E7AB",
                  "p-id": "9517"
                }),
                vue.createElementVNode("path", {
                  d: "M561 962l401-401V318.5L318.5 962z",
                  fill: "#16E7AB",
                  "p-id": "9518"
                }),
                vue.createElementVNode("path", {
                  d: "M584.5 962L962 584.5V342.1L342.1 962z",
                  fill: "#15E7AB",
                  "p-id": "9519"
                }),
                vue.createElementVNode("path", {
                  d: "M608.1 962L962 608.1V365.6L365.6 962z",
                  fill: "#14E7AB",
                  "p-id": "9520"
                }),
                vue.createElementVNode("path", {
                  d: "M631.6 962L962 631.6V389.2L389.2 962z",
                  fill: "#13E7AB",
                  "p-id": "9521"
                }),
                vue.createElementVNode("path", {
                  d: "M655.2 962L962 655.2V412.7L412.7 962z",
                  fill: "#12E7AC",
                  "p-id": "9522"
                }),
                vue.createElementVNode("path", {
                  d: "M678.7 962L962 678.7V436.3L436.3 962z",
                  fill: "#11E8AC",
                  "p-id": "9523"
                }),
                vue.createElementVNode("path", {
                  d: "M702.2 962L962 702.2V459.8L459.8 962z",
                  fill: "#10E8AC",
                  "p-id": "9524"
                }),
                vue.createElementVNode("path", {
                  d: "M725.8 962L962 725.8V483.4L483.4 962z",
                  fill: "#0FE8AC",
                  "p-id": "9525"
                }),
                vue.createElementVNode("path", {
                  d: "M962 737V506.9L506.9 962H737c4.3 0 8.5-0.1 12.7-0.4l211.9-211.9c0.3-4.2 0.4-8.4 0.4-12.7z",
                  fill: "#0EE8AD",
                  "p-id": "9526"
                }),
                vue.createElementVNode("path", {
                  d: "M962 737V530.5L530.5 962H737c13.4 0 26.6-1.2 39.4-3.5l182.2-182.2c2.2-12.7 3.4-25.9 3.4-39.3z",
                  fill: "#0DE8AD",
                  "p-id": "9527"
                }),
                vue.createElementVNode("path", {
                  d: "M962 737V554L554 962h183c24.8 0 48.6-4.1 71-11.6L950.4 808c7.5-22.4 11.6-46.2 11.6-71z",
                  fill: "#0CE8AD",
                  "p-id": "9528"
                }),
                vue.createElementVNode("path", {
                  d: "M962 737V577.6L577.6 962H737c41.8 0 80.9-11.5 114.6-31.6l78.9-78.9c20-33.6 31.5-72.7 31.5-114.5z",
                  fill: "#0BE8AD",
                  "p-id": "9529"
                }),
                vue.createElementVNode("path", {
                  d: "M962 601.1L601.1 962H737c123.8 0 225-101.3 225-225V601.1z",
                  fill: "#0AE9AE",
                  "p-id": "9530"
                }),
                vue.createElementVNode("path", {
                  d: "M962 624.7L624.7 962H737c123.8 0 225-101.3 225-225V624.7z",
                  fill: "#09E9AE",
                  "p-id": "9531"
                }),
                vue.createElementVNode("path", {
                  d: "M962 648.2L648.2 962H737c123.8 0 225-101.3 225-225v-88.8z",
                  fill: "#08E9AE",
                  "p-id": "9532"
                }),
                vue.createElementVNode("path", {
                  d: "M962 671.8L671.8 962H737c123.8 0 225-101.3 225-225v-65.2z",
                  fill: "#07E9AE",
                  "p-id": "9533"
                }),
                vue.createElementVNode("path", {
                  d: "M962 695.3L695.3 962H737c123.8 0 225-101.3 225-225v-41.7z",
                  fill: "#06E9AF",
                  "p-id": "9534"
                }),
                vue.createElementVNode("path", {
                  d: "M962 718.8L718.8 962H737c123.8 0 225-101.3 225-225v-18.2z",
                  fill: "#05E9AF",
                  "p-id": "9535"
                }),
                vue.createElementVNode("path", {
                  d: "M961.9 742.5L742.5 961.9c119.4-2.9 216.5-100 219.4-219.4z",
                  fill: "#04E9AF",
                  "p-id": "9536"
                }),
                vue.createElementVNode("path", {
                  d: "M959.8 768.1L768.1 959.8c99-13.8 177.9-92.7 191.7-191.7z",
                  fill: "#03EAAF",
                  "p-id": "9537"
                }),
                vue.createElementVNode("path", {
                  d: "M953.6 797.9L797.9 953.6c75-21.3 134.4-80.7 155.7-155.7z",
                  fill: "#02EAB0",
                  "p-id": "9538"
                }),
                vue.createElementVNode("path", {
                  d: "M938.7 836.3L836.3 938.7c44.3-21.9 80.5-58.1 102.4-102.4z",
                  fill: "#01EAB0",
                  "p-id": "9539"
                }),
                vue.createElementVNode("path", {
                  d: "M386 269h252c19.9 0 36 16.1 36 36v414c0 19.9-16.1 36-36 36H386c-19.9 0-36-16.1-36-36V305c0-19.9 16.1-36 36-36z",
                  fill: "#F6FDFA",
                  "p-id": "9540"
                }),
                vue.createElementVNode("path", {
                  d: "M602 683H422c-19.9 0-36-16.1-36-36V350c0-19.9 16.1-36 36-36h180c19.9 0 36 16.1 36 36v297c0 19.9-16.1 36-36 36z",
                  fill: "#CAF6E1",
                  "p-id": "9541"
                }),
                vue.createElementVNode("path", {
                  d: "M512 719m-18 0a18 18 0 1 0 36 0 18 18 0 1 0-36 0Z",
                  fill: "#CAF6E1",
                  "p-id": "9542"
                })
              ]))
            ]),
            vue.withDirectives(vue.createElementVNode(
              "input",
              {
                "onUpdate:modelValue": _cache[4] || (_cache[4] = ($event) => $setup.formData.phone = $event),
                placeholder: "请输入手机号",
                class: vue.normalizeClass(["form-input", { "error": $setup.errors.phone }])
              },
              null,
              2
              /* CLASS */
            ), [
              [vue.vModelText, $setup.formData.phone]
            ])
          ]),
          $setup.errors.phone ? (vue.openBlock(), vue.createElementBlock(
            "text",
            {
              key: 0,
              class: "error-message"
            },
            vue.toDisplayString($setup.errors.phone),
            1
            /* TEXT */
          )) : vue.createCommentVNode("v-if", true)
        ]),
        vue.createElementVNode("view", { class: "form-item" }, [
          vue.createElementVNode("button", {
            loading: $setup.loading,
            onClick: $setup.submitForm,
            class: "register-button"
          }, [
            vue.createElementVNode("text", { class: "button-text" }, "注册")
          ], 8, ["loading"])
        ]),
        vue.createElementVNode("view", { class: "login-link" }, [
          vue.createElementVNode("text", null, "已有账号？"),
          vue.createElementVNode("text", {
            class: "link",
            onClick: $setup.goLogin
          }, "立即登录")
        ])
      ])
    ]);
  }
  const PagesLoginRegister = /* @__PURE__ */ _export_sfc(_sfc_main$2, [["render", _sfc_render$1], ["__scopeId", "data-v-838b72c9"], ["__file", "D:/IDEA/work/Minecraft-system/minecraft-App/pages/login/register.vue"]]);
  const _sfc_main$1 = {
    __name: "index",
    setup(__props, { expose: __expose }) {
      __expose();
      const activeIndex = vue.ref(0);
      const __returned__ = { activeIndex, ref: vue.ref, LoginComponent: PagesLoginLogin, RegisterComponent: PagesLoginRegister };
      Object.defineProperty(__returned__, "__isScriptSetup", { enumerable: false, value: true });
      return __returned__;
    }
  };
  function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
    return vue.openBlock(), vue.createElementBlock("view", { class: "login-index" }, [
      vue.createElementVNode("view", { class: "tab-content" }, [
        $setup.activeIndex === 0 ? (vue.openBlock(), vue.createBlock($setup["LoginComponent"], { key: 0 })) : $setup.activeIndex === 1 ? (vue.openBlock(), vue.createBlock($setup["RegisterComponent"], { key: 1 })) : vue.createCommentVNode("v-if", true)
      ])
    ]);
  }
  const PagesLoginIndex = /* @__PURE__ */ _export_sfc(_sfc_main$1, [["render", _sfc_render], ["__scopeId", "data-v-d08ef7d4"], ["__file", "D:/IDEA/work/Minecraft-system/minecraft-App/pages/login/index.vue"]]);
  __definePage("pages/index/index", PagesIndexIndex);
  __definePage("pages/discover/index", PagesDiscoverIndex);
  __definePage("pages/community/index", PagesCommunityIndex);
  __definePage("pages/news/index", PagesNewsIndex);
  __definePage("pages/news/detail", PagesNewsDetail);
  __definePage("pages/groupTravel/index", PagesGroupTravelIndex);
  __definePage("pages/travelPlan/index", PagesTravelPlanIndex);
  __definePage("pages/product/index", PagesProductIndex);
  __definePage("pages/attraction/index", PagesAttractionIndex);
  __definePage("pages/attraction/detail", PagesAttractionDetail);
  __definePage("pages/hotel/index", PagesHotelIndex);
  __definePage("pages/hotel/detail", PagesHotelDetail);
  __definePage("pages/food/index", PagesFoodIndex);
  __definePage("pages/food/detail", PagesFoodDetail);
  __definePage("pages/ticket/index", PagesTicketIndex);
  __definePage("pages/profile/index", PagesProfileIndex);
  __definePage("pages/profile/settings", PagesProfileSettings);
  __definePage("pages/profile/about", PagesProfileAbout);
  __definePage("pages/order/index", PagesOrderIndex);
  __definePage("pages/collection/index", PagesCollectionIndex);
  __definePage("pages/comment/index", PagesCommentIndex);
  __definePage("pages/chat/index", PagesChatIndex);
  __definePage("pages/login/index", PagesLoginIndex);
  __definePage("pages/login/login", PagesLoginLogin);
  __definePage("pages/login/register", PagesLoginRegister);
  const _sfc_main = {
    onLaunch: function() {
      formatAppLog("log", "at App.vue:5", "App Launch");
    },
    onShow: function() {
      formatAppLog("log", "at App.vue:8", "App Show");
    },
    onHide: function() {
      formatAppLog("log", "at App.vue:11", "App Hide");
    },
    onExit: function() {
      formatAppLog("log", "at App.vue:32", "App Exit");
    }
  };
  const App = /* @__PURE__ */ _export_sfc(_sfc_main, [["__file", "D:/IDEA/work/Minecraft-system/minecraft-App/App.vue"]]);
  function createApp() {
    const app = vue.createVueApp(App);
    return {
      app
    };
  }
  const { app: __app__, Vuex: __Vuex__, Pinia: __Pinia__ } = createApp();
  uni.Vuex = __Vuex__;
  uni.Pinia = __Pinia__;
  __app__.provide("__globalStyles", __uniConfig.styles);
  __app__._component.mpType = "app";
  __app__._component.render = () => {
  };
  __app__.mount("#app");
})(Vue);
