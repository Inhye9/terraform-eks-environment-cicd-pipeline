FROM docker.inner-test.com/library/openjdk:8-jdk-alpine-custom-v5

ARG PRJ_NAME=${PRJ_NAME:-app}
ARG FILE_EXT=jar
ARG JAVA_OPTS_MEM=${JAVA_OPTS_MEM}
ARG TZ="Asia/Seoul"

ENV APP_NAME app.${FILE_EXT:-jar}

ENV JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -Djava.awt.headless=true -Dfile.encoding=UTF-8 \ 
			  -Dsun.net.inetaddr.ttl=0 -Duser.timezone=${TZ} -Dlog4j2.formatMsgNoLookups=true \
              -server ${JAVA_OPTS_MEM} -Dhttps.protocols=TLSv1,TLSv1.1,TLSv1.2 -XX:+DisableExplicitGC \
              -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/tmp/heapdump.bin 

USER appuser
EXPOSE 80

WORKDIR /

COPY target/app.${FILE_EXT:-jar} /$APP_NAME

ENTRYPOINT ["/bin/bash", "-c", "java ${JAVA_OPTS} -jar /$APP_NAME"]
