#!/bin/bash

function _cargo_usage() {
    echo "Cargo script helper"
    echo ""
    echo "./cargo.sh test   Run tests for Cargo"
    echo "./cargo.sh deploy $tagnanme  Deploy a tag on bintray"

}



function _cargo_test(){
    echo "Running tests"
    (cd `dirname $0`/..; ./gradlew test)
}


function _cargo_deploy(){
    echo "Push from current branch"
    (cd `dirname $0`/..; git fetch origin; git checkout tags/$1; CARGOVERSION=$1; ./gradlew bintrayUpload)
}

case "$1" in
    test)
        _cargo_test
    ;;
    deploy)
        _cargo_deploy $2
    ;;
    *)
        _cargo_usage
    ;;
esac
