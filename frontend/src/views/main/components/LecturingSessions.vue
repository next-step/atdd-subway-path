<template>
  <div v-if="lecturingSessions.length > 0" class="max-width-md mx-auto  mb-12">
    <SessionList :sessions="lecturingSessions">
      <h1 class="relative headline text-bold">진행중인 강의</h1>
    </SessionList>
  </div>
</template>

<script>
import { SESSION_STATUS } from '@/utils/constants'
import SessionList from '@/views/main/components/SessionList'

export default {
  name: 'LecturingSessions',
  props: {
    sessions: {
      type: Array,
      required: true
    }
  },
  components: {
    SessionList
  },
  data() {
    return {
      lecturingSessions: this.sessions.filter(session => session.status === SESSION_STATUS.LECTURING)
    }
  }
}
</script>