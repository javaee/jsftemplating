You do NOT need apt-ant.jar if you are using the ant bundleed in this
project (default).

If you are using your own version of ant (must be version 1.6 or greater),
then copy apt-ant.jar to your ant lib directory.  This is required in order
for the <apt> ant target to work.

If you are using netbeans 5.x to build this project, make sure you add apt-ant.jar to ant's classpath.  To do this, go to Tools->Options, select Miscellaneous on the left frame, expands Ant on the right frame, press the Manage Classpath... button to add apt-ant.jar to the classpath.
