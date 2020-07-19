# java_preproc_macros

I needed a fast way to get the current line number potentially thousands of times in a frame based application where hitting a framerate of 60fps is the target.

After searching for literally hours on the internet all I could find was getting the stackTrace or something simulair like using the new StackWalker. This is painfully slow, specially in a frame based application.

What I wanted was something like `__LINE__` that is used in the preprocessor of C. This library provides that functionallity using [javassist](https://www.javassist.org/), a library for Java bytecode manipulation.

# Usage

You have to attach the *Java_Preproc_Macros.jar* to the virtual machine. In vscode in *launch.json* for example it would look like this:

```json
"vmArgs": "-javaagent:${workspaceFolder}/lib/Java_Preproc_Macros.jar"
```

Then the class where you would like to use the preprocessor macros's has to have the `@Using_Preproc_Macros` annotation, like so:

```java
@sfjl_Using_Preproc_Macros
public class Preproc_Macros_01 {
    // ...
}
```

After that you can use it like this:

```java
System.out.println("__LINE__ "+__LINE__());
System.out.println("__FILE__ "+__FILE__());
System.out.println("__CLASS__ "+__CLASS__());
System.out.println("__FUNC__ "+__FUNC__());
System.out.println("__DATE__ "+__DATE__());
System.out.println("__TIME__ "+__TIME__());
System.out.println("__LINE__ "+__LINE__());
```

Output:
> __LINE__ 10  
__FILE__ Java_Preproc_Macro_Example_01.java  
__CLASS__ java_preproc_macros_examples.Java_Preproc_Macro_Example_01  
__FUNC__ main  
__DATE__ Jul 19 2020  
__TIME__ 01:10:23  
__LINE__ 16  


# Fallback

If you do not attach the java agent a warning will be issued in the console:

> [WARNING] agent java_preproc_macros.Java_Preproc_Macros was not attached to the JVM!

Same if you don't add the `@Using_Preproc_Macros` annotation:

> [WARNING] the class java_preproc_macros_examples.Java_Preproc_Macro_Example_01 does not have the @Using_Preproc_Macros annotation.  
It will fallback to figuring things out at runtime which is slow!

In both cases the library will still work but it will fallback to using the slow stackTrace.

# Notes

I have little experience with [javassist](https://www.javassist.org/). Also I was able to use the debugger before but that ship sunk a long time ago. This could be due to the java extensions maintained by Microsoft cause they break things all the time but I can't say for sure. Anyway I wrote this to get the job done and for me it works. If you have any improvements, feel free to send a pull request.
