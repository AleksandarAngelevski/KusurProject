import { createLoadingSpinner ,removeLoadingSpinner} from "./loadingSpinnerModule.js";
let initHeight;
let payee;
let receiver;
let template =`
<div class="settlmentWindow">
    <input type="text" name="" id="">
    <span>===></span>
    <input type="text" name="" id="">
</div>`

export async function fillModal(groupId,modalNode){
    try{
    createLoadingSpinner(modalNode)
    let balances = await fetchGroupNetBalances(groupId);
    removeLoadingSpinner();
    console.log(balances);
    const wrapper = createBalanceListWrapper();
    const balanceDivs = createBalanceDivs(balances);
    
    modalNode.append(...balanceDivs)
    modalNode.appendChild(wrapper);
    }catch(err){
        alert(err.message);
    }
}

async function fetchGroupNetBalances(groupId){
    try{
        const host = window.location.host;
        const token = localStorage.getItem("token");
        const groupBalances = await fetch("http://"+host+"/group/balances/user/"+document.querySelector("#groupId").textContent,{
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
        console.log(data);
        return data;
        
    }catch(err){
        throw new Error(err);
    }
}

function pause(milliseconds) {
	var dt = new Date();
	while ((new Date()) - dt <= milliseconds) { /* Do nothing */ }
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
        balanceItemWrapper.addEventListener("click",expandSettlementPaymentModal);
        array.push(balanceItemWrapper);
    })
    return array;
}


function expandSettlementPaymentModal(e){
    console.log(e.target);
    initHeight = e.target.offsetHeight;
    e.target.parentElement.style.height = e.target.parentElement.offsetHeight+"px";
    e.target.parentElement.insertAdjacentHTML("beforeend",template);
    e.target.parentElement.style.height = e.target.parentElement.scrollHeight+"px";

}