<template>
    <div class="error-page">
        <h1>{{ errorCode }}</h1>
        <p>{{ errorMessage }}</p>
    </div>
</template>
<script setup>
defineProps({
    errorCode: {
        type: Number,
        required: true
    },
    errorMessage: {
        type: String,
        required: true
    }
})
</script>