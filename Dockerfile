FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /workspace

COPY pom.xml .
RUN mvn -B -ntp dependency:go-offline

COPY src ./src
RUN mvn -B -ntp -Pdocker-container -DskipTests package

FROM eclipse-temurin:21-jre-jammy
RUN apt-get update \
    && apt-get install -y --no-install-recommends \
        libasound2 \
        libfontconfig1 \
        libfreetype6 \
        libgl1 \
        libgtk-3-0 \
        libx11-6 \
        libxext6 \
        libxfixes3 \
        libxi6 \
        libxrender1 \
        libxtst6 \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app
COPY --from=build /workspace/target/scientific-computing-workbench-1.0.0.jar /app/app.jar
COPY --from=build /workspace/target/jpackage-input /app/lib

ENV DISPLAY=host.docker.internal:0.0
ENV MATHAPP_API_BASE_URL=http://backend:8081

ENTRYPOINT ["java", "--module-path", "/app/lib", "--add-modules", "javafx.controls,javafx.fxml", "-cp", "/app/app.jar", "mathapp.gui.MainGUIApp"]
