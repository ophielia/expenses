FROM openjdk:8-jdk-alpine
RUN addgroup -S expenses && adduser -S expenses -G expenses
USER expenses:expenses
ARG DEPENDENCY=target/dependency
COPY ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY ${DEPENDENCY}/META-INF /app/META-INF
COPY ${DEPENDENCY}/BOOT-INF/classes /app
ENTRYPOINT ["java","-cp","app:app/lib/*","meg.swapout.BankmigrationApplication"]