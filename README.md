COWWEB2
=======
Cosay Web API version 2. This simple micro API server is powered by [Helidon](https://helidon.io/#/).

```
$ curl "http://localhost:8080/cowsay/say?say=Hello%20cowweb"
 ______________
< Hello cowweb >
 --------------
        \   ^__^
         \  (oo)\_______
            (__)\       )\/\
                ||--WWW |
                ||     ||
```

How to build and run
--------------------

### Docker
Build a docker image and run localy.

#### Install Docker
You need a environment docker is installed. See [the official documentation](https://docs.docker.com/install/).

#### Clone this repository.

```
git clone https://github.com/hhiroshell/cowweb2.git && cd cowweb2
```

#### Build and run.

```
docker build -t cowweb .
```

\[Optional\]: You can specify another docker file `Dockerfile_native`. By this, cowweb will be build as a [GraalVM Native Image](https://www.graalvm.org/docs/reference-manual/aot-compilation/) running inside the docker container.

```
docker build -t cowweb -f Dockerfile_native .
```

And then run the container publishing the container’s 8080 port to the host.
```
docker run -p 80:8080 cowweb
```

#### Call the API.
You can call the API via `localhost:80` .

```
curl "http://localhost/cowsay/say"
```

And you can specify a message using `say` query (special characters have to be URL encorded).

```
curl "http://localhost/cowsay/say?say=hello%20cowweb"
```
