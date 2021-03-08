<template>
  <div v-if="finishedSessions.length > 0" class="max-width-md mx-auto mb-12">
    <SessionList :sessions="spliceLatestSessions">
      <div class="d-flex justify-space-between">
        <h1 class="relative headline text-bold d-inline-block">종료된 강의</h1>
        <v-btn to="finishedSessions" v-if="finishedSessions.length > 3" text color="grey darken-2">모두 보기</v-btn>
      </div>
    </SessionList>
  </div>
</template>

<script>
import { SESSION_STATUS } from '@/utils/constants'
import SessionList from '@/views/main/components/SessionList'
import { mapMutations } from 'vuex'
import { SET_FINISHED_SESSIONS } from '@/store/shared/mutationTypes'

export default {
  name: 'FinishedSessions',
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
    ...mapMutations([SET_FINISHED_SESSIONS])
  },
  created() {
    this.$store.commit(SET_FINISHED_SESSIONS, this.finishedSessions)
  },
  computed: {
    spliceLatestSessions() {
      return this.finishedSessions.slice(0, 4)
    }
  },
  data() {
    return {
      finishedSessions: this.sessions.filter(session => session.status === SESSION_STATUS.FINISH)
    }
  }
}
</script>
