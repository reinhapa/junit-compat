/**
 * File Name: RepeatFailedTests.java
 * 
 * Copyright (c) 2014, 2019 Patrick Reinhart, All Rights Reserved.
 */

package net.reini.junit;

import static java.nio.file.Files.isRegularFile;
import static java.nio.file.Files.newInputStream;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLFilterImpl;
import org.xml.sax.helpers.XMLReaderFactory;

import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Helper class to execute all failed http://ant.apache.org[*ANT*] tests according the `consoleText`
 * as defined in the system property `error.log.url`. You can also specify multiple URLs using the
 * comma (`,`).
 * 
 * `-Derror.log.url=http://jenkins.acme.com/job/myjob/lastBuild/consoleText`
 * `-Derror.suite.files=/somedir/TESTS-TestSuites.xml`
 *
 * @author Patrick Reinhart
 */
public class RepeatFailedTests {

  /**
   * @return the builded test suite based on the ANT log output containing the failed tests
   */
  public static Test suite() {
    Logger logger = Logger.getLogger(RepeatFailedTests.class.getName());
    TestSuite suite = new TestSuite("Failed JUnit tests");
    Set<String> processedClasses = new HashSet<>();
    // process console URL's
    for (String urlValue : System.getProperty("error.log.url", "").split(",")) {
      if (!urlValue.isEmpty()) {
        try (BufferedReader reader =
            new BufferedReader(new InputStreamReader(new URL(urlValue).openStream()))) {
          Pattern testNamePattern = Pattern.compile("\\[junit\\] Running (.+)$");
          Pattern testResultPattern = Pattern
              .compile("\\[junit\\] Tests run: [0-9]+, Failures: ([0-9]+), Errors: ([0-9]+),");
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
                      addTest(processedClasses, logger, suite, className);
                    } else {
                      logger.warning(() -> "Skipped Jython test ".concat(className));
                    }
                  }
                }
              }
            }
          }
        } catch (Exception e) {
          logger.log(Level.SEVERE, e, () -> "Error getting data from URL ".concat(urlValue));
        }
      }
    }
    // process test suite files
    for (String suiteFileName : System.getProperty("error.suite.files", "").split(",")) {
      try {
        Path suiteFile = Paths.get(suiteFileName);
        if (isRegularFile(suiteFile)) {
          try (InputStream in = newInputStream(suiteFile)) {
            XMLReader reader = XMLReaderFactory.createXMLReader();
            reader.setContentHandler(new TestReportFilter(processedClasses, logger, suite));
            reader.parse(new InputSource(in));
          }
        }
      } catch (SAXException | IOException e) {
        logger.log(Level.SEVERE, e,
            () -> "Error getting data from suite file ".concat(suiteFileName));
      }
    }
    return suite;
  }

  static void addTest(Set<String> processedClasses, Logger logger, TestSuite suite,
      String className) {
    if (processedClasses.add(className)) {
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
    }
  }

  static final class TestReportFilter extends XMLFilterImpl {
    private final Set<String> processedClasses;
    private final Logger logger;
    private final TestSuite suite;

    TestReportFilter(Set<String> processedClasses, Logger logger, TestSuite suite) {
      this.processedClasses = processedClasses;
      this.logger = logger;
      this.suite = suite;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts)
        throws SAXException {
      if ("testcase".equals(qName)) {
        addTest(processedClasses, logger, suite, atts.getValue("classname"));
      }
    }
  }
}
