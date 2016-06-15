package com.framework.util.commonutil;

/**
 * Created by caijianmin on 2016/6/10.
 */

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CommonUtils {
    private static Logger logger = Logger.getLogger(CommonUtils.class);
    public static final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
    public static final String ENCODING = "UTF-8";
    public static final String EOL = System.getProperty("line.separator");
    private static final int DEFAULT_CHUNK_SIZE = 4096;
    private static final String SPACES = "                                 ";
    private static final int SPACES_LEN = "                                 ".length();

    private static final byte[] XML_PFX = {60, 63, 120, 109, 108};

    public static String getOSName() {
        String osName = System.getProperty("os.name");
        return osName;
    }

    public static boolean isWindows() {
        return File.pathSeparator.equals(";");
    }

    public static boolean isMac() {
        return getOSName().toLowerCase().contains("mac");
    }

    public static boolean isLinux() {
        return getOSName().toLowerCase().indexOf("nux") > 0;
    }

    public static void validateArgument(String arg) {
        if (hasNotValue(arg))
            throw new IllegalArgumentException(new StringBuilder().append("Invalid argument: ").append(arg).toString());
    }

    public static boolean hasValue(String pString) {
        return (pString != null) && (pString.trim().length() != 0);
    }

    public static boolean hasNotValue(String pString) {
        return !hasValue(pString);
    }

    public static boolean hasValue(String[] pStringArray) {
        return (pStringArray != null) && (pStringArray.length != 0);
    }

    public static boolean hasNotValue(String[] pStringArray) {
        return !hasValue(pStringArray);
    }

    public static String now() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(cal.getTime());
    }

    public static long getCurrentMillisecondTime() {
        Date date = new Date();
        return date.getTime();
    }

    public static String getNowTimeYYYYMMDDHHmmSS() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    public static String displayArray(String[] pStrings) {
        StringBuilder result = new StringBuilder();
        result.append("{");

        if ((pStrings != null) && (pStrings.length > 0)) {
            for (int i = 0; i < pStrings.length; i++) {
                String item = pStrings[i];
                result.append(item);
                if (i < pStrings.length - 1) {
                    result.append(",");
                }
            }
        }

        result.append("}");
        return result.toString();
    }

    public static String displayArray(String[][] pStrings) {
        StringBuilder result = new StringBuilder();
        result.append("{");

        if ((pStrings != null) && (pStrings.length > 0)) {
            for (int i = 0; i < pStrings.length; i++) {
                String[] item = pStrings[i];
                result.append(displayArray(item));
                if (i < pStrings.length - 1) {
                    result.append(",");
                }
            }
        }

        result.append("}");
        return result.toString();
    }

    public static String getClassName(String pClass) {
        if (pClass == null) {
            return null;
        }

        String result = pClass;
        int firstChar = pClass.lastIndexOf(46) + 1;
        if (firstChar > 0) {
            result = pClass.substring(firstChar);
        }

        return result;
    }

    public static void sleep(int pTimeInMs) {
        try {
            Thread.sleep(pTimeInMs);
        } catch (InterruptedException e) {
        }
    }

    public static Object noNull(String pString) {
        if (pString == null) {
            return "";
        }
        return pString;
    }

    public static boolean isInteger(String pNumber) {
        if (!hasValue(pNumber))
            return false;
        try {
            Integer.parseInt(pNumber);
            return true;
        } catch (Throwable e) {
        }
        return false;
    }

    public static boolean containsString(String pName, String[] pList, boolean pOptimistic) {
        if ((pName == null) || (pList == null)) {
            return pOptimistic == true;
        }

        for (int i = 0; i < pList.length; i++) {
            String item = pList[i];
            if (item.contains(pName)) {
                return true;
            }
        }
        return false;
    }

    public static int parseInteger(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw e;
        }
    }

    public static boolean parseBoolean(String value) {
        if (!hasValue(value)) {
            return false;
        }
        return value.trim().equalsIgnoreCase("true");
    }

    public static String getCurrentTestClassName() {
        String cname = null;
        for (StackTraceElement one : Thread.currentThread().getStackTrace()) {
            cname = one.getClassName();

            if ((cname != null) && (cname.contains("Test"))) {
                break;
            }
        }
        return cname;
    }

    public static String getUniqueIdentifier() {
        return Long.toHexString(System.nanoTime());
    }

    public static String mapToString(Map map) {
        String ret = "{\n";
        for (Iterator i$ = map.keySet().iterator(); i$.hasNext(); ) {
            Object key = i$.next();
            ret = new StringBuilder().append(ret).append(" ").append(key).append(" = ").append(map.get(key)).append("\n").toString();
        }

        return new StringBuilder().append(ret).append("}\n").toString();
    }

    public static String makeHTTPSServerURL(String serverUrl) {
        serverUrl = serverUrl.trim();
        if (!serverUrl.startsWith("https://")) {
            serverUrl = new StringBuilder().append("https://").append(serverUrl).toString();
        }

        if (serverUrl.endsWith("/")) {
            int i = serverUrl.lastIndexOf(47);
            serverUrl = serverUrl.substring(0, i);
        }
        return serverUrl;
    }

    public static String makeHTTPServerURL(String serverUrl) {
        serverUrl = serverUrl.trim();
        if (!serverUrl.startsWith("http://")) {
            serverUrl = new StringBuilder().append("http://").append(serverUrl).toString();
        }

        if (serverUrl.endsWith("/")) {
            int i = serverUrl.lastIndexOf(47);
            serverUrl = serverUrl.substring(0, i);
        }
        return serverUrl;
    }

    public static String loadTextFrom(String path) {
        validateArgument(path);

        BufferedReader reader = null;
        StringBuilder buffer = new StringBuilder();
        String content = null;
        try {
            InputStream is = CommonUtils.class.getResourceAsStream(path);
            if (is == null) {
                is = new FileInputStream(path);
            }

            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String str;
            while ((str = reader.readLine()) != null) {
                buffer.append(str);
            }

            content = buffer.toString();
            reader.close();
        } catch (IOException e) {
            if (reader != null)
                try {
                    reader.close();
                } catch (IOException ex) {
                }
        }
        return content;
    }

    public static boolean resourceExists(String path) {
        if (hasNotValue(path)) {
            return false;
        }
        boolean exists = false;
        try {
            InputStream is = CommonUtils.class.getResourceAsStream(path);
            if (is == null) {
                is = new FileInputStream(path);
            }
            is.close();
            exists = true;
        } catch (IOException e) {
        }
        return exists;
    }

    public static JSONObject loadJSONFile(String configPath) {
        validateArgument(configPath);
        if ((hasNotValue(configPath)) || (!configPath.trim().endsWith("json"))) {
            throw new IllegalArgumentException(new StringBuilder().append("Invalid JSON path: ").append(configPath).toString());
        }

        String jsonStr = null;
        JSONObject json = null;
        try {
            jsonStr = loadTextFrom(configPath);
            json = JSON.parseObject(jsonStr);
        } catch (JSONException ex) {
        }
        return json;
    }

    public static JSONObject loadJSONFile(String configPath, String name) {
        validateArgument(configPath);
        if ((hasNotValue(configPath)) || (!configPath.trim().endsWith("json"))) {
            throw new IllegalArgumentException(new StringBuilder().append("Invalid JSON path: ").append(configPath).toString());
        }

        if (hasNotValue(name)) {
            throw new IllegalArgumentException("Invalid name passed.");
        }

        String jsonStr = null;
        JSONObject json = null;
        try {
            jsonStr = loadTextFrom(configPath);

            if (hasValue(jsonStr)) {
                jsonStr = jsonStr.trim();

                if ((jsonStr.startsWith("[")) && (jsonStr.endsWith("]"))) {
                    JSONArray jarry = JSON.parseArray(jsonStr);

                    for (int i = 0; i < jarry.size(); i++) {
                        JSONObject one = jarry.getJSONObject(i);
                        if (name.equalsIgnoreCase(one.getString("name"))) {
                            json = one;
                            break;
                        }
                    }
                } else {
                    json = JSON.parseObject(jsonStr);
                }
            } else {
                throw new JSONException("Either null or empty JSON string value passed.");
            }
        } catch (JSONException ex) {
            logger.fatal(new StringBuilder().append("Invalid json string: \n").append(jsonStr).toString());
        }

        if (json == null) {
            logger.warn("Couldn't find valid named JSON object. A null object will be returned.");
        }

        return json;
    }

    public static Properties loadProperties(String path) {
        InputStream is = null;
        Properties props = new Properties();

        if (resourceExists(path)) {
            try {
                is = toInputStream(path);
                props.load(is);
            } catch (IOException ex) {
                return null;
            }
        } else {
            return null;
        }

        return props;
    }

    public static void writeFile(File pFile, byte[] pData) {
        if ((pData != null) && (pData.length >= 1)) {
            try {
                if (pFile.exists()) {
                    pFile.delete();
                }

                File path = new File(pFile.getParent());
                path.mkdirs();
                path = null;

                pFile.createNewFile();
                BufferedOutputStream bw = new BufferedOutputStream(new FileOutputStream(pFile));

                bw.write(pData);
                bw.flush();
                bw.close();
            } catch (Throwable e) {
            }
        }
    }

    public static void writeStringToFile(String fileName, String inputString)
            throws IOException {
        if ((inputString == null) || (fileName == null)) {
            return;
        }

        BufferedWriter outWriter = new BufferedWriter(new FileWriter(fileName));

        outWriter.write(inputString);
        outWriter.close();
    }

    public static InputStream toInputStream(String path)
            throws IOException {
        if (hasNotValue(path)) {
            throw new IOException("Empty value passed.");
        }

        InputStream is = CommonUtils.class.getResourceAsStream(path);
        if (is == null) {
            File file = new File(path);
            if (!file.isDirectory()) {
                if (file.exists())
                    is = new FileInputStream(file);
                else
                    throw new IOException(new StringBuilder().append("File not exist: ").append(path).toString());
            } else {
                throw new IOException(new StringBuilder().append("Unexpected dir path passed (a file path should be passed): ").append(path).toString());
            }
        }

        return is;
    }

    public static boolean isValidPath(String path) {
        if (hasValue(path)) {
            if (CommonUtils.class.getResourceAsStream(path) != null) {
                return true;
            }

            return new File(path).exists();
        }

        return false;
    }

    public static String[] split(String splittee, String splitChar, boolean truncate) {
        if ((splittee == null) || (splitChar == null)) {
            return new String[0];
        }
        String EMPTY_ELEMENT = "";

        int splitLength = splitChar.length();
        String adjacentSplit = new StringBuilder().append(splitChar).append(splitChar).toString();
        int adjacentSplitLength = adjacentSplit.length();
        if (truncate) {
            int spot;
            while ((spot = splittee.indexOf(adjacentSplit)) != -1) {
                splittee = new StringBuilder().append(splittee.substring(0, spot + splitLength)).append(splittee.substring(spot + adjacentSplitLength, splittee.length())).toString();
            }
            if (splittee.startsWith(splitChar)) {
                splittee = splittee.substring(splitLength);
            }
            if (splittee.endsWith(splitChar)) {
                splittee = splittee.substring(0, splittee.length() - splitLength);
            }
        }
        List returns = new ArrayList();
        int length = splittee.length();
        int start = 0;
        int spot = 0;
        while ((start < length) && ((spot = splittee.indexOf(splitChar, start)) > -1)) {
            if (spot > 0)
                returns.add(splittee.substring(start, spot));
            else {
                returns.add("");
            }
            start = spot + splitLength;
        }
        if (start < length)
            returns.add(splittee.substring(start));
        else if (spot == length - splitLength) {
            returns.add("");
        }
        return (String[]) returns.toArray(new String[returns.size()]);
    }

    public static String[] split(String splittee, String splitChar) {
        return split(splittee, splitChar, true);
    }

    public static String[] split(String splittee, String delims, String def) {
        StringTokenizer tokens = new StringTokenizer(splittee, delims, def != null);
        boolean lastWasDelim = false;
        List strList = new ArrayList();
        while (tokens.hasMoreTokens()) {
            String tok = tokens.nextToken();
            if ((tok.length() == 1) && (delims.indexOf(tok) != -1)) {
                if (lastWasDelim) {
                    strList.add(def);
                }
                lastWasDelim = true;
            } else {
                lastWasDelim = false;
                strList.add(tok);
            }
        }
        if (lastWasDelim) {
            strList.add(def);
        }
        return (String[]) strList.toArray(new String[strList.size()]);
    }

    public static StringBuilder rightAlign(StringBuilder in, int len) {
        int pfx = len - in.length();
        if (pfx <= 0) {
            return in;
        }
        if (pfx > SPACES_LEN) {
            pfx = SPACES_LEN;
        }
        in.insert(0, "                                 ".substring(0, pfx));
        return in;
    }

    public static StringBuilder leftAlign(StringBuilder in, int len) {
        int sfx = len - in.length();
        if (sfx <= 0) {
            return in;
        }
        if (sfx > SPACES_LEN) {
            sfx = SPACES_LEN;
        }
        in.append("                                 ".substring(0, sfx));
        return in;
    }

    public static String booleanToSTRING(boolean value) {
        return value ? "TRUE" : "FALSE";
    }

    public static String replaceFirst(String source, String search, String replace) {
        int start = source.indexOf(search);
        int len = search.length();
        if (start == -1) {
            return source;
        }
        if (start == 0) {
            return new StringBuilder().append(replace).append(source.substring(len)).toString();
        }
        return new StringBuilder().append(source.substring(0, start)).append(replace).append(source.substring(start + len)).toString();
    }

    public static String replaceAllChars(String source, char search, String replace) {
        char[] chars = source.toCharArray();
        StringBuilder sb = new StringBuilder(source.length() + 20);
        for (char c : chars) {
            if (c == search)
                sb.append(replace);
            else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static String substitute(String input, String pattern, String sub) {
        StringBuilder ret = new StringBuilder(input.length());
        int start = 0;
        int index = -1;
        int length = pattern.length();
        while ((index = input.indexOf(pattern, start)) >= start) {
            ret.append(input.substring(start, index));
            ret.append(sub);
            start = index + length;
        }
        ret.append(input.substring(start));
        return ret.toString();
    }

    public static String trim(String input, String delims) {
        StringTokenizer tokens = new StringTokenizer(input, delims);
        return tokens.hasMoreTokens() ? tokens.nextToken() : "";
    }

    public static String trimBothEndsBlank(String input) {
        if (input.length() > 0) {
            char[] array = input.toCharArray();
            int start = 0;
            int end = array.length - 1;
            while (array[start] == ' ')
                start++;
            while (array[end] == ' ')
                end--;
            return new String(array, start, end - start + 1);
        }
        return "";
    }

    public static byte[] getByteArraySlice(byte[] array, int begin, int end) {
        byte[] slice = new byte[end - begin + 1];
        System.arraycopy(array, begin, slice, 0, slice.length);
        return slice;
    }

    public static void closeQuietly(Closeable cl) {
        try {
            if (cl != null)
                cl.close();
        } catch (IOException ignored) {
        }
    }

    public static void closeQuietly(Socket sock) {
        try {
            if (sock != null)
                sock.close();
        } catch (IOException ignored) {
        }
    }

    public static void closeQuietly(ServerSocket sock) {
        try {
            if (sock != null)
                sock.close();
        } catch (IOException ignored) {
        }
    }

    public static boolean startsWith(byte[] target, byte[] search, int offset) {
        int targetLength = target.length;
        int searchLength = search.length;
        if ((offset < 0) || (searchLength > targetLength + offset)) {
            return false;
        }
        for (int i = 0; i < searchLength; i++) {
            if (target[(i + offset)] != search[i]) {
                return false;
            }
        }
        return true;
    }

    public static boolean isXML(byte[] target) {
        return startsWith(target, XML_PFX, 0);
    }

    public static String baToHexString(byte[] ba) {
        StringBuilder sb = new StringBuilder(ba.length * 2);
        for (int i = 0; i < ba.length; i++) {
            int j = ba[i] & 0xFF;
            if (j < 16) {
                sb.append("0");
            }
            sb.append(Integer.toHexString(j));
        }
        return sb.toString();
    }

    public static String baToHexString(byte[] ba, char separator) {
        StringBuilder sb = new StringBuilder(ba.length * 2);
        for (int i = 0; i < ba.length; i++) {
            if ((i > 0) && (separator != 0)) {
                sb.append(separator);
            }
            int j = ba[i] & 0xFF;
            if (j < 16) {
                sb.append("0");
            }
            sb.append(Integer.toHexString(j));
        }
        return sb.toString();
    }

    public static byte[] baToHexBytes(byte[] ba) {
        byte[] hb = new byte[ba.length * 2];
        for (int i = 0; i < ba.length; i++) {
            byte upper = (byte) ((ba[i] & 0xF0) >> 4);
            byte lower = (byte) (ba[i] & 0xF);
            hb[(2 * i)] = toHexChar(upper);
            hb[(2 * i + 1)] = toHexChar(lower);
        }
        return hb;
    }

    private static byte toHexChar(byte in) {
        if (in < 10) {
            return (byte) (in + 48);
        }
        return (byte) (in - 10 + 97);
    }

    public static int read(InputStream is, byte[] buffer, int offset, int length)
            throws IOException {
        int remaining = length;
        while (remaining > 0) {
            int location = length - remaining;
            int count = is.read(buffer, location, remaining);
            if (-1 == count) {
                break;
            }
            remaining -= count;
        }
        return length - remaining;
    }

/*    public static void displayThreads(boolean includeDaemons) {
        Map m = Thread.getAllStackTraces();
        String lineSeparator = System.getProperty("line.separator");
        StringBuilder builder = new StringBuilder();
        for (Map.Entry e : m.entrySet()) {
            boolean daemon = ((Thread) e.getKey()).isDaemon();
            if ((includeDaemons) || (!daemon)) {
                builder.setLength(0);
                StackTraceElement[] ste = (StackTraceElement[]) e.getValue();
                for (StackTraceElement stackTraceElement : ste) {
                    int lineNumber = stackTraceElement.getLineNumber();
                    builder.append(new StringBuilder().append(stackTraceElement.getClassName()).append("#").append(stackTraceElement.getMethodName()).append(lineNumber >= 0 ? new StringBuilder().append(" at line:").append(stackTraceElement.getLineNumber()).toString() : "").append(lineSeparator).toString());
                }

                System.out.println(new StringBuilder().append(((Thread) e.getKey()).toString()).append(daemon ? " (daemon)" : "").append(", stackTrace:").append(builder.toString()).toString());
            }
        }
    }*/

    public static String nullifyIfEmptyTrimmed(String input) {
        if (input == null) {
            return null;
        }
        String trimmed = input.trim();
        if (trimmed.length() == 0) {
            return null;
        }
        return trimmed;
    }

    public static boolean isBlank(String value) {
        return StringUtils.isBlank(value);
    }

    public static void write(byte[] data, OutputStream output)
            throws IOException {
        int bytes = data.length;
        int offset = 0;
        while (bytes > 0) {
            int chunk = Math.min(bytes, 4096);
            output.write(data, offset, chunk);
            bytes -= chunk;
            offset += chunk;
        }
    }

    public static String readFileToString(File file) {
        StringBuffer sb = new StringBuffer();
        try {
            readToBuffer(sb, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static void readToBuffer(StringBuffer buffer, File file) throws IOException {
        InputStream is = new FileInputStream(file);

        BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf-8"));
        String line = reader.readLine();
        while (line != null) {
            buffer.append(line);
            buffer.append("\n");
            line = reader.readLine();
        }
        reader.close();
        is.close();
    }

    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);

        return isNum.matches();
    }
}