#!/usr/bin/env bash

export JAVA_HOME=$(/System/Library/Frameworks/JavaVM.framework/Versions/A/Commands/java_home -v "1.8")

./gradlew shadowJar
protoc --plugin=./protoc-gen-spring --spring_out=. example.proto
