/**
 * File Name: RecursiveTestSuiteBuilder.java
 * 
 * Copyright (c) 2014 Patrick Reinhart, All Rights Reserved.
 */

package net.reini.junit;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import junit.framework.JUnit4TestAdapter;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Helper class that builds test suite content based on a class file location
 * 
 * @author Patrick Reinhart
 */
public abstract class RecursiveTestSuiteBuilder {

  private static TestClassFilter _filter;

  /**
   * @return the default file name filter
   */
  public static FilenameFilter getFilenameFilter() {
    if (_filter == null) {
      _filter = new TestClassFilter();
    }
    return _filter;
  }

  /**
   * Builds all recursive test suites starting in the directories of the given
   * <code>classInPackages</code>
   * 
   * @param classInPackages class array used to get those packages
   * @param rootSuite the root test suite
   * @throws Exception if the the suite could not be built
   */
  public static void build(Class<?>[] classInPackages, TestSuite rootSuite) throws Exception {
    for (Class<?> classInPackage : classInPackages) {
      build(classInPackage, rootSuite);
    }
  }

  /**
   * Builds all recursive test suites starting in the directory of the given
   * <code>classInPackage</code>
   * 
   * @param classInPackage specifies a class file to get the package for
   * @param rootSuite the root test suite
   * @throws Exception if the the suite could not be built
   */
  public static void build(Class<?> classInPackage, TestSuite rootSuite) throws Exception {
    String testSuiteClassName = classInPackage.getName();
    File suiteFile = new File(classInPackage.getClassLoader()
        .getResource(testSuiteClassName.replace('.', '/').concat(".class")).getFile());
    String basePackage = classInPackage.getPackage().getName();
    File baseDir = suiteFile.getParentFile();
    TestSuite suite = new TestSuite(basePackage);
    rootSuite.addTest(suite);
    build(baseDir.getAbsolutePath().length(), basePackage, baseDir, getFilenameFilter(), suite);
  }

  /**
   * Builds all recursive test suites for the given <code>rootSuite</code>
   * 
   * @param prefixLength the length of the prefix
   * @param basePackage the base package name
   * @param currentDir the current directory
   * @param filter a file name filter
   * @param rootSuite the root test suite
   * @throws Exception if the the suite could not be built
   */
  public static void build(int prefixLength, String basePackage, File currentDir,
      FilenameFilter filter, TestSuite rootSuite) throws Exception {
    List<File> potentialDirectories = Arrays.asList(currentDir.listFiles(filter));
    if (!potentialDirectories.isEmpty()) {
      StringBuilder currentPackageName = new StringBuilder(200);
      currentPackageName.append(basePackage);
      String replaced =
          currentDir.getAbsolutePath().substring(prefixLength).replaceAll("[\\\\|/]", ".");
      currentPackageName.append(replaced);
      List<File> classFiles = new ArrayList<>(potentialDirectories.size());
      Collections.sort(potentialDirectories, new FileComparator());
      for (File potentialDirectory : potentialDirectories) {
        if (potentialDirectory.isDirectory()) {
          TestSuite subTestSuite = new TestSuite(potentialDirectory.getName());
          build(prefixLength, basePackage, potentialDirectory, filter, subTestSuite);
          // only if suite contains tests
          if (subTestSuite.countTestCases() > 0) {
            rootSuite.addTest(subTestSuite);
          }
        } else {
          classFiles.add(potentialDirectory);
        }
      }
      for (File file : classFiles) {
        final String fileName = file.getName().replaceFirst(".class$", "");
        final String className = new StringBuilder(200).append(currentPackageName).append('.')
            .append(fileName).toString();
        try {
          Class<?> clazz = Class.forName(className);
          if (!Modifier.isAbstract(clazz.getModifiers())) {
            if (TestCase.class.isAssignableFrom(clazz)) {
              @SuppressWarnings("unchecked")
              Class<? extends TestCase> testClass = (Class<? extends TestCase>) clazz;
              rootSuite.addTestSuite(testClass);
            } else {
              rootSuite.addTest(new JUnit4TestAdapter(clazz));
            }
          }
        } catch (Throwable t) {
          Logger.getLogger(RecursiveTestSuiteBuilder.class.getName()).log(Level.SEVERE,
              "Unable to load class ".concat(className), t);
        }
      }
    }
  }

  static class TestClassFilter implements FilenameFilter {
    @Override
    public boolean accept(File dir, String name) {
      if (name.endsWith("Test.class")) {
        return true;
      }
      return new File(dir, name).isDirectory();
    }
  }

  static class FileComparator implements Comparator<File> {
    @Override
    public int compare(File file1, File file2) {
      return file1.getAbsolutePath().compareTo(file2.getAbsolutePath());
    }
  }
}
