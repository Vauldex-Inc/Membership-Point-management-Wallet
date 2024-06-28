<template>
  <div
    class="flex min-h-full flex-1 flex-col justify-center px-6 py-12 lg:px-8"
  >
    <div class="sm:mx-auto sm:w-full sm:max-w-sm">
      <h2
        class="mt-10 text-center text-2xl font-bold leading-9 tracking-tight text-gray-800"
      >
        {{ t('sign_in.header') }}
      </h2>
    </div>

    <div class="sm:mx-auto sm:w-full sm:max-w-sm">
      <PLanguageSwitcher id="language-switcher" class="text-center" />
    </div>

    <div class="mt-10 sm:mx-auto sm:w-full sm:max-w-sm">
      <form
        name="signinField"
        class="space-y-6"
        action="#"
        method="POST"
        @submit.prevent="signin"
      >
        <PEmailField v-model="merchant.email" :show-email-validation="false" />

        <PPasswordField
          v-model="merchant.password"
          :show-password="showPassword"
          :show-password-validation="false"
          :placeholder="t('forms.password')"
          @toggle-password="togglePassword"
        >
          {{ t('forms.password') }}
          <span class="red"> *</span>
        </PPasswordField>

        <div
          class="text-right text-sm font-semibold text-gray-800 hover:text-gray-700"
        >
          <NuxtLinkLocale to="/password/forgot">
            {{ t('sign_in.forgot_password') }}
          </NuxtLinkLocale>
        </div>
        <p-button
          :is-loading="isLoading"
          :type="'submit'"
          :is-disabled="isDisabled"
        >
          {{ t('forms.submit') }}
        </p-button>
      </form>

      <p class="mt-10 text-center text-sm text-gray-700">
        {{ t('sign_in.not_a_member') }}
        {{ ' ' }}
        <NuxtLinkLocale
          to="/signup"
          class="font-semibold leading-6 text-gray-800 hover:text-gray-700"
        >
          {{ t('sign_in.sign_up_now') }}
        </NuxtLinkLocale>
      </p>
    </div>
  </div>
</template>

<script setup lang="ts">
definePageMeta({
  middleware: 'guest',
  layout: false
})
const { t } = useI18n()
const { signin, merchant } = useMerchant()
const { isLoading, isDisabled } = useUtils()
const { showPassword, togglePassword } = usePasswordUtil()
</script>

<style lang="postcss" scoped>
  .poin-input {
    @apply mt-2 block w-full rounded-md border-0 p-3 py-1.5 text-gray-800 shadow-sm ring-1 ring-inset ring-gray-300 focus:ring-2 focus:ring-inset focus:ring-gray-800 sm:text-sm sm:leading-6;
  }
  .poin-label {
    @apply block text-sm font-medium leading-6 text-gray-800;
  }

  .red {
    @apply text-red-700;
  }
</style>
