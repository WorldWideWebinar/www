FROM openjdk:17-jdk AS build
WORKDIR /app
COPY . .
RUN chmod +x ./gradlew
RUN ./gradlew bootJar --no-daemon

# Step 2: Create the final image with JRE and the built application
FROM openjdk:17-jdk
WORKDIR /app
COPY --from=build /app/build/libs/globalcc-0.0.1-SNAPSHOT.jar app.jar
ENV SPRING_PROFILES_ACTIVE=prod
ENTRYPOINT ["java","-DSPRING.PROFILES.ACTIVE=prod","-Djava.security.egd=file:/dev/./urandom","-jar","/docker-springboot.jar"]
