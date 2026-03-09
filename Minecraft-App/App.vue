<script>
	export default {
		onLaunch: function() {
			console.log('App Launch')
			// 检查系统主题
			this.checkSystemTheme()
		},
		onShow: function() {
			console.log('App Show')
		},
		onHide: function() {
			console.log('App Hide')
		},
		methods: {
			checkSystemTheme() {
				// 检查系统是否为暗黑模式
				const isDark = window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches
				if (isDark) {
					document.documentElement.classList.add('dark-mode')
				} else {
					document.documentElement.classList.remove('dark-mode')
				}
				
				// 监听系统主题变化
				if (window.matchMedia) {
					window.matchMedia('(prefers-color-scheme: dark)').addEventListener('change', (e) => {
						if (e.matches) {
							document.documentElement.classList.add('dark-mode')
						} else {
							document.documentElement.classList.remove('dark-mode')
						}
					})
				}
			}
		}
	}
</script>

<style>
	/*引入主题样式 */
	@import "./src/styles/theme.css";
	
	/*每个页面公共css */
	* {
		margin: 0;
		padding: 0;
		box-sizing: border-box;
	}
	
	body {
		font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;
		background-color: var(--bg-color);
		color: var(--text-color);
	}
</style>
