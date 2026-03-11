<script setup lang="ts">

import { ref } from "vue"
import { useRouter } from "vue-router"
import api from "../api/api"

const router = useRouter()

const email = ref("")
const message = ref("")
const error = ref("")

const sendOtp = async () => {

  message.value=""
  error.value=""

  try{

    const res = await api.post("/auth/forgot-password",{
      email: email.value
    })

    message.value = res.data.message

    router.push("/verify-otp?email=" + email.value)

  }catch(err:any){

    error.value = err.response?.data?.message || "Failed"

  }

}

</script>

<template>

<div class="auth-container">

<div class="auth-card">

<h2>Forgot password</h2>

<input
v-model="email"
placeholder="Email"
class="input"
/>

<button class="primary-btn" @click="sendOtp">
Send OTP
</button>

<p class="error" v-if="error">{{error}}</p>
<p class="success" v-if="message">{{message}}</p>

<router-link to="/login">Back to login</router-link>

</div>

</div>

</template>

<style scoped>

.auth-container{
  display:flex;
  justify-content:center;
  align-items:center;
  height:100vh;
  background:#f7f8fb;
}

.auth-card{
  width:360px;
  padding:40px;
  background:white;
  border-radius:10px;
  box-shadow:0 10px 30px rgba(0,0,0,0.08);
  text-align:center;
}

h2{
  margin-bottom:20px;
}

.input{
  width:100%;
  padding:10px;
  margin-bottom:12px;
  border:1px solid #ddd;
  border-radius:6px;
}

.primary-btn{
  width:100%;
  padding:10px;
  background:#3b82f6;
  border:none;
  border-radius:6px;
  color:white;
  cursor:pointer;
}

.primary-btn:hover{
  background:#2563eb;
}

.error{
  margin-top:10px;
  color:#e74c3c;
}

.success{
  margin-top:10px;
  color:#2ecc71;
}

a{
  display:block;
  margin-top:15px;
  font-size:14px;
  color:#3b82f6;
}

</style>