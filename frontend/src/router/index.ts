import { createRouter, createWebHistory } from "vue-router"

import LoginView from "../views/LoginView.vue"
import SignupView from "../views/SignupView.vue"
import ForgotPasswordView from "../views/ForgotPasswordView.vue"
import VerifyOtpView from "../views/VerifyOtpView.vue"
import ResetPasswordView from "../views/ResetPasswordView.vue"

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: "/", redirect: "/login" },

    {
      path: "/login",
      name: "login",
      component: LoginView
    },

    {
      path: "/signup",
      name: "signup",
      component: SignupView
    },

    {
      path: "/forgot-password",
      name: "forgot-password",
      component: ForgotPasswordView
    },

    {
      path: "/verify-otp",
      name: "verify-otp",
      component: VerifyOtpView
    },

    {
      path: "/reset-password",
      name: "reset-password",
      component: ResetPasswordView
    }
  ]
})

export default router