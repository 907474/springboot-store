@echo off
call mvnw.cmd clean package -DskipTests
java -jar target/store-0.0.1-SNAPSHOT.jar