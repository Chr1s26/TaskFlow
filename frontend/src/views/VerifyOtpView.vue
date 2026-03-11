<script setup lang="ts">

import { ref } from "vue"
import { useRouter,useRoute } from "vue-router"
import api from "../api/api"

const router = useRouter()
const route = useRoute()

const email = route.query.email as string
const otp = ref("")
const error = ref("")

const verify = async ()=>{

try{

await api.post("/auth/verify-otp",{
email:email,
otp:otp.value
})

router.push("/reset-password?email="+email)

}catch(err:any){

error.value = err.response?.data?.message

}

}

</script>

<template>

<div class="auth-container">

<div class="auth-card">

<h2>Verify OTP</h2>

<input
v-model="otp"
placeholder="Enter OTP"
class="input"
/>

<button class="primary-btn" @click="verify">
Verify
</button>

<p class="error" v-if="error">{{error}}</p>

</div>

</div>

</template>