# INICIO DE LA APLICACIÓN

La presente aplicación pretende simular los servicios de reservaciones de un hotel,
además de prestar los diferentes servicios de planes, manejo de habitación, y la
gestión de un perfil de usuario con los accesos respectivos para la realización
y mantenibilidad de una reserva dado un usuario. 

La aplicación está  basada en Backend y está siendo realizada con Java con Spring Boot 
en su versión 3.3.2 originalmente y conectada a una base de datos PostgreSQL manejada 
por medio de un contenedor de Docker, y como ORM, estamos usando JPA/Hibernate.

La aplicación contendrá la "milla extra" donde implementaremos la carga de imágenes
usando Cloudinary, así como también implementaremos el envío de emails. Se considera 
a posteriorí pero no inmediatamente la implementación de reportes y la funcionalidad
de login usando Google.

### Desarrollado por: ###
Desarrollador de Backend: [Juan Sebastian Medina Toro](https://www.linkedin.com/in/juan-sebastian-medina-toro-887491249/)

### Levantamiento de la aplicación:
Para correr la aplicación en ambiente de desarrollo necesitamos:

* Levante el contenedor, ejecute el comando:
````dockerfile
$ docker compose up -d
````
* Ejecute la semilla por medio del siguiente End Point:
````dockerfile
$ Pendiente por definición.
````

* La documentación del proyecto está en la siguiente URL:
````dockerfile
$ http://localhost:${APP-PORT}/swagger-ui/index.html
````
...
