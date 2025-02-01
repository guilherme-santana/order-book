#!/bin/bash
# wait-for-it.sh

HOST=$1
PORT=$2
shift 2
CMD=$@

until nc -z $HOST $PORT; do
  echo "Aguardando o MySQL... ($HOST:$PORT)"
  sleep 2
done

echo "MySQL está pronto!"
exec $CMD
