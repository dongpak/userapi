#!/bin/sh
SYSTEM_PROPERTIES=""

if [ -f /run/secrets/churchclerk ]; then
  ln -f -s /run/secrets/churchclerk /home/config/application.properties
  if [ "$DEBUG" = "true" ]; then
    cat /run/secrets/churchclerk
  fi
else
  if [ "$DEBUG" = "true" ]; then
    echo "No secret specified"
  fi
fi
cd /home
java $SYSTEM_PROPERTIES -cp /home -jar /home/app.jar $APP_ARG
