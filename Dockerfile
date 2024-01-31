FROM lacework/datacollector:latest-sidecar as agent-build-image
FROM amazoncorretto:8-alpine-jre
EXPOSE 5000
RUN apk --no-cache add curl ca-certificates
RUN mkdir -p /app/
RUN rm -rf /var/cache/apk/*
RUN mkdir -p /shared
WORKDIR /shared/
COPY --from=agent-build-image /var/lib/lacework-backup /var/lib/lacework-backup
RUN mkdir -p /app/
ADD build/libs/*.jar /app/app.jar
COPY startup.sh /app/
ENV LaceworkVerbose=true
CMD ["sh","/app/startup.sh"]
#testing
