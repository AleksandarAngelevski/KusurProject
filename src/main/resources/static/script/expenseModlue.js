const priceField = document.querySelector(".add-expense-modal .wrapper input");
const reg= /^[1-9]\d*(\.\d+)?$/;
let id=null;
let splitChoicesModalExpanded=false;
let activeChoice=null;
let groupExpenseDto ={
    userId:null,
    groupId:null,
    description:"",
    amount:0.0,
    users:[],
    splitChoice:null,
}
export function getGroupId(element){
    id = element.target.getAttribute("groupid");
    groupExpenseDto.groupId= id===null ? null:Number(id);
    
}
export function addPersonToExpense(e){
    id=e.target.parentElement.parentElement.firstChild.getAttribute("groupid");
    e.target.classList.toggle("activeGroupMember")
    toggle(e.target.textContent,groupExpenseDto.users);
}
export function addUserToExpense(username){ // dumb function name, can't think of a better ona atm.
    groupExpenseDto.userId=username;
    groupExpenseDto.splitChoice=1;
    console.log(groupExpenseDto);
}
export function clearGroup(){    
    groupExpenseDto.groupId=null;
    groupExpenseDto.users=[];
}
export function clearUser(){
    groupExpenseDto.userId=null;
    groupExpenseDto.splitChoice=null;
    
}
export async function addExpense(e){
    groupExpenseDto.description=document.querySelector("#description").value;
    groupExpenseDto.amount=Number(document.querySelector("#sum").value);
    if(!priceField.value.match(reg)){
        alert("Enter a valid input!! \n ex. 100 ");
    }else if(groupExpenseDto.groupId===null && groupExpenseDto.userId===null){
        alert("Select user or group!");
    }
    else if(groupExpenseDto.users.length===0 && groupExpenseDto.userId===null){
        alert("Select atleast one group member");
    }
    else{
        const host = window.location.host;
        const token = localStorage.getItem("token");
        const response = await fetch("http://"+host+"/expense/add",{
            method: "POST",
            credentials: "include",
            headers:{
                "Authorization": `Bearer ${token}`,
                "Content-Type": "application/json",

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
        }).catch(err => alert(err.message)).finally(()=>{
            document.querySelector("#overlay").style.display="none";
            document.querySelector(".create-group-modal ul").innerHTML="";
            document.querySelector(".create-group-modal").style.display="none";
            document.querySelector(".add-expense-modal").style.display="none";
            document.querySelector(".add-expense-modal .choices").innerHTML="";
            document.querySelector(".add-expense-modal .wrapper input").value="";
            document.querySelector(".add-expense-modal .wrapper #description").value="";
            
            clearGroup();
            clearUser();
        });
    }

}

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


function toggle(element,list){
    if(list.indexOf(element) > -1){
        list.splice(list.indexOf(element),1)
    }else{
        list.push(element);
    }
}

let choice1 = document.createElement("span");
choice1.classList.add("split-choice");
choice1.classList.add("selected");
choice1.textContent="You paid, split equally.";
choice1.setAttribute("choice","1")
activeChoice=choice1;

let choice2 = document.createElement("span");
choice2.classList.add("split-choice");
choice2.setAttribute("choice","2")

let choice3 = document.createElement("span");
choice3.classList.add("split-choice");
choice3.setAttribute("choice","3")

let choice4 = document.createElement("span");
choice4.classList.add("split-choice");
choice4.setAttribute("choice","4")

export function expenseSplit(element){

    if(!element.style.height){
        element.style.height="100px";
    }
    
    let wrapper = document.createElement("div");
    wrapper.addEventListener("click",(e,)=>{
        console.log(e.target);
        console.log(splitChoicesModalExpanded);
        if(!splitChoicesModalExpanded){
            expandExpenseModal(e);
        }else if(splitChoicesModalExpanded && e.target.classList.contains("split-choice")){
            selectSplitChoice(e);
        }
    });
    wrapper.classList.add("expenseSplits");
    wrapper.insertAdjacentElement("beforeend",activeChoice);
    if(document.querySelector(".expenseSplits")!==null){
        document.querySelector(".expenseSplits").remove();
    }
    element.insertAdjacentElement("beforeend",wrapper);
    wrapper.setAttribute("active","true");
    let endHeight = 149;
    element.insertAdjacentElement("beforeend",wrapper);
    setTimeout(()=>{
        element.style.height=endHeight+"px"
    },20)

}
function openExpenseSplitModal(){
    let choice1 = `<span id="1">You paid, split equally.</span>`
    let wrapper = document.createElement("div");
    let modal = document.createElement("expenseSplitModal");
}

function expandExpenseModal(e){
    splitChoicesModalExpanded=true;
    console.log("EXPAND MODAL:  "+splitChoicesModalExpanded)
    let wrapper = document.querySelector(".expenseSplits");
    wrapper.textContent="";
    choice2.textContent="You are owed the full amount.";
    choice3.textContent=`${groupExpenseDto.userId} paid,split equally.`
    choice4.textContent=`${groupExpenseDto.userId} is owed the full amount.`
    wrapper.insertAdjacentElement("beforeend",choice1)
    wrapper.insertAdjacentElement("beforeend",choice2)
    wrapper.insertAdjacentElement("beforeend",choice3)
    wrapper.insertAdjacentElement("beforeend",choice4)
    document.querySelector(".wrapper").style.height=document.querySelector(".wrapper").scrollHeight+"px";
}
export async function shrinkExpenseModal(timeoutValue){
    console.log("Shrink expense modal")
    splitChoicesModalExpanded=false;
        if(document.querySelector(".add-expense-modal .wrapper").style.height!=="149px"){
        console.log("TEST");
        document.querySelector(".add-expense-modal .wrapper").style.height="149px";
    }
    setTimeout(()=>{
        expenseSplit(document.querySelector(".add-expense-modal .wrapper"));
    },timeoutValue)
    console.log(document.querySelector(".add-expense-modal .wrapper"))
    
    document.querySelector(".add-expense-modal .wrapper").offsetHeight;
}
export function collapseExpenseModal(){
    splitChoicesModalExpanded=false;
    if(document.querySelector(".expenseSplits")!= null){
        document.querySelector(".add-expense-modal .wrapper").style.height="100px"; 
        setTimeout(()=>{
            document.querySelector(".expenseSplits").remove();    
        },100)
        
        console.log("collapsed expense modal")
    }
}

function selectSplitChoice(e){
    console.log(splitChoicesModalExpanded)
    console.log("Select split choice")
    if(activeChoice!== e.target && splitChoicesModalExpanded){
        console.log("Select split choice, expanded and not the same choice")
        activeChoice.classList.toggle("selected");
        e.target.classList.toggle("selected")
        activeChoice=e.target;
        shrinkExpenseModal(120);
        groupExpenseDto.splitChoice=Number(e.target.getAttribute("choice"));
    }
    else if(splitChoicesModalExpanded){
        console.log("Select split choice, expanded and the same choice")
        shrinkExpenseModal(120);
        groupExpenseDto.splitChoice=Number(e.target.getAttribute("choice"));
    }
    console.log(groupExpenseDto)
    console.log(splitChoicesModalExpanded);
}

export function clearSplitChoice(){
    activeChoice.classList.toggle("selected");
    activeChoice=choice1; 
    activeChoice.classList.toggle("selected");
}