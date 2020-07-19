#!/bin/bash

if [ $# -eq 0 ]
  then
    echo "You need to provide the bin directory of where the class files can be found as the first argument"
    echo "For example \"/Users/doekewartena/Library/Application Support/Code/User/workspaceStorage/ab9e8a929b43b45018d52c5741cea509/redhat.java/jdt_ws/java_preproc_macros_8c2f09d3/bin\""
    echo "Sorry for the inconvenience, without this I need to rely to much on the java extensions for vscode made by Microsoft and they have been to buggy the last 2 years..."
    exit 1
fi


bin_folder=$1
start_dir=$PWD


if [ ! -d "$bin_folder" ]
then
    echo "bin dir does not exist!"
    exit 1
fi

# shopt -s globstar
cd "$bin_folder"
jar cfm $start_dir/lib/Java_Preproc_Macros.jar \
    java_preproc_macros/Java_Preproc_Macros_MANIFEST.MF \
    java_preproc_macros/Java_Preproc_Macros*.class

cd $start_dir/src
jar uf $start_dir/lib/Java_Preproc_Macros.jar \
    java_preproc_macros/Java_Preproc_Macros.java