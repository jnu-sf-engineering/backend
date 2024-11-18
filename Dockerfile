# OpenJDK 17을 기반으로 하는 멀티 플랫폼 경량화 스프링 부트 이미지
# 빌드용 베이스 이미지 + 별칭
FROM openjdk:17-slim AS builder

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src

# gradlew 실행 권한 부여
RUN chmod +x ./gradlew
# gradlew를 통해 실행 가능한 jar파일 생성
RUN ./gradlew bootJar


# 최종 실행용 베이스 이미지 생성
FROM openjdk:17-slim

# 작업 디렉토리 설정
WORKDIR /app

# JAR 파일을 컨테이너에 복사 (jar파일이 하나만 생기도록 설정해줘야 함.)
ARG JAR_FILE=build/libs/*.jar
COPY --from=builder ${JAR_FILE} app.jar

# 포트 설정
EXPOSE 8081

# jar 파일 실행
ENTRYPOINT ["java", "-jar", "app.jar"]