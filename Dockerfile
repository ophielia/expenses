FROM openjdk:8-jre-alpine

ENV LANG en_GB.UTF-8

# JRE fails to load fonts if there are no standard fonts in the image; DejaVu is a good choice,
# see https://github.com/docker-library/openjdk/issues/73#issuecomment-207816707

RUN apk add --update ttf-dejavu && rm -rf /var/cache/apk/*

VOLUME /tmp
ARG APP_DIRECTORY=/Users/margaretmartin/projects/bankmigration
ARG DEPENDENCY=target/dependency
COPY ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY ${DEPENDENCY}/META-INF /app/META-INF
COPY ${DEPENDENCY}/BOOT-INF/classes /app
RUN mkdir -p /Users/margaretmartin/projects/generated
EXPOSE 8080

ENV DOCKERIZE_VERSION v0.6.0
RUN wget https://github.com/jwilder/dockerize/releases/download/$DOCKERIZE_VERSION/dockerize-alpine-linux-amd64-$DOCKERIZE_VERSION.tar.gz \
    && tar -C /usr/local/bin -xzvf dockerize-alpine-linux-amd64-$DOCKERIZE_VERSION.tar.gz \
    && rm dockerize-alpine-linux-amd64-$DOCKERIZE_VERSION.tar.gz

#CMD dockerize -wait tcp://config-server:8888 -timeout 60m java -cp app:/lib/* meg.swapout.BankmigrationApplication
CMD ["dockerize","-wait","tcp://config-server:8888", "-timeout" ,"60m" ,"java","-XX:+UnlockExperimentalVMOptions","-XX:+UseCGroupMemoryLimitForHeap","-XX:MaxRAMFraction=1","-XshowSettings:vm","-cp","app:app/lib/*","meg.swapout.BankmigrationApplication"]
