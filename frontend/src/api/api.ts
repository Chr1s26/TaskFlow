import axios from "axios"

// const api = axios.create({
//   baseURL: "http://localhost:8084/api",
//   headers: {
//     "Content-Type": "application/json"
//   }
// })

const api = axios.create({
  baseURL: "/api",
  headers: {
    "Content-Type": "application/json"
  }
})

const publicEndpoints = [
  "/auth/signin",
  "/auth/signup",
  "/auth/forgot-password",
  "/auth/verify-otp",
  "/auth/reset-password"
]

api.interceptors.request.use((config) => {

  const token = localStorage.getItem("token")

  if (
    token &&
    config.url &&
    !publicEndpoints.some(e => config.url?.startsWith(e))
  ) {
    config.headers.Authorization = `Bearer ${token}`
  }

  return config
})

export default api