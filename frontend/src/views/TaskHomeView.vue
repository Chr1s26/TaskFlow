<script setup lang="ts">

import { ref,onMounted } from "vue"
import { useRouter } from "vue-router"
import api from "../api/api"
import { getErrorMessage } from "../utils/error"

const router = useRouter()

const tasks = ref<any[]>([])
const username = ref("")
const role = ref("")

const error = ref("")
const success = ref("")

const page = ref(0)
const totalPages = ref(0)
const size = 5

const filterPriority = ref("")
const filterStatus = ref("")

const editingTaskId = ref<number|null>(null)

const newTask = ref({
  title:"",
  description:"",
  priority:"LOW",
  status:"TODO",
  dueDate:null
})

const editingTask = ref<any>({})

/* ---------------- USER ---------------- */

const loadUser = async ()=>{

  try{

    const res = await api.get("/users/me")

    username.value = res.data.name
    role.value = res.data.role

  }catch{

    localStorage.removeItem("token")

    router.push("/login")

  }

}

/* ---------------- TASKS ---------------- */

const loadTasks = async ()=>{

  const res = await api.get("/tasks",{
    params:{
      page:page.value,
      size:size,
      priority:filterPriority.value || undefined,
      status:filterStatus.value || undefined
    }
  })

  tasks.value = res.data.content
  totalPages.value = res.data.page.totalPages
  page.value = res.data.page.number

}

/* ---------------- CREATE ---------------- */

const createTask = async ()=>{

  error.value=""
  success.value=""

  try{

    await api.post("/tasks",newTask.value)

    success.value="Task created successfully"

    newTask.value={
      title:"",
      description:"",
      priority:"LOW",
      status:"TODO",
      dueDate:null
    }

    loadTasks()

  }catch(err:any){

    error.value = getErrorMessage(err)

  }

}

/* ---------------- UPDATE ---------------- */

const startEdit = (task:any)=>{

  editingTaskId.value = task.id

  editingTask.value = {...task}

}

const saveEdit = async ()=>{

  try{

        await api.put("/tasks/"+editingTaskId.value,{
      title: editingTask.value.title,
      description: editingTask.value.description,
      priority: editingTask.value.priority,
      status: editingTask.value.taskStatus,   
      dueDate: editingTask.value.dueDate
    })

    success.value="Task updated"

    editingTaskId.value=null

    loadTasks()

  }catch(err:any){

    error.value=getErrorMessage(err)

  }

}

/* ---------------- DELETE ---------------- */

const deleteTask = async (id:number)=>{

  try{

    await api.delete("/tasks/"+id)

    success.value="Task deleted"

    loadTasks()

  }catch(err:any){

    error.value=getErrorMessage(err)

  }

}

/* ---------------- PAGINATION ---------------- */

const nextPage = ()=>{

  if(page.value < totalPages.value-1){

    page.value++

    loadTasks()

  }

}

const prevPage = ()=>{

  if(page.value>0){

    page.value--

    loadTasks()

  }

}

/* ---------------- FILTER ---------------- */

const applyFilter = ()=>{

  page.value=0

  loadTasks()

}

/* ---------------- NAVIGATION ---------------- */

const goAdmin = ()=>{

  router.push("/admin")

}

const logout = ()=>{

  localStorage.removeItem("token")

  router.push("/login")

}

onMounted(async ()=>{

  await loadUser()

  loadTasks()

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

<button
v-if="role==='ADMIN'"
class="admin-btn"
@click="goAdmin"
>
Admin Panel
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


<!-- CREATE TASK -->

<div class="create-card">

<h3>Create Task</h3>

<div class="create-row">

<input v-model="newTask.title" placeholder="Title"/>

<select v-model="newTask.priority">

<option value="LOW">LOW</option>
<option value="MEDIUM">MEDIUM</option>
<option value="HIGH">HIGH</option>

</select>

<select v-model="newTask.status">

<option value="TODO">TODO</option>
<option value="IN_PROGRESS">IN_PROGRESS</option>
<option value="DONE">DONE</option>

</select>

<input type="datetime-local" v-model="newTask.dueDate"/>

<button @click="createTask">
Add Task
</button>

</div>

<textarea
v-model="newTask.description"
placeholder="Description"
class="description"
/>

</div>


<!-- FILTER -->

<div class="filter-card">

<h3>Filter Tasks</h3>

<div class="filter-row">

<select v-model="filterPriority">

<option value="">All Priority</option>
<option value="LOW">LOW</option>
<option value="MEDIUM">MEDIUM</option>
<option value="HIGH">HIGH</option>

</select>

<select v-model="filterStatus">

<option value="">All Status</option>
<option value="TODO">TODO</option>
<option value="IN_PROGRESS">IN_PROGRESS</option>
<option value="DONE">DONE</option>

</select>

<button @click="applyFilter">
Apply
</button>

</div>

</div>


<!-- TASK TABLE -->

<div class="table-card">

<h3>Your Tasks</h3>

<table>

<thead>

<tr>

<th>Title</th>
<th>Description</th>
<th>Priority</th>
<th>Status</th>
<th>Due Date</th>
<th>Actions</th>

</tr>

</thead>

<tbody>

<tr v-for="task in tasks" :key="task.id">

<td>

<input
v-if="editingTaskId===task.id"
v-model="editingTask.title"
/>

<span v-else>{{task.title}}</span>

</td>

<td>

<input
v-if="editingTaskId===task.id"
v-model="editingTask.description"
/>

<span v-else>{{task.description}}</span>

</td>

<td>

<select
v-if="editingTaskId===task.id"
v-model="editingTask.priority"
>

<option>LOW</option>
<option>MEDIUM</option>
<option>HIGH</option>

</select>

<span v-else>{{task.priority}}</span>

</td>

<td>

<select
v-if="editingTaskId===task.id"
v-model="editingTask.taskStatus"
>

<option>TODO</option>
<option>IN_PROGRESS</option>
<option>DONE</option>

</select>

<span v-else>{{task.taskStatus}}</span>

</td>

<td>

<input
v-if="editingTaskId===task.id"
type="datetime-local"
v-model="editingTask.dueDate"
/>

<span v-else>{{task.dueDate}}</span>

</td>

<td>

<button
v-if="editingTaskId!==task.id"
class="edit"
@click="startEdit(task)"
>
Edit
</button>

<button
v-if="editingTaskId===task.id"
class="save"
@click="saveEdit"
>
Save
</button>

<button
class="delete"
@click="deleteTask(task.id)"
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

.admin-btn{
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

/* CREATE */

.create-card{
background:white;
padding:20px;
border-radius:10px;
margin-bottom:20px;
}

.create-row{
display:flex;
gap:10px;
margin-bottom:10px;
}

.create-row input,
.create-row select{
padding:6px;
border:1px solid #ddd;
border-radius:6px;
}

.create-row button{
background:#3b82f6;
color:white;
border:none;
padding:6px 14px;
border-radius:6px;
}

.description{
width:100%;
padding:8px;
border:1px solid #ddd;
border-radius:6px;
}

/* FILTER */

.filter-card{
background:white;
padding:20px;
border-radius:10px;
margin-bottom:20px;
}

.filter-row{
display:flex;
gap:10px;
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