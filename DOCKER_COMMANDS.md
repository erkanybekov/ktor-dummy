# 🐳 Docker Commands Cheat Sheet

Основные команды Docker для повседневной разработки.

## 📦 **Контейнеры (Containers)**

### Управление жизненным циклом
```bash
docker run <image>                    # Запустить контейнер
docker run -d <image>                 # Запустить в фоне (detached)
docker run -it <image> bash           # Интерактивный режим с bash
docker run -p 8080:80 <image>         # Проброс портов (хост:контейнер)

docker start <container>              # Запустить остановленный контейнер
docker stop <container>               # Остановить контейнер
docker restart <container>            # Перезапустить
docker kill <container>               # Принудительно убить
```

### Просмотр и отладка
```bash
docker ps                            # Запущенные контейнеры
docker ps -a                         # Все контейнеры (включая остановленные)
docker logs <container>              # Посмотреть логи
docker logs -f <container>           # Следить за логами в реальном времени

docker exec -it <container> bash     # Зайти внутрь запущенного контейнера
docker inspect <container>           # Подробная информация о контейнере
```

### Удаление
```bash
docker rm <container>                # Удалить контейнер
docker rm -f <container>             # Принудительно удалить (с остановкой)
docker container prune               # Удалить все остановленные контейнеры
```

## 🖼️ **Образы (Images)**

```bash
docker images                        # Список образов
docker pull <image>                  # Скачать образ
docker build -t <name> .             # Собрать образ из Dockerfile
docker rmi <image>                   # Удалить образ
docker image prune                   # Удалить неиспользуемые образы
```

## 💾 **Volumes (Данные)**

```bash
docker volume ls                     # Список volumes
docker volume create <name>          # Создать volume
docker volume rm <name>              # Удалить volume
docker volume prune                  # Удалить неиспользуемые volumes
```

## 🌐 **Networks**

```bash
docker network ls                    # Список сетей
docker network create <name>         # Создать сеть
docker network rm <name>             # Удалить сеть
```

## 🚀 **Docker Compose (Мультиконтейнерные приложения)**

### Основные команды
```bash
docker-compose up                    # Запустить все сервисы
docker-compose up -d                 # Запустить в фоне
docker-compose up <service>          # Запустить конкретный сервис

docker-compose stop                  # Остановить все сервисы
docker-compose down                  # Остановить и удалить контейнеры
docker-compose down -v               # + удалить volumes (⚠️ ОСТОРОЖНО!)
```

### Мониторинг и отладка
```bash
docker-compose ps                    # Статус сервисов
docker-compose logs                  # Логи всех сервисов
docker-compose logs <service>        # Логи конкретного сервиса
docker-compose logs -f <service>     # Следить за логами в реальном времени
```

### Сборка и обновление
```bash
docker-compose build                 # Пересобрать образы
docker-compose pull                  # Обновить образы
docker-compose up --build            # Пересобрать и запустить
```

## 🧹 **Очистка системы**

```bash
docker system df                     # Сколько места занимает Docker
docker system prune                  # Удалить неиспользуемые объекты
docker system prune -a               # Агрессивная очистка (все неиспользуемые образы)
docker system prune -a --volumes     # + удалить volumes (⚠️ ОЧЕНЬ ОСТОРОЖНО!)
```

## 🔍 **Мониторинг**

```bash
docker stats                         # Использование ресурсов (CPU, RAM)
docker top <container>               # Процессы внутри контейнера
docker port <container>              # Проброшенные порты
docker inspect <container>           # Полная информация о контейнере
```

## 💡 **Полезные флаги**

| Флаг | Описание |
|------|----------|
| `-d, --detach` | Запуск в фоне |
| `-it` | Интерактивный режим с TTY |
| `-p, --publish` | Проброс портов (хост:контейнер) |
| `-v, --volume` | Монтирование volume/папки |
| `-e, --env` | Переменные окружения |
| `--name` | Имя контейнера |
| `--rm` | Автоудаление после остановки |

## 🔥 **Часто используемые команды в разработке**

### Быстрый старт проекта
```bash
docker-compose up -d
```

### Посмотреть логи приложения
```bash
docker-compose logs app
```

### Зайти в базу данных
```bash
docker exec -it ktor-todo-postgres psql -U postgres -d ktor_todo
```

### Перезапустить конкретный сервис
```bash
docker-compose restart app
```

### Полная пересборка и запуск
```bash
docker-compose down && docker-compose up --build -d
```

### Очистка после разработки
```bash
docker-compose down -v && docker system prune
```

## 🛡️ **Безопасность данных**

### ✅ Безопасные команды (данные сохраняются)
```bash
docker-compose stop          # Остановка сервисов
docker-compose down          # Удаление контейнеров (volumes остаются)
docker-compose restart       # Перезапуск
```

### ⚠️ Опасные команды (данные могут потеряться)
```bash
docker-compose down -v       # Удаляет volumes
docker volume rm <name>      # Удаляет конкретный volume
docker system prune --volumes # Удаляет все неиспользуемые volumes
```

## 🔧 **Примеры для разных сценариев**

### Работа с базой данных
```bash
# Создать backup базы
docker exec -t postgres-container pg_dump -U postgres db_name > backup.sql

# Восстановить из backup
cat backup.sql | docker exec -i postgres-container psql -U postgres -d db_name

# Подключиться к PostgreSQL
docker exec -it postgres-container psql -U postgres
```

### Отладка контейнера
```bash
# Посмотреть что происходит внутри
docker exec -it <container> bash

# Проверить переменные окружения
docker exec <container> env

# Посмотреть процессы
docker exec <container> ps aux
```

### Работа с образами
```bash
# Собрать образ с тегом
docker build -t myapp:latest .

# Сохранить образ в файл
docker save myapp:latest > myapp.tar

# Загрузить образ из файла
docker load < myapp.tar
```

---

**💡 Совет:** Добавьте эти команды в закладки или создайте alias'ы для часто используемых команд!

```bash
# Полезные alias'ы для .bashrc/.zshrc
alias dps='docker ps'
alias dcu='docker-compose up -d'
alias dcd='docker-compose down'
alias dcl='docker-compose logs'
alias dcr='docker-compose restart'
``` 