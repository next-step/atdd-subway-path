<template>
  <div v-if="openSessions.length > 0" class="max-width-md mx-auto mb-12">
    <SessionList :sessions="openSessions">
      <h1 class="relative headline text-bold">모집중인 강의</h1>
    </SessionList>
  </div>
</template>

<script>
import { SESSION_STATUS } from '@/utils/constants'
import SessionList from '@/views/main/components/SessionList'

export default {
  name: 'OpenSessions',
  props: {
    sessions: {
      type: Array,
      required: true
    }
  },
  components: {
    SessionList
  },
  methods: {
    isEnrollingSessions(session) {
      return session.status === SESSION_STATUS.PREPARING || session.status === SESSION_STATUS.ENROLLING
    }
  },
  data() {
    return {
      openSessions: this.sessions.filter(session => this.isEnrollingSessions(session))
    }
  }
}
</script>
