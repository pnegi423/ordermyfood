FROM openjdk:8-jdk-alpine
VOLUME /tmp
ADD target/ordermyfood-0.0.1-SNAPSHOT.jar app.jar
ENV JAVA_OPTS="-Xms256m -Xms256m"
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar" ]
EXPOSE 1433
FROM ubuntu
COPY ./wait-for.sh /
WORKDIR /
RUN chmod +x ./wait-for.sh
CMD sh -c './wait-for.sh www.google.com:80 -- echo "google site is up"
RUN apt-get -q update && apt-get -qy install netcat