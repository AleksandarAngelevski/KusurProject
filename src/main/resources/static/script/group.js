import{addPersonToExpense,clearGroup} from "./expenseModlue.js"
let settleExpenseButton = document.querySelector("#settle-expense-btn");
let settleExpenseModal  = document.querySelector(".settle-expense-modal");
let closeExpenseModalButon = document.querySelector("#close-btn-modal");
let addExpenseButton = document.querySelector("#add-expense-btn");
let groupMembers = document.querySelectorAll(".user");
let expenseMembersSet = new Set();
let payee = groupMembers[0];

groupMembers.forEach((e)=>{
    e.addEventListener("click",selectUser);
})
settleExpenseButton.addEventListener("click",openSettleExpenseModal);
closeExpenseModalButon.addEventListener("click",closeSettleExpenseModal);
addExpenseButton.addEventListener("click",openExpenseModal);


function openSettleExpenseModal(e){
    document.querySelector("#overlay").style.display = "block";
    settleExpenseModal.style.display = "block";
    getGroupBalances();
}

function openExpenseModal(e){
    document.querySelector("#overlay").style.display = "block";
    settleExpenseModal.style.display = "block";
    document.querySelector(".settle-expense-modal-wrapper").insertAdjacentHTML("beforeend",expenseModalTemplate);
    groupMembers = document.querySelectorAll(".user");
    let tempSpanElement = document.createElement("span");
    tempSpanElement.textContent=payee.textContent;
    document.querySelector(".settle-expense-modal .settle-expense-modal-wrapper .payee").addEventListener("click",openPayeeModal);
    groupMembers.forEach((e)=>{
        e.addEventListener("click",selectUser);
    })
    document.querySelector(".settle-expense-modal-wrapper .add-expense-modal .payee").insertAdjacentHTML("beforeend",tempSpanElement.textContent)
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
    getGroupMembers();
}

async function getGroupMembers(){
    let members;
    const response = await fetch("http://"+host+"/group/"+id+"/members",{
            method: "GET",
            credentials: "include",
            headers:{
                "Authorization": `Bearer ${token}`,
                "Content-Type": "application/json",
                "Access-Control-Allow-Origin":"*",
            }
        },false).then(response => response.json()).then((data)=>{members=data});
    console.log(members);
}

async function getGroupBalances(){
    const host = window.location.host;
    const token = localStorage.getItem("token");
    const response = await fetch("http://"+host+"/group/balances/"+document.querySelector("#groupId").textContent,{
            method: "GET",
            credentials: "include",
            headers:{
                "Authorization": `Bearer ${token}`,
                "Content-Type": "application/json",

            },            
        }).then(response => response.json()).then(data => console.log(data));
}






let expenseModalTemplate = `
    <div class="add-expense-modal">
        <div class="wrapper">
            <div class="row1">
                <input type="text" name="" id="sum" placeholder="Sum..." style="width: 50%;"><button style="width: 50%;">Add expense</button>
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