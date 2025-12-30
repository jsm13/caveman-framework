# syntax=docker/dockerfile:1

# ------------------------
# Setup Builder container

FROM clojure AS builder

# Set Clojure CLI version (defaults to latest release)
# ENV CLOJURE_VERSION=1.11.1.1413

# Create directory for project code (working directory)
RUN mkdir -p /build

# Set Docker working directory
WORKDIR /build

# Cache and install Clojure dependencies
# Add before copying code to cache the layer even if code changes
COPY deps.edn /build/
RUN clojure -P -X:build

# Copy project to working directory
# .dockerignore file excludes all but essential files
COPY ./ /build

RUN clojure -T:build uber

# End of Docker builder image
# ------------------------------------------
# ------------------------
# Setup Run-time Container

# Official OpenJDK Image
FROM eclipse-temurin AS final

# Add operating system packages
# - dumb-init to ensure SIGTERM sent to java process running Clojure service
# - Curl and jq binaries for manual running of system integration scripts
ENV DEBIAN_FRONTEND=noninteractive
RUN apt update && \
    apt install -y dumb-init curl jq && \
    apt autoremove && \
    rm -rf /var/lib/apt/lists/*

# Create Non-root group and user to run service securely
RUN addgroup --system clojure && adduser --system clojure --ingroup clojure

# Create directory to contain service archive, owned by non-root user
RUN mkdir -p /service && chown -R clojure /service

# Copy service archive file from Builder image
WORKDIR /service
COPY --from=builder /build/target/sandbox-0.0.0-standalone.jar /service/

# [TODO] maybe change src/system.clj to not error out on missing .env
RUN touch /service/.env

# Expose port of HTTP Server
EXPOSE 8080

# JDK_JAVA_OPTIONS environment variable for setting JVM options
# Use JVM options that optomise running in a container
# For very low latency, use the Z Garbage collector "-XX:+UseZGC"
ENV JDK_JAVA_OPTIONS="-XshowSettings:system -XX:+UseContainerSupport -XX:MaxRAMPercentage=90"


# ------------------------
# Run service
ENTRYPOINT ["/usr/bin/dumb-init", "--"]
CMD ["java", "-jar", "/service/sandbox-0.0.0-standalone.jar"]


# Docker Entrypoint documentation
# https://docs.docker.com/engine/reference/builder/#entrypoint

# $kill PID For Graceful Shutdown(SIGTERM) - can be caught for graceful shutdown
# $kill -9 PID For Forceful Shutdown(SIGKILL) - process ends immeciately
# SIGSTOP cannot be intercepted, process ends immediately
