/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015, 2020 Patrick Reinhart
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package net.reini.junit;

import static java.nio.file.Files.isRegularFile;
import static java.nio.file.Files.newInputStream;
import static java.util.regex.Pattern.compile;

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

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

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
  private static final Pattern testName = compile("\\[junit\\] Running (.+)$");
  private static final Pattern testResult =
      compile("\\[junit\\] Tests run: [0-9]+, Failures: ([0-9]+), Errors: ([0-9]+),");
  private static final Pattern gradleName = compile("^\\[.+\\] (.+) > .*FAILED$");

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
          String line = null;
          while ((line = reader.readLine()) != null) {
            Matcher nameMatcher = testName.matcher(line);
            if (nameMatcher.find()) {
              line = reader.readLine();
              if (line != null) {
                Matcher resultMatcher = testResult.matcher(line);
                if (resultMatcher.find()) {
                  String failures = resultMatcher.group(1);
                  String errors = resultMatcher.group(2);
                  if (!"0".equals(errors) || !"0".equals(failures)) {
                    addNonJythonTest(processedClasses, logger, suite, nameMatcher.group(1));
                  }
                }
              }
            }
            Matcher gradleNameMatcher = gradleName.matcher(line);
            if (gradleNameMatcher.matches()) {
              addNonJythonTest(processedClasses, logger, suite, gradleNameMatcher.group(1));
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
            SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
            parser.parse(new InputSource(in),
                new TestReportFilter(processedClasses, logger, suite));
          }
        }
      } catch (ParserConfigurationException | SAXException | IOException e) {
        logger.log(Level.SEVERE, e,
            () -> "Error getting data from suite file ".concat(suiteFileName));
      }
    }
    return suite;
  }

  static void addNonJythonTest(Set<String> processedClasses, Logger logger, TestSuite suite,
      String className) {
    if (!className.contains("/")) { // filter out jython tests
      addTest(processedClasses, logger, suite, className);
    } else {
      logger.warning(() -> "Skipped Jython test ".concat(className));
    }
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

  static final class TestReportFilter extends DefaultHandler {
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
