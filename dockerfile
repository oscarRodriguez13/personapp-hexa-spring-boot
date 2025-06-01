FROM maven:3.9.9-amazoncorretto-11 AS build

WORKDIR /app
COPY pom.xml .
COPY common/pom.xml ./common/
COPY domain/pom.xml ./domain/
COPY application/pom.xml ./application/
COPY maria-output-adapter/pom.xml ./maria-output-adapter/
COPY mongo-output-adapter/pom.xml ./mongo-output-adapter/
COPY rest-input-adapter/pom.xml ./rest-input-adapter/
COPY cli-input-adapter/pom.xml ./cli-input-adapter/

#RUN mvn dependency:go-offline
COPY common/src ./common/src
COPY domain/src ./domain/src
COPY application/src ./application/src
COPY maria-output-adapter/src ./maria-output-adapter/src
COPY mongo-output-adapter/src ./mongo-output-adapter/src
COPY rest-input-adapter/src ./rest-input-adapter/src
COPY cli-input-adapter/src ./cli-input-adapter/src

RUN mvn install


FROM amazoncorretto:11.0.27-alpine3.21 AS deploy

WORKDIR /app
COPY --from=build /app/rest-input-adapter/target/rest-input-adapter-0.0.1-SNAPSHOT.jar /app/laboratorio2-rest.jar

CMD ["java", "-jar", "/app/laboratorio2-rest.jar"]
#CMD ["java","-cp", "/app/laboratorio2-rest.jar", "co.edu.javeriana.as.personapp.PersonAppRestApi"]
