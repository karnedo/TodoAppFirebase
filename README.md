# To-do App

### Objetivo
El objetivo de la aplicación es la de proveer de una manera intuitiva y rápida la habilidad de crear y gestionar tareas. Cada tarea consta de un nombre, una fecha de cumplimiento y una prioridad.

### Requisitos
- **Diseño**: La aplicación cumple con un diseño minimalista y ligero a la vista. Se complementa con el soporte de tanto un modo claro como un modo oscuro.
- **Values y traducciones**: La aplicación está desarrollada de manera que exista la mínima cantidad de valores hard coded: esto significa que todas las cadenas de texto, todos los colores y todas las dimensiones de la interfaz se encuentran almacenados en el directorio values. Además, esta traducida a los idiomas Español e Inglés.\
- **Intent/Bundle**: Se ha empleado el uso de las clases Intent y Bundle para pasar datos entre actividades.
- **SavedInstanceStated**: los datos no se pierden cuando se cambia la orientación del dispositivo.
- **Portrait/Landscape**: La aplicación se adapta correctamente tanto cuando el dispositivo está en orientación vertical como en horizontal.

### Extras
- **Persistencia**: Al no ser Firebase contenido del presente tema, se ha optado por almacenar las tareas en un fichero de texto plano. De esta forma, todas las tareas permanecen guardadas en la memoria interna del dispositivo, pudiendo cerrar la aplicación o apagar el teléfono mantentiendo todas las tareas intactas.
- **Bienvenida**: La primera vez que se ejecuta la aplicación, se crean algunas tareas de ejemplo para ayudar al usuario a entender mejor su funcionamiento. Estas tareas no vuelven a crearse, es decir, que si se eliminan no volverán a aparecer.
- **Diseño**: Se ha incluido algunos elementos al diseño para hacer más amena la experiencia al usuario. Por ejemplo, si una tarea no se ha realizado antes de cumplir su fecha de expiración, la fecha se pondrá en color rojo.

