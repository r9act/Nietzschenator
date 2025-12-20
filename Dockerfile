FROM eclipse-temurin:21-jre

WORKDIR /app

ARG JAR_FILE=target/nietzschenator.jar
COPY ${JAR_FILE} app.jar

EXPOSE 8081

ENTRYPOINT ["java","-jar","app.jar"]
