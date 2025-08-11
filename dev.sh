#!/bin/bash

# Простой запуск для разработки
export DATABASE_URL="jdbc:postgresql://localhost:5432/ktor_todo"
export DATABASE_USER="postgres"
export DATABASE_PASSWORD="password"

./gradlew run 