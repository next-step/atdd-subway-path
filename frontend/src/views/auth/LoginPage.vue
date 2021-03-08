<template>
  <div class="d-flex flex-column justify-center height-100vh-65px">
    <div class="d-flex justify-center relative">
      <v-card width="350" class="card-border px-3 pt-3 pb-5">
        <v-form ref="loginForm" v-model="valid" @submit.prevent>
          <v-card-title class="font-weight-bold justify-center">
            로그인
          </v-card-title>
          <v-card-text class="px-4 pt-4 pb-0">
            <div class="d-flex">
              <v-text-field
                color="grey darken-1"
                label="이메일을 입력해주세요."
                v-model="member.email"
                prepend-inner-icon="mdi-email"
                dense
                outlined
                :rules="rules.member.email"
              ></v-text-field>
            </div>
            <div class="d-flex mt-2">
              <v-text-field
                color="grey darken-1"
                label="비밀번호를 입력해주세요."
                v-model="member.password"
                prepend-inner-icon="mdi-lock"
                dense
                outlined
                type="password"
                :rules="rules.member.password"
              ></v-text-field>
            </div>
          </v-card-text>
          <v-card-actions class="px-4 pb-4">
            <v-spacer></v-spacer>
            <v-btn @click.prevent="onLogin" :disabled="!valid" color="amber" class="width-100">
              로그인
            </v-btn>
          </v-card-actions>
          <router-link to="join" class="d-flex justify-center">
            <span>아직 회원이 아니신가요?</span>
          </router-link>
        </v-form>
      </v-card>
    </div>
  </div>
</template>

<script>
import validator from '@/utils/validator'
import { SNACKBAR_MESSAGES } from '@/utils/constants'
import { mapActions, mapGetters, mapMutations } from 'vuex'
import { SHOW_SNACKBAR } from '@/store/shared/mutationTypes'
import { LOGIN } from '@/store/shared/actionTypes'

export default {
  name: 'LoginPage',
  computed: {
    ...mapGetters(['accessToken'])
  },
  methods: {
    ...mapMutations([SHOW_SNACKBAR]),
    ...mapActions([LOGIN]),
    isValid() {
      return this.$refs.loginForm.validate()
    },
    async onLogin() {
      if (!this.isValid()) {
        return
      }
      try {
        const { email, password } = this.member
        await this.login({ email, password })
        await this.$router.replace(`/`)
        this.showSnackbar(SNACKBAR_MESSAGES.LOGIN.SUCCESS)
      } catch (e) {
        this.showSnackbar(SNACKBAR_MESSAGES.LOGIN.FAIL)
      }
    }
  },
  data() {
    return {
      valid: false,
      rules: { ...validator },
      member: {
        email: '',
        password: ''
      }
    }
  }
}
</script>
