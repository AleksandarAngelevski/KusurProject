let add_friend_button = document.querySelector("#add-friend-btn")
let add_friend_input_field = document.querySelector("#add-friend-field")
add_friend_button.addEventListener("click",send_request)

async function send_request(){
    if(add_friend_input_field.value.trim()===""){
        alert("Enter a name first");
    }
    const host = window.location.host;
    let header = new Headers();
    const token = localStorage.getItem("token");
    const response = await fetch("http://"+host+"/add-friend",{
        method: "POST",
        credentials: "include",
        headers:{
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`,
        },
        body: JSON.stringify({username:add_friend_input_field.value.trim()}),
    }).then(response => response.text()).then(data => alert(data));
    alert(response.statusText);
}

const createGroupBtn = document.querySelector("#create-group-btn");
createGroupBtn.addEventListener("click",openModal);

//MODAL FUNCTIONALITY FROM HERE
const closeModalBtn = document.querySelector("#close-modal-btn");
closeModalBtn.addEventListener("click",closeModal);
const groupModal = document.querySelector(".create-group-modal");
let ul = document.querySelector(".create-group-modal ul").addEventListener("click", addUser);
// REQUEST BODY
let body = new Set();
function addUser(e){
    if(e.target.tagName.toLowerCase() == "ul") return;
    e.target.classList.toggle("selected")
    toggle(e.target.textContent)
    console.log(e.target.textContent);
    console.log(body)
}
async function openModal(e){
    const host = window.location.host;
    let header = new Headers();
    const token = localStorage.getItem("token");
    const response = await fetch("http://"+host+"/get-friends",{
        method: "GET",
        credentials: "include",
        header:{
            "Authorization": `Bearer ${token}`,
            "Content-Type": "application/json",

        }
        
    }).then(response => response.text()).then(data => insertNames(data)).catch(err => console.log(err));
    
    document.querySelector("#overlay").style.display="block";
    document.querySelector(".create-group-modal").style.display="block";
}
function toggle(name){
    if(body.has(name)){
        body.delete(name);
    }else{
        body.add(name);
    }
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
    let modal=document.querySelector(".create-group-modal").style.display="none";
    modal.textContent="";

}




