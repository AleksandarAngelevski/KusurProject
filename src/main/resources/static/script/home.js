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