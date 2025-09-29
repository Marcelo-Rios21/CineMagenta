# Cine Magenta 🎬

## Requisitos previos
- JDK 17+
- Maven 3.8+
- Docker + Docker Compose

---

## Despliegue con Docker

Para levantar la base de datos MySQL y la interfaz Adminer se utilizó:

```bash
docker compose --env-file docker/.env -f docker/docker-compose.yml up -d
```
Probar conexion:
```bash
echo "SHOW DATABASES;" | docker exec -i cine_mysql sh -lc 'MYSQL_PWD="$MYSQL_PASSWORD" mysql -u "$MYSQL_USER"'
```

Detener y limpiar contenedores:
```bash
docker compose -f docker/docker-compose.yml down -v

```
Despues de actualkizar, limpiar y recompilar
```bash
mvn clean compile

