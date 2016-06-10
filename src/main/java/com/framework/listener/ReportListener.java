package com.framework.listener;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.testng.IExecutionListener;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestResult;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

public class ReportListener implements IExecutionListener, IInvokedMethodListener
{
  public static final String BUILD_NUMBER = (String)System.getenv().get("BUILD_NUMBER");
  public static final String JOB_NAME = (String)System.getenv().get("JOB_NAME");
  public static final String BUILD_URL = (String)System.getenv().get("BUILD_URL");
  public static final String NODE_NAME = (String)System.getenv().get("NODE_NAME");
  public static String EXTRAHOST = (String)System.getenv().get("EXTRAHOST");
  public static String HOST = null;


  @Override
  public void beforeInvocation(IInvokedMethod method, ITestResult testResult) {

  }

  public void afterInvocation(IInvokedMethod method, ITestResult testResult)
  {
  }

  public Map<String, Object> getBuildInfoFromJenkins()
  {
    Map<String,Object> jenkisnResultMap = new HashMap<String,Object>();

    JSONObject jsonObj = getJenkinsInfoByAPI(BUILD_URL + "api/json");
    if (jsonObj != null) {
      Timestamp startTime = jsonObj.getTimestamp("timestamp");
      JSONArray jSONArray = jsonObj.getJSONArray("actions");
      String performer = getPerformer(BUILD_URL + "api/json");
      for (int i = 0; i < jSONArray.size(); i++) {
        JSONObject jsonObj2 = JSONObject.parseObject(jSONArray.getString(i));
        if (jsonObj2.get("parameters") != null) {
          JSONArray parametersArray = jsonObj2.getJSONArray("parameters");
          for (int j = 0; j < parametersArray.size(); j++) {
            JSONObject jsonObj3 = JSONObject.parseObject(parametersArray.getString(j));
            if ((jsonObj3.getString("name") != null) && (jsonObj3.getString("name").equalsIgnoreCase("HOST"))) {
              HOST = jsonObj3.getString("value");
            }
          }
        }
      }
      jenkisnResultMap.put("startTime", startTime);
      jenkisnResultMap.put("performer", performer);
      return jenkisnResultMap;
    }
    return null;
  }

  public String getPerformer(String buildUrl)
  {
    String performer = "";
    String baseUrl = "http://" + buildUrl.split("/")[2];
    JSONObject jsonObj = getJenkinsInfoByAPI(buildUrl + "/api/json");
    if (jsonObj != null) {
      JSONArray jSONArray = jsonObj.getJSONArray("actions");
      for (int i = 0; i < jSONArray.size(); i++) {
        JSONObject jsonObj2 = JSONObject.parseObject(jSONArray.getString(i));
        if (jsonObj2.get("causes") != null) {
          JSONObject causes_0 = JSONObject.parseObject(jsonObj2.getJSONArray("causes").getString(0));
          String upstreamBuild = causes_0.getString("upstreamBuild");
          if (upstreamBuild != null) {
            String upstreamProject = causes_0.getString("upstreamProject");
            String reqUrl = baseUrl + "/job/" + upstreamProject + "/" + upstreamBuild + "/api/json";
            performer = getPerformer(reqUrl);
          } else {
            String userId = causes_0.getString("userId");
            performer = userId;
            if (userId == null) {
              performer = "timer";
            }
          }
        }
      }
    }

    return performer;
  }

  public JSONObject getJenkinsInfoByAPI(String url)
  {
    JSONObject jsonObj = null;
    try {
      for (int i = 0; i < 5; i++) {
        String jsonStr = sendGet(url);
        jsonObj = JSONObject.parseObject(jsonStr);
        if (jsonObj == null)
          Thread.sleep(2000L);
        else
          return jsonObj;
      }
    }
    catch (Exception e)
    {
      System.out.println("Jenksin 接口地址" + url + "返回数据异常！");
      e.printStackTrace();
    }
    return null;
  }

  public String sendGet(String url)
  {
    String responseStr = "";
    CloseableHttpClient httpclient = HttpClients.createDefault();
    try
    {
      HttpGet httpget = new HttpGet(url);

      CloseableHttpResponse response = httpclient.execute(httpget);
      try
      {
        HttpEntity entity = response.getEntity();

        System.out.println(response.getStatusLine());
        if (entity != null)
          responseStr = EntityUtils.toString(entity);
      }
      finally
      {
        response.close();
      }
    } catch (ClientProtocolException e) {
      e.printStackTrace();
      try
      {
        httpclient.close();
      } catch (IOException e3) {
        e.printStackTrace();
      }
    }
    catch (ParseException e)
    {
      e.printStackTrace();
      try
      {
        httpclient.close();
      } catch (IOException e1) {
        e1.printStackTrace();
      }
    }
    catch (IOException e)
    {
      e.printStackTrace();
      try
      {
        httpclient.close();
      } catch (IOException e2) {
        e2.printStackTrace();
      }
    }
    finally
    {
      try
      {
        httpclient.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return responseStr;
  }

  @Override
  public void onExecutionStart() {

  }

  @Override
  public void onExecutionFinish() {

  }
}