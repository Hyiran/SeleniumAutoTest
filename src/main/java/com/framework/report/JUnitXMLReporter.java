package com.framework.report;

/**
 * Created by caijianmin on 2016/1/6.
 */

import org.apache.velocity.VelocityContext;
import org.testng.IClass;
import org.testng.ISuite;
import org.testng.ISuiteResult;
import org.testng.ITestResult;
import org.testng.xml.XmlSuite;

import java.io.File;
import java.util.*;

public class JUnitXMLReporter extends AbstractReporter {
    private static final String RESULTS_KEY = "results";
    private static final String TEMPLATES_PATH = "com/test/util/qa/report/templates/xml/";
    private static final String RESULTS_FILE = "results.xml";
    private static final String REPORT_DIRECTORY = "xml";

    public JUnitXMLReporter() {
        super("com/test/util/qa/report/templates/xml/");
    }

    public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites, String outputDirectoryName) {
        this.removeEmptyDirectories(new File(outputDirectoryName));
        File outputDirectory = new File(outputDirectoryName, "xml");
        outputDirectory.mkdir();
        Collection flattenedResults = this.flattenResults(suites);
        Iterator i$ = flattenedResults.iterator();

        while(i$.hasNext()) {
            JUnitXMLReporter.TestClassResults results = (JUnitXMLReporter.TestClassResults)i$.next();
            VelocityContext context = this.createContext();
            context.put("results", results);

            try {
                this.generateFile(new File(outputDirectory, results.getTestClass().getName() + '_' + "results.xml"), "results.xml.vm", context);
            } catch (Exception var10) {
                throw new ReportNGException("Failed generating JUnit XML report.", var10);
            }
        }

    }

    private Collection<TestClassResults> flattenResults(List<ISuite> suites) {
        HashMap flattenedResults = new HashMap();
        Iterator i$ = suites.iterator();

        while(i$.hasNext()) {
            ISuite suite = (ISuite)i$.next();
            Iterator i$1 = suite.getResults().values().iterator();

            while(i$1.hasNext()) {
                ISuiteResult suiteResult = (ISuiteResult)i$1.next();
                this.organiseByClass(suiteResult.getTestContext().getFailedConfigurations().getAllResults(), flattenedResults);
                this.organiseByClass(suiteResult.getTestContext().getSkippedConfigurations().getAllResults(), flattenedResults);
                this.organiseByClass(suiteResult.getTestContext().getFailedTests().getAllResults(), flattenedResults);
                this.organiseByClass(suiteResult.getTestContext().getSkippedTests().getAllResults(), flattenedResults);
                this.organiseByClass(suiteResult.getTestContext().getPassedTests().getAllResults(), flattenedResults);
            }
        }

        return flattenedResults.values();
    }

    private void organiseByClass(Set<ITestResult> testResults, Map<IClass, TestClassResults> flattenedResults) {
        Iterator i$ = testResults.iterator();

        while(i$.hasNext()) {
            ITestResult testResult = (ITestResult)i$.next();
            this.getResultsForClass(flattenedResults, testResult).addResult(testResult);
        }

    }

    private JUnitXMLReporter.TestClassResults getResultsForClass(Map<IClass, TestClassResults> flattenedResults, ITestResult testResult) {
        JUnitXMLReporter.TestClassResults resultsForClass = (JUnitXMLReporter.TestClassResults)flattenedResults.get(testResult.getTestClass());
        if(resultsForClass == null) {
            resultsForClass = new JUnitXMLReporter.TestClassResults(testResult.getTestClass());
            flattenedResults.put(testResult.getTestClass(), resultsForClass);
        }

        return resultsForClass;
    }

    public static final class TestClassResults {
        private final IClass testClass;
        private final Collection<ITestResult> failedTests;
        private final Collection<ITestResult> skippedTests;
        private final Collection<ITestResult> passedTests;
        private long duration;

        private TestClassResults(IClass testClass) {
            this.failedTests = new LinkedList();
            this.skippedTests = new LinkedList();
            this.passedTests = new LinkedList();
            this.duration = 0L;
            this.testClass = testClass;
        }

        public IClass getTestClass() {
            return this.testClass;
        }

        void addResult(ITestResult result) {
            switch(result.getStatus()) {
                case 1:
                    this.passedTests.add(result);
                    break;
                case 3:
                    if(AbstractReporter.META.allowSkippedTestsInXML()) {
                        this.skippedTests.add(result);
                        break;
                    }
                case 2:
                case 4:
                    this.failedTests.add(result);
            }

            this.duration += result.getEndMillis() - result.getStartMillis();
        }

        public Collection<ITestResult> getFailedTests() {
            return this.failedTests;
        }

        public Collection<ITestResult> getSkippedTests() {
            return this.skippedTests;
        }

        public Collection<ITestResult> getPassedTests() {
            return this.passedTests;
        }

        public long getDuration() {
            return this.duration;
        }
    }
}
