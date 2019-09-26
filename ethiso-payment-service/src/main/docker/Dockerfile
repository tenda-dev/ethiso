FROM java:8-jre

RUN mkdir /opt/${project.artifactId}

COPY maven/${project.artifactId}-${project.version}.jar /opt/${project.artifactId}

WORKDIR /opt/${project.artifactId}

ENTRYPOINT ["/usr/bin/java", "-cp", "${project.artifactId}-${project.version}.jar", "com.std.ie.ethiso.ethereum.PaymentServiceMain"]