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

# --- Build a custom JRE ---
FROM openjdk:11-jdk-slim AS jre-builder

WORKDIR /jlink
RUN jlink --output ${project.artifactId}-jre \
          --add-modules java.base,java.desktop,java.logging,java.management

# --- Build an application container ---
FROM debian:buster-slim

WORKDIR /workdir
COPY --from=jre-builder /jlink/${project.artifactId}-jre /opt/${project.artifactId}-jre
ENV PATH /opt/${project.artifactId}-jre/bin:$PATH

RUN mkdir /app
COPY libs /app/libs
COPY ${project.artifactId}.jar /app

CMD ["java", "-jar", "/app/${project.artifactId}.jar"]
