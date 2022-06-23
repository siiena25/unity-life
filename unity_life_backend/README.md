# README #
Documentation:
http://localhost:5434/swagger-ui.html

udo docker run --rm --name coursework-pgdocker -e POSTGRES_PASSWORD=password -e POSTGRES_USER=user -e POSTGRES_DB=coursework_db -d -p 5433:5432 -v $HOME/docker/volumes/postgres:/var/lib/postgresql/data postgres
