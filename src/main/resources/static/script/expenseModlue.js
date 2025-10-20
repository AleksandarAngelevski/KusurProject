
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
    payee:null,
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
    groupExpenseDto.payee = document.querySelector("#usernameeee").textContent;
    console.log(groupExpenseDto);
}
export function clearGroup(){    
    groupExpenseDto.groupId=null;
    groupExpenseDto.users=[];
    groupExpenseDto.payee=null;
    groupExpenseDto.description = null;
    groupExpenseDto.amount=0.0;
    groupExpenseDto.splitChoice=null;
}
export function clearUser(){
    groupExpenseDto.userId=null;
    groupExpenseDto.splitChoice=null;
    
}
export async function addExpense(e){
    groupExpenseDto.description=document.querySelector("#description").value;
    groupExpenseDto.amount=Number(document.querySelector("#sum").value);
    console.log("State of dto")
    console.log(groupExpenseDto)
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
choice1.setAttribute("username",document.querySelector("#usernameeee").textContent);
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

export function binaryExpenseSplit(element){

    if(!element.style.height){
        element.style.height="100px";
    }
    
    let wrapper = document.createElement("div");
    wrapper.addEventListener("click",(e,)=>{
        console.log(e.target);
        console.log(splitChoicesModalExpanded);
        if(!splitChoicesModalExpanded){
            if(e.target.classList.contains("split-choice")){
                console.log("Expand expense modal");
                expandExpenseModal(e);
            }else if(e.target.classList.contains("payee")){
                groupExpenseSplit(e);
                expandElement(document.querySelector(".groupExpensePayeeWrapper"));
            }
        }else if(splitChoicesModalExpanded && e.target.classList.contains("split-choice")){
            selectSplitChoiceUserExpense(e);
        }else if (splitChoicesModalExpanded && e.target.classList.contains("payee")){
            console.log("Select group expense payee")
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
    choice2.setAttribute("username",document.querySelector("#usernameeee").textContent);
    choice3.textContent=`${groupExpenseDto.userId} paid,split equally.`
    choice3.setAttribute("username",groupExpenseDto.userId)
    choice4.textContent=`${groupExpenseDto.userId} is owed the full amount.`
    choice4.setAttribute("username",groupExpenseDto.userId)
    wrapper.insertAdjacentElement("beforeend",choice1)
    wrapper.insertAdjacentElement("beforeend",choice2)
    wrapper.insertAdjacentElement("beforeend",choice3)
    wrapper.insertAdjacentElement("beforeend",choice4)
    document.querySelector(".add-expense-modal .wrapper").style.height=document.querySelector(".add-expense-modal .wrapper").scrollHeight+"px";
}
export async function shrinkExpenseModal(timeoutValue){
    console.log("Shrink expense modal")
    splitChoicesModalExpanded=false;
        if(document.querySelector(".add-expense-modal .wrapper").style.height!=="149px"){
        console.log("TEST");
        document.querySelector(".add-expense-modal .wrapper").style.height="149px";
    }
    setTimeout(()=>{
        binaryExpenseSplit(document.querySelector(".add-expense-modal .wrapper"));
    },timeoutValue)
    console.log(document.querySelector(".add-expense-modal .wrapper"))
    
    document.querySelector(".add-expense-modal .wrapper").offsetHeight;
}
export async function collapseExpenseSplitModal(){
    splitChoicesModalExpanded=false;
    if(document.querySelector(".expenseSplits")!= null){
        console.log("Delete expenseSplits")
        document.querySelector(".add-expense-modal .wrapper").style.height="100px"; 
        return new Promise((resolve)=>{
            setTimeout(()=>{
                
                document.querySelector(".expenseSplits").remove();    
                resolve();
            },100)

        });
        
    }else if(document.querySelector(".groupExpensePayeeWrapper")!= null){
        console.log("Delete groupExpenseSplits")
        
        document.querySelector(".add-expense-modal .wrapper").style.height="100px"; 
        return new Promise((resolve)=>{setTimeout(()=>{
            console.log(document.querySelector(".groupExpensePayeeWrapper"));
            document.querySelector(".groupExpensePayeeWrapper").remove();
            resolve();
            
        },100)});
    }
    console.log("collapsed expense modal")
}

// Don't ask why this is here, i don't know.
function selectSplitChoiceUserExpense(e){
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
    console.log(splitChoicesModalExpanded);
    groupExpenseDto.payee=e.target.getAttribute("username");
    console.log(groupExpenseDto)
}

export function clearSplitChoice(){
    activeChoice.classList.toggle("selected");
    activeChoice=choice1; 
    activeChoice.classList.toggle("selected");
}
let defaultGroupPayee= document.createElement("span");
defaultGroupPayee.textContent="Paid by you";
defaultGroupPayee.setAttribute("username",document.querySelector("#usernameeee").textContent);
defaultGroupPayee.classList.add("payee");
defaultGroupPayee.classList.add("active");
let activeGroupPayee = defaultGroupPayee;


let groupPayeeChoices = new Array();
export function groupExpenseSplit(event){
    if(activeChoice!=event.target){
        let groupExpensePayeeWrapper = document.createElement("div");
        groupExpensePayeeWrapper.addEventListener("click",selectPayee);
        groupExpensePayeeWrapper.classList.add("groupExpensePayeeWrapper");
        groupExpenseDto.payee = activeGroupPayee.getAttribute("username");
        
        console.log("A")
        
        setTimeout(()=>{
            if(document.querySelector(".groupExpensePayeeWrapper")!=null){
                document.querySelector(".groupExpensePayeeWrapper").remove();
            }
            groupExpensePayeeWrapper.insertAdjacentElement("beforeend",activeGroupPayee);
            insertDefaultChoice(groupExpensePayeeWrapper)
        },50)
        
    }else{
        console.log("B")
        setToHeight(document.querySelector(".add-expense-modal .wrapper"),"100px");
        insertDefaultChoice(groupExpensePayeeWrapper);
        setToHeight(document.querySelector(".add-expense-modal .wrapper"),"149px");
    }
}
async function setToHeight(element,height){
    
    return new Promise((resolve)=>{
        element.style.height=height;
        console.log("height "+ element.style.height);
        console.log("element ");
        console.log(element);
    });
}
function insertDefaultChoice(elementNode){
    
    setTimeout(()=>{
        console.log("insert default choice set to 100px");
        setToHeight(document.querySelector(".add-expense-modal .wrapper"),"100px")
        groupExpenseDto.payee = activeGroupPayee.getAttribute("username");
        console.log("Insert default choice group expense");
        let wrapperElement = document.querySelector(".add-expense-modal .wrapper");
        splitChoicesModalExpanded=false;
        console.log("Scroll height wrapper insertDefaultChoice");
        wrapperElement.offsetHeight;
        console.log(wrapperElement.scrollHeight);
        wrapperElement.insertAdjacentElement("beforeend",elementNode);
        console.log(wrapperElement.scrollHeight);
        wrapperElement.offsetHeight;    
        shrinkElement(document.querySelector(".add-expense-modal .wrapper"),"149px");
    },50)
}

export function createPayeeChoices(data){
    groupPayeeChoices = [];
    data.forEach(element => {
        let temp = document.createElement("span");
        temp.classList.add("payee");
        if(element.username ===document.querySelector("#usernameeee").textContent){
            temp.textContent="Paid by you";
            activeGroupPayee=temp;
        }else{
            temp.textContent="Paid by "+element.username;
        }
        if(activeGroupPayee.textContent === temp.textContent){
            temp.classList.add("active");
            activeGroupPayee=temp;
        }
        temp.setAttribute("username",element.username)
        groupPayeeChoices.push(temp)
    });
    console.log("Payees")
    console.log(groupPayeeChoices)
}
function insertPayeeChoices(){
    if(document.querySelector(".groupExpensePayeeWrapper") !=null){
        document.querySelector(".groupExpensePayeeWrapper").remove();
    }
    console.log("Insert payee choices");
    let wrapperElement = document.querySelector(".add-expense-modal .wrapper");
    let groupExpensePayeeWrapper = document.createElement("div");
    groupExpensePayeeWrapper.addEventListener("click",selectPayee);
    groupExpensePayeeWrapper.classList.add("groupExpensePayeeWrapper");
    groupPayeeChoices.forEach((e)=>{
    groupExpensePayeeWrapper.insertAdjacentElement("beforeend",e);
    })
    splitChoicesModalExpanded=true;
    wrapperElement.insertAdjacentElement("beforeend",groupExpensePayeeWrapper);
}


function selectPayee(e){
    if(e.target.classList.contains("payee")){
        console.log("Payee selected");
        if(!splitChoicesModalExpanded){
            
            insertPayeeChoices()
            expandElement(document.querySelector(".add-expense-modal .wrapper"));
        }else{
            console.log("Choice");
            console.log(e.target);
            activeGroupPayee.classList.toggle("active")
            activeGroupPayee=e.target;
            activeGroupPayee.classList.toggle("active")
            groupExpenseDto.payee=activeGroupPayee.getAttribute("username");
            console.log(groupExpenseDto);
            setToHeight(document.querySelector(".add-expense-modal .wrapper"),"149px").then(groupExpenseSplit(e));
            
            

        }
    }
}

function expandElement(element){
    console.log("Expand element")
    console.log(element);
    console.log(element.scrollHeight);
    element.offsetHeight;
    element.style.height = element.scrollHeight +"px";
}
async function shrinkElement(element,height){
    return new Promise((resolve)=>{
        element.style.height = "auto";
        
        element.style.offsetHeight  
        element.style.height = height;
        resolve();
    })
}
