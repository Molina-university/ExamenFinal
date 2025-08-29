# 📌 Análisis del Problema

La agricultura de pequeña escala enfrenta múltiples desafíos debido al uso de métodos tradicionales y la falta de acceso a tecnología. Muchos agricultores riegan y aplican insumos manualmente, basándose solo en la experiencia. Esto conlleva consecuencias como:

- **Uso ineficiente del agua:** se riega sin medir la necesidad real del suelo o cultivo, lo que genera un desperdicio valioso.
- **Baja productividad:** estrés hídrico y plagas se detectan tarde, disminuyendo el rendimiento.
- **Falta de información objetiva:** sin datos en tiempo real, las decisiones dependen únicamente de la intuición.




## ✅ ¿Qué soluciona nuestro sistema?

**AgroSense** visualiza las condiciones de cultivo para generar alertas y recomendaciones claras, brindando:

- Visualización sencilla del estado de cada lote.
- Alertas oportunas sobre necesidades de riego o cuidados.
- Recomendaciones prácticas y fáciles de entender.
## 🌱 Beneficios para los agricultores

- **Ahorro de agua y recursos:** se riega solo cuando es necesario.
- **Mejor rendimiento de los cultivos:** al evitar pérdidas, se optimizan los resultados.
- **Tecnología accesible:** no requiere conocimientos técnicos complejos.

# 👉 Descripción del Proyecto

**AgroSense** es un sistema inteligente de monitoreo agrícola diseñado para optimizar el cuidado de cultivos en pequeñas y medianas explotaciones.  
Su objetivo es brindar a los agricultores una herramienta accesible que transforme datos en decisiones prácticas, reduciendo pérdidas y aumentando la productividad.

El sistema combina hardware y software de manera integrada:
- **Sensores de humedad y temperatura** instalados en el campo para recopilar datos clave del suelo y del ambiente.
- **Plataforma digital** que procesa la información y la presenta en un panel intuitivo.
- **Alertas inteligentes** que notifican al agricultor cuándo y dónde regar, o si existe riesgo de condiciones críticas.
- **Recomendaciones personalizadas** para mejorar la eficiencia en el uso del agua y proteger la salud de los cultivos.

*De esta forma, el agricultor puede tener un control en tiempo real de sus lotes, tomar decisiones más informadas y garantizar un uso más sostenible de los recursos.*

# 🎯 Objetivos del Proyecto

## 🌱 Objetivo General
Diseñar e implementar un sistema de monitoreo agrícola que permita optimizar el uso del agua, mejorar la productividad y facilitar la toma de decisiones de los agricultores mediante el análisis de datos en tiempo real.

## 🌱 Objetivos Específicos
- Desarrollar una plataforma que represente lotes de cultivo y su estado de manera visual e intuitiva.  
- Simular el comportamiento de sensores de humedad y temperatura para generar información útil.  
- Implementar un sistema de alertas que notifique al agricultor sobre condiciones críticas en sus cultivos.  
- Proporcionar recomendaciones prácticas que promuevan el uso eficiente de recursos.  
- Mostrar el potencial de integrar tecnología en la agricultura como base para futuros modelos de negocio sostenibles.

# ✨ Características principales 

🌍 **Gestión de Lotes**  
Permite registrar y visualizar diferentes lotes de cultivo con información relevante sobre su estado.  

🌡️ **Monitoreo de Condiciones**  
Simulación de sensores de humedad, temperatura y otros factores clave para el crecimiento de los cultivos.  

🔔 **Alertas Inteligentes**  
Notificaciones cuando se detecten condiciones críticas, como baja humedad o exceso de calor.  

📊 **Visualización de Datos**  
Representación clara y gráfica del estado de cada lote para facilitar la toma de decisiones.  

💡 **Recomendaciones de Cuidado**  
Consejos prácticos basados en el estado del lote para optimizar el riego y el uso de recursos.  

# 🏗️ Arquitectura del proyecto

ExamenFinal/                                        <- Carpeta raíz del proyecto
│
├── README.md                                       <- 📘 Documentación principal
├── .gitignore                                      
│
├── src/                                            <- 💻 Código fuente
│   ├── Main.java                                   <- 🚀 Punto de entrada del sistema
│
│   ├── models/                                     <- 🌱 Clases principales
│   │   ├── Lote.java                               <- Representa un lote agrícola
│   │   ├── Cultivo.java                            <- Representa un cultivo
│   │   ├── Sensor.java                             <- Clase abstracta/base de sensores
│   │   ├── SensorHumedad.java                      <- Sensor de humedad del suelo
│   │   ├── SensorTemperatura.java                  <- Sensor de temperatura ambiental
│   │   └── Alerta.java                             <- Manejo de alertas del sistema
│
│   ├── services/                                   <- ⚙️ Lógica de negocio
│   │   ├── MonitorService.java                     <- Monitorea lotes y genera alertas
│   │   ├── SimuladorService.java                   <- Genera datos simulados de sensores
│   │   └── RecomendacionService.java               <- Reglas de riego/fertilización
│
│   ├── ui/                                         <- 🎨 Interfaz de usuario
│   │   ├── ConsolaUI.java                          <- Interfaz por consola (menús simples)
│   │   └── GUI.java                                <- Interfaz gráfica o móvil
│
│   └── utils/                                      <- 🛠️ Utilidades generales
│       └── Logger.java                             <- Registro de eventos y mensajes
│
└── tests/                                          <- ✅ Pruebas unitarias
    ├── LoteTest.java
    ├── SensorTest.java
    └── MonitorServiceTest.java



# ⚡ Tecnologías Utilizadas

👉 Este proyecto integra diversas tecnologías que permiten combinar el monitoreo agrícola con soluciones inteligentes:

- **Java** 🟦  
  Lenguaje principal para la programación orientada a objetos (POO). Permite estructurar el sistema en clases y servicios.

- **Sensores IoT** 🌡️💧  
  - Sensor de Humedad → Para medir la humedad del suelo.  
  - Sensor de Temperatura → Para registrar condiciones ambientales.  

- **Servicios de Lógica** ⚙️  
  Módulos internos encargados de monitoreo, generación de alertas y recomendaciones.

- **Interfaz de Usuario** 🎨  
  - Consola (menús simples).  
  - Futuro: GUI o aplicación móvil.

- **Pruebas Unitarias** ✅  
  Aseguran la confiabilidad y correcto funcionamiento del sistema.

# Instalación y Uso ⚙️.

Clone the project

```bash
  git clone https://github.com/Molina-university/ExamenFinal.git
```

Go to the project directory

```bash
  cd ExamenFinal
```

and open the project directory to run the program

```bash
  code .
```

### Interacción

- Selecciona opciones desde el menú por consola.

- Monitorea los lotes y revisa alertas generadas.

- Consulta recomendaciones de riego o fertilización.


## Authors

- [@Jhoan Molina 192490](https://github.com/Molina-university)
- [@Adrian Rincon 192490](https://github.com/Molina-university)
- [@Isaac algo 192490](https://github.com/Molina-university)
