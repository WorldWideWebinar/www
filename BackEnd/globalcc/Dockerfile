FROM eclipse-temurin:17 AS build
WORKDIR /app
COPY . .
RUN apt-get update && apt-get install -y findutils

RUN chmod +x ./gradlew
RUN ./gradlew bootJar

# Step 2: Create the final image with JRE and the built application
FROM eclipse-temurin:17
WORKDIR /app
COPY --from=build /app/build/libs/globalcc-0.0.1-SNAPSHOT.jar app.jar
ENV SPRING_PROFILES_ACTIVE=prod
ENTRYPOINT ["java","-DSPRING.PROFILES.ACTIVE=prod","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
