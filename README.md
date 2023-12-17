# Chatter

Chatter is a small chat plattform for teams. Users can create multiple teams and also be invited
to them. Each team has also multiple channels for communicating via chat. The application uses
a [`kotlin`](https://kotlinlang.org/) backend that mostly revolves around [`ktor`](https://ktor.io/),
[`sqldelight`](https://github.com/cashapp/sqldelight), [`arrow`](https://arrow-kt.io/) and [`dagger`](https://dagger.dev/)
combined with [`anvil`](https://github.com/square/anvil). The frontend is a client rendered [`SolidJS`](https://www.solidjs.com/) SPA.

## Running

The application can be run two ways: Either by using `docker compose` or the local dev environment (which also uses partly docker).

### Docker Compose

The requirements with this variant is just `docker` itself and `docker-compose` on an older version of docker
that doesn't have `docker-compose` built in. Before the application can be started, you have to create a
`.env` file at the root of the project. The requiered contents can be found in an example file `.env.example`. This
configures the env variables for the db. A `config.json` file should be also created at the root of the project. A
`config.example.json` file is also provided here with mostly prefilled values and two that are needed to be customized.

After this, the application can be started using `docker compose up` (or `docker-compose up` on an older version of docker).
It should be available on the `8080` port. The persisted data of the different persisted storages are saved in docker volumes.

### Local

For the local development, the frontend and backend are started separately. There is also a `compose.dev.yml` which starts
all the persisted storages, which can be started using `docker-compose -f compose.dev.yml up`.

#### Backend


The backend can be build and started with gradle and also requires the most recent java version `21` to be available.
When invoking gradle directly a apropriate jvm should be installed automatically. This can be also just simply
be done by using `Intellij IDEA` and starting the `src/main/kotlin/chatter/Main.kt` in the `backend` folder.
Also the most recent stable version of `Intellij IDEA` has to be installed because otherwise it has problems
with working with the gradle 8.5 version.  For configuring the backend a `config.json`
file has to be created in the backend folder. The is an `config.example.json` that describes the requires values.

#### Frontend

The frontend is deloped using [vite](https://vitejs.dev/) so a recentish `node.js` version is required.
The package manager `pnpm` is also required. Theoretically `npm` can be also used but it won't install the locked package versions.
For starting the frontend have to run:

- `pnpm i` (Installing dependencies)
- `pnpm dev` (Starting the dev server)

