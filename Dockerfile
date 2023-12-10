# base-image
FROM openjdk:11-jdk
# jar file 경로
ARG JAR_FILE=build/libs/*.jar
# 파일 컨테이너로 복사
COPY ${JAR_FILE} aplus.jar
# jar 파일 실행
ENTRYPOINT ["java", "-jar", "/aplus.jar"]