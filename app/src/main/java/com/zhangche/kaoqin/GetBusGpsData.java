package com.zhangche.kaoqin;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import org.apache.http.HttpResponse;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class GetBusGpsData {
    final String TAG = "zhangche";
    String company2Home = "http://www.bjbus.com/home/ajax_rtbus_data.php?act=busTime&selBLine=523&selBDir=5074871549194769576&selBStop=";//4
    String home2Company = "http://www.bjbus.com/home/ajax_rtbus_data.php?act=busTime&selBLine=523&selBDir=5592193305562839972&selBStop=";//9
    public boolean need_exit = false;
    double myLatitude = 0.0;
    double myLongitude = 0.0;
    Context mContext;
    Handler mHandler;
    LocationManager locationManager;
    String[] tagList = {
            "<[.[^>]]*>",
            " ",
    };
    BusInfo bus;
    public class StationInfo {
        int id;
        String name;
        double latitude;
        double longitude;
        int distance2Start;
        int cross2Start;
        public StationInfo(int id, String name,double latitude,double longitude,int distance2Start,int cross2Start) {
            this.id = id;
            this.name = name;
            this.latitude = latitude;
            this.longitude = longitude;
            this.distance2Start = distance2Start;
            this.cross2Start = cross2Start;
        }
    }

    public class BusInfo {
        StationInfo myStation = noStation;
        StationInfo busStation = noStation;
        int distance = 0;
        int arriveTime = 0;
        boolean isGoHome = false;
    }
    StationInfo[] home2CompanyStations = {
           new StationInfo(1,"科创十六街",39.7757620512,116.5724921037,0,0),
           new StationInfo(2,"科创十五街",39.7775760797,116.5699600984,350,1),
           new StationInfo(3,"科创十四街",39.7773133266,116.5629464117,1100,3),
           new StationInfo(4,"经海三路",39.7774617455,116.5596848455,1600,3),
           new StationInfo(5,"科创十三街",39.7798858757,116.5608221021,1900,3),
           new StationInfo(6,"地铁经海路站",39.7839094162,116.5641480413,2600,5),
           new StationInfo(7,"经海五路",39.7900363526,116.5656396763,3600,7),
           new StationInfo(8,"经海七路",39.7943395775,116.5694376842,4400,9),
           new StationInfo(9,"定海园",39.7977289125,116.5643449680,5100,11),
           new StationInfo(10,"科创九街",39.7915385132,116.5476637556,6900,15),
           new StationInfo(11,"生物医药园西",39.8010054013,116.5397522308,8100,18),
           new StationInfo(12,"科创六街",39.8015659004,116.5371183022,8500,19),
           new StationInfo(13,"经海二路",39.8024931533,116.5341211447,8800,19),
           new StationInfo(14,"荣京东街东口",39.7991823400,116.5228663577,10100,22),
           new StationInfo(15,"永昌北路",39.7968001041,116.5178720845,10600,23),
           new StationInfo(16,"永昌中路北口",39.7943608063,116.5184683349,10950,24),
           new StationInfo(17,"兴盛街西口",39.7919000878,116.5166068819,11300,25),
           new StationInfo(18,"地铁荣京东街",39.7930740271,116.5139571103,11700,25),
    };
    StationInfo[] company2HomeStations = {
            new StationInfo(1,"地铁荣京东街",39.7930740271,116.5139571103,0,0),
            new StationInfo(2,"永昌北路",39.7968001041,116.5178720845,643,1),
            new StationInfo(3,"荣京东街东口",39.7991823400,116.5228663577,1200,2),
            new StationInfo(4,"经海二路",39.8024931533,116.5341211447,2500,4),
            new StationInfo(5,"科创六街",39.8015659004,116.5371183022,2800,5),
            new StationInfo(6,"生物医药园西",39.8010054013,116.5397522308,3100,5),
            new StationInfo(7,"科创九街",39.7915385132,116.5476637556,4400,8),
            new StationInfo(8,"定海园",39.7977289125,116.5643449680,6100,13),
            new StationInfo(9,"经海七路",39.7943395775,116.5694376842,6700,14),
            new StationInfo(10,"经海五路",39.7900363526,116.5656396763,7500,15),
            new StationInfo(11,"地铁经海路站",39.7839094162,116.5641480413,8600,17),
            new StationInfo(12,"科创十三街",39.7798858757,116.5608221021,9300,19),
            new StationInfo(13,"经海三路",39.7774617455,116.5596848455,9600,20),
            new StationInfo(14,"科创十四街",39.7773133266,116.5629464117,10000,21),
            new StationInfo(15,"科创十五街",39.7775760797,116.5699600984,10900,22),
            new StationInfo(16,"科创十六街",39.7757620512,116.5724921037,11300,24),
    };
    StationInfo noStation = new StationInfo(0,"没找到", 0,0,0,0);

    public GetBusGpsData(Context context,Handler handler) {
        mHandler = handler;
        mContext = context;
        getLngAndLat(mContext);
        bus = new BusInfo();
    }

    public void run() {
        new Thread() {
            @Override
            public void run() {
                try {
                    while (true) {
                        if (need_exit)
                            break;
                        getMyStation();
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
        HttpGet httpget = new HttpGet((bus.isGoHome ? company2Home : home2Company) +bus.myStation.id);
        HttpResponse httpresponse;

        httpresponse = new DefaultHttpClient().execute(httpget);
        if (httpresponse.getStatusLine().getStatusCode() == 200) {

            String s = unicodeToString(EntityUtils.toString(httpresponse.getEntity()));
            String s1 = s.replaceAll("&nbsp", "");
            s = s1.replace("><div", ">\n<div");
            String[] temp = s.split("\n");
            for (String str : temp) {
                if (str.contains("分段计价"))
                    s = str;
            }
            parseBus(removeTags(s));
            Log.d(TAG, removeTags(s));
        }
    }

    public static String unicodeToString(String str) {
        Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");
        Matcher matcher = pattern.matcher(str);
        char ch;
        while (matcher.find()) {
            ch = (char) Integer.parseInt(matcher.group(2), 16);
            str = str.replace(matcher.group(1), ch + "");
        }
        return str;
    }

    private String removeTags(String str) {
        String temp = str;
        for (String tag : tagList)
            temp = temp.replaceAll(tag, "");
        return temp;
    }

    private void parseBus(String record) {
        Double len = 0.0;
        String[] strs = record.split(";");
        int size = strs.length;
        if (size < 5) {
            bus.arriveTime = 0;
            bus.busStation = noStation;
            bus.distance = 0;
        } else {
            bus.arriveTime = Integer.parseInt(strs[size - 2]);
            bus.busStation = getStationFromId(bus.myStation.id - Integer.parseInt(strs[size - 6]));
            len = Double.parseDouble(strs[size - 4]);
            if (strs[size - 3].startsWith("公里"))
                len *= 1000;

            bus.distance = len.intValue();
        }
        Message message = mHandler.obtainMessage();
        message.obj = bus;
        mHandler.sendMessage(message);
        Log.d(TAG, "Time is " + bus.arriveTime + ",station is " + bus.busStation.name + ",length is " + bus.distance);
    }

    private StationInfo getStationFromId(int id) {
        return bus.isGoHome ? company2HomeStations[id -1] : home2CompanyStations[id - 1];
    }
    public void getMyStation() {
        float distance = 5000;
        StationInfo targetStation = company2HomeStations[0];
        float[] result = new float[3];
        for (int i = 0;i<company2HomeStations.length; i++) {
            Location.distanceBetween(myLatitude,myLongitude,company2HomeStations[i].latitude,company2HomeStations[i].longitude,result);
//            Log.d(TAG,"distance to " + company2HomeStations[i].name + " is " + result[0]);
            if (distance > result[0]) {
                distance = result[0];
                targetStation = company2HomeStations[i];
            }
        }
        bus.myStation = targetStation;
    }

    private void getOppositeBus() throws Exception{
        //TODO
        HttpGet httpget = new HttpGet((!bus.isGoHome ? (company2Home + "18") : (home2Company + "16")));
        HttpResponse httpresponse;

        httpresponse = new DefaultHttpClient().execute(httpget);
        if (httpresponse.getStatusLine().getStatusCode() == 200) {

            String s = unicodeToString(EntityUtils.toString(httpresponse.getEntity()));
            String s1 = s.replaceAll("&nbsp", "");
            s = s1.replace("><div", ">\n<div");
            String[] temp = s.split("\n");
            for (String str : temp) {
                if (str.contains("分段计价"))
                    s = str;
            }
            Double len = 0.0;
            String[] strs = removeTags(s).split(";");

            int size = strs.length;
            if (size > 5) {
                bus.arriveTime = Integer.parseInt(strs[size - 2]);
                bus.busStation = getStationFromId(bus.myStation.id - Integer.parseInt(strs[size - 6]));
                len = Double.parseDouble(strs[size - 4]);
                if (strs[size - 3].startsWith("公里"))
                    len *= 1000;

                bus.distance = len.intValue();
            }
            Log.d(TAG, "Time is " + bus.arriveTime + ",station is " + bus.busStation.name + ",length is " + bus.distance);
        }
    }
    //For Current user location
    private void getLngAndLat(Context context) {
        Log.d(TAG, "get location start ");
        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "onCreate: 没有权限 ");
            return;
        }
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            //从gps获取经纬度
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 1, locationListener);
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
                myLatitude = location.getLatitude();
                myLongitude = location.getLongitude();
                return;
            }
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 1, locationListener);
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (location != null) {
            myLatitude = location.getLatitude();
            myLongitude = location.getLongitude();
        }
    }

    LocationListener locationListener = new LocationListener() {

        @Override
        public void onStatusChanged(String provider, int status, Bundle arg2) {

        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.d(TAG, "onProviderEnabled: " + provider + ".." + Thread.currentThread().getName());
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.d(TAG, "onProviderDisabled: " + provider + ".." + Thread.currentThread().getName());
        }

        @Override
        public void onLocationChanged(Location location) {
            Log.d(TAG, "onLocationChanged: " + ".." + Thread.currentThread().getName());
            //如果位置发生变化,重新显示
            Log.d(TAG,"定位成功------->"+"location------>经度为：" + location.getLatitude() + "\n纬度为" + location.getLongitude());
            myLatitude = location.getLatitude();
            myLongitude = location.getLongitude();
        }
    };
}
