FROM maven:3-amazoncorretto-17 as develop-stage-grupscooperatius
WORKDIR /app

COPY /config/iesmanacor-e0d4f26d9c2c.json /resources/iesmanacor-e0d4f26d9c2c.json

COPY /api/gestsuite-common/ /external/
RUN mvn clean compile install -f /external/pom.xml

COPY /api/gestsuite-grups-cooperatius .
RUN mvn clean package -f pom.xml
ENTRYPOINT ["mvn","spring-boot:run","-f","pom.xml"]

FROM maven:3-amazoncorretto-17 as build-stage-grupscooperatius
WORKDIR /resources

COPY /api/gestsuite-common/ /external/
RUN mvn clean compile install -f /external/pom.xml


COPY /api/gestsuite-grups-cooperatius .
RUN mvn clean package -f pom.xml

FROM amazoncorretto:17-alpine-jdk as production-stage-grupscooperatius
COPY --from=build-stage-grupscooperatius /resources/target/grups-cooperatius-0.0.1-SNAPSHOT.jar grupscooperatius.jar
COPY /config/iesmanacor-e0d4f26d9c2c.json /resources/iesmanacor-e0d4f26d9c2c.json
ENTRYPOINT ["java","-jar","/grupscooperatius.jar"]