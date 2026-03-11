<script setup lang="ts">

import { ref } from "vue"
import { useRouter } from "vue-router"
import api from "../api/api"

const router = useRouter()

const username = ref("")
const email = ref("")
const password = ref("")
const confirmPassword = ref("")

const error = ref("")
const success = ref("")
const loading = ref(false)

const signup = async () => {

  error.value = ""
  success.value = ""

  if(password.value !== confirmPassword.value){
    error.value = "Passwords do not match"
    return
  }

  loading.value = true

  try{

    const res = await api.post("/auth/signup",{
      username: username.value,
      email: email.value,
      password: password.value,
      role: "USER"
    })

    success.value = res.data.message

    setTimeout(()=>{
      router.push("/login")
    },1500)

  }catch(err:any){

    if(err.response?.data?.message){
      error.value = err.response.data.message
    }else{
      error.value = "Signup failed"
    }

  }finally{
    loading.value = false
  }

}

</script>

<template>

<div class="auth-container">

  <div class="auth-card">

    <h1 class="logo">TaskFlow</h1>

    <h2>Create account</h2>

    <input
      v-model="username"
      placeholder="Username"
      class="input"
    />

    <input
      v-model="email"
      placeholder="Email"
      class="input"
    />

    <input
      type="password"
      v-model="password"
      placeholder="Password"
      class="input"
    />

    <input
      type="password"
      v-model="confirmPassword"
      placeholder="Confirm password"
      class="input"
    />

    <button
      class="primary-btn"
      @click="signup"
      :disabled="loading"
    >
      {{ loading ? "Creating account..." : "Create account" }}
    </button>

    <p v-if="error" class="error">{{ error }}</p>
    <p v-if="success" class="success">{{ success }}</p>

    <div class="links">

      <router-link to="/login">
        Already have an account? Sign in
      </router-link>

    </div>

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
}

.logo{
  text-align:center;
  margin-bottom:10px;
}

h2{
  text-align:center;
  margin-bottom:25px;
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

.links{
  margin-top:15px;
  text-align:center;
}

.links a{
  font-size:13px;
  color:#3b82f6;
}

.error{
  margin-top:10px;
  color:#e74c3c;
  text-align:center;
}

.success{
  margin-top:10px;
  color:#2ecc71;
  text-align:center;
}

</style>