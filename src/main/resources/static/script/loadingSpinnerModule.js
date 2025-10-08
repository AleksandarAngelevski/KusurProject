let template = `<div class="loading-spinner-wrapper">
    <div class="loading-spinner"></div>
    <div class="loading-spinner-child"></div>
</div>`;

export function createLoadingSpinner(parentNode){
    parentNode.insertAdjacentHTML("afterbegin",template);
}

export function removeLoadingSpinner(){
    const spinner = document.querySelector(".loading-spinner-wrapper");
    if(spinner){
        spinner.remove();
    }
}