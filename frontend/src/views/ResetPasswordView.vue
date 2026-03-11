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