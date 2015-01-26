/**
 * File Name: CustomTestSuite.java
 * 
 * Copyright (c) 2015 BISON Schweiz AG, All Rights Reserved.
 */

package CH.obj;

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
 * Helper class to execute all defined tests <tt>-Dcustom.tests=&lt;path_to_test_definition_file&gt;</tt>.
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
    Path customTests = Paths.get(System.getProperty("custom.tests", ""));
    if (exists(customTests) && isRegularFile(customTests)) {
      try (BufferedReader reader = newBufferedReader(customTests, StandardCharsets.ISO_8859_1)) {
        String className = null;
        while ((className = reader.readLine()) != null) {
          if (!className.contains("/")) { // filter out jython tests
            try {
              Class<?> failedClass = Class.forName(className);
              if (TestCase.class.isAssignableFrom(failedClass)) {
                @SuppressWarnings("unchecked")
                Class<TestCase> testCase = (Class<TestCase>)failedClass;
                suite.addTestSuite(testCase);
              } else {
                suite.addTest(new JUnit4TestAdapter(failedClass));
              }
            } catch (Exception e) {
              logger.severe(String.format("Unable to load class %s (%s)", className, e.getClass().getName()));
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