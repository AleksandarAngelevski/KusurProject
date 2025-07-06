const priceField = document.querySelector(".add-expense-modal .wrapper input");
const reg= /^[1-9]\d*$/;
let id=0;

let groupExpenseDto ={
    groupId:0,
    description:"",
    amount:0.0,
    users:[]
}
export function addPersonToExpense(e){
    id=e.target.parentElement.parentElement.firstChild.getAttribute("groupid");
    console.log(e.target.parentElement.parentElement.firstChild)
    e.target.classList.toggle("activeGroupMember")
    toggle(e.target.textContent,groupExpenseDto.users);
    console.log(groupExpenseDto);
}

export function clearMembers(){
    groupExpenseDto={
        groupId:0,
        description:"",
        amount:0.0,
        users:[]
    }
}

export async function addExpense(e){
    groupExpenseDto.description=document.querySelector("#description").value;
    groupExpenseDto.amount=document.querySelector("#sum").value;
    groupExpenseDto.groupId=Number(id);
    if(!priceField.value.match(reg)){
        alert("Enter a valid input!! \n ex. 100 ");
    }
    else if(groupExpenseDto.users.length===0){
        alert("Select atleast one group member");
    }
    else{
        const host = window.location.host;
        const token = localStorage.getItem("token");
        console.log(groupExpenseDto);
        const response = await fetch("http://"+host+"/expense/add",{
            method: "POST",
            credentials: "include",
            headers:{
                "Authorization": `Bearer ${token}`,
                "Content-Type": "application/json",

            },
            body: JSON.stringify(groupExpenseDto),
            
        }).then(response => response.text()).then(data => console.log(data)).catch(err => console.log(err)).finally(()=>{});
    }

}


function toggle(element,list){
    if(list.indexOf(element) > -1){
        list.splice(list.indexOf(element),1)
    }else{
        list.push(element);
    }
}