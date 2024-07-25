#!/bin/bash

# check args 1 and 2 exist
if [ -z "$1" ] || [ -z "$2" ] || [ -z "$3" ]; then
  echo "Usage: $0 <source folder> <destination_pod> <namespace>"
  echo "Example: $0 /mnt/data art-tomcat-1234567890-abcde my-namespace"
  exit 1
fi

source=$1
destination_pod=$2
namespace=$3

# check if the source folder exists
if [ ! -d "$source" ]; then
  echo "Error: Source folder $source does not exist"
  exit 1
fi

# check if the destination pod exists
if [ -z "$(kubectl get pods -n $namespace | grep $destination_pod)" ]; then
  echo "Error: Destination pod $destination_pod does not exist"
  exit 1
fi

echo "Migrating data from $source to $destination_pod in namespace $namespace"

# This script is used to migrate the data from the old linux mount to the new kubernetes pvc mount
# The script is run in the old linux mount
kubectl cp -n $namespace $source/. $destination_pod:/
