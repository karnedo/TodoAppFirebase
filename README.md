# To-do App

### RecyclerView
Las tareas se muestras en una lista contenida en el RecyclerView. Al añadir una tarea, esta se mostrará en la primera posición. Al marcarla como terminada, se irá a la última posición. 

### Firebase Realtime
La aplicación almacena las tareas introducidas por el usuario con su nombre, su fecha de cumplimiento y su prioridad. Éstas se puedan marcar como completadas o borrarlas, todo ello siendo actualizado en tiempo real en la base de datos.

### Firebase Auth
La aplicación permite el registro introduciendo un correo electrónico y contraseña, o directamente haciendo inicio de sesión con una cuenta Google. Las tareas no son públicas, es decir, cada usuario tiene su propia lista de tareas privada.
El inicio de sesión se guarda automáticamente. Una vez introducida una cuenta, no se volverá a pedir al usuario. **Si se desea salir de la sesión, se puede hacer click en la imagen superior derecha.**

### Firebase Storage
La aplicación guarda imágenes en forma de fotos de perfil que aparecerán en la esquina superior derecha. **Para cambiar la foto, mantenga presionada la imagen superior derecha.** La imagen por defecto es el logo de la aplicación.
