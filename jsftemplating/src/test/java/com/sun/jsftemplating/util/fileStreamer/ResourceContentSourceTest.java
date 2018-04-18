/**
 * 
 */
package com.sun.jsftemplating.util.fileStreamer;

import org.junit.Assert;
import org.junit.Test;

/**
 * TestCase for <code>ResourceContentSource</code>.
 */
public class ResourceContentSourceTest {

    @Test(expected = IllegalArgumentException.class)
    public void normalizeDoesNotAccessContextRootParent() {
        final String testPath = "../bad/path";
        ResourceContentSource.normalize(testPath);
    }

    @Test(expected = IllegalArgumentException.class)
    public void normalizeDoesNotAccessContextRootParentWithLeadingSlash() {
        final String testPath = "/../bad/path";
        ResourceContentSource.normalize(testPath);
    }

    @Test(expected = IllegalArgumentException.class)
    public void doesNotGoBackTooFar() {
        final String testPath = "/path/../../../../too/many/backward";
        ResourceContentSource.normalize(testPath);
    }

    @Test
    public void removesExtraSlashesAndBackwardPaths() {
        final String testPath = "//OK/path//with/extra/slashes/and/..//in/the/middle/";
        final String result = ResourceContentSource.normalize(testPath);
        Assert.assertEquals("Wrong result", "OK/path/with/extra/slashes/in/the/middle", result);
    }
}
