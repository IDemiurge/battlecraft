@ECHO OFF
chcp 65001
set resourcePath="target/resource"
set xmlPath="target/XML"
if exist %resourcePath% (rmdir %resourcePath%)
mklink /D %resourcePath% "%CD%/../resources/src/main/resources"

if exist %xmlPath% (rmdir %xmlPath%)
mklink /D %xmlPath% "%CD%/../C-Engine/src/main/resources/XML"
