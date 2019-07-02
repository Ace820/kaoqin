package com.zhangche.kaoqin;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.apache.http.HttpResponse;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class GetBusGpsData {
    final String TAG = "zhangche";
    String company2Home  = "http://www.bjbus.com/home/ajax_rtbus_data.php?act=busTime&selBLine=523&selBDir=5074871549194769576&selBStop=4";
    String home2Company  = "http://www.bjbus.com/home/ajax_rtbus_data.php?act=busTime&selBLine=523&selBDir=5592193305562839972&selBStop=9";
    public boolean isGoHome = false;
    public boolean need_exit = false;
    String[] tagList = {
            "<[.[^>]]*>",
            " ",
    };
    String[] home2CompanyStations = {
           "科创十六街",
           "科创十五街",
           "科创十四街",
           "经海三路",
           "科创十三街",
           "地铁经海路站",
           "经海五路",
           "经海七路",
           "定海园",
           "科创九街",
           "生物医药园西",
           "科创六街",
           "经海二路",
           "荣京东街东口",
           "永昌北路",
           "永昌中路北口",
           "兴盛街西口",
           "地铁荣京东街",
    };
    String[] company2HomeStations = {
           "地铁荣京东街",
           "永昌北路",
           "荣京东街东口",
           "经海二路",
           "科创六街",
           "生物医药园西",
           "科创九街",
           "定海园",
           "经海七路",
           "经海五路",
           "地铁经海路站",
           "科创十三街",
           "经海三路",
           "科创十四街",
           "科创十五街",
           "科创十六街",
    };
    Handler mHandler;
    int[] busData = {0,0,0};
    public GetBusGpsData(Handler handler) {
        mHandler = handler;
    }
    public void run() {
        new Thread() {
            @Override
            public void run() {
                try {
                    while (true) {
                        if (need_exit)
                            break;
                        getData();
                        Thread.sleep(5000);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public void getData() throws Exception {
        HttpGet httpget = new HttpGet(isGoHome ? company2Home : home2Company);
        HttpResponse httpresponse;

        httpresponse = new DefaultHttpClient().execute(httpget);
        if (httpresponse.getStatusLine().getStatusCode() == 200) {

            String s = unicodeToString(EntityUtils.toString(httpresponse.getEntity()));
            String s1 = s.replaceAll("&nbsp","");
            s = s1.replace("><div",">\n<div");
            String[] temp = s.split("\n");
            for (String str :temp) {
                if (str.contains("分段计价"))
                    s = str;
            }
            parseBus(removeTags(s));
            Log.d(TAG,removeTags(s));
        }
    }
    public String getCurrentStation() {
        if (busData[1] == 0)
            return "暂时没车";
        if (isGoHome)
            return company2HomeStations[3 - busData[1]];
        else
            return home2CompanyStations[8 - busData[1]];
    }
    public static String unicodeToString(String str) {
        Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");
        Matcher matcher = pattern.matcher(str);
        char ch;
        while (matcher.find()) {
            ch = (char) Integer.parseInt(matcher.group(2), 16);
            str = str.replace(matcher.group(1), ch+"" );
        }
        return str;
    }
    private String removeTags(String str) {
        String temp = str;
        for (String tag:tagList)
            temp = temp.replaceAll(tag,"");
        return temp;
    }
    private void parseBus(String record) {
        Double len = 0.0;
        String[] strs = record.split(";");
        Log.d(TAG,"" + strs.length);
        int size = strs.length;
        if (size < 5) {
            busData[0] = 0;
            busData[1] = 0;
            busData[2] = 0;
        } else {
            busData[0] = Integer.parseInt(strs[size - 2]);
            busData[1] = Integer.parseInt(strs[size - 6]);
            len = Double.parseDouble(strs[size - 4]);
            if (strs[size - 3].startsWith("公里"))
                len *= 1000;

            busData[2] = len.intValue();
        }
        Message message = mHandler.obtainMessage();
        message.obj = busData;
        mHandler.sendMessage(message);
        Log.d(TAG,"Time is " + busData[0] + ",station is " + busData[1] + ",length is " + busData[2]);
    }
}
