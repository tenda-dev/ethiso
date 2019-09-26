FROM java:8-jre

EXPOSE 4567

RUN mkdir /opt/${project.artifactId}

COPY maven/*.jar /opt/${project.artifactId}/

WORKDIR /opt/${project.artifactId}

ENTRYPOINT ["/usr/bin/java", "-cp", "${project.artifactId}-${project.version}.jar", "com.rbs.ie.emerald.iso.Iso20022Broker"]

CMD ["rabbitmq", "iso20022", "payment-service"]
