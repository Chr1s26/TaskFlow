<script setup lang="ts">

import { ref,onMounted } from "vue"
import { useRouter } from "vue-router"
import api from "../api/api"
import { getErrorMessage } from "../utils/error"

const router = useRouter()

const users = ref<any[]>([])

const username = ref("")
const role = ref("")

const error = ref("")
const success = ref("")

const page = ref(0)
const totalPages = ref(0)
const size = 5

const editingUserId = ref<number|null>(null)

const editingUser = ref<any>({})

const newUser = ref({
  name:"",
  email:"",
  password:"",
  role:"USER"
})

/* ---------------- CURRENT USER ---------------- */

const loadCurrentUser = async ()=>{

  try{

    const res = await api.get("/users/me")

    username.value = res.data.name
    role.value = res.data.role

    if(role.value !== "ADMIN"){
      router.push("/tasks")
    }

  }catch{

    router.push("/login")

  }

}

/* ---------------- LOAD USERS ---------------- */

const loadUsers = async ()=>{

  const res = await api.get("/users",{
    params:{
      page:page.value,
      size:size
    }
  })

  users.value = res.data.content
  totalPages.value = res.data.page.totalPages
  page.value = res.data.page.number

}

/* ---------------- CREATE USER ---------------- */

const createUser = async ()=>{

  error.value=""
  success.value=""

  try{

    await api.post("/users",newUser.value)

    success.value="User created successfully"

    newUser.value={
      name:"",
      email:"",
      password:"",
      role:"USER"
    }

    loadUsers()

  }catch(err:any){

    error.value = getErrorMessage(err)

  }

}

/* ---------------- EDIT USER ---------------- */

const startEdit = (user:any)=>{

  editingUserId.value = user.id

  editingUser.value = {...user}

}

const saveEdit = async ()=>{

  try{

    await api.put("/users/"+editingUserId.value,{
      name:editingUser.value.name,
      email:editingUser.value.email,
      profileImageUrl:editingUser.value.profileImageUrl
    })

    success.value="User updated"

    editingUserId.value=null

    loadUsers()

  }catch(err:any){

    error.value=getErrorMessage(err)

  }

}

/* ---------------- DELETE USER ---------------- */

const deleteUser = async (id:number)=>{

  try{

    await api.delete("/users/"+id)

    success.value="User deleted"

    loadUsers()

  }catch(err:any){

    error.value=getErrorMessage(err)

  }

}

/* ---------------- PAGINATION ---------------- */

const nextPage = ()=>{

  if(page.value < totalPages.value-1){

    page.value++

    loadUsers()

  }

}

const prevPage = ()=>{

  if(page.value>0){

    page.value--

    loadUsers()

  }

}

/* ---------------- NAVIGATION ---------------- */

const goTasks = ()=>{

  router.push("/tasks")

}

const logout = ()=>{

  localStorage.removeItem("token")

  router.push("/login")

}

onMounted(async ()=>{

  await loadCurrentUser()

  loadUsers()

})

</script>

<template>

<div class="dashboard">

<!-- HEADER -->

<header class="header">

<h2>TaskFlow</h2>

<div class="header-right">

<div class="avatar"></div>

<span class="username">{{username}}</span>

<button class="task-btn" @click="goTasks">
Task Panel
</button>

<button class="logout" @click="logout">
Logout
</button>

</div>

</header>


<div class="container">

<!-- ERROR / SUCCESS -->

<p v-if="error" class="error">{{error}}</p>
<p v-if="success" class="success">{{success}}</p>


<!-- CREATE USER -->

<div class="create-card">

<h3>Create User</h3>

<div class="create-row">

<input v-model="newUser.name" placeholder="Name"/>

<input v-model="newUser.email" placeholder="Email"/>

<input v-model="newUser.password" type="password" placeholder="Password"/>

<select v-model="newUser.role">

<option value="USER">USER</option>
<option value="ADMIN">ADMIN</option>

</select>

<button @click="createUser">
Create
</button>

</div>

</div>


<!-- USER TABLE -->

<div class="table-card">

<h3>Users</h3>

<table>

<thead>

<tr>

<th>ID</th>
<th>Name</th>
<th>Email</th>
<th>Role</th>
<th>Actions</th>

</tr>

</thead>

<tbody>

<tr v-for="user in users" :key="user.id">

<td>{{user.id}}</td>

<td>

<input
v-if="editingUserId===user.id"
v-model="editingUser.name"
/>

<span v-else>{{user.name}}</span>

</td>

<td>

<input
v-if="editingUserId===user.id"
v-model="editingUser.email"
/>

<span v-else>{{user.email}}</span>

</td>

<td>{{user.role}}</td>

<td>

<button
v-if="editingUserId!==user.id"
class="edit"
@click="startEdit(user)"
>
Edit
</button>

<button
v-if="editingUserId===user.id"
class="save"
@click="saveEdit"
>
Save
</button>

<button
class="delete"
@click="deleteUser(user.id)"
>
Delete
</button>

</td>

</tr>

</tbody>

</table>

</div>


<!-- PAGINATION -->

<div class="pagination">

<button @click="prevPage">Prev</button>

<span>Page {{page+1}} / {{totalPages}}</span>

<button @click="nextPage">Next</button>

</div>

</div>

</div>

</template>

<style scoped>

.dashboard{
background:#f4f6fb;
min-height:100vh;
font-family:Arial;
}

/* HEADER */

.header{
height:70px;
background:white;
display:flex;
justify-content:space-between;
align-items:center;
padding:0 40px;
box-shadow:0 2px 10px rgba(0,0,0,0.05);
}

.header-right{
display:flex;
align-items:center;
gap:15px;
}

.avatar{
width:38px;
height:38px;
border-radius:50%;
background:#3b82f6;
}

.task-btn{
background:#10b981;
border:none;
color:white;
padding:6px 12px;
border-radius:6px;
cursor:pointer;
}

.logout{
background:#ef4444;
border:none;
color:white;
padding:6px 12px;
border-radius:6px;
cursor:pointer;
}

.container{
max-width:1200px;
margin:auto;
padding:30px;
}

/* CREATE USER */

.create-card{
background:white;
padding:20px;
border-radius:10px;
margin-bottom:20px;
}

.create-row{
display:flex;
gap:10px;
}

.create-row input,
.create-row select{
padding:6px;
border:1px solid #ddd;
border-radius:6px;
}

.create-row button{
background:#3b82f6;
border:none;
color:white;
padding:6px 14px;
border-radius:6px;
}

/* TABLE */

.table-card{
background:white;
padding:20px;
border-radius:10px;
}

table{
width:100%;
border-collapse:collapse;
}

th{
background:#f9fafb;
padding:12px;
text-align:left;
}

td{
padding:12px;
border-bottom:1px solid #eee;
}

.edit{
background:#f59e0b;
border:none;
color:white;
padding:5px 10px;
border-radius:5px;
margin-right:6px;
}

.save{
background:#10b981;
border:none;
color:white;
padding:5px 10px;
border-radius:5px;
margin-right:6px;
}

.delete{
background:#ef4444;
border:none;
color:white;
padding:5px 10px;
border-radius:5px;
}

/* PAGINATION */

.pagination{
display:flex;
justify-content:center;
gap:20px;
margin-top:20px;
}

.pagination button{
background:#3b82f6;
border:none;
color:white;
padding:6px 14px;
border-radius:6px;
}

/* MESSAGES */

.error{
color:#ef4444;
margin-bottom:10px;
font-weight:500;
}

.success{
color:#10b981;
margin-bottom:10px;
font-weight:500;
}

</style>