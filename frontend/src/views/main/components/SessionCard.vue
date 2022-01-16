<template>
  <v-card :href="`/s/${session.id}`" :elevation="isMySession ? 0 : 1" :outlined="isMySession" class="no-pdd session-card">
    <v-img class="session-card-image" :src="session.coverUrl ? session.coverUrl : '/images/default/default-cover.jpeg'"></v-img>
    <div class="px-3 pb-4">
      <slot name="chip"></slot>
      <v-card-title class="text-overflow-ellipsis height-70px text-normal lh-1-3">
        <span>{{ session.title }}</span>
      </v-card-title>
      <v-list-item class="grow mt-1">
        <v-list-item-avatar color="grey darken-3">
          <v-img class="elevation-7" :src="session.creator.avatarUrl"></v-img>
        </v-list-item-avatar>

        <v-list-item-content>
          <v-list-item-title>
            {{ session.creator.name }}
          </v-list-item-title>
        </v-list-item-content>
      </v-list-item>
    </div>
  </v-card>
</template>

<script>
export default {
  name: 'SessionCard',
  props: {
    session: {
      type: Object,
      required: true
    },
    isMySession: {
      type: Boolean,
      required: false
    }
  }
}
</script>

<style lang="scss" scoped>
.text-overflow-ellipsis {
  display: block;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}

.height-70px {
  height: 70px;
}

.mrg-top-7 {
  margin-top: 7px;
}

.session-card-image {
  min-height: 145px;
}

@media only screen and (max-width: 600px) {
  .session-card {
    max-width: 300px;
  }

  .session-card-image {
    height: 195px;
  }
}
</style>
