#!/bin/bash

image=message
version=$(date "+%Y.%m.%d")

if [ $1 ]; then
  version=$1
fi

mvn clean package -Dmaven.test.skip=true


registry="registry.cn-hangzhou.aliyuncs.com/ygj/${image}:${version}"
docker build . -t ${registry}
docker push ${registry}

echo ${registry}
