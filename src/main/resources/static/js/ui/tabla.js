const Tabla = {

    resaltar(id){

        const fila=document.getElementById(id);

        if(!fila)return;

        fila.classList.add("fila-nueva");

        setTimeout(()=>{

            fila.classList.remove("fila-nueva");

        },3000);

    }

};