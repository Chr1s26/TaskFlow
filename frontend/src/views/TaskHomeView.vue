<script setup lang="ts">

import { ref,onMounted } from "vue"
import { useRouter } from "vue-router"
import api from "../api/api"

const router = useRouter()

const username = ref("User")
const role = ref("USER")

const tasks = ref<any[]>([])

const page = ref(0)
const totalPages = ref(0)
const size = 5

const editingTaskId = ref<number|null>(null)

const filterPriority = ref("")
const filterStatus = ref("")

const newTask = ref({
  title:"",
  description:"",
  priority:"LOW",
  status:"TODO",
  dueDate:""
})

/* ---------------- USER INFO ---------------- */

const loadUser = async ()=>{

  try{

    const res = await api.get("/auth/user")

    username.value = res.data.username
    role.value = res.data.role.replace("ROLE_","")

  }catch{

    localStorage.removeItem("token")
    router.push("/login")

  }

}

/* ---------------- LOAD TASKS ---------------- */

const loadTasks = async ()=>{

  const params:any = {
    page:page.value,
    size:size
  }

  if(filterPriority.value){
    params.priority = filterPriority.value
  }

  if(filterStatus.value){
    params.status = filterStatus.value
  }

  const res = await api.get("/tasks",{ params })

  tasks.value = res.data.content
  totalPages.value = res.data.page.totalPages
  page.value = res.data.page.number

}

/* ---------------- CREATE TASK ---------------- */

const createTask = async ()=>{

  await api.post("/tasks",newTask.value)

  newTask.value={
    title:"",
    description:"",
    priority:"LOW",
    status:"TODO",
    dueDate:""
  }

  page.value=0

  loadTasks()

}

/* ---------------- EDIT ---------------- */

const editTask = (task:any)=>{
  editingTaskId.value = task.id
}

const saveTask = async (task:any)=>{

  await api.put("/tasks/"+task.id,{
    title:task.title,
    description:task.description,
    priority:task.priority,
    status:task.taskStatus,
    dueDate:task.dueDate
  })

  editingTaskId.value=null

  loadTasks()

}

/* ---------------- DELETE ---------------- */

const deleteTask = async (id:number)=>{

  await api.delete("/tasks/"+id)

  loadTasks()

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

/* ---------------- LOGOUT ---------------- */

const logout = ()=>{

  localStorage.removeItem("token")
  router.push("/login")

}

/* ---------------- DATE FORMAT ---------------- */

const formatDate = (date:string)=>{

  if(!date) return ""

  return new Date(date).toLocaleString()

}

onMounted(async ()=>{

  await loadUser()
  await loadTasks()

})

</script>

<template>

<div class="dashboard">

<header class="header">

<h2>TaskFlow</h2>

<div class="header-right">

<div class="avatar"></div>

<span class="username">{{username}}</span>

<button
v-if="role==='ADMIN'"
class="admin-btn"
@click="router.push('/admin')"
>
Admin Panel
</button>

<button class="logout" @click="logout">
Logout
</button>

</div>

</header>


<div class="container">

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

<button @click="createTask">Add</button>

</div>

<textarea
v-model="newTask.description"
placeholder="Description"
></textarea>

</div>


<!-- FILTERS -->

<div class="filter-row">

<select v-model="filterPriority" @change="loadTasks">

<option value="">All Priority</option>
<option value="LOW">LOW</option>
<option value="MEDIUM">MEDIUM</option>
<option value="HIGH">HIGH</option>

</select>

<select v-model="filterStatus" @change="loadTasks">

<option value="">All Status</option>
<option value="TODO">TODO</option>
<option value="IN_PROGRESS">IN_PROGRESS</option>
<option value="DONE">DONE</option>

</select>

</div>


<!-- TASK TABLE -->

<div class="table-card">

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
v-model="task.title"
/>

<span v-else>{{task.title}}</span>

</td>

<td>

<input
v-if="editingTaskId===task.id"
v-model="task.description"
/>

<span v-else>{{task.description}}</span>

</td>

<td>

<select
v-if="editingTaskId===task.id"
v-model="task.priority"
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
v-model="task.taskStatus"
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
v-model="task.dueDate"
/>

<span v-else>{{formatDate(task.dueDate)}}</span>

</td>

<td>

<button
v-if="editingTaskId!==task.id"
class="edit"
@click="editTask(task)"
>
Edit
</button>

<button
v-if="editingTaskId===task.id"
class="save"
@click="saveTask(task)"
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

.username{
font-weight:600;
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
border:none;
color:white;
padding:6px 12px;
border-radius:6px;
}

textarea{
width:100%;
padding:8px;
border:1px solid #ddd;
border-radius:6px;
}

.filter-row{
display:flex;
gap:10px;
margin-bottom:20px;
}

.filter-row select{
padding:6px;
border:1px solid #ddd;
border-radius:6px;
}

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

</style>