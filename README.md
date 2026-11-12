# Plataforma de subastas con liquidacion por tiempos

Proyecto final de estructuras de datos

## Partes

- backend: Java con Spring Boot
- frontend: HTML CSS y JavaScript
- base_de_datos: tablas de PostgreSQL

## Como correrlo

1. Crear en PostgreSQL una base llamada subastas y ejecutar base_de_datos/schema.sql una sola vez
2. Poner el usuario y la clave de PostgreSQL en backend/src/main/resources/application.properties
3. Entrar a la carpeta backend y ejecutar gradlew bootRun
4. Abrir http://localhost:8080/index.html

El administrador se crea solo al arrancar con los datos de application.properties

## Estructuras de datos

- ColaPujas: cola de prioridad propia hecha con un arreglo (monticulo) donde la puja mas alta queda arriba. Se usa para saber quien va ganando y quien gana al cerrar
- PriorityQueue de cierres: las subastas activas ordenadas por la hora en que terminan. Cada segundo se revisa la de arriba para cerrarla
- HashMap: subastas y usuarios por id y las sesiones por token
- Queue: las pujas que llegan se procesan una por una en orden de llegada
- ArrayList: historial de pujas de cada producto en orden de tiempo

## Pruebas

Dentro de backend ejecutar gradlew test

- ColaPujasTest: prueba la cola de prioridad propia
- SubastaTest: prueba las reglas de las pujas y el orden de cierres
- RendimientoTest: mide el tiempo de la cola propia y del HashMap contra recorrer un ArrayList

Los logs quedan en backend/logs/Subastas.log
