#!/bin/sh
exec java -Xmx2048m -jar gradle/wrapper/gradle-wrapper.jar "$@"
