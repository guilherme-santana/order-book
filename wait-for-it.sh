#!/bin/bash
# wait-for-it.sh

HOST=$1
PORT=$2
shift 2
CMD=$@

until nc -z $HOST $PORT; do
  echo "Aguardando o Banco... ($HOST:$PORT)"
  sleep 2
done

echo "Banco está pronto!"
exec $CMD
