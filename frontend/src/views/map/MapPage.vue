<template>
  <div>
    <section class="lists-container mt-10 mr-12">
      <v-card v-for="(line, index) in map" max-width="300" :key="index" class="mx-5 list">
        <v-toolbar :color="line.color" dense flat>
          <v-toolbar-title>{{ line.name }}</v-toolbar-title>
        </v-toolbar>
        <v-card-text class="overflow-y-auto pa-0">
          <v-list dense class="max-height-300px">
            <v-list-item v-for="(sesction, index) in line.stations" :key="index">
              <v-list-item-content>
                <v-list-item-title v-text="sesction.station.name"></v-list-item-title>
              </v-list-item-content>
            </v-list-item>
          </v-list>
        </v-card-text>
      </v-card>
    </section>
  </div>
</template>

<script>
import { mapActions, mapGetters } from 'vuex'
import { FETCH_MAP } from '@/store/shared/actionTypes'

export default {
  name: 'MapPage',
  created() {
    this.fetchMap()
  },
  computed: {
    ...mapGetters(['map'])
  },
  methods: {
    ...mapActions([FETCH_MAP])
  }
}
</script>
<style lang="scss" scoped>
.lists-container {
  display: flex;
  align-items: start;
  padding: 0 0.8rem 0.8rem;
  overflow-x: auto;
  height: calc(100vh - 110px);

  .list {
    flex: 0 0 27rem;
    display: flex;
    flex-direction: column;
    background-color: #e2e4e6;
    max-height: calc(100vh - 140px);
    border-radius: 0.3rem;
    margin-right: 1rem;
  }
}
</style>
