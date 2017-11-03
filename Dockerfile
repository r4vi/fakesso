FROM java:8-alpine
MAINTAINER Ravi Kotecha <kotecha.ravi@gmail.com>

ADD target/fakesso-0.0.1-SNAPSHOT-standalone.jar /fakesso/app.jar

EXPOSE 8080

CMD ["java", "-jar", "/fakesso/app.jar"]
