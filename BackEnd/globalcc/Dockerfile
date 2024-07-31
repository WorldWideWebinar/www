FROM eclipse-temurin:17
WORKDIR /app
COPY build/libs/globalcc-0.0.1-SNAPSHOT.jar app.jar
RUN mkdir resources
ENV SPRING_PROFILES_ACTIVE=prod
ENTRYPOINT ["java","-DSPRING.PROFILES.ACTIVE=prod","-Djava.security.egd=file:/dev/./urandom","-jar","app.jar"]
