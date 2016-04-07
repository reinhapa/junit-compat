/**
 * File Name: RepeatFailedTests.java
 * 
 * Copyright (c) 2014 Patrick Reinhart, All Rights Reserved.
 */

package net.reini.junit;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Helper class to execute all failed tests according the <tt>consoleText</tt> as defined in the
 * system property <tt>error.log.url</tt> (be defined with <tt>-Derror.log.url=&lt;url&gt;</tt>).
 *
 * @author Patrick Reinhart
 */
public class RepeatFailedTests {

  /**
   * @return the builded test suite based on the logfile containing the failed tests
   */
  public static Test suite() {
    Logger logger = Logger.getLogger(RepeatFailedTests.class.getName());
    TestSuite suite = new TestSuite("Failed JUnit tests");
    for (String urlValue : System.getProperty("error.log.url", "").split(",")) {
      try (BufferedReader reader =
          new BufferedReader(new InputStreamReader(new URL(urlValue).openStream()))) {
        Pattern testNamePattern = Pattern.compile("\\[junit\\] Running (.+)$");
        Pattern testResultPattern =
            Pattern.compile("\\[junit\\] Tests run: [0-9]+, Failures: ([0-9]+), Errors: ([0-9]+),");
        String line = null;
        while ((line = reader.readLine()) != null) {
          Matcher nameMatcher = testNamePattern.matcher(line);
          if (nameMatcher.find()) {
            String className = nameMatcher.group(1);
            line = reader.readLine();
            if (line != null) {
              Matcher resultMatcher = testResultPattern.matcher(line);
              if (resultMatcher.find()) {
                String failures = resultMatcher.group(1);
                String errors = resultMatcher.group(2);
                if (!"0".equals(errors) || !"0".equals(failures)) {
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
                      logger.severe(String.format("Unable to load class %s (%s)", className,
                          e.getClass().getName()));
                    }
                  } else {
                    logger.warning("Skipped Jython test ".concat(className));
                  }
                }
              }
            }
          }
        }
      } catch (Exception e) {
        logger.log(Level.SEVERE, "Error getting data from URL ".concat(urlValue), e);
      }
    }
    return suite;
  }
}
