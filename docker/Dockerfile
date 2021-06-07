# Build docker image using the secured image from dockerhub.gemalto.com
FROM adoptopenjdk/openjdk11

RUN mkdir -p /app/lib
RUN mkdir -p /app/bin
RUN mkdir -p /app/etc
RUN mkdir -p /app/etc/minimart


ENV APP_LIB="/app/lib"
ENV APP_BIN="/app/bin"
ENV APP_ETC="/app/etc"
RUN export PATH=$APP_BIN:$PATH
# Copy libraries...
#COPY ./context/jars $APP_LIB/atmpoc

# Copy application...
COPY ./jars/minimart.jar $APP_LIB

# Copy script for running application...
COPY ./bin/start_minimart.sh /

# Copy config
COPY ./minimart $APP_ETC/minimart

# Add permissions...
RUN chmod -R 777 /start_minimart.sh
RUN ls $APP_BIN
# Define entry point...
ENTRYPOINT ["/start_minimart.sh"]
CMD [""]

