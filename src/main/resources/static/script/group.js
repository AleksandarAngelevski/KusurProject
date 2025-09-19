let settleExpenseButton = document.querySelector("#settle-expense-btn");
let settleExpenseModal  = document.querySelector(".settle-expense-modal");
let closeExpenseModalButon = document.querySelector("#close-btn-modal");

settleExpenseButton.addEventListener("click",openSettleExpenseModal);
closeExpenseModalButon.addEventListener("click",closeSettleExpenseModal);

function openSettleExpenseModal(e){
    document.querySelector("#overlay").style.display = "block";
    settleExpenseModal.style.display = "block";
    getGroupBalances();
}
function closeSettleExpenseModal(e){
    document.querySelector("#overlay").style.display = "none";
    settleExpenseModal.style.display = "none";
}
function clearSettleExpenseModal(e){
    settleExpenseModal.textContent = "";
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