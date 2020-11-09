FROM openjdk:8
COPY ./build/libs/ /tmp
WORKDIR /tmp
ENTRYPOINT ["java","Main"]
