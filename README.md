# fakesso

A really insecure oauth2 provider implementation.

Provides the following endpoints:

### /o/authorize/

Redirect your user to
`/o/authorize/?response_type=code&client_id=whatever&redirect_uri=http%3A%2F%2Flocalhost%3A9001%2Flogin%2Fcallback%2F&state=1234`
and `fakesso` will redirect them to the `redirect_uri` you provided with the
same `state` you provided and an additional query-param called `code`.

### /o/token/

Http POST to `/o/token/` with the code you were given (or any code, it doesn't
care) and it'll return you an access token in the form of json:

If you want it to return any custom scopes then just add `scope=whatever` to your POST body.

```json
{
    "access_token": "RFHEYFGWTPGNRCAAMYAD",
    "expires_in": 57861,
    "scope": "read write",
    "token_type": "Bearer"
}

```

### /o/introspect/
If you're using an app that is validation oauth2 access tokens using RFC 7662,
then point that app to use the provided introspect endpoint.
It will respond saying that any `access_token` is active and expires in the
future.

If you want it to return any custom scopes then just add `scope=whatever` to your POST body.

```json
{
    "active": true,
    "exp": 96348,
    "scope": "read write"
}

```


## User Getting Started
1. Download a release: [releases]
1. make sure you have java 8
1. decide what port you want it running on and export the PORT environment variable: `export PORT=12345`
1. Run `java -jar fakesso-0.0.2-SNAPSHOT-standalone.jar`


## Developer Getting Started

1. Start the application: `lein run`
2. Go to [localhost:8080](http://localhost:8080/) to see: `Hello World!`
3. Read your app's source code at src/fakesso/service.clj. Explore the docs of functions
   that define routes and responses.
4. Run your app's tests with `lein test`. Read the tests at test/fakesso/service_test.clj.
5. Learn more! See the [Links section below](#links).


## Configuration

To configure logging see config/logback.xml. By default, the app logs to stdout.
To learn more about configuring Logback, read its [documentation](http://logback.qos.ch/documentation.html).


## Developing your service

1. Start a new REPL: `lein repl`
2. Start your service in dev-mode: `(def dev-serv (run-dev))`
3. Connect your editor to the running REPL session.
   Re-evaluated code will be seen immediately in the service.

### [Docker](https://www.docker.com/) container support

1. Build an uberjar of your service: `lein uberjar`
2. Build a Docker image: `sudo docker build -t fakesso .`
3. Run your Docker image: `docker run -p 8080:8080 fakesso`

### [OSv](http://osv.io/) unikernel support with [Capstan](http://osv.io/capstan/)

1. Build and run your image: `capstan run -f "8080:8080"`

Once the image it built, it's cached.  To delete the image and build a new one:

1. `capstan rmi fakesso; capstan build`


## Links
* [Other examples](https://github.com/pedestal/samples)
* [releases]: https://github.com/r4vi/fakesso/releases/
