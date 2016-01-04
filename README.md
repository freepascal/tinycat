# Tinycat
A Tomcat/Java build tool written in Java
```
This tool generates .class files from .java files. I use it for my Tomcat projects.
```
##Usage
```
Syntax: java -jar {filename.jar} -sourcedir [src] -classdir [classes]
```
1. -sourcedir: The top-level directory of source code (use project root if you want)
2. -classedir: the top-level directory will contain .class files generated
For instance:
```
java -jar javautil.jar -sourcedir /home/{user}/src -classdir /home/{user}/classes
```
##How to compile using Gradle
```
cd tinycat
gradle build
```
