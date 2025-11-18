# Project: NAME

## Contributors
Sharon Zheng

## Dependencies
- LANGUAGE AND VERSION
    - Java, JDK 11
- EXTERNAL LIBRARIES
    - ASM (https://asm.ow2.io/) library for program analysis
- ETC

## Build Instructions
To build and run the project,

1. setup JDK to be 11
2. run ./gradlew build to build the project
3. edit Run Configuration by adding program argument "example.MyFirstLinter".
4. run ./gradlew run to run the project

This project uses the String constructor of ClassReader (see documentation), which only reads the input bytecode from a default directory. To use the ClassReader to read files in any arbitrary directory, You will need either the byte array constructor or InputStream constructor.
