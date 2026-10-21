import { createHash } from 'node:crypto'
import { readFileSync } from 'node:fs'
import { resolve } from 'node:path'
import { loadEnv } from 'vite'

/**
 * 运行时应用配置插件
 *
 * 构建时把 .env 中以 VITE_GLOB_ 开头的变量抽取到
 * dist/_app-config-{version}-{hash}.js，并自动注入 index.html。
 * 部署后直接修改该文件即可切换接口地址等配置，刷新页面生效，无需重新打包。
 */

const CONFIG_FILE_NAME = '_app-config'
const GLOBAL_KEY = '__APP_CONFIG__'
const ENV_PREFIX = 'VITE_GLOB_'

/**
 * 生成配置文件内容
 * 通过 Object.freeze + defineProperty 防止运行时被业务代码修改
 */
function createSource(configs) {
  const json = JSON.stringify(configs)
  return `/* 运行时配置：部署后可直接修改下方配置项，刷新页面生效，无需重新打包 */\nwindow.${GLOBAL_KEY}=${json};\nObject.freeze(window.${GLOBAL_KEY});\nObject.defineProperty(window,"${GLOBAL_KEY}",{configurable:false,writable:false});\n`
}

/**
 * @param {{ mode: string, root: string }} options
 * @returns {import('vite').Plugin}
 */
export function extraAppConfigPlugin({ mode, root }) {
  let publicPath = '/'
  let source = ''
  let fileName = ''

  return {
    name: 'vite:extra-app-config',
    apply: 'build',
    configResolved(config) {
      publicPath = config.base.endsWith('/') ? config.base : `${config.base}/`

      // 只抽取 VITE_GLOB_ 前缀变量，变量名排序保证 hash 稳定
      const env = loadEnv(mode, root, ENV_PREFIX)
      const configs = Object.keys(env)
        .sort()
        .reduce((acc, key) => {
          acc[key] = env[key]
          return acc
        }, {})

      source = createSource(configs)

      const { version = '0.0.0' } = JSON.parse(readFileSync(resolve(root, 'package.json'), 'utf-8'))
      const hash = createHash('sha256').update(source).digest('hex').slice(0, 8)
      fileName = `${CONFIG_FILE_NAME}-${version}-${hash}.js`
    },
    generateBundle() {
      this.emitFile({ fileName, source, type: 'asset' })
    },
    transformIndexHtml() {
      return [
        {
          tag: 'script',
          attrs: { src: `${publicPath}${fileName}` },
          injectTo: 'head-prepend'
        }
      ]
    }
  }
}
