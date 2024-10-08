# 1단계: 빌드 단계
FROM gradle:7.6.0-jdk17 AS build
WORKDIR /app

COPY build.gradle settings.gradle ./
RUN gradle build --no-daemon || return 0

COPY . .
RUN gradle bootJar --no-daemon

# 2단계: 실행 단계
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar

# 9000번 포트 개방
EXPOSE 9000

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar", "--server.port=9000"]