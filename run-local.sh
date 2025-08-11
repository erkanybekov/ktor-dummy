#!/bin/bash

# Запуск Ktor приложения локально
echo "🚀 Запускаем Ktor приложение..."

# Запускаем PostgreSQL если не запущена
echo "📦 Проверяем PostgreSQL..."
docker-compose up -d postgres

# Ждем немного для запуска базы
sleep 2

# Запускаем приложение с нужными переменными
echo "🔥 Запускаем Ktor..."
DATABASE_URL="jdbc:postgresql://localhost:5432/ktor_todo" \
DATABASE_USER="postgres" \
DATABASE_PASSWORD="password" \
./gradlew run 