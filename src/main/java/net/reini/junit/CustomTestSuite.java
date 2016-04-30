/**
 * File Name: CustomTestSuite.java
 * 
 * Copyright (c) 2015 Patrick Reinhart, All Rights Reserved.
 */

package net.reini.junit;

import static java.nio.file.Files.exists;
import static java.nio.file.Files.isRegularFile;
import static java.nio.file.Files.newBufferedReader;

import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Helper class to execute all defined tests.
 * 
 * The default behavior is to look for a `customtests` file in the current user directory.
 * 
 * In order specify a other custom file containing the test classes you could specify that using the 
 * `custom.tests` system property:
 * 
 * `-Dcustom.tests=*dir/somefile*`
 * 
 * The content of the test definitions file contains one test class on each line without the
 * `.class` suffix:
 * 
 * [source,xml,subs="verbatim,attributes"]
 * ----
 * # some comment
 * net.reini.demo.SomeTest
 * net.reini.demo.SomeOtherTest
 * ----
 * 
 * @author Patrick Reinhart
 */
public class CustomTestSuite {

  /**
   * @return the builded test suite based on a custom list for JUnit classes
   */
  public static Test suite() {
    Logger logger = Logger.getLogger(CustomTestSuite.class.getName());
    TestSuite suite = new TestSuite("Custom JUnit tests");
    String customTestFile = System.getProperty("custom.tests", "");
    Path customTests;
    if (customTestFile.isEmpty()) {
      customTests = Paths.get(System.getProperty("user.dir"), "customtests");
    } else {
      customTests = Paths.get(customTestFile);
    }
    if (exists(customTests) && isRegularFile(customTests)) {
      try (BufferedReader reader = newBufferedReader(customTests, StandardCharsets.ISO_8859_1)) {
        String className = null;
        while ((className = reader.readLine()) != null) {
          if (!className.contains("/")) { // filter out jython tests
            try {
              Class<?> failedClass = Class.forName(className);
              if (TestCase.class.isAssignableFrom(failedClass)) {
                @SuppressWarnings("unchecked")
                Class<TestCase> testCase = (Class<TestCase>) failedClass;
                suite.addTestSuite(testCase);
              } else {
                suite.addTest(new JUnit4TestAdapter(failedClass));
              }
            } catch (Exception e) {
              logger.severe(
                  String.format("Unable to load class %s (%s)", className, e.getClass().getName()));
            }
          } else {
            logger.warning("Skipped Jython test ".concat(className));
          }
        }
      } catch (Exception e) {
        logger.log(Level.SEVERE, "Error getting tests from " + customTests, e);
      }
    }
    return suite;
  }
}
