#!/bin/bash

if sudo docker ps | grep -q 'CNO-UHDoCDN'; then
    sudo docker stop CNO-UHDoCDN && \
    sudo docker rm -f CNO-UHDoCDN
fi