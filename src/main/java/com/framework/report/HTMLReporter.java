package com.framework.report;

/**
 * Created by caijianmin on 2016/1/6.
 */

import org.apache.velocity.VelocityContext;
import org.testng.*;
import org.testng.xml.XmlSuite;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

public class HTMLReporter extends AbstractReporter {
    private static final String FRAMES_PROPERTY = "Report.frames";
    private static final String ONLY_FAILURES_PROPERTY = "Report.failures-only";
    private static final String TEMPLATES_PATH = "com/jd/pop/qa/report/templates/html/";
    private static final String INDEX_FILE = "indexMain.html";
    private static final String SUITES_FILE = "suites.html";
    private static final String OVERVIEW_FILE = "overview.html";
    private static final String GROUPS_FILE = "groups.html";
    private static final String RESULTS_FILE = "results.html";
    private static final String CHRONOLOGY_FILE = "chronology.html";
    private static final String OUTPUT_FILE = "output.html";
    private static final String CUSTOM_STYLE_FILE = "custom.css";
    private static final String SUITE_KEY = "suite";
    private static final String SUITES_KEY = "suites";
    private static final String GROUPS_KEY = "groups";
    private static final String RESULT_KEY = "result";
    private static final String FAILED_CONFIG_KEY = "failedConfigurations";
    private static final String SKIPPED_CONFIG_KEY = "skippedConfigurations";
    private static final String FAILED_TESTS_KEY = "failedTests";
    private static final String SKIPPED_TESTS_KEY = "skippedTests";
    private static final String PASSED_TESTS_KEY = "passedTests";
    private static final String METHODS_KEY = "methods";
    private static final String ONLY_FAILURES_KEY = "onlyReportFailures";
    private static final String REPORT_DIRECTORY = "";
    private static final Comparator<ITestNGMethod> METHOD_COMPARATOR = new TestMethodComparator();
    private static final Comparator<ITestResult> RESULT_COMPARATOR = new TestResultComparator();
    private static final Comparator<IClass> CLASS_COMPARATOR = new TestClassComparator();

    public HTMLReporter() {
        super("com/jd/pop/qa/report/templates/html/");
    }

    public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites, String outputDirectoryName) {
        removeEmptyDirectories(new File(outputDirectoryName));
        outputDirectoryName = addTimeStampToPath(outputDirectoryName);
        boolean useFrames = System.getProperty("Report.frames", "true").equals("true");

        boolean onlyFailures = System.getProperty("Report.failures-only", "false").equals("true");

        File outputDirectory = new File(outputDirectoryName, "");
        outputDirectory.mkdirs();
        try {
            if (useFrames) {
                createFrameset(outputDirectory);
            }
            createOverview(suites, outputDirectory, !useFrames, onlyFailures);
            createSuiteList(suites, outputDirectory, onlyFailures);
            createGroups(suites, outputDirectory);
            createResults(suites, outputDirectory, onlyFailures);

            createLog(outputDirectory, onlyFailures);
            copyResources(outputDirectory);
        } catch (Exception ex) {
            throw new ReportNGException("Failed generating HTML report.", ex);
        }
    }

    private void createFrameset(File outputDirectory) throws Exception {
        VelocityContext context = createContext();
        generateFile(new File(outputDirectory, "indexMain.html"), "indexMain.html.vm", context);
    }

    private void createOverview(List<ISuite> suites, File outputDirectory, boolean isIndex, boolean onlyFailures)
            throws Exception {
        VelocityContext context = createContext();
        context.put("suites", suites);
        context.put("onlyReportFailures", Boolean.valueOf(onlyFailures));
        generateFile(new File(outputDirectory, isIndex ? "indexMain.html" : "overview.html"), "overview.html.vm", context);
    }

    private void createSuiteList(List<ISuite> suites, File outputDirectory, boolean onlyFailures)
            throws Exception {
        VelocityContext context = createContext();
        context.put("suites", suites);
        context.put("onlyReportFailures", Boolean.valueOf(onlyFailures));
        generateFile(new File(outputDirectory, "suites.html"), "suites.html.vm", context);
    }

    private void createResults(List<ISuite> suites, File outputDirectory, boolean onlyShowFailures)
            throws Exception {
        int index = 1;
        for (ISuite suite : suites) {
            int index2 = 1;
            for (ISuiteResult result : suite.getResults().values()) {
                boolean failuresExist = (result.getTestContext().getFailedTests().size() > 0) || (result.getTestContext().getFailedConfigurations().size() > 0);

                if ((!onlyShowFailures) || (failuresExist)) {
                    VelocityContext context = createContext();
                    context.put("result", result);
                    context.put("failedConfigurations", sortByTestClass(result.getTestContext().getFailedConfigurations()));

                    context.put("skippedConfigurations", sortByTestClass(result.getTestContext().getSkippedConfigurations()));

                    context.put("failedTests", sortByTestClass(result.getTestContext().getFailedTests()));

                    context.put("skippedTests", sortByTestClass(result.getTestContext().getSkippedTests()));

                    context.put("passedTests", sortByTestClass(result.getTestContext().getPassedTests()));

                    String fileName = String.format("suite%d_test%d_%s", new Object[]{Integer.valueOf(index), Integer.valueOf(index2), "results.html"});

                    generateFile(new File(outputDirectory, fileName), "results.html.vm", context);
                }

                index2++;
            }
            index++;
        }
    }

    private void createChronology(List<ISuite> suites, File outputDirectory)
            throws Exception {
        int index = 1;
        for (ISuite suite : suites) {
            List methods = suite.getAllInvokedMethods();
            if (!methods.isEmpty()) {
                VelocityContext context = createContext();
                context.put("suite", suite);
                context.put("methods", methods);
                String fileName = String.format("suite%d_%s", new Object[]{Integer.valueOf(index), "chronology.html"});

                generateFile(new File(outputDirectory, fileName), "chronology.html.vm", context);
            }

            index++;
        }
    }

    private SortedMap<IClass, List<ITestResult>> sortByTestClass(IResultMap results) {
        SortedMap sortedResults = new TreeMap(CLASS_COMPARATOR);

        for (ITestResult result : results.getAllResults()) {
            List resultsForClass = (List) sortedResults.get(result.getTestClass());

            if (resultsForClass == null) {
                resultsForClass = new ArrayList();
                sortedResults.put(result.getTestClass(), resultsForClass);
            }
            int index = Collections.binarySearch(resultsForClass, result, RESULT_COMPARATOR);

            if (index < 0) {
                index = Math.abs(index + 1);
            }
            resultsForClass.add(index, result);
        }
        return sortedResults;
    }

    private void createGroups(List<ISuite> suites, File outputDirectory) throws Exception {
        int index = 1;
        for (ISuite suite : suites) {
            SortedMap groups = sortGroups(suite.getMethodsByGroups());

            if (!groups.isEmpty()) {
                VelocityContext context = createContext();
                context.put("suite", suite);
                context.put("groups", groups);
                String fileName = String.format("suite%d_%s", new Object[]{Integer.valueOf(index), "groups.html"});

                generateFile(new File(outputDirectory, fileName), "groups.html.vm", context);
            }

            index++;
        }
    }

    private void createLog(File outputDirectory, boolean onlyFailures) throws Exception {
        if (!Reporter.getOutput().isEmpty()) {
            VelocityContext context = createContext();
            context.put("onlyReportFailures", Boolean.valueOf(onlyFailures));
            generateFile(new File(outputDirectory, "output.html"), "output.html.vm", context);
        }
    }

    private SortedMap<String, SortedSet<ITestNGMethod>> sortGroups(Map<String, Collection<ITestNGMethod>> groups) {
        SortedMap sortedGroups = new TreeMap();
        for (Entry entry : groups.entrySet()) {
            SortedSet methods = new TreeSet(METHOD_COMPARATOR);

            methods.addAll((Collection) entry.getValue());
            sortedGroups.put(entry.getKey(), methods);
        }
        return sortedGroups;
    }

    private void copyResources(File outputDirectory) throws IOException {
        copyClasspathResource(outputDirectory, "reportng.css", "reportng.css");
        copyClasspathResource(outputDirectory, "reportng.js", "reportng.js");
        copyClasspathResource(outputDirectory, "sorttable.js", "sorttable.js");
        copyImgFile(outputDirectory, "logo.png", "logo.png");

        File customStylesheet = META.getStylesheetPath();

        if (customStylesheet != null)
            if (customStylesheet.exists()) {
                copyFile(outputDirectory, customStylesheet, "custom.css");
            } else {
                InputStream stream = ClassLoader.getSystemClassLoader().getResourceAsStream(customStylesheet.getPath());

                if (stream != null)
                    copyStream(outputDirectory, stream, "custom.css");
            }
    }

    private String addTimeStampToPath(String path) {
        DateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMddHHmmss");
        String dt = DATE_FORMAT.format(new Date());
        path = path + File.separator + System.getProperty("TestTimeStamp", dt);
        return path;
    }
}