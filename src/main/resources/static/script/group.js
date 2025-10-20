import * as SettleExpense from "./settleExpenseModule.js";

let settleExpenseButton = document.querySelector("#settle-expense-btn");
let settleExpenseModal  = document.querySelector(".settle-expense-modal");
let closeExpenseModalButon = document.querySelector("#close-btn-modal");
let addExpenseButton = document.querySelector("#add-expense-btn");
let groupMembers = document.querySelectorAll(".user");
let expenseMembersSet = new Set();
let currentPayee = document.querySelector("#usernameeee");

groupMembers.forEach((e)=>{
    e.addEventListener("click",selectUser);
})
settleExpenseButton.addEventListener("click",openSettleExpenseModal);
closeExpenseModalButon.addEventListener("click",closeSettleExpenseModal);
addExpenseButton.addEventListener("click",openExpenseModal);


function openSettleExpenseModal(e){
 
    document.querySelector("#overlay").style.display = "block";
    settleExpenseModal.style.display = "block";
    SettleExpense.fillModal(document.querySelector("#groupId").textContent,document.querySelector(".settle-expense-modal .settle-expense-modal-wrapper"))
}
function updateCurrentPayee(){
    document.querySelector(".settle-expense-modal-wrapper .add-expense-modal .payee span").remove();
    let tempSpanElement = document.createElement("span");
    tempSpanElement.textContent=currentPayee.textContent;
    document.querySelector(".settle-expense-modal-wrapper .add-expense-modal .payee").insertAdjacentElement("beforeend",tempSpanElement)
}

function populateExpenseModal(){
    document.querySelector(".settle-expense-modal .settle-expense-modal-wrapper .groupMembers").textContent = "";
    members.forEach((e)=>{
        let div = document.createElement("div");
        div.classList.add("user");
        div.textContent = e.username;
        document.querySelector(".settle-expense-modal .settle-expense-modal-wrapper .groupMembers").appendChild(div);
        div.addEventListener("click",selectUser);
    })
}


function openExpenseModal(e){
    
    document.querySelector("#overlay").style.display = "block";
    settleExpenseModal.style.display = "block";
    document.querySelector(".settle-expense-modal-wrapper").insertAdjacentHTML("beforeend",expenseModalTemplate);
    console.log(document.querySelector(".add-expense-modal .row1 button"));
    getGroupMembers().then(()=>populateExpenseModal());
    let tempSpanElement = document.createElement("span");
    tempSpanElement.textContent=currentPayee.textContent;
    document.querySelector(".settle-expense-modal .settle-expense-modal-wrapper .payee").addEventListener("click",openPayeeModal);
    console.log(currentPayee)
    console.log(tempSpanElement);
    document.querySelector(".settle-expense-modal-wrapper .add-expense-modal .payee").insertAdjacentElement("beforeend",tempSpanElement)
    document.querySelector(".add-expense-modal .row1 #submitBtn").addEventListener("click",addGroupExpense);
}

function closeSettleExpenseModal(e){
    document.querySelector("#overlay").style.display = "none";
    settleExpenseModal.style.display = "none";
    clearSettleExpenseModal();
}

function clearSettleExpenseModal(e){
    document.querySelector(".settle-expense-modal-wrapper").textContent = "";
}

function selectUser(e){
    
    e.target.classList.toggle("activeExpenseMember");
    if(expenseMembersSet.has(e.target.textContent)){
        expenseMembersSet.delete(e.target.textContent);

    }else{
        expenseMembersSet.add(e.target.textContent);
    }

    console.log(expenseMembersSet)
}


function openPayeeModal(e){
    let modal = document.createElement("div");
    modal.classList.add("payeesModal");
    document.body.appendChild(modal);
    document.querySelector("#overlay").style.zIndex = "2000";
    modal.style.zIndex = "2001";
    getGroupMembers().then(()=>populatePayeeModal(members));
}
let members;
async function getGroupMembers(){
    
    const host = window.location.host;
    const token = localStorage.getItem("token");
    const id = document.querySelector("#groupId").textContent;  
    const response = await fetch("https://"+host+"/group/"+id+"/members",{
            method: "GET",
            credentials: "include",
            headers:{
                "Authorization": `Bearer ${token}`,
                "Content-Type": "application/json",
                "Access-Control-Allow-Origin":"*",
            }
        },false).then(response => response.json()).then((data)=>{members=data});
}


function populatePayeeModal(membersArray){
    let payeesModal = document.querySelector(".payeesModal");
    membersArray.forEach((e)=>{
        let div = document.createElement("div");
        div.classList.add("payeeUser");
        div.textContent = e.username;
        payeesModal.appendChild(div);
        div.addEventListener("click",selectPayee);
        if(e.username === currentPayee.textContent){
            div.classList.add("payeeUserActive");
        }
    })
    payeesModal.style.display = "flex";
    
    
}

async function getGroupBalances(){
    const host = window.location.host;
    const token = localStorage.getItem("token");
    const response = await fetch("https://"+host+"/group/balances/"+document.querySelector("#groupId").textContent,{
            method: "GET",
            credentials: "include",
            headers:{
                "Authorization": `Bearer ${token}`,
                "Content-Type": "application/json",

            },            
        }).then(response => response.json()).then(data => console.log(data));
}


function selectPayee(e){
    currentPayee = e.target;
    console.log(currentPayee);
    closePayeeModal();
    updateCurrentPayee();
}
function closePayeeModal(e){
    document.querySelector(".payeesModal").remove();
    document.querySelector("#overlay").style.zIndex = "1000";
}

const reg= /^[1-9]\d*(\.\d+)?$/;
let groupExpenseDto ={
    userId:null,
    groupId:null,
    description:"",
    amount:0.0,
    users:[],
    splitChoice:null,
    payee:null,
}

async function addGroupExpense(e){
    console.log(e.target);
    groupExpenseDto.description=document.querySelector("#description").value;
    groupExpenseDto.groupId=document.querySelector("#groupId").textContent;
    groupExpenseDto.amount=Number(document.querySelector("#sum").value);
    groupExpenseDto.payee=currentPayee.textContent;
    groupExpenseDto.users=Array.from(expenseMembersSet);
    console.log("Current payee: "+groupExpenseDto.payee);
    console.log(groupExpenseDto.users);
    groupExpenseDto.userId=document.querySelector("#usernameeee").textContent;
    document.querySelector("#overlay").style.display="block";
    console.log("Submitting expense");
    console.log("State of dto")
    console.log(groupExpenseDto)
    if(!document.querySelector(".add-expense-modal .wrapper .row1 #sum").value.match(reg)){
        alert("Enter a valid input!! \n ex. 100 ");
    }else if(groupExpenseDto.users.length===0){
        alert("Select atleast one group member");
    }
    else{
        const host = window.location.host;
        const token = localStorage.getItem("token");
        const response = await fetch("https://"+host+"/expense/add",{
            method: "POST",
            credentials: "include",
            headers:{
                "Authorization": `Bearer ${token}`,
                "Content-Type": "application/json",
                "Access-Control-Allow-Origin":"*",

            },
            body: JSON.stringify(groupExpenseDto),
            
        }).then(response => {
            if(!response.ok){
                return response.json().then(errorData => {
                    throw new Error(errorData.description || "Unknown error");
                })
            }
            return response.json();
        }).then(data => {
            console.log(data);
            insertExpense(data);
        }).catch(err => console.log(err)).finally(()=>{
            clearExpenseModal();
            closeSettleExpenseModal();
            clearGroupExpenseDto();
    
        });
    }

}
function clearExpenseModal(){
    document.querySelector(".add-expense-modal .wrapper input").value="";
    document.querySelector(".add-expense-modal .groupMembers").textContent="";
    document.querySelector(".add-expense-modal .payee span").remove();
    expenseMembersSet.clear();
}
function clearGroupExpenseDto(){    
    groupExpenseDto.groupId=null;
    groupExpenseDto.users=[];
    groupExpenseDto.payee=null;
    groupExpenseDto.description = null;
    groupExpenseDto.amount=0.0;
    groupExpenseDto.splitChoice=null;
}

let expenseModalTemplate = `
    <div class="add-expense-modal">
        <div class="wrapper">
            <div class="row1">
                <input type="text" name="" id="sum" placeholder="Sum..." style="width: 50%;"><button id="submitBtn" style="width: 50%;">Add expense</button>
            </div>
            <div class="row2">
                <input type="text" name="" id="description" placeholder="Description..." style="width: 100%;">
            </div>
        </div>
        <div class="payee">Paid by : </div>
        <div class="payee-modal">
            <div class="user">user1</div>
            <div class="user">user2</div>
            <div class="user">user3</div>
            <div class="user">user4</div>
        </div>
        <div class="groupMembers">
            <div class="user">user1</div>
            <div class="user">user2</div>
            <div class="user">user3</div>
            <div class="user">user4</div>
        </div>
    </div>`


    

function insertExpense(expenseResponse){
    let elem = document.createElement("div");
    let h1= document.createElement("h1");
    let list = `<ul><li>Created by: ${expenseResponse.creatorId}</li></ul>`
    console.log(expenseResponse)
    h1.textContent = `${expenseResponse.description} - Total: ${Number(expenseResponse.amount).toFixed(1)}MKD`;
    elem.classList.add("expense")
    
    elem.insertAdjacentElement("afterbegin",h1)
    elem.insertAdjacentHTML("beforeend",list);
    document.querySelector(".expenses-container").insertAdjacentElement("afterbegin",elem)
}

