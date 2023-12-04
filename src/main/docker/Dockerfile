# Build Stage
FROM quay.io/quarkus/quarkus-micro-image:2.0 AS build
WORKDIR /workspace
COPY . .
RUN ./mvnw clean package -Pnative

# Final Stage
FROM quay.io/quarkus/quarkus-micro-image:2.0
WORKDIR /work/
RUN chown 1001 /work && chmod "g+rwX" /work && chown 1001:root /work
COPY --from=build /workspace/target/*-runner /work/application
EXPOSE 8080
USER 1001
ENTRYPOINT ["./application", "-Dquarkus.http.host=0.0.0.0"]
