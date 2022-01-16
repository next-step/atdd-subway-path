<template>
  <div :last-lesson="lastLesson">
    <div
      v-if="lastLesson"
      @mouseover="stopTimer()"
      class="alert-last-lesson alert alert-warning alert-dismissible z-1000"
      :class="isShow ? '' : 'disappeared'"
      role="alert"
    >
      <button @click="closeLastLessonAlert()" type="button" class="close" data-dismiss="alert" aria-label="Close">
        <span aria-hidden="true">&times;</span>
      </button>
      <a :href="`${lastLesson.url}`" class="alert-link"
        >최근에 학습한 강의로 이동 : <strong>{{ lastLesson.title }}</strong></a
      >
    </div>
  </div>
</template>

<script>
export default {
  name: 'AlertLastVisitedSession',
  props: {
    loginUser: Object
  },
  mounted() {
    if (this.loginUser) {
      this.showLastLessonAlert()
    }
  },
  methods: {
    showLastLessonAlert() {
      if (!this.lastLesson) {
        return
      }
      this.toggleIsShowClass()
      this.timer = setTimeout(() => this.toggleIsShowClass(), 5000)
    },
    toggleIsShowClass() {
      this.isShow = !this.isShow
    },
    closeLastLessonAlert() {
      localStorage.removeItem('lastLesson')
    },
    stopTimer() {
      clearTimeout(this.timer)
    }
  },
  data() {
    return {
      lastLesson: JSON.parse(localStorage.getItem('lastLesson')),
      isShow: false
    }
  }
}
</script>

<style lang="scss" scoped>
.alert-last-lesson {
  position: fixed;
  top: 0%;
  left: 50%;
  transform: translateX(-50%);
  transition: margin 700ms;

  &.disappeared {
    margin-top: -70px;
  }
}
</style>
