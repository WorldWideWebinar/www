FROM eclipse-temurin:17
WORKDIR /app
COPY build/libs/globalcc-0.0.1-SNAPSHOT.jar app.jar
RUN mkdir resources
ENV SPRING_PROFILES_ACTIVE=prod
ENV DEEPL_API = ${DEEPL_API}
ENV JWT_SECRET = ${JWT_SECRET}
ENV DATASOURCE_USERNAME = ${DATASOURCE_USERNAME}
ENV DATASOURCE_PASSWORD = ${DATASOURCE_PASSWORD}
ENV OPENVIDU_SECRET = ${OPENVIDU_SECRET}
ENV RABBIT_USERNAME = ${RABBIT_USERNAME}
ENV RABBIT_PASSWORD = ${RABBIT_PASSWORD}
ENV EMOJI_API_KEY = ${EMOJI_API_KEY}
ENV OPENAI_API_KEY = ${OPENAI_API_KEY}

ENTRYPOINT ["java","-DSPRING.PROFILES.ACTIVE=prod","-Djava.security.egd=file:/dev/./urandom","-jar","app.jar"]
