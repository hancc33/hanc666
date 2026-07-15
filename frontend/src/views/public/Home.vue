<template>
  <div class="home-page">
    <!-- 紧凑 Banner -->
    <section class="hero">
      <div class="hero-content">
        <h2 class="hero-title">助苗·助梦·护苗</h2>
        <div class="hero-underline"></div>
        <p class="desc">泉州职业技术大学八方书院筑梦护苗实践队 —— 通过安全教育、成长陪伴、公益服务为儿童提供帮助</p>
        <div class="hero-stats">
          <div class="hero-stat">
            <div class="number">{{ videoCount }}</div>
            <div class="label">实践视频</div>
          </div>
          <div class="hero-stat">
            <div class="number">{{ imageCount }}</div>
            <div class="label">活动图片</div>
          </div>
          <div class="hero-stat">
            <div class="number">{{ messageCount }}</div>
            <div class="label">用户留言</div>
          </div>
        </div>
      </div>
    </section>

    <!-- 4张卡片横排一行 -->
    <section class="section cards-section">
      <div class="cards-row">
        <div class="achievement-section">
          <h3>📋 实践背景</h3>
          <p>引导大学生在社会实践中受教育、长才干、作贡献，我们'助苗·助梦·护苗'实践队深入基层，围绕困境儿童开展了一系列帮扶活动。</p>
        </div>
        <div class="achievement-section">
          <h3>🎯 实践目标</h3>
          <p>通过安全教育课堂、成长陪伴、公益服务等方式，提高儿童安全意识和自我保护能力，助力青少年健康成长。</p>
        </div>
        <div class="achievement-section">
          <h3>👧 服务对象</h3>
          <p>社区困境儿童、留守儿童、外来务工人员子女等需要关爱帮助的青少年群体。</p>
        </div>
        <div class="achievement-section">
          <h3>📚 活动内容</h3>
          <ul class="content-list">
            <li>安全用电教育</li>
            <li>消防安全知识</li>
            <li>防溺水安全教育</li>
            <li>课业学习辅导</li>
            <li>心理健康陪伴</li>
          </ul>
        </div>
      </div>
    </section>

    <!-- 实践视频 -->
    <section class="section" v-if="videos.length">
      <div class="section-title">
        <h2>实践视频</h2>
        <div class="underline"></div>
      </div>
      <div class="card-grid">
        <div class="card video-card" v-for="v in videos.slice(0, 3)" :key="v.id" @click="$router.push(`/videos/${v.id}`)">
          <div class="card-cover">
            <img v-if="v.coverUrl" :src="v.coverUrl" :alt="v.title" />
            <div class="play-btn">▶</div>
          </div>
          <div class="card-body">
            <h3>{{ v.title }}</h3>
            <p>{{ v.description }}</p>
            <span class="card-tag">{{ v.category || '实践记录' }}</span>
          </div>
        </div>
      </div>
    </section>

    <!-- 活动瞬间 -->
    <section class="section" v-if="images.length">
      <div class="section-title">
        <h2>活动瞬间</h2>
        <div class="underline"></div>
      </div>
      <div class="gallery-grid">
        <div class="gallery-item" v-for="img in images.slice(0, 6)" :key="img.id">
          <img v-if="img.imageUrl" :src="img.imageUrl" :alt="img.title" />
          <div v-else style="font-size: 48px; opacity: 0.3;">📷</div>
          <div class="gallery-overlay">
            <div style="font-weight: 600;">{{ img.title }}</div>
          </div>
        </div>
      </div>
    </section>

    <!-- 留言墙 -->
    <section class="section" v-if="messages.length">
      <div class="section-title">
        <h2>留言墙</h2>
        <div class="underline"></div>
      </div>
      <div class="message-list">
        <div class="message-item" v-for="msg in messages.slice(0, 3)" :key="msg.id">
          <div class="msg-header">
            <span class="msg-user">{{ msg.username }}</span>
            <span class="msg-time">{{ formatDate(msg.createTime) }}</span>
          </div>
          <div class="msg-title">{{ msg.title }}</div>
          <div class="msg-content">{{ msg.content }}</div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { publicApi } from '../../api'

const videos = ref([])
const images = ref([])
const messages = ref([])
const videoCount = ref(0)
const imageCount = ref(0)
const messageCount = ref(0)

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  return dateStr.replace('T', ' ').substring(0, 10)
}

onMounted(async () => {
  try {
    const [v, i, m] = await Promise.all([
      publicApi.getVideos(),
      publicApi.getImages(),
      publicApi.getMessages()
    ])
    videos.value = v.data || []
    images.value = i.data || []
    messages.value = m.data || []
    videoCount.value = (v.data || []).length
    imageCount.value = (i.data || []).length
    messageCount.value = (m.data || []).length
  } catch (e) {
    console.error('Failed to load home data:', e)
  }
})
</script>
