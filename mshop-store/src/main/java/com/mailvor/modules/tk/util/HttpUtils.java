package com.mailvor.modules.tk.util;


import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtils {

    public static String doGet(String url) {
        HttpURLConnection client = null;
        InputStream is = null;
        BufferedReader br = null;
        StringBuffer result = new StringBuffer();
        try {
            URL httpUrl = new URL(url);
            client = (HttpURLConnection) httpUrl.openConnection();
            client.setRequestMethod("GET");
            client.setReadTimeout(15000);
            client.connect();
            if (client.getResponseCode() == 200) {
                is = client.getInputStream();
                if (null != is) {
                    br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                    String temp = null;
                    while (null != (temp = br.readLine())) {
                        result.append(temp);
                    }
                    return result.toString();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            client.disconnect();
        }
        return result.toString();
    }


    public static String doPost(String httpUrl, String jsonStr) {
        StringBuffer result = new StringBuffer();
        HttpURLConnection connection = null;
        OutputStream  out = null;
        InputStream is = null;
        BufferedReader br = null;
        try {
            URL url = new URL(httpUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestProperty("Content-Type", "application/json");
            out = connection.getOutputStream();
            out.write(jsonStr.getBytes());
            if (connection.getResponseCode() == 200) {
                is = connection.getInputStream();
                if (null != is) {
                    br = new BufferedReader(new InputStreamReader(is, "GBK"));
                    String temp = null;
                    while (null != (temp = br.readLine())) {
                        result.append(temp);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            connection.disconnect();
        }
        return result.toString();
    }

}
