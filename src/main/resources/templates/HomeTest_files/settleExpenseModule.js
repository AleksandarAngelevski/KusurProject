import { createLoadingSpinner ,removeLoadingSpinner} from "./loadingSpinnerModule.js";
let initHeight;
let activeNode;
let payee;
let receiver;
let template =`
<div class="settlementWindow">
    <div class="row"><input type="text" name="" id="" disabled>
    <span>===></span>
    <input type="text" name="" id="" disabled></div>
    <br>
    <div class="row"><input id="amount" type="text" name="" id="" placeholder="Amount..."></div>
    <br>
    <div class="row"><button class="send-settlement-btn">Settle Payment</button></div>
</div>`

export async function fillModal(groupId,modalNode){
    try{
    createLoadingSpinner(modalNode)
    let balances
  
    if(groupId){
        
        balances = await fetchGroupNetBalances(groupId);
    }else{
        
        balances = await fetchNetBalances();
    }
    removeLoadingSpinner();
    if(balances.length===0){
        modalNode.textContent="No balances to settle";
        return;
    }
    const wrapper = createBalanceListWrapper();
    const balanceDivs = createBalanceDivs(balances);
    wrapper.append(...balanceDivs)
    modalNode.appendChild(wrapper);
    }catch(err){
        alert(err.message);
        console.error(err);
    }
}
function fillSettlemntWindow(){
    let inputs = activeNode.querySelectorAll(".settlementWindow input");
    inputs[0].value = activeNode.getAttribute("debtor")===document.querySelector("#usernameeee").textContent ? "You" : activeNode.getAttribute("debtor");
    inputs[1].value = activeNode.getAttribute("debtee")===document.querySelector("#usernameeee").textContent ? "You" : activeNode.getAttribute("debtee");
    inputs[2].value = activeNode.getAttribute("amount")
    document.querySelector(".settlementWindow .send-settlement-btn").addEventListener("click",(e)=>{
        populateSettlementDto();
    })
}
async function fetchGroupNetBalances(groupId){
    try{
        const host = window.location.host;
        const token = localStorage.getItem("token");
        const groupBalances = await fetch("http://"+host+"/group/balances/user/"+groupId,{
            method: "GET",
            credentials: "include",
            headers:{
                "Authorization": `Bearer ${token}`,
                "Content-Type": "application/json",
            },            
        });
        if(!groupBalances.ok){
            throw new Error("Could not fetch group balances");

        }
        const data = await groupBalances.json();
        // pause(1000);
        return data;
        
    }catch(err){
        throw new Error(err);
    }
}

async function fetchNetBalances(){
    try{
        const host = window.location.host;
        const token = localStorage.getItem("token");
        const netBalances = await fetch("http://"+host+"/netBalances",{
            method: "GET",
            credentials: "include",
            headers:{
                "Authorization": `Bearer ${token}`,
                "Content-Type": "application/json",
            },            
        });
        if(!netBalances.ok){
            throw new Error("Could not fetch net balances");

        }
        const data = await netBalances.json();
        // pause(1000);
        return data;
        
    }catch(err){
        throw new Error(err);
    }
}

function pause(milliseconds) {
	var dt = new Date();
	while ((new Date()) - dt <= milliseconds) { /* Do nothing */ }
}
function pauseV2(milliseconds) {  /* Different version of pausing the code execution but using Promises */
  return new Promise(resolve => setTimeout(resolve, milliseconds));
}

function createBalanceListWrapper(){
    const wrapper = document.createElement("div");
    wrapper.classList.add("balance-list-wrapper");
    return wrapper;
}
function createBalanceDivs(balances){
    let array = [];
    balances.forEach(balance=>{
        const balanceItemWrapper = document.createElement("div");
        balanceItemWrapper.classList.add("balance-item-wrapper");
        balanceItemWrapper.style.cursor="pointer";
        balanceItemWrapper.style.overflow="hidden";
        balanceItemWrapper.style.transition="height 0.3s ease";
        const balanceItem = document.createElement("div");
        balanceItemWrapper.appendChild(balanceItem);
        balanceItem.classList.add("balance-item");
        balanceItem.textContent=balance.message;
        balanceItemWrapper.setAttribute("debtor",balance.debtor.username);
        balanceItemWrapper.setAttribute("debtee",balance.debtee.username);
        balanceItemWrapper.setAttribute("amount",balance.amount);
        balanceItemWrapper.addEventListener("click",expandSettlementPaymentModal);
        array.push(balanceItemWrapper);
    })
    return array;
}


async function expandSettlementPaymentModal(e){
    if(activeNode===e.target.parentElement && e.target.classList.contains("balance-item")){
        console.log("shrinking current");
        shrinkSettlementPaymentModal(e);
        
    }else if(activeNode!==e.target.parentElement && e.target.classList.contains("balance-item")){
        if(activeNode){
            console.log("shrinking previous");
            shrinkSettlementPaymentModal(e);
            await pauseV2(200);
        }
        console.log("expanding new");
        activeNode = e.target.parentElement;
        initHeight = e.target.offsetHeight;
        e.target.parentElement.style.height = e.target.parentElement.offsetHeight+"px";
        e.target.parentElement.insertAdjacentHTML("beforeend",template);
        fillSettlemntWindow();
        e.target.parentElement.style.height = e.target.parentElement.scrollHeight+"px";
        setTimeout(()=>{e.target.parentElement.querySelector(".settlementWindow .row #amount").focus();},350);
    
    }
}
async function shrinkSettlementPaymentModal(e){
    activeNode.style.height = initHeight+"px";
    setTimeout(()=>{
        const settlementWindow = activeNode.children[1];
        if(settlementWindow){
            settlementWindow.remove();
            activeNode = null;
        }
    },200);
 }

let settlementDto = {
    groupId: null,
    amount: null,
    debtorName: null,
    debteeName: null,
}
async function populateSettlementDto(){
    try{
        let inputs = activeNode.querySelectorAll(".settlementWindow input");
        if(document.querySelector("#groupId")){
            settlementDto.groupId = document.querySelector("#groupId").textContent;
        }
        
        amountValidation(inputs[2].value);
        settlementDto.amount = inputs[2].value;
        settlementDto.debtorName= inputs[0].value==="You" ? document.querySelector("#usernameeee").textContent: inputs[0].value;
        settlementDto.debteeName= inputs[1].value==="You" ? document.querySelector("#usernameeee").textContent: inputs[1].value;
        console.log(settlementDto);
        await sendSettlePaymentRequest(settlementDto);
    }
    catch(error){
        alert(error.message);
        settlementDto.amount = null;
        settlementDto.deborName= null;
        settlementDto.deborName= null;
        return;
    }    
}

async function sendSettlePaymentRequest(settlementData){
    try{ /* Send the required data to the backend */
        const host = window.location.host;
        const token = localStorage.getItem("token");
        let response = await fetch("http://"+host+"/settlePayment",{
            method: "POST",
            credentials: "include",
            headers:{   
                "Authorization": `Bearer ${token}`,
                "Content-Type": "application/json",
                "Access-Control-Allow-Origin":"*",
            },
            body: JSON.stringify(settlementDto)
        })
        if(!response.ok){
                const errorData = await response.text();
                throw Error(errorData);
            }
            else{
                let data = await response.json();
                console.log(data)
                return data;
            }
        
    }
    catch(err)   {
        throw Error(err.message);
    }
}


function amountValidation(amountString){
    const reg= /^[1-9]\d*(\.\d+)?$/;
    let amountInt = parseFloat(amountString);
    if(Number.isNaN(amountInt)){
        throw Error("Inserted amount must e a number!");
    } else if(!amountString.match(reg)){
        throw Error("Inserted amount must be in the format of 100.00 or 100");
    }
    
}
