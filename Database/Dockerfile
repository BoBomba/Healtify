FROM postgres:latest

ENV POSTGRES_DB healtify
ENV POSTGRES_USER admin
ENV POSTGRES_PASSWORD admin

COPY . /app/database
COPY init.sql /docker-entrypoint-initdb.d/