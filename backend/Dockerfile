FROM openjdk:11
RUN mkdir -p /usr/src/myapp
COPY ./target/donchess-0.0.1-SNAPSHOT.war /usr/src/myapp
WORKDIR /usr/src/myapp
ENV CHESS_WAR="/usr/src/myapp/donchess-0.0.1-SNAPSHOT.war"
CMD ["sh", "-c", "java -jar $CHESS_WAR --spring.profiles.active=$CHESS_PROFILE"]
