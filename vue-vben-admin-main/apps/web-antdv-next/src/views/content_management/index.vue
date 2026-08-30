<template>
  span
    <Button type="primary" @click="notify('info')">信息</Button>
    <Button type="primary" @click="notify('success')">成功</Button>
    <Button type="primary" @click="notify('warning')">警告</Button>
    <Button type="primary" @click="notify('error')">错误</Button>
</template>
