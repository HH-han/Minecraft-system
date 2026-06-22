<script>
	import { isLoggedIn } from './utils/storage.js'
	import wsService from './utils/websocket.js'

	export default {
		onLaunch: function() {
			console.log('App Launch')

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
				const loggedIn = isLoggedIn()
				console.log('登录状态:', loggedIn ? '已登录' : '未登录')

				// 如果已登录，自动连接 WebSocket
				if (loggedIn) {
					const pages = getCurrentPages()
					if (pages.length > 0) {
						const userInfo = uni.getStorageSync('userInfo')
						if (userInfo?.id) {
							wsService.connect(userInfo.id)
						}
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
	/*每个页面公共css */
	page {
		background-color: #f5f5f5;
		font-family: -apple-system, BlinkMacSystemFont, 'Helvetica Neue', Helvetica, Segoe UI, Arial, Roboto, sans-serif;
	}
</style>
