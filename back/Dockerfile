FROM maven AS build
RUN mkdir -p /workspace
WORKDIR /workspace
COPY pom.xml /workspace
COPY src /workspace/src
RUN mvn -B -f pom.xml clean package -DskipTests
FROM eclipse-temurin
COPY --from=build /workspace/target/*.jar app.jar
EXPOSE 3001
ENTRYPOINT ["java", "-jar","app.jar"]
