FROM maven:3.8.4-openjdk-17 as builder

#Set an environment variable to store where the app is installed to inside
#of the Docker image.
ENV INSTALL_PATH /notifications-service
RUN mkdir -p $INSTALL_PATH
WORKDIR $INSTALL_PATH

COPY ./pom.xml ./pom.xml
#RUN mvn dependency:go-offline -B
COPY ./src ./src
RUN mvn package

FROM builder as test

#### develop ####
FROM maven:3.8.4-openjdk-17 as latest
WORKDIR /app
COPY --from=builder /notifications-service/target/xcale-whatsapp-0.0.1-SNAPSHOT.jar .
EXPOSE 8080
ENTRYPOINT ["java","-jar","xcale-whatsapp-0.0.1-SNAPSHOT.jar"]
