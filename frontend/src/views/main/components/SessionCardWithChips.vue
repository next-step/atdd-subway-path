<template>
  <SessionCard :session="session" :is-my-session="isMySession">
    <template slot="chip">
      <div v-if="isMySession">
        <div :key="index" v-for="(status, index) in sessionInfo">
          <v-chip v-if="!isRecruitWaitings(status)" x-small class="top-15" :class="index > 0 ? 'ml-2' : 'ml-3'" :color="status.color">
            {{ status.text }}
          </v-chip>
        </div>
      </div>
      <div v-else-if="isEnrollingStatus">
        <v-chip :key="index" v-for="(status, index) in sessionInfo" x-small class="top-15" :class="index > 0 ? 'ml-2' : 'ml-3'" :color="status.color">
          {{ status.text }}
        </v-chip>
      </div>
    </template>
  </SessionCard>
</template>

<script>
import { SESSION_STATUS, ENROLL_STATUS } from '@/utils/constants'
import SessionCard from '@/views/main/components/SessionCard'

export default {
  name: 'SessionCardWithChips',
  components: { SessionCard },
  props: {
    session: {
      type: Object,
      required: true
    },
    isMySession: {
      type: Boolean,
      required: true
    }
  },
  computed: {
    isEnrollingStatus() {
      return this.session.status === SESSION_STATUS.PREPARING || this.session.status === SESSION_STATUS.ENROLLING
    }
  },
  created() {
    this.sessionInfo = this.sessionStatus()
  },
  methods: {
    isRecruitWaitings(status) {
      return status.text === ENROLL_STATUS.recruitWaitings
    },
    sessionStatus() {
      switch (this.session.status) {
        case SESSION_STATUS.PREPARING:
          return this.status.preparing
        case SESSION_STATUS.ENROLLING:
          return this.session.possibleEnrollment ? this.status.enrolling : this.status.fulled
        case SESSION_STATUS.LECTURING:
          return this.status.lecturing
        case SESSION_STATUS.FINISH:
          return this.status.finish
        default:
          return null
      }
    }
  },
  data() {
    return {
      status: {
        preparing: [
          {
            text: '준비중',
            color: 'cyan lighten-5'
          }
        ],
        enrolling: [
          {
            text: '모집중',
            color: 'cyan accent-1'
          }
        ],
        lecturing: [
          {
            text: '진행중',
            color: 'green lighten-4'
          }
        ],
        finish: [
          {
            text: '종료',
            color: 'grey lighten-2'
          }
        ],
        fulled: [
          {
            text: '모집마감',
            color: 'grey darken-2 text-white'
          },
          {
            text: '대기모집중',
            color: 'light-blue lighten-4'
          }
        ]
      },
      sessionInfo: []
    }
  }
}
</script>
