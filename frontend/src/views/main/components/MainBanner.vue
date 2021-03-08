<template>
  <div class="banner-container">
    <div class="desktop-banner-container bg-dark relative">
      <div class="banner relative">
        <div class="max-width-md mx-auto">
          <a :href="`/s/${latestSession.id}`" class="main-session-container width-90">
            <v-img
              class="cover-img shadow-md relative"
              :src="latestSession.coverUrl ? latestSession.coverUrl : '/images/default/default-cover.jpeg'"
            ></v-img>
            <div class="session-info relative ml-auto pdd-left-15 pdd-top-5">
              <h1 class="text-white d-inline-block font-bamin mrg-btm-0">
                {{ latestSession.title }}
              </h1>
              <p class="text-white mrg-btm-5 font-size-16">{{ latestSession.title }} 커리큘럼을 지금 확인해 보세요!</p>
              <v-list-item class="grow pl-0">
                <v-list-item-avatar color="grey darken-3">
                  <v-img class="elevation-6" :src="latestSession.creator.avatarUrl"></v-img>
                </v-list-item-avatar>

                <v-list-item-content>
                  <v-list-item-title class="text-white">{{ latestSession.creator.name }}</v-list-item-title>
                </v-list-item-content>
              </v-list-item>
              <img class="lookfor-img absolute right-0" src="/images/pages/index/lookfor.png" />
            </div>
          </a>
        </div>
      </div>
    </div>
    <div>
      <div class="mobile-banner-container bg-dark">
        <div class="width-90 mx-auto min-width-300px max-width-400px">
          <v-chip x-small class="text-dark" color="cyan lighten-1">New</v-chip>
          <h1 class="relative display-1 text-bold text-white mobile-banner-session-title">
            {{ latestSession.title }}
          </h1>
          <a :href="`/s/${latestSession.id}`" class="main-session-container width-90">
            <v-img
              class="shadow-md mt-5 rounded"
              :src="latestSession.coverUrl ? latestSession.coverUrl : '/images/default/default-cover.jpeg'"
            ></v-img>
          </a>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'MainBanner',
  props: {
    latestSession: {
      type: Object,
      required: true
    }
  }
}
</script>

<style lang="scss" scoped>
.desktop-banner-container {
  height: 160px;
  margin-bottom: 100px;

  .banner {
    height: 240px;
  }

  .lookfor-img {
    width: 100px;
    top: 90px;
  }

  .main-session-container {
    height: 150px;

    .cover-img {
      width: 250px;
      height: 170px;
      top: 30px;
    }

    .session-info {
      width: calc(100% - 290px);
      top: -150px;
    }
  }
}

.mobile-banner-container {
  display: none;
}

@media only screen and (max-width: 600px) {
  .banner-container {
    height: 250px;
  }

  .desktop-banner-container {
    display: none;
  }

  .mobile-banner-container {
    display: block;
    height: 220px;
  }

  .mobile-banner-session-title {
    display: block;
    -webkit-line-clamp: 2;
    display: -webkit-box;
    -webkit-box-orient: vertical;
    overflow: hidden;
    text-overflow: ellipsis;
  }
}
</style>
