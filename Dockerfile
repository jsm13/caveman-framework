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

# Copy service archive file from Builder image
WORKDIR /service
COPY --from=builder /build/target/sandbox-0.0.0-standalone.jar /service/

# [TODO] maybe change src/system.clj to not error out on missing .env
RUN touch /service/.env

# Expose port of HTTP Server
EXPOSE 8080

# ------------------------
# Run service
CMD ["java", "-jar", "/service/sandbox-0.0.0-standalone.jar"]


# Docker Entrypoint documentation
# https://docs.docker.com/engine/reference/builder/#entrypoint

# $kill PID For Graceful Shutdown(SIGTERM) - can be caught for graceful shutdown
# $kill -9 PID For Forceful Shutdown(SIGKILL) - process ends immeciately
# SIGSTOP cannot be intercepted, process ends immediately
