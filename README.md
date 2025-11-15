# caveman framework

following https://caveman.mccue.dev

## local setup

### [prerequisites](https://caveman.mccue.dev/tutorial/clojure/0_prerequisites)

### add .env file to project root

```
ENVIRONMENT=development
POSTGRES_USERNAME=postgres
POSTGRES_PASSWORD=postgres
PORT=9999
```

### start postgres docker container

```sh
docker compose up -d
```