<template>
  <v-snackbar v-model="snackbar" :timeout="2000" multi-line>
    <div class="text-center width-100 font-size-15">{{ snackbarMsg }}</div>
  </v-snackbar>
</template>

<script>
import { mapGetters } from 'vuex'

export default {
  name: 'Snackbar',
  computed: {
    ...mapGetters(['isShow', 'message'])
  },
  watch: {
    isShow() {
      this.showSnackbar(this.message)
    }
  },
  methods: {
    showSnackbar(msg) {
      this.snackbarMsg = msg
      this.snackbar = true
    }
  },
  data() {
    return {
      snackbar: false,
      snackbarMsg: ''
    }
  }
}
</script>
