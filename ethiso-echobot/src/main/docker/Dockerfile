FROM java:8-jre

RUN mkdir /opt/${project.artifactId}

COPY maven/*.jar /opt/${project.artifactId}/

WORKDIR /opt/${project.artifactId}

ENTRYPOINT ["/usr/bin/java", "-cp", "${project.artifactId}-${project.version}.jar", "com.rbs.ie.emerald.echobot.EchoBot"]
                                                                           
CMD ["rabbitmq", "payment-service-b", "GB29RBOS83040210126939"]


