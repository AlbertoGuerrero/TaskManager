# **Sistema de Gestión de Tareas** 📋🚀

> Un sistema de microservicios para gestionar proyectos y tareas, con notificaciones por correo cuando se asignan tareas a los usuarios. El sistema utiliza **Spring Cloud**, **JWT** para autenticación y autorización, y **Apache Kafka** para la gestión del envío de correos electrónicos. La base de datos utilizada es **SQL Server**.

## **Descripción** 📖

Este proyecto es un sistema de microservicios diseñado para gestionar proyectos y tareas, ofreciendo una solución modular que facilita la escalabilidad y mantenimiento. La arquitectura basada en microservicios incluye la autenticación y autorización de usuarios mediante **JWT**, y utiliza **Apache Kafka** para manejar la comunicación y envío de correos electrónicos cuando se asignan tareas a los usuarios.

## **Características** ✨

- **Gestión de Proyectos**: CRUD completo de proyectos.
- **Gestión de Tareas**: CRUD completo para las tareas en proyectos.
- **Autenticación y Autorización Seguras**: Implementación de JWT para proteger los endpoints.
- **Sistema de Notificación por Correo**: Notificaciones automáticas mediante Apache Kafka.
- **Configuración Centralizada**: Uso de Spring Cloud Config para la gestión de propiedades.
- **Descubrimiento de Servicios**: Eureka para el descubrimiento y registro de servicios.
- **Gateway**: Filtrado y gestión de rutas mediante un API Gateway.

## **Tecnologías Utilizadas** 🛠️

- **Java** y **Spring Boot** para la creación de microservicios.
- **Spring Cloud** (Eureka, Config, Gateway) para la configuración, descubrimiento y enrutamiento de servicios.
- **JSON Web Token (JWT)** para la autenticación y autorización de usuarios.
- **Apache Kafka** para la mensajería y envío de notificaciones por correo.
- **SQL Server** como base de datos para almacenar la información de usuarios, proyectos y tareas.
- **Librería Commons**: Librería compartida que contiene DTOs y otros objetos comunes usados en los distintos microservicios.
