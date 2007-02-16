apt-ant.jar
===========

The apt-ant.jar file is used by the build.xml/aptbuild.xml files.  You do not
need to do any extra configuration if you are building with ant (1.6 or
greater).

If you are using ant build files for your project, you may need to copy
apt-ant.jar to your ant lib directory.  This is required in order for the
<apt> ant target to work.

If you are using netbeans 5.x to build this project, make sure you add
apt-ant.jar to ant's classpath.  To do this, go to Tools->Options, select
Miscellaneous on the left frame, expands Ant on the right frame, press the
Manage Classpath... button to add apt-ant.jar to the classpath. (NOTE: Is
this still needed??)


jsftemplating-dynafaces-0.1.jar
===============================

The jsftemplating-dynafaces-0.1.jar file is required by JSFTemplating and
provides Ajax support.  The classes in this file are from the Dynamic Faces
package of the jsf-extensions project.  You can find out more information
about Dynamic Faces at its web site:

    https://jsf-extensions.dev.java.net


Woodstock
=========

Project Woodstock provides a rich JSF Component library.  The .jar files in
the "woodstock" directory are the Woodstock jar files plus their
dependencies.  You can learn more about Woodstock at the Woodstock web site:

    https://woodstock.dev.java.net

