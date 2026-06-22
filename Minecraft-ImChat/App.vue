<script>
	import { auth } from './utils/auth.js'
	import wsService from './utils/websocket.js'
	import env from './config/index.js'

	export default {
		onLaunch: function() {
			console.log('App Launch')
			console.log('当前环境:', env.NODE_ENV)
			console.log('API 地址:', env.API_BASE_URL)
			console.log('WebSocket:', env.WS_URL)

			// 检查登录状态
			this.checkLoginStatus()
		},
		onShow: function() {
			console.log('App Show')
		},
		onHide: function() {
			console.log('App Hide')
		},
		onError: function(err) {
			console.error('App Error:', err)
		},
		methods: {
			/**
			 * 检查登录状态
			 */
			checkLoginStatus() {
				const loggedIn = auth.isLoggedIn()
				console.log('登录状态:', loggedIn ? '已登录' : '未登录')

				// 如果已登录，自动连接 WebSocket
				if (loggedIn) {
					const userInfo = auth.getUserInfo()
					if (userInfo?.id) {
						wsService.connect(userInfo.id)
					}
				}

				// 获取当前页面路径
				const pages = getCurrentPages()
				const currentPage = pages[pages.length - 1]
				const currentPath = currentPage?.route || ''

				// 公开页面（无需登录）
				const publicPages = [
					'pages/login/login',
					'pages/register/register',
					'pages/forgot-password/forgot-password'
				]

				const isPublicPage = publicPages.some(p => currentPath.includes(p))

				// 如果未登录且不在公开页面，跳转到登录页
				if (!loggedIn && !isPublicPage) {
					uni.reLaunch({ url: '/pages/login/login' })
				}
			}
		}
	}
</script>

<style>
	/* 引入公共样式 */
	@import './static/css/common.css';
</style>
