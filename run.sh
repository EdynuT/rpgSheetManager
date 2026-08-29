#!/bin/bash
shopt -s nullglob
echo "Compiling..."
javac -cp "lib/postgresql-42.7.13.jar" -d bin Main.java src/config/*.java src/dao/*.java src/gui/*.java src/model/*.java src/service/*.java src/utils/*.java

if [ $? -eq 0 ]; then
    echo "Running..."
    java -cp "bin:lib/postgresql-42.7.13.jar" Main
else
    echo "Compilation error!"
fi