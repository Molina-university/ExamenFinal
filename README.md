# AgroSense - Sistema de Monitoreo Agrícola 🌿

Sistema inteligente de monitoreo agrícola con interfaz gráfica moderna en **JavaFX** y persistencia de datos en JSON.

## Características

- 🌱 **Gestión de Lotes**: Registro y administración de lotes de cultivo
- 📡 **Sensores IoT**: Monitoreo de humedad y temperatura en tiempo real
- 📊 **Visualización**: Interfaz gráfica moderna con datos actualizados
- ⚠️ **Alertas Inteligentes**: Detección automática de condiciones críticas
- 💡 **Recomendaciones**: Consejos basados en datos para optimizar cultivos

## Requisitos

- Java 17 o superior
- Maven 3.6+

## Instalación y Ejecución

### 1. Compilar el proyecto

```powershell
mvn clean compile
```

### 2. Ejecutar la aplicación (JavaFX)

**Opción A - Usando Maven (Recomendado):**
```powershell
mvn javafx:run
```
o
```powershell
mvn exec:java
```

## Uso de la Aplicación

### Interfaz Gráfica (JavaFX)

1. **Gestión de Lotes** (🌱)
   - Registre lotes con ID, nombre, tipo de cultivo y área
   - Vea todos los lotes registrados en la tabla

2. **Sensores** (📡)
   - Agregue sensores de HUMEDAD o TEMPERATURA a cada lote
   - Especifique la ubicación del sensor

3. **Monitoreo** (📊)
   - Haga clic en "Simular Lectura de Sensores"
   - Observe los valores y el estado de cada sensor

4. **Alertas** (⚠️)
   - Vea el historial de alertas generadas
   - Lea las recomendaciones inteligentes

## Estructura del Proyecto

```
src/main/java/com/agrosense/
├── model/              # Entidades del dominio
├── service/            # Lógica de negocio
└── ui/                 # Interfaces de usuario
    ├── AgroSenseFX.java     # Interfaz gráfica JavaFX (Principal)
    ├── AgroSenseGUI.java    # Interfaz gráfica Swing (Legacy)
    └── ConsoleUI.java       # Interfaz de consola
```

## Tecnologías

- **Java 17**: Lenguaje de programación
- **JavaFX**: Interfaz gráfica moderna
- **Maven**: Gestión de dependencias y construcción

## Autor

AgroSense - Sistema de Monitoreo Agrícola Inteligente
