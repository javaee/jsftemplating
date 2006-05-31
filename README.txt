
HOW TO BUILD jsftemplating.jar
==============================

It is recommended that you use the version of ant included in the lib/ant
directory.  This version includes the "apt" ant target needed by this
project.  You may use your own version, see lib/external/README.txt for
more information, you will need ant 1.6 or a more recent build.

1) From a terminal (or command prompt) window, "cd" to the jsftemplating
   root directory.  For example:

    cd /jsftemplating

  windows:

    cd \jsftemplating

2) Copy build.properties.example to build.properties

    cp build.properties.example build.properties

  windows:

    copy build.properties.example build.properties

3) Edit build.properties and set either "glassfish-home", or set
   "servlet-api.jar" and "jsf-api.jar" to the correct paths.
   
   If you don't know how to do this, download and install GlassFish from:
   https://glassfish.dev.java.net and set glassfish-home to your
   installation directory.

4) Run ant:

    lib/ant/bin/ant

  windows:

    lib\ant\bin\ant.bat

  NOTE: You may want to put the ant "bin" directory in your path.  You should
        also make sure ANT_HOME is not set, or is set to the ant that is
	included with this project.

  NOTE: On some machines with ant preinstalled, you may need to use the
	--noconfig option to pick up the ant jar files under lib/ant when
	building. e.g.:

    ant --noconfig clean

That's it!  The jsftemplating.jar file can be found in your "dist"
directory.  If you encounter build problems, resolve them and try again;
email dev@jsftemplating.dev.java.net for help.

Good luck!

Ken Paulsen
