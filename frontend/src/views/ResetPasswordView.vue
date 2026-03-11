<script setup lang="ts">

import { ref } from "vue"
import { useRouter,useRoute } from "vue-router"
import api from "../api/api"

const router = useRouter()
const route = useRoute()

const email = route.query.email as string

const password = ref("")
const message = ref("")
const error = ref("")

const reset = async ()=>{

try{

const res = await api.post("/auth/reset-password",{
email:email,
newPassword:password.value
})

message.value = res.data.message

router.push("/login")

}catch(err:any){

error.value = err.response?.data?.message

}

}

</script>

<template>

<div class="auth-container">

<div class="auth-card">

<h2>Reset password</h2>

<input
type="password"
v-model="password"
placeholder="New password"
class="input"
/>

<button class="primary-btn" @click="reset">
Reset password
</button>

<p class="error" v-if="error">{{error}}</p>
<p class="success" v-if="message">{{message}}</p>

</div>

</div>

</template>