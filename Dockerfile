FROM openjdk:8-jdk-alpine
RUN apk add --no-cache ttf-dejavu
RUN addgroup -S expenses && adduser -S expenses -G expenses
USER expenses:expenses
ARG DEPENDENCY=target/dependency
COPY ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY ${DEPENDENCY}/META-INF /app/META-INF
COPY ${DEPENDENCY}/BOOT-INF/classes /app


RUN mkdir /tmp/images

ENTRYPOINT ["java", \
"-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005", \
"-cp","app:app/lib/*", \
"meg.swapout.BankmigrationApplication"]