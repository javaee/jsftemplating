This jar file is used by the build.xml/aptbuild.xml files.  You do not need
to do any extra configuration if you are building with ant (1.6 or greater).

If you are using your own ant build files, you may need to copy apt-ant.jar
to your ant lib directory.  This is required in order for the <apt> ant target
to work.

If you are using netbeans 5.x to build this project, make sure you add
apt-ant.jar to ant's classpath.  To do this, go to Tools->Options, select
Miscellaneous on the left frame, expands Ant on the right frame, press the
Manage Classpath... button to add apt-ant.jar to the classpath. (NOTE: Is
this still needed??)
