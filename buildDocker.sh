#!/bin/bash

mvn install -DskipTests && docker build -t mafp/app-role-service .
