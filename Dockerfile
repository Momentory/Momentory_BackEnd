# 1단계: 빌드 환경 (Gradle + JDK)
FROM gradle:7.6.2-jdk17 AS build
WORKDIR /app

# Gradle 캐시 최적화: 의존성 먼저 복사
COPY build.gradle settings.gradle ./
COPY gradle gradle
RUN gradle dependencies --no-daemon || return 0

# 소스 복사 후 빌드
COPY src src
RUN gradle bootJar --no-daemon

# 2단계: 실행 환경
FROM eclipse-temurin:17-jdk-jammy
ENV TZ=Asia/Seoul
RUN ln -sf /usr/share/zoneinfo/Asia/Seoul /etc/localtime
ENV JAVA_TOOL_OPTIONS="-Duser.timezone=Asia/Seoul"

WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
