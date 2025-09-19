import { addPersonToExpense,addExpense,addUserToExpense,clearUser,getGroupId, clearGroup, shrinkExpenseModal, collapseExpenseSplitModal,clearSplitChoice, binaryExpenseSplit,groupExpenseSplit,createPayeeChoices} from "./expenseModlue.js";


let arr;
let groupName;
let startHeight;
const add_friend_button = document.querySelector("#add-friend-btn")
const add_friend_input_field = document.querySelector("#add-friend-field")
const createGroupBtn = document.querySelector("#create-group-btn");
const add_expense_btn = document.querySelector("#add-expense-btn")

document.querySelector(".add-expense-modal .wrapper button").addEventListener("click",addExpense);

createGroupBtn.addEventListener("click",openGroupCreationModal);
add_expense_btn.addEventListener("click",openExpenseModal)
add_friend_button.addEventListener("click",send_request)

async function send_request(){
    if(add_friend_input_field.value.trim()===""){
        alert("Enter a name first");
    }
    const host = window.location.host;
    const token = localStorage.getItem("token");
    const response = await fetch("https://"+host+"/add-friend",{
        method: "POST",
        credentials: "include",
        headers:{
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`,
        },
        body: JSON.stringify({username:add_friend_input_field.value.trim()}),
    }).then(response => response.text()).then(data => alert(data)).finally();
    alert(response.statusText);
}



//MODAL FUNCTIONALITY FROM HERE
let closeModalBtn = document.querySelector(".close-friends-modal-btn");

closeModalBtn.addEventListener("click",closeModal);
const groupModal = document.querySelector(".create-group-modal");
let ul = document.querySelector(".create-group-modal ul").addEventListener("click", addUserToGroup);
// REQUEST BODY
let body = new Set();
function addUserToGroup(e){
    if(e.target.tagName.toLowerCase() == "ul") return;
    e.target.classList.toggle("selected")
    toggle(e.target.textContent)
    
}
async function openGroupCreationModal(e){
    const host = window.location.host;
    const token = localStorage.getItem("token");
    const response = await fetch("https://"+host+"/get-friends",{
        method: "GET",
        credentials: "include",
        headers:{
            "Authorization": `Bearer ${token}`,
            "Content-Type": "application/json",

        }
        
    }).then(response => response.text()).then(data => insertNames(data)).catch(err => console.log(err)).finally(()=>{
        document.querySelector("#overlay").style.display="block";
        document.querySelector(".create-group-modal").style.display="flex";
        document.body.style.overflow="hidden";
    });
    
    
}
function toggle(name){
    if(body.has(name)){
        body.delete(name);
    }else{
        body.add(name);
    }
    console.log(body)
}
function insertNames(data){
    let names=JSON.parse(data);
    let insertData="";
    names.forEach(element => {
        insertData+=`<li class="user">${element.username}</li>`
    });
    document.querySelector(".create-group-modal ul").insertAdjacentHTML("beforeend",insertData)
}
function closeModal(e){
    document.querySelector("#overlay").style.display="none";
    document.querySelector(".create-group-modal ul").innerHTML="";
    document.querySelector(".create-group-modal").style.display="none";
    document.querySelector(".add-expense-modal").style.display="none";
    document.querySelector(".add-expense-modal .choices").innerHTML="";
    document.querySelector(".add-expense-modal .wrapper input").value="";
    document.querySelector(".add-expense-modal .wrapper #description").value="";
    if(document.querySelector(".expenseSplits")!= null)document.querySelector(".expenseSplits").remove();
    document.querySelector(".wrapper").style.height="100px";
    document.body.style.overflow="auto";
    clearGroup();
    clearUser();
}

let createGroupSubmitButton = document.querySelector("#create-group-btn-modal");
createGroupSubmitButton.addEventListener("click",(e)=>{
        
    if(document.querySelector(".create-group-modal .wrappper input").value.trim()==""){
        alert("Enter a group name first");
        return;
    }
    if(body.size==0){
        alert("Select minimum one friend");
    }else{
    makeRequest(e);
    }
});

async function makeRequest(e){
    arr = Array.from(body); 
    
    groupName= document.querySelector(".create-group-modal .wrappper input").value;
    const host = window.location.host;
    
    const token = localStorage.getItem("token");
    const response = await fetch("https://"+host+"/group/create",{
        method: "POST",
        credentials: "include",
        headers:{
            "Authorization": `Bearer ${token}`,
            "Content-Type": "application/json",

        },
        body:JSON.stringify({
            groupName: groupName,
            users: arr,
        }),
        
    }).then(response => response.text()).then(data => alert(data)).finally(closeModal(e));
    
}

// ADD EXPENSE FUNCTIONALITY
let closeExpenseModalBtn = document.querySelector(".close-expense-modal-btn");

closeExpenseModalBtn.addEventListener("click",closeModal);
async function openExpenseModal(e) {

    const host = window.location.host;
    const token = localStorage.getItem("token");
    const response = await fetch("https://"+host+"/get-friends",{
        method: "GET",
        credentials: "include",
        headers:{
            "Authorization": `Bearer ${token}`,
            "Content-Type": "application/json",

        }
        
    }).then(response => response.text()).then(document.querySelector(".choices").insertAdjacentHTML("beforeend","<h3>People:</h3>")).then(data => insertChoices(data)).catch(err => console.log(err)).finally(()=>{
        document.querySelector(".choices").insertAdjacentHTML("beforeend","<h3>Groups:</h3>")
        });
    const response2 = await fetch("https://"+host+"/group/getAll",{
        method:"GET",
        credentials:"include",
        headers:{
            "Authorization": `Bearer ${token}`,
            "Content-Type": "application/json",
        }
    }).then(response2 => response2.json()).then(data => insertGroups(data)).catch(err => console.log(err)).finally(()=>{
        document.querySelector("#overlay").style.display="block";
        document.querySelector(".add-expense-modal").style.display="flex";
        document.body.style.overflow="hidden";
    });
    
}

function insertChoices(data){
    let people = JSON.parse(data);
    let holder = document.querySelector(".choices");
    people.forEach((elem)=>{
        let choice = document.createElement("div");
        choice.classList.add("choice");
        let person = document.createElement("div");
        person.classList.add("person");
        person.textContent= elem.username;
        person.addEventListener("click",selectUser);
        choice.insertAdjacentElement("beforeend",person);
        holder.insertAdjacentElement("beforeend",choice);
    })
    let choices = document.querySelectorAll(".choices .choice .person");
    }

function insertGroups(groups){

    let holder = document.querySelector(".choices");
    groups.forEach((elem)=>{
        let choice = document.createElement("div");
        let group = document.createElement("div");
        choice.classList.add("choice");
        group.classList.add("group");
        group.textContent = elem.name;
        group.setAttribute("id",elem.id)
        group.setAttribute("groupId",elem.id)
        group.addEventListener("click",selectGroup);
        choice.insertAdjacentElement("beforeend",group);
        holder.insertAdjacentElement("beforeend",choice);
        
    })
}
let template=`<ul id="split">
<li>One</li>
<li>Two</li>
</ul>`;


let activeElement = null;
function insertPeople(e){
    if(e.target === activeElement){
        document.querySelector("#split").remove();
        activeElement = null;
    }else{
        if(activeElement!==null){
            document.querySelector("#split").remove();
        }
        activeElement = e.target;
        e.target.parentElement.insertAdjacentHTML("beforeend",template)
    }
    

}

function select(e){
    
    if(activeElement===e.target.parentElement){
        if(document.querySelector("#groupMembers") !==null){
            document.querySelector("#groupMembers").remove()
            activeElement.style.height = startHeight+'px';
        }
        activeElement.classList.toggle("activeChoice");
        activeElement=null;
    }else if(activeElement===null){
        activeElement=e.target.parentElement;
        activeElement.classList.toggle("activeChoice");
    }else{
        if(document.querySelector("#groupMembers") !==null){
            document.querySelector("#groupMembers").remove()
            activeElement.style.height = startHeight+'px';
        }
        activeElement.classList.toggle("activeChoice");
        activeElement = e.target.parentElement;
        activeElement.classList.toggle("activeChoice");
    }
    
}
function selectUser(e){
    clearGroup();
    clearUser();
    console.log("Active element ")
    console.log(activeElement)
    if(activeElement===e.target.parentElement){
        collapseExpenseSplitModal();    
    }else if(activeElement===null){
        
        addUserToExpense(e.target.textContent);
        binaryExpenseSplit(document.querySelector(".add-expense-modal .wrapper"))
    }
    else{
        collapseExpenseSplitModal().then(()=>{binaryExpenseSplit(document.querySelector(".add-expense-modal .wrapper"))});
        clearSplitChoice();
        addUserToExpense(e.target.textContent);
        
    }
    select(e);
    
    

}
async function selectGroup(e){
    clearSplitChoice();
    
    clearUser();
    
    if(activeElement===e.target.parentElement){
        clearGroup();
        select(e);
        collapseExpenseSplitModal();
    }else{
        clearGroup();
        console.log("SelectGroup");
        getGroupId(e);
        select(e);
        const host = window.location.host;
        const token = localStorage.getItem("token");
        const id = e.target.getAttribute("id");
        const response = await fetch("https://"+host+"/group/"+id+"/members",{
        method: "GET",
        credentials: "include",
        headers:{
            "Authorization": `Bearer ${token}`,
            "Content-Type": "application/json",
        }
    }).then(response => response.json()).then(data => {insertMembers(data);createPayeeChoices(data);collapseExpenseSplitModal().then(()=>groupExpenseSplit(e));});
    }
}

function insertMembers(members){
    startHeight = activeElement.offsetHeight;
    let ul = document.createElement("ul");
    members.forEach((e)=>{
        let li = document.createElement("li");
        li.textContent=e.username;
        ul.insertAdjacentElement("beforeend",li);
        ul.setAttribute("id","groupMembers");
        ul.addEventListener("click",addPersonToExpense)
        activeElement.insertAdjacentElement("beforeend",ul);
    })
    
    const endHeight = activeElement.scrollHeight;
    activeElement.style.height= startHeight+'px';
    activeElement.offsetHeight;
    activeElement.style.height= endHeight+'px';
}




function animateHeight(element){

}
