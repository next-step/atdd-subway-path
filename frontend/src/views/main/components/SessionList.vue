<template>
  <div class="width-100">
    <div class="desktop-view">
      <slot></slot>
      <v-row>
        <v-col :key="session.id" v-for="session in sessions" cols="12" sm="4" md="3" lg="3" class="no-pdd">
          <div class="ma-2">
            <SessionCardWithChips :session="session" :is-my-session="isMySession" />
          </div>
        </v-col>
      </v-row>
    </div>
    <div class="pa-5 top-100 relative mobile-view">
      <slot></slot>
      <div v-if="sessions.length > 1">
        <v-sheet class="mx-auto mt-3">
          <v-slide-group class="slide-group" :next-icon="nextIcon ? 'mdi-plus' : undefined">
            <v-slide-item v-for="session in sessions" :key="session.id" class="mr-2">
              <SessionCardWithChips :session="session" :is-my-session="isMySession" />
            </v-slide-item>
          </v-slide-group>
        </v-sheet>
      </div>
      <div v-if="sessions.length === 1">
        <v-row>
          <v-col cols="12" sm="4" md="3" lg="3" class="no-pdd">
            <div class="ma-2">
              <SessionCardWithChips :session="sessions[0]" :is-my-session="isMySession" />
            </div>
          </v-col>
        </v-row>
      </div>
    </div>
  </div>
</template>

<script>
import SessionCardWithChips from '@/views/main/components/SessionCardWithChips'

export default {
  name: 'SessionList',
  props: {
    sessions: {
      type: Array,
      required: true
    },
    isMySession: {
      type: Boolean,
      required: false
    }
  },
  components: {
    SessionCardWithChips
  },
  mounted() {},
  data() {
    return {
      nextIcon: 'mdi-plus'
    }
  }
}
</script>

<style scoped>
.text-overflow-ellipsis {
  display: block;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}

.slide-group {
  height: 350px;
}
</style>
