<template>
  <v-menu bottom v-model="isActive">
    <template #activator="{ on }">
      <div v-on="on" class="d-inline-block" @click="showMenu">
        <IconButton>
          <template slot="icon">
            <slot name="icon" />
          </template>
        </IconButton>
      </div>
    </template>
    <v-list>
      <slot name="items" />
    </v-list>
  </v-menu>
</template>

<script>
import IconButton from '@/components/buttons/IconButton'
import { mapGetters, mapMutations } from 'vuex'
import { SHOW_MENU } from '@/store/shared/mutationTypes'

export default {
  name: 'IconMenu',
  components: { IconButton },
  computed: {
    ...mapGetters(['menuStatus'])
  },
  watch: {
    menuStatus() {
      this.isActive = this.menuStatus
    }
  },
  methods: {
    ...mapMutations([SHOW_MENU])
  },
  data() {
    return {
      isActive: false
    }
  }
}
</script>
