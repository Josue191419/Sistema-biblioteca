# Biblioteca01

Proyecto Java Maven con arquitectura por capas para gestion de biblioteca en consola.

## Estructura
- `model`: entidades del dominio.
- `repository`: acceso a datos (en memoria).
- `service`: logica de negocio.
- `controller`: flujo entre vista y servicio.
- `view`: menu y entrada/salida por consola.

## Ejecutar
```powershell
mvn compile
mvn exec:java -Dexec.mainClass="org.example.Main"
```

Si falla `mvn exec:java`, agrega `exec-maven-plugin` al `pom.xml`.

