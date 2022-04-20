# Дипломный проект профессии «Тестировщик ПО»

## Документация проекта

* [План автоматизации тестирования](https://github.com/BirrTaty/Diplom-QA/blob/master/docs/Plan.md)
* [Отчет о тестировании](https://github.com/BirrTaty/Diplom-QA/blob/master/docs/Report.md)
* [Отчёт о проведённой автоматизации тестирования]()

## Инструкция по запуску тестов
#### Необходимо установит и настроить: 
* IntelliJ IDEA Ultimate
* Docker

#### Запуск SUT и автотестов

1. Склонировать репозиториий [Diplom-QA](https://github.com/BirrTaty/Diplom-QA) на свой ПК командой и открыть его в IntelliJ IDEA Ultimate.
2. В терминале развернуть контейнер командой `docker-compose up` . 
3. Запустить SUT:
 + Для MySQL в новом окне терминала ввести команду `java -jar artifacts/aqa-shop.jar --spring.profiles.active=mysql`
 + Для PostgreSQL: в новом окне терминала ввести команду `java -jar artifacts/aqa-shop.jar --spring.profiles.active=postgresql`
4. Запустить тесты:
 + Для БД MySQL: в новом окне терминала ввести команду `./gradlew test -DdbUrl=jdbc:mysql://localhost:3306/app-db`
 + Для БД PostgreSQL: в новом окне терминала ввести команду `./gradlew test -DdbUrl=jdbc:postgresql://localhost:5432/app-db`

#### Формирование отчетов тестирования
В новом окне терминала запустить формирование отчета командой ./gradlew allureServe
