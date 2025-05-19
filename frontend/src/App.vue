<template>
  <el-config-provider>
    <!-- 根据路由判断是否显示导航栏和侧边栏 -->
    <div v-if="isLoginPage" class="app-container-login">
      <router-view />
    </div>
    <div v-else class="app-container">
      <el-container>
        <el-aside width="200px">
          <el-menu
            router
            default-active="/"
            class="el-menu-vertical"
            background-color="#545c64"
            text-color="#fff"
            active-text-color="#ffd04b">
            <!-- 导航菜单项 -->
            <el-menu-item index="/">
              <el-icon><HomeFilled /></el-icon>
              <span>首页</span>
            </el-menu-item>
            <!-- 其他菜单项 -->
            <!-- ... -->
          </el-menu>
        </el-aside>
        <el-container>
          <el-header>
            <!-- 头部内容 -->
          </el-header>
          <el-main>
            <router-view />
          </el-main>
        </el-container>
      </el-container>
    </div>
  </el-config-provider>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { HomeFilled } from '@element-plus/icons-vue'

const route = useRoute()

// 判断当前是否为登录页
const isLoginPage = computed(() => {
  return route.path === '/login'
})
</script>

<style scoped>
.app-container {
  height: 100vh;
  width: 100vw;
}

.app-container-login {
  height: 100vh;
  width: 100vw;
}

/* 其他样式保持不变 */
</style>
