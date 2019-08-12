#
# Copyright (c) 2019 Hiroshi Hayakawa <hhiroshell@gmail.com>
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

FROM openjdk:11-jdk-slim AS builder

# --- Build a custom JRE ---
WORKDIR /jlink
RUN jlink --output cowweb-jre \
          --add-modules java.base,java.desktop,java.logging,java.management \
          --compress=2

# --- Maven build ---
# Create a first layer to cache the "Maven World" in the local repository.
# Incremental docker builds will always resume after that, unless you update
# the pom
WORKDIR /mvn
COPY ./pom.xml ./pom.xml
COPY ./mvnw ./mvnw
COPY ./.mvn ./.mvn
RUN ./mvnw package -DskipTests

# Do the Maven build!
# Incremental docker builds will resume here when you change sources
COPY ./src ./src
RUN ./mvnw package

# --- Build an application container ---
FROM debian:buster-slim

RUN groupadd -g 61000 cowweb && \
    useradd -g 61000 -l -M -s /bin/false -u 61000 cowweb

COPY --from=builder --chown=cowweb:cowweb /jlink/cowweb-jre /opt/cowweb-jre
ENV PATH /opt/cowweb-jre/bin:$PATH

RUN mkdir /app
COPY --from=builder --chown=cowweb:cowweb /mvn/target/libs /app/libs
COPY --from=builder --chown=cowweb:cowweb /mvn/target/cowweb.jar /app

USER cowweb
ENTRYPOINT ["java"]
CMD ["-jar", "/app/cowweb.jar"]
EXPOSE 8080