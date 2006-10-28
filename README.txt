
HOW TO BUILD jsftemplating.jar
==============================

You must use ant 1.6.x or higher.  There is a version of ant included in the
lib/ant directory of the jsftemplating project.

1) From a terminal (or command prompt) window, "cd" to the jsftemplating
   root directory.  For example:

  linux or solaris:

    cd /jsftemplating

  windows:

    cd \jsftemplating

2) Copy build.properties.example to build.properties

  linux or solaris:

    cp build.properties.example build.properties

  windows:

    copy build.properties.example build.properties

3) Edit build.properties and set either "glassfish-home", or set
   "servlet-api.jar" and "jsf-api.jar" to the correct paths.
   
   If you don't know how to do this, download and install GlassFish from:
   https://glassfish.dev.java.net and set glassfish-home to your
   installation directory.

** If you want to use netbeans to build this project, skip Step 4 and follow Step 5 instead.

4) From the "jsftemplating" directory, run ant:

    ant

If you don't have any on your machine, there is a copy provided as part of
jsftemplating, try:

    lib/ant/bin/ant clean build

  windows:

    lib\ant\bin\ant.bat clean build

That's it!  The jsftemplating.jar file can be found in your "dist"
directory. 

5) Using netbeans to build project

    jsftemplating has been configurated as a netbeans project.  After following
    Steps 1-3 to setup your environment, you can open the project by pointing
    to the directory where you check out the source.

    Note: The next paragraph may not be required anymore, I need to test this.

    You need to add ant-apt.jar to the ant classpath.  To do so, go to
    Tools->Options, select Miscellaneous on the left side, then Ant on the right
    hand side.  Press the "Manage Classpath..." button, then "Add JAR/ZIP..." 
    button. Specify ant-api.jar that can be found under this project's 
    lib/external directory.

    You can now build your project.  The jsftemplating.jar can be found in your
    "dist" dirctory".  

If you encounter build problems, resolve them and try again;
email dev@jsftemplating.dev.java.net for help.

Good luck!

Ken Paulsen
