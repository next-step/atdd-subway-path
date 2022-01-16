<template>
  <div class="d-flex flex-column justify-center height-100vh-65px">
    <div class="d-flex justify-center relative">
      <v-card width="500" max-width="600" max-height="600" class="card-border">
        <v-card-title class="font-weight-bold justify-center relative">
          노선 관리
        </v-card-title>
        <v-card-text class="relative px-0 pb-0 mb-6 line-list-container d-flex flex-column">
          <div class="relative">
            <LineCreateButton />
          </div>
          <v-divider />
          <div class="mt-4 overflow-y-auto">
            <v-list-item-group v-model="line" color="grey darken-3">
              <v-list-item v-for="line in lines" :key="line.name">
                <v-list-item-content>
                  <v-list-item-title @click="setLineDetail(line)">
                    <v-avatar :color="line.color" size="10" class="relative bottom-1" left />
                    <span>{{ line.name }}</span>
                  </v-list-item-title>
                </v-list-item-content>
                <v-list-item-action class="flex-row">
                  <LineEditButton :line="line" />
                  <LineDeleteButton :line="line" />
                </v-list-item-action>
              </v-list-item>
            </v-list-item-group>
          </div>
        </v-card-text>
      </v-card>
    </div>
  </div>
</template>

<script>
import { mapActions, mapGetters } from 'vuex'
import LineDeleteButton from '@/views/line/components/LineDeleteButton'
import LineEditButton from '@/views/line/components/LineEditButton'
import LineCreateButton from '@/views/line/components/LineCreateButton'
import { FETCH_LINES } from '@/store/shared/actionTypes'

export default {
  name: 'LinePage',
  components: { LineCreateButton, LineEditButton, LineDeleteButton },
  created() {
    this.fetchLines()
  },
  computed: {
    ...mapGetters(['lines'])
  },
  methods: {
    ...mapActions([FETCH_LINES]),
    setLineDetail(line) {
      this.lineDetail = line
    }
  },
  data() {
    return {
      line: {},
      lineDetail: null
    }
  }
}
</script>

<style lang="scss" scoped>
.line-list-container {
  height: calc(100% - 80px);
}
</style>
