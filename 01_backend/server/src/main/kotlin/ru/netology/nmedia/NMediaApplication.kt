package ru.netology.nmedia

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class NMediaApplication


/*
Создать JAr архив $./gradlew bootJar / или из самого градл в студии
зайти в папку build/libs и найти там jar

запустить из командной строки /snap/android-studio/101/android-studio/jre/bin# ./java -jar ~/AndroidStudioProjects/netologia_spring_server/andin-code/01_backend/server/build/libs/nmedia-0.0.1-SNAPSHOT.jar

открыть браузер и проверить работоспособность http://localhost:9999/api/posts

 */

fun main(args: Array<String>) {
    runApplication<NMediaApplication>(*args)
}
