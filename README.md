# Tinycat
A Tomcat/Java build tool written in Java
This tool generates .class files from .java files. I use it for my Tomcat projects.
##Usage
```
Syntax: java -jar {filename.jar} -sourcedir [src] -classdir [classes]
```
For instance:
```
java -jar javautil.jar -sourcedir /home/{user}/src -classdir /home/{user}/classes
```
##How to compile using Gradle
```
cd tinycat
gradle build
```
