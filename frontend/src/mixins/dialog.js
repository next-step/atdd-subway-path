import validator from '@/utils/validator'

const dialog = {
  methods: {
    closeDialog() {
      this.close = !this.close
    },
    isValid($form) {
      return $form.validate()
    }
  },
  data() {
    return {
      close: false,
      rules: {
        ...validator
      },
      valid: false,
      isRequested: false
    }
  }
}

export default dialog
