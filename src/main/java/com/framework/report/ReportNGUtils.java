package com.framework.report;


import org.testng.*;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

/**
 * Report Data
 *
 * @author HanLingZhi
 *         revise NiHuaiqing
 * @version 1.0.0
 */
public class ReportNGUtils {
    private static final NumberFormat DURATION_FORMAT = new DecimalFormat("#0.000");
    private static final NumberFormat PERCENTAGE_FORMAT = new DecimalFormat("#0.00%");

    public long getDuration(ITestContext context) {
        long duration = getDuration(context.getPassedConfigurations().getAllResults());
        duration += getDuration(context.getPassedTests().getAllResults());
        duration += getDuration(context.getSkippedConfigurations().getAllResults());
        duration += getDuration(context.getSkippedTests().getAllResults());
        duration += getDuration(context.getFailedConfigurations().getAllResults());
        duration += getDuration(context.getFailedTests().getAllResults());
        return duration;
    }

    public long getPassedLen(ITestContext context) {
        int passedlen = context.getPassedTests().size();
        return passedlen;
    }

    private long getDuration(Set<ITestResult> results) {
        long duration = 0;
        for (ITestResult result : results) {
            duration += (result.getEndMillis() - result.getStartMillis());
        }
        return duration;
    }

    public String formatDuration(long startMillis, long endMillis) {
        long elapsed = endMillis - startMillis;
        return formatDuration(elapsed);
    }

    public String formatPercentage(int numerator, int denominator) {
        return PERCENTAGE_FORMAT.format(numerator / (double) denominator);
    }

    public String formatDuration(long elapsed) {
        String timeStr = null;
        int seconds = (int) (elapsed / 1000);
        int hour = 0;
        int minute = 0;
        int second = 0;
        if (seconds <= 0) {
            return "00:00";
        } else {
            minute = seconds / 60;
            if (minute < 60) {
                second = seconds % 60;
                timeStr = unitFormat(minute) + "分" + unitFormat(second) + "秒";
                return timeStr;
            } else {
                hour = minute / 60;
                if (hour > 99)
                    return "99:59:59";
                minute = minute % 60;
                second = seconds - hour * 3600 - minute * 60;
                timeStr = unitFormat(hour) + "小时" + unitFormat(minute) + "分" + unitFormat(second) + "秒";
                return timeStr;
            }
        }
        //return DURATION_FORMAT.format(seconds);
    }

    public static String unitFormat(int i) {
        String retStr = null;
        if (i >= 0 && i < 10)
            retStr = "0" + Integer.toString(i);
        else
            retStr = "" + i;
        return retStr;
    }

    public List<Throwable> getCauses(Throwable t) {
        List<Throwable> causes = new LinkedList<Throwable>();
        Throwable next = t;
        while (next.getCause() != null) {
            next = next.getCause();
            causes.add(next);
        }
        return causes;
    }

    public List<String> getTestOutput(ITestResult result) {
        return Reporter.getOutput(result);
    }

    public List<String> getAllOutput() {
        return Reporter.getOutput();
    }

    public boolean hasArguments(ITestResult result) {
        return result.getParameters().length > 0;
    }

    public String getArguments(ITestResult result) {
        Object[] arguments = result.getParameters();
        List<String> argumentStrings = new ArrayList<String>(arguments.length);
        for (Object argument : arguments) {
            argumentStrings.add(renderArgument(argument));
        }
        return commaSeparate(argumentStrings);
    }

    private String renderArgument(Object argument) {
        if (argument == null) {
            return "null";
        } else if (argument instanceof String) {
            return "\"" + argument + "\"";
        } else if (argument instanceof Character) {
            return "\'" + argument + "\'";
        } else {
            return argument.toString();
        }
    }

    public boolean hasDependentGroups(ITestResult result) {
        return result.getMethod().getGroupsDependedUpon().length > 0;
    }

    public String getDependentGroups(ITestResult result) {
        String[] groups = result.getMethod().getGroupsDependedUpon();
        return commaSeparate(Arrays.asList(groups));
    }

    public boolean hasDependentMethods(ITestResult result) {
        return result.getMethod().getMethodsDependedUpon().length > 0;
    }

    public String getDependentMethods(ITestResult result) {
        String[] methods = result.getMethod().getMethodsDependedUpon();
        System.out.printf("+++++++++++++++methods:" + methods);
        return commaSeparate(Arrays.asList(methods));
    }

    public boolean hasSkipException(ITestResult result) {
        return result.getThrowable() instanceof SkipException;
    }

    public String getSkipExceptionMessage(ITestResult result) {
        return hasSkipException(result) ? result.getThrowable().getMessage() : "";
    }

    public boolean hasGroups(ISuite suite) {
        return !suite.getMethodsByGroups().isEmpty();
    }

    private String commaSeparate(Collection<String> strings) {
        StringBuilder buffer = new StringBuilder();
        Iterator<String> iterator = strings.iterator();
        while (iterator.hasNext()) {
            String string = iterator.next();
            buffer.append(string);
            if (iterator.hasNext()) {
                buffer.append(", ");
            }
        }
        return buffer.toString();
    }

    public String escapeString(String s) {
        if (s == null) {
            return null;
        }

        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            buffer.append(escapeChar(s.charAt(i)));
        }
        return buffer.toString();
    }

    private String escapeChar(char character) {
        switch (character) {
//		case '<':
//			return "&lt;";
//		case '>':
//			return "&gt;";
//		case '"':
//			return "&quot;";
//		case '\'':
//			return "&apos;";
//		case '&':
//			return "&amp;";
            default:
                return String.valueOf(character);
        }
    }

    public String escapeHTMLString(String s) {
        if (s == null) {
            return null;
        }

        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            switch (ch) {
                case ' ':
                    // 除了最后一个，在一个连续的空格都被转换为一个非中断的空格 (&nbsp;)。
                    char nextCh = i + 1 < s.length() ? s.charAt(i + 1) : 0;
                    buffer.append(nextCh == ' ' ? "&nbsp;" : " ");
                    break;
                case '\n':
                    buffer.append("<br/>\n");
                    break;
                default:
                    buffer.append(escapeChar(ch));
            }
        }
        return buffer.toString();
    }

    public String stripThreadName(String threadId) {
        if (threadId == null) {
            return null;
        } else {
            int index = threadId.lastIndexOf('@');
            return index >= 0 ? threadId.substring(0, index) : threadId;
        }
    }

    public long getStartTime(List<IInvokedMethod> methods) {
        long startTime = System.currentTimeMillis();
        for (IInvokedMethod method : methods) {
            startTime = Math.min(startTime, method.getDate());
        }
        return startTime;
    }

    public long getEndTime(ISuite suite, IInvokedMethod method,
                           List<IInvokedMethod> methods) {
        boolean found = false;
        for (IInvokedMethod m : methods) {
            if (m == method) {
                found = true;
            }
            // Once a method is found, find subsequent method on same thread.
            else if (found && m.getTestMethod().getId().equals(method.getTestMethod().getId())) {
                return m.getDate();
            }
        }
        return getEndTime(suite, method);
    }

    private long getEndTime(ISuite suite, IInvokedMethod method) {
        // 从测试套件中的所有测试中，找到最新的结束时间.
        for (Map.Entry<String, ISuiteResult> entry : suite.getResults().entrySet()) {
            ITestContext testContext = entry.getValue().getTestContext();
            for (ITestNGMethod m : testContext.getAllTestMethods()) {
                if (method == m) {
                    return testContext.getEndDate().getTime();
                }
            }
            // 如果我们找不到一个匹配的测试方法，它必须是一个配置方法。
            for (ITestNGMethod m : testContext.getPassedConfigurations().getAllMethods()) {
                if (method == m) {
                    return testContext.getEndDate().getTime();
                }
            }
            for (ITestNGMethod m : testContext.getFailedConfigurations().getAllMethods()) {
                if (method == m) {
                    return testContext.getEndDate().getTime();
                }
            }
        }
        throw new IllegalStateException("Could not find matching end time.");
    }
}