#STEP 1: Build the docker image
FROM maven:3.9.9-eclipse-temurin-21 AS build

#set the working directory
WORKDIR /app

#Copy the Pom and src file

COPY pom.xml .

COPY src ./src

#RUN maven to build package
RUN mvn clean package -DskipTests


#STEP 2: Create the final image

FROM eclipse-temurin:21

#Set the working and running directory
WORKDIR /opt/app

#Copy the Jar file into the working directory
COPY --from=build /app/target/picturegaller-0.0.1-SNAPSHOT.jar .

#Expose the Port
EXPOSE 8080

CMD ["java", "-jar", "picturegaller-0.0.1-SNAPSHOT.jar"]

