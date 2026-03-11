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

<style scoped>

.auth-container{
  display:flex;
  justify-content:center;
  align-items:center;
  height:100vh;
  background:#f4f6fb;
  font-family: Arial, Helvetica, sans-serif;
}

.auth-card{
  width:380px;
  padding:40px;
  background:white;
  border-radius:10px;
  box-shadow:0 10px 30px rgba(0,0,0,0.08);
  text-align:center;
}

h2{
  margin-bottom:25px;
  font-weight:600;
}

.input{
  width:100%;
  padding:12px;
  margin-bottom:15px;
  border:1px solid #ddd;
  border-radius:6px;
  font-size:14px;
}

.input:focus{
  outline:none;
  border-color:#3b82f6;
}

.primary-btn{
  width:100%;
  padding:12px;
  background:#3b82f6;
  border:none;
  border-radius:6px;
  color:white;
  font-size:14px;
  cursor:pointer;
  transition:0.2s;
}

.primary-btn:hover{
  background:#2563eb;
}

.error{
  margin-top:12px;
  color:#e74c3c;
  font-size:14px;
}

.success{
  margin-top:12px;
  color:#16a34a;
  font-size:14px;
}

</style>