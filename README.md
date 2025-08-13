# 🐉 Pokédex - Primera Generación

Una aplicación web moderna desarrollada con **Spring Boot** que contiene información completa sobre los 151 Pokémon de la primera generación.

## ✨ Características

- **Pokédex Completa**: Información detallada de todos los 151 Pokémon de la primera generación
- **Datos Automáticos**: Se obtienen directamente de la PokeAPI oficial
- **Imágenes Oficiales**: Sprites de alta calidad de cada Pokémon
- **Interfaz Web Moderna**: Diseño responsivo con Bootstrap 5 y Thymeleaf
- **API REST Completa**: Endpoints para desarrolladores
- **Búsqueda Avanzada**: Por nombre, número o tipo
- **Estadísticas Visuales**: Gráficos interactivos de las estadísticas base
- **Base de Datos H2**: En memoria, se inicializa automáticamente
- **Panel de Administración**: Para recargar datos desde la API

## 🚀 Tecnologías Utilizadas

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Data JPA**
- **H2 Database**
- **Thymeleaf**
- **Bootstrap 5**
- **Font Awesome**

## 📋 Requisitos Previos

- Java 17 o superior
- Maven 3.6 o superior
- Navegador web moderno

## 🛠️ Instalación y Ejecución

### 1. Clonar el Repositorio
```bash
git clone <url-del-repositorio>
cd PokemonApp-Java-Cursor
```

### 2. Compilar el Proyecto
```bash
mvn clean compile
```

### 3. Ejecutar la Aplicación
```bash
mvn spring-boot:run
```

### 4. Acceder a la Aplicación
- **Aplicación Web**: http://localhost:8080
- **Panel de Admin**: http://localhost:8080/admin
- **Consola H2**: http://localhost:8080/h2-console
- **API REST**: http://localhost:8080/api/pokemon

### 5. Cargar Datos (Primera vez)
La primera vez que ejecutes la aplicación, se cargarán automáticamente los 151 Pokémon desde la PokeAPI. 
Si quieres recargar los datos, puedes usar el panel de administración.

## 🎯 Funcionalidades

### Página Principal
- Lista completa de Pokémon con tarjetas interactivas
- Barra de búsqueda por nombre
- Filtrado por tipo
- Información resumida de estadísticas

### Detalle del Pokémon
- Información completa del Pokémon seleccionado
- Estadísticas base con gráficos visuales
- Navegación entre Pokémon (anterior/siguiente)
- Información física (altura, peso, tipos)

### Búsqueda y Filtros
- Búsqueda por nombre (parcial)
- Filtrado por tipo de Pokémon
- Resultados en tiempo real

## 🔌 API REST

La aplicación incluye una API REST completa para desarrolladores:

### Endpoints Disponibles

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `GET` | `/api/pokemon` | Obtener todos los Pokémon |
| `GET` | `/api/pokemon/{numero}` | Obtener Pokémon por número |
| `GET` | `/api/pokemon/nombre/{nombre}` | Obtener Pokémon por nombre |
| `GET` | `/api/pokemon/buscar/{nombre}` | Buscar Pokémon por nombre parcial |
| `GET` | `/api/pokemon/tipo/{tipo}` | Obtener Pokémon por tipo |
| `POST` | `/api/pokemon` | Crear nuevo Pokémon |
| `PUT` | `/api/pokemon/{id}` | Actualizar Pokémon existente |
| `DELETE` | `/api/pokemon/{id}` | Eliminar Pokémon |

### Ejemplo de Uso de la API

```bash
# Obtener todos los Pokémon
curl http://localhost:8080/api/pokemon

# Obtener Bulbasaur por número
curl http://localhost:8080/api/pokemon/1

# Buscar Pokémon por nombre
curl http://localhost:8080/api/pokemon/buscar/char

# Obtener Pokémon de tipo Fuego
curl http://localhost:8080/api/pokemon/tipo/Fuego
```

## 🗄️ Base de Datos

- **H2 Database**: Base de datos en memoria
- **Inicialización Automática**: Se cargan los 151 Pokémon al iniciar
- **Consola H2**: Accesible en `/h2-console`
  - **URL**: `jdbc:h2:mem:pokemondb`
  - **Usuario**: `sa`
  - **Contraseña**: `password`

## 📱 Características de la Interfaz

- **Diseño Responsivo**: Optimizado para móviles y escritorio
- **Tarjetas Interactivas**: Efectos hover y transiciones suaves
- **Colores por Tipo**: Cada tipo de Pokémon tiene su color distintivo
- **Iconografía**: Uso de Font Awesome para mejor experiencia visual
- **Navegación Intuitiva**: Menú de navegación claro y accesible

## 🎨 Personalización

### Colores por Tipo de Pokémon
- **Planta**: Verde (#4CAF50)
- **Fuego**: Naranja (#FF5722)
- **Agua**: Azul (#2196F3)
- **Eléctrico**: Amarillo (#FFC107)
- **Hielo**: Azul Claro (#00BCD4)
- **Lucha**: Rojo (#F44336)
- **Veneno**: Púrpura (#9C27B0)
- **Tierra**: Marrón (#795548)
- **Volador**: Azul Cielo (#03A9F4)
- **Psíquico**: Rosa (#E91E63)
- **Bicho**: Verde Claro (#8BC34A)
- **Roca**: Gris (#607D8B)
- **Fantasma**: Púrpura Oscuro (#673AB7)
- **Dragón**: Índigo (#3F51B5)
- **Siniestro**: Negro (#212121)
- **Acero**: Gris Metálico (#9E9E9E)
- **Hada**: Rosa Claro (#FFB3E5)

## 🚀 Despliegue

### Despliegue Local
```bash
mvn spring-boot:run
```

### Despliegue con JAR
```bash
mvn clean package
java -jar target/pokemon-app-0.0.1-SNAPSHOT.jar
```

### Variables de Entorno
- `server.port`: Puerto del servidor (por defecto: 8080)
- `spring.datasource.url`: URL de la base de datos
- `spring.jpa.hibernate.ddl-auto`: Modo de creación de tablas

## 🧪 Pruebas

```bash
# Ejecutar pruebas unitarias
mvn test

# Ejecutar pruebas de integración
mvn verify
```

## 📝 Estructura del Proyecto

```
src/
├── main/
│   ├── java/
│   │   └── com/pokemon/
│   │       ├── PokemonAppApplication.java
│   │       ├── controller/
│   │       │   ├── PokemonController.java
│   │       │   └── WebController.java
│   │       ├── model/
│   │       │   └── Pokemon.java
│   │       ├── repository/
│   │       │   └── PokemonRepository.java
│   │       ├── service/
│   │       │   └── PokemonService.java
│   │       └── config/
│   │           └── DataInitializer.java
│   └── resources/
│       ├── templates/
│       │   ├── index.html
│       │   ├── detalle.html
│       │   └── about.html
│       └── application.properties
└── test/
    └── java/
        └── com/pokemon/
```

## 🤝 Contribuir

1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

## 📄 Licencia

Este proyecto está bajo la Licencia MIT. Ver el archivo `LICENSE` para más detalles.

## 👨‍💻 Autor

Desarrollado como proyecto de demostración de Spring Boot.

## 🙏 Agradecimientos

- **Nintendo/Game Freak**: Por crear la franquicia Pokémon
- **Spring Team**: Por el excelente framework Spring Boot
- **Comunidad Open Source**: Por las librerías y herramientas utilizadas

---

**¡Disfruta explorando la primera generación de Pokémon!** 🎮✨
