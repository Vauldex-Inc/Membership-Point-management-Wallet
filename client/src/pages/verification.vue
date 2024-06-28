<template>
  <div
    class="flex min-h-full flex-1 flex-col justify-center px-6 py-12 lg:px-8"
  >
    <div class="sm:mx-auto sm:w-full sm:max-w-sm">
      <h2
        class="mt-4 text-center text-2xl font-bold leading-9 tracking-tight text-gray-800"
      >
        {{ t('verification.header') }}
      </h2>
    </div>

    <div class="sm:mx-auto sm:w-full sm:max-w-sm">
      <h3 class="text-l mt-8 font-bold leading-9 tracking-tight text-gray-800">
        {{ t('verification.check_email') }}
      </h3>

      <p class="text-sm text-gray-700">
        {{ t('verification.note') }}
      </p>
    </div>

    <div class="mt-10 sm:mx-auto sm:w-full sm:max-w-sm">
      <form
        name="verificationCodeField"
        action="#"
        method="PATCH"
        @submit.prevent="verificationCode"
      >
        <PVerificationCodeField v-model="code" />
        <p-button
          :is-loading="isLoading"
          :type="'submit'"
          :is-disabled="isDisabled"
        >
          {{ t('forms.verify') }}
        </p-button>
      </form>

      <p class="mt-4 text-center text-sm text-gray-700">
        {{ t('verification.did_not_receive') }}
        <button
          class="font-semibold leading-6 text-gray-800 hover:text-gray-700"
          @click.prevent="resendCode"
        >
          {{ t('verification.resend') }}
        </button>
      </p>
    </div>
  </div>
</template>

<script setup lang="ts">
definePageMeta({
  middleware: ['auth', 'account-verified'],
  layout: false
})
const { t } = useI18n()
const { isLoading, isDisabled } = useUtils()
const { code, verificationCode, resendCode } = useVerification()
</script>

<style lang="postcss" scoped></style>
