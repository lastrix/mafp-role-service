#!/bin/bash

docker-compose --compatibility down

sleep 5s

docker-compose --compatibility up -d
