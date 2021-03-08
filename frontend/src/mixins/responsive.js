const responsive = {
  methods: {
    xsOnly() {
      return this.$vuetify.breakpoint.xsOnly
    },
    smAndUp() {
      return this.$vuetify.breakpoint.smAndUp
    }
  }
}

export default responsive
