package com.julien.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import net.sf.json.JSONString;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    Button button = null;
    Button button2 = null;
    TextView textView = null;
    EditText user = null;
    EditText pwd = null;
    String userIndex  = null;
    TextView showInFo = null;
    String []data =new String[5];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
    // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.planets_array, android.R.layout.simple_spinner_item);
    // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
     // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               String text=(String) spinner.getSelectedItem();
               data[2]= text;
               data[4] = String.valueOf(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        button = this.findViewById(R.id.btn);
        button2 = this.findViewById(R.id.btn_logout);
        textView = this.findViewById(R.id.text);
        showInFo = this.findViewById(R.id.info);

        user = this.findViewById(R.id.user);
        pwd = this.findViewById(R.id.pwd);

        String []shareData =  getData();
        if ("null".equals(shareData[0])){
            spinner.setSelection(2,true);
        }else {
            user.setText(shareData[0]);
            pwd.setText(shareData[1]);
            spinner.setSelection(Integer.parseInt(shareData[4]),true);
        }

        getUserInFo();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data[0] = String.valueOf(user.getText());
                data[1] = String.valueOf(pwd.getText());
                data[2]=(String) spinner.getSelectedItem();
                savedata(data);
                System.out.println(data[0]+data[1]+data[2]);
                myHttp(data);
            }
        });


        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               logout();
            }
        });
    }

    // 保存数据
   void  savedata(String []data){
       //获取SharedPreferences对象
       Context ctx = MainActivity.this;
       SharedPreferences sp = ctx.getSharedPreferences("SP", MODE_PRIVATE);
       //存入数据
       SharedPreferences.Editor editor = sp.edit();
       editor.putString("username", data[0]);
       editor.putString("password", data[1]);
       editor.putString("service", data[2]);
       editor.putString("isSave",data[3]);
       editor.putString("serviceId",data[4]);
       editor.commit();
       //返回account的值,如果没有就返回默认的empty
       Log.d("SP", sp.getString("account", "empty"));

   }

    /**
     * 退出登录
     */
    void logout(){
       //创建OkHttpClient对象
       OkHttpClient okHttpClient = new OkHttpClient.Builder()
               .connectTimeout(10, TimeUnit.SECONDS)
               .writeTimeout(10, TimeUnit.SECONDS)
               .readTimeout(20, TimeUnit.SECONDS)
               .build();
       //post方式提交的数据
       String params = "queryString=wlanuserip%253D57d35c239b2324316cd24c6e51f95581%2526wlanacname%253D86aeba9fa75c3397517ce58fe6aca3a1%2526ssid%253D%2526nasip%253D4826f7b7351dd62c90efb453f599d425%2526snmpagentip%253D%2526mac%253D511fa98d409af714ebee766c3186de09%2526t%253Dwireless-v2%2526url%253D2c0328164651e2b4f13b933ddf36628bea622dedcc302b30%2526apmac%253D%2526nasid%253D86aeba9fa75c3397517ce58fe6aca3a1%2526vid%253D0876c30aa757a4d0%2526port%253D5dc4946d64dba368%2526nasportid%253D5b9da5b08a53a540de3a39287dd9a647b4e8dc0881e46769a6625c1012b5c045&operatorPwd=&operatorUserId=&validcode=&passwordEncrypt=false";
       RequestBody body = RequestBody.create(MediaType.parse("application/x-www-form-urlencoded;charset=utf-8"),
               params);
       final Request request = new Request.Builder()
               .url("http://auth.ysu.edu.cn/eportal/InterFace.do?method=logout")  //请求的url
               .post(body)//设置请求方式，get()/post()  查看Builder()方法知，在构建时默认设置请求方式为GET
               .build(); //构建一个请求Request对象
       String rep = null;
       //创建/Call
       Call call = okHttpClient.newCall(request);
       call.enqueue(new Callback() {
           //请求错误回调方法
           @Override
           public void onFailure(Call call, IOException e) {
               e.printStackTrace();
               System.out.println("连接失败");
           }
           //异步请求(非主线程)
           @Override
           public void onResponse(Call call, Response response) throws IOException {
               if (response.code() == 200) {
                   final String re = response.body().string();
                   System.out.println(re);
                   new Thread(new Runnable() {
                       public void run() {
                           textView.post(new Runnable() {
                               @Override
                               public void run() {
                                   try {
                                       JSONObject jsonObject = new JSONObject(re);
                                       String  result = jsonObject.get("result").toString();
                                      if("success".equals(result)){
                                          showInFo.setText("退出成功");
                                      }else{
                                          showInFo.setText("退出失败");
                                      }
                                   } catch (JSONException e) {
                                       e.printStackTrace();
                                   }
                               }
                           });
                       }
                   }).start();
               }
           }
       });
   }

    /**
     *
     * 获取当前状态
     */
    void getUserInFo(){

        //创建OkHttpClient对象
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build();
        //post方式提交的数据
        String params = "queryString=wlanuserip%253D57d35c239b2324316cd24c6e51f95581%2526wlanacname%253D86aeba9fa75c3397517ce58fe6aca3a1%2526ssid%253D%2526nasip%253D4826f7b7351dd62c90efb453f599d425%2526snmpagentip%253D%2526mac%253D511fa98d409af714ebee766c3186de09%2526t%253Dwireless-v2%2526url%253D2c0328164651e2b4f13b933ddf36628bea622dedcc302b30%2526apmac%253D%2526nasid%253D86aeba9fa75c3397517ce58fe6aca3a1%2526vid%253D0876c30aa757a4d0%2526port%253D5dc4946d64dba368%2526nasportid%253D5b9da5b08a53a540de3a39287dd9a647b4e8dc0881e46769a6625c1012b5c045&operatorPwd=&operatorUserId=&validcode=&passwordEncrypt=false";
        RequestBody body = RequestBody.create(MediaType.parse("application/x-www-form-urlencoded;charset=utf-8"),
                params);
        final Request request = new Request.Builder()
                .url("http://auth.ysu.edu.cn/eportal/InterFace.do?method=getOnlineUserInfo")  //请求的url
                .post(body)//设置请求方式，get()/post()  查看Builder()方法知，在构建时默认设置请求方式为GET
                .build(); //构建一个请求Request对象
        String rep = null;
        //创建/Call
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            //请求错误回调方法
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                System.out.println("连接失败");
            }

            //异步请求(非主线程)
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() == 200) {
                    final String re = response.body().string();
                    System.out.println(re);
                    new Thread(new Runnable() {
                        public void run() {
                            showInFo.post(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        String  result =null;
                                        JSONObject jsonObject = new JSONObject(re);
                                        if ("success".equals(jsonObject.get("result").toString())){
                                            result = jsonObject.get("userName").toString()+jsonObject.get("service").toString()+"在线！！";
                                            showInFo.setTextColor(Color.rgb(25,255,2));
                                        }else {
                                            showInFo.setTextColor(Color.rgb(255,2,2));
                                            result = "未登录或者获取失败";
                                        }
                                        showInFo.setText(result);

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    }).start();
                }else {
                    new Thread(new Runnable() {
                        public void run() {
                            showInFo.post(new Runnable() {
                                @Override
                                public void run() {

                                    showInFo.setTextColor(Color.rgb(255,2,2));
                                    String  result = "获取失败";
                                    showInFo.setText(result);
                                }
                            });
                        }
                    }).start();
                }
            }
        });
    }


    /**
     * 读取xml数据
     * @return
     */
    String [] getData(){
         Context ctx = MainActivity.this;
         SharedPreferences sp = ctx.getSharedPreferences("SP", MODE_PRIVATE);
         return new String[]{
                 sp.getString("username", "null"),
                sp.getString("password", "null"),
                sp.getString("service", "null"),
                sp.getString("isSave", "null"),
                sp.getString("serviceId", "null")
         };
     }


    /**
     * url 编码
     * @param url
     * @return
     */
    private  String encode(String url)
    {

        try {
            String encodeURL= URLEncoder.encode( url, "UTF-8" );

            return encodeURL;

        } catch (UnsupportedEncodingException e) {

            return "Issue while encoding" +e.getMessage();

        }

    }


    /**
     * 登录模块
     * @param data
     */
    void myHttp(String[] data) {
        String encodeService = encode(encode(data[2]));
        //创建OkHttpClient对象
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build();
        //post方式提交的数据
        String params = "userId="+data[0]+"&password="+data[1]+"&service="+encodeService+"&queryString=wlanuserip%253D57d35c239b2324316cd24c6e51f95581%2526wlanacname%253D86aeba9fa75c3397517ce58fe6aca3a1%2526ssid%253D%2526nasip%253D4826f7b7351dd62c90efb453f599d425%2526snmpagentip%253D%2526mac%253D511fa98d409af714ebee766c3186de09%2526t%253Dwireless-v2%2526url%253D2c0328164651e2b4f13b933ddf36628bea622dedcc302b30%2526apmac%253D%2526nasid%253D86aeba9fa75c3397517ce58fe6aca3a1%2526vid%253D0876c30aa757a4d0%2526port%253D5dc4946d64dba368%2526nasportid%253D5b9da5b08a53a540de3a39287dd9a647b4e8dc0881e46769a6625c1012b5c045&operatorPwd=&operatorUserId=&validcode=&passwordEncrypt=false";
        RequestBody body = RequestBody.create(MediaType.parse("application/x-www-form-urlencoded;charset=utf-8"),
                params);
        final Request request = new Request.Builder()
                .url("http://auth.ysu.edu.cn/eportal/InterFace.do?method=login")  //请求的url
                .post(body)//设置请求方式，get()/post()  查看Builder()方法知，在构建时默认设置请求方式为GET
                .build(); //构建一个请求Request对象
        String rep = null;
        //创建/Call
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            //请求错误回调方法
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                System.out.println("连接失败");
            }
            //异步请求(非主线程)
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() == 200) {
                    final String re = response.body().string();
                    System.out.println(re);
                    new Thread(new Runnable() {
                        public void run() {
                            textView.post(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        JSONObject jsonObject = new JSONObject(re);
                                        String  result = jsonObject.get("result").toString();
                                        showInFo.setText(result);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    }).start();
                }
            }
        });

    }

}