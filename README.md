[![MIT License](https://img.shields.io/badge/license-MIT-orange.svg)](https://github.com/reinhapa/junit-compat/blob/master/LICENSE)
[![Supported Versions](https://img.shields.io/badge/Java-7%2C%208-blue.svg)](https://travis-ci.org/reinhapa/junit-compat)
[![Build Status](http://reinharts.dyndns.org:8888/job/junit-compat/badge/icon)](http://reinharts.dyndns.org:8888/job/junit-compat/)

# Provides partial backwards compatibility classes
This project contains some of the old JUnit classes that where removed in the newer versions
of JUnit. Using this project besides the actual JUnit library can help migrate to a newer
version more smoothly.

# Enhanced provided helper classes
Additional to the re-added removed JUnit 3 classes there are some additional helper classes
building test suites:

## CustomTestSuite
Creates a test suite based on a given text file containing all canonical test class names for
the test suite.

## RepeatFailedTests
Creates a test suite based on a given URIs where the JUnit runner log output is taken to get
the failed tests and build it upon.

## RecursiveTestSuite
Creates a test suite based on a set of test class files as starting point using their the local
file system location, searching recursively for more test classes that are added to the suite.