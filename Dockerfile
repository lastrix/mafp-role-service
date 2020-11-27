FROM openjdk:11-jdk-slim
COPY role-service/target/*.jar app.jar
ENTRYPOINT ["java", "-Dspring.profiles.active=dev", "-Xmx2G", "-XX:+UseG1GC", "-Dcom.sun.management.jmxremote", "-Dcom.sun.management.jmxremote.port=9080", "-Dcom.sun.management.jmxremote.local.only=false", "-Dcom.sun.management.jmxremote.authenticate=false", "-Dcom.sun.management.jmxremote.ssl=false", "-jar","app.jar"]
