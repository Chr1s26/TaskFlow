<script setup lang="ts">

import { ref } from "vue"
import { useRouter } from "vue-router"
import api from "../api/api"

const router = useRouter()

const username = ref("")
const password = ref("")
const error = ref("")
const loading = ref(false)

const login = async () => {

  error.value = ""
  loading.value = true

  try {

    const res = await api.post("/auth/signin", {
      username: username.value,
      password: password.value
    })

    const token = res.data.jwtToken
    localStorage.setItem("token", token)

    router.push("/tasks")

  } catch (err:any) {

    if(err.response?.data?.message){
      error.value = err.response.data.message
    }else{
      error.value = "Login failed"
    }

  } finally {
    loading.value = false
  }
}

</script>

<template>

<div class="auth-container">

  <div class="auth-card">

    <h1 class="logo">TaskFlow</h1>

    <h2>Sign in</h2>

    <input
      v-model="username"
      placeholder="Username"
      class="input"
    />

    <input
      type="password"
      v-model="password"
      placeholder="Password"
      class="input"
    />

    <button class="primary-btn" @click="login" :disabled="loading">
      {{ loading ? "Signing in..." : "Sign in" }}
    </button>

    <p v-if="error" class="error">
      {{ error }}
    </p>

    <div class="links">

      <router-link to="/forgot-password">
        Forgot password?
      </router-link>

      <router-link to="/signup">
        Create account
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
  display:flex;
  justify-content:space-between;
  margin-top:15px;
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

</style>