<template>
  <SessionList :sessions="sessions" :is-my-session="true">
    <v-chip class="mb-2" color="#333" text-color="white">
      <v-avatar left>
        <v-img :src="loginUser.avatarUrl"></v-img>
      </v-avatar>
      {{ loginUser.name }}님의 강의
    </v-chip>
  </SessionList>
</template>

<script>
import SessionList from '@/views/main/components/SessionList'

export default {
  name: 'MySessionsView',
  props: {
    loginUser: {
      type: Object,
      required: true
    },
    sessions: {
      type: Array,
      required: true
    }
  },
  components: {
    SessionList
  }
}
</script>
