const priceField = document.querySelector(".add-expense-modal .wrapper input");
const reg= /^[1-9]\d*$/;
let id=null;

let groupExpenseDto ={
    userId:null,
    groupId:null,
    description:"",
    amount:0.0,
    users:[]
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
    console.log(groupExpenseDto);
}
export function clearGroup(){    
    groupExpenseDto.groupId=null;
    groupExpenseDto.users=[];
}
export function clearUser(){
    groupExpenseDto.userId=null;
    
}
export async function addExpense(e){
    groupExpenseDto.description=document.querySelector("#description").value;
    groupExpenseDto.amount=document.querySelector("#sum").value;
    
    
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
            
        }).then(response => response.json()).then(data => {
            console.log(data);
            insertExpense(data)
        }).catch(err => console.log(err)).finally(()=>{
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
    let list = `<ul><li>Created by: ${expenseResponse.userId}</li></ul>`
    
    h1.textContent = `${expenseResponse.description} - Total: ${expenseResponse.amount} MKD`;
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