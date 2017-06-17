package com.example.asus.ui_project.communication;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutionException;


import android.util.Log;


public class HttpUtils {
    /**
     * 作为标记在
     */
    private static final String TAG = "HttpUtils处理类";
    /**
     * 用户识别
     */
    public static final String User_Agent = "";

    private static String sessionId;

    private static JSONObject postRequest(String url, List<NameValuePair> params) {
        //在这里可以进行一些处理，如添加时间戳，对参数进行排序等等

        try {
            //建立JSONObject对象
            JSONObject object = null;
            //建立HttpClient对象
            HttpClient httpClient = new DefaultHttpClient();
            //设置请求路径
            System.err.println(url);
            HttpPost post = new HttpPost(url);
            // 设置sessionId
            if(sessionId != null && !"".equals(sessionId)) {
                post.setHeader("Cookie", "JSESSIONID=" + sessionId);
                Log.i("cookie", sessionId + "");
            }
            //设置请求参数
            post.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
            //设置用户识别
            post.addHeader("User-Agent", User_Agent);
            //建立HttpParams对象
            HttpParams httpParams = post.getParams();
            //设置读取超时
            httpParams.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
            //设置请求超时
            httpParams.setParameter(CoreConnectionPNames.SO_TIMEOUT, 5000);
            //发送请求获得回馈
            HttpResponse httpResponse = httpClient.execute(post);
            //取得反馈信息
            HttpEntity httpEntity = httpResponse.getEntity();
            //如果httpEntity不为空
            if (httpEntity != null) {
                String result = EntityUtils.toString(httpEntity);
                object = new JSONObject(result);

                Log.i(TAG, "post请求成功:" + result);

                if(sessionId == null || "".equals(sessionId)) {
                    List<Cookie> cookies = ((AbstractHttpClient) httpClient).getCookieStore().getCookies();
                    for (int i = 0; i < cookies.size(); i++) {
                        if("JSESSIONID".equals(cookies.get(i).getName())) {
                            sessionId = cookies.get(i).getValue();
                            Log.i("cookie", sessionId + "");
                            break;
                        }
                    }
                }
                return object;
            } else {
                Log.e(TAG, "post请求失败");

                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, e.toString());
            return null;

        }

    }

    /**
     * post方法传参
     *
     * @param url    ip路径
     * @param method 方法名
     * @param params 参数
     * @return
     */
    public static JSONObject post(String url, String method, List<NameValuePair> params) throws Exception {

        return postRequest(url + method, params);
    }

    private static JSONObject post(String url, List<NameValuePair> params){
        try {
            URL httpUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) httpUrl.openConnection();
            connection.setRequestMethod("POST");
            connection.setReadTimeout(5000);
            for(NameValuePair pair : params) {
                connection.addRequestProperty(pair.getName(), pair.getValue());
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuffer sb = new StringBuffer();
            String str;
            while((str = reader.readLine()) != null) {
                sb.append(str);
            }
            System.out.println(sb.toString());
            return new JSONObject(sb.toString());
        }catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}
