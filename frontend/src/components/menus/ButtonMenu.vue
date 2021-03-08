<template>
  <v-menu offset-y transition="slide-y-transition" class="z-10" bottom>
    <template v-slot:activator="{ on, attrs }">
      <div v-on="on" v-bind="attrs">
        <slot name="button" />
      </div>
    </template>
    <v-list class="py-0">
      <slot name="items" />
    </v-list>
  </v-menu>
</template>

<script>
export default {
  name: 'ButtonMenu'
}
</script>
