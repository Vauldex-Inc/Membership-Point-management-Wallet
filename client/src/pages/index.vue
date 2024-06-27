<template>
  <div class="bg-white">
    <header class="absolute inset-x-0 top-0 z-50">
      <nav
        class="flex items-center justify-between p-6 lg:px-8"
        aria-label="Global"
      >
        <div class="flex lg:flex-1">
          <NuxtLinkLocale to="/" class="p-1.5">
            <h1 class="text-3xl font-bold">
              {{ t('app.title') }}
            </h1>
          </NuxtLinkLocale>
        </div>
        <div class="flex flex-1 justify-end">
          <NuxtLinkLocale
            to="/signup"
            class="mr-3 text-sm font-semibold leading-6 text-gray-800"
          >
            {{ t('pages.sign_up') }}
          </NuxtLinkLocale>
          <NuxtLinkLocale
            to="/signin"
            class="text-sm font-semibold leading-6 text-gray-800"
          >
            {{ t('pages.sign_in') }}
          </NuxtLinkLocale>
        </div>
      </nav>
    </header>
  </div>
</template>

<script setup lang="ts">
definePageMeta({
  middleware: 'guest',
  layout: false
})

const { t } = useI18n()
</script>
