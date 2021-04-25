package com.julien.app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
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
    EditText service = null;
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
        spinner.setSelection(2,true);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        button = this.findViewById(R.id.btn);
        button2 = this.findViewById(R.id.btn_logout);
        textView = this.findViewById(R.id.text);
        user = this.findViewById(R.id.user);
        pwd = this.findViewById(R.id.pwd);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myHttp("201811050496", "", "中国电信");
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                myHttp("201811050496", "", "中国电信");
                            }
                        });
                    }
                }).start();
            }
        });
    }
    private  String encode(String url)
    {

        try {

            String encodeURL= URLEncoder.encode( url, "UTF-8" );

            return encodeURL;

        } catch (UnsupportedEncodingException e) {

            return "Issue while encoding" +e.getMessage();

        }

    }
    void myHttp(String username,String password,String service) {
        String encodeService = encode(encode(service));
        //创建OkHttpClient对象
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build();
        //post方式提交的数据
        String queryString = "wlanuserip=57d35c239b2324316cd24c6e51f95581&wlanacname=86aeba9fa75c3397517ce58fe6aca3a1&ssid=&nasip=4826f7b7351dd62c90efb453f599d425&snmpagentip=&mac=511fa98d409af714ebee766c3186de09&t=wireless-v2&url=2c0328164651e2b4f13b933ddf36628bea622dedcc302b30&apmac=&nasid=86aeba9fa75c3397517ce58fe6aca3a1&vid=0876c30aa757a4d0&port=5dc4946d64dba368&nasportid=5b9da5b08a53a540de3a39287dd9a647b4e8dc0881e46769a6625c1012b5c045&operatorPwd=&operatorUserId=&validcode=&passwordEncrypt=false";
        FormBody formBody = new FormBody.Builder()
                .add("userId", username)
                .add("password", password)
                .add("service", service)
                .add("queryString", queryString)
                .build();
        String params = "userId=201811050496&password=&service=%25E4%25B8%25AD%25E5%259B%25BD%25E7%2594%25B5%25E4%25BF%25A1&queryString=wlanuserip%253D57d35c239b2324316cd24c6e51f95581%2526wlanacname%253D86aeba9fa75c3397517ce58fe6aca3a1%2526ssid%253D%2526nasip%253D4826f7b7351dd62c90efb453f599d425%2526snmpagentip%253D%2526mac%253D511fa98d409af714ebee766c3186de09%2526t%253Dwireless-v2%2526url%253D2c0328164651e2b4f13b933ddf36628bea622dedcc302b30%2526apmac%253D%2526nasid%253D86aeba9fa75c3397517ce58fe6aca3a1%2526vid%253D0876c30aa757a4d0%2526port%253D5dc4946d64dba368%2526nasportid%253D5b9da5b08a53a540de3a39287dd9a647b4e8dc0881e46769a6625c1012b5c045&operatorPwd=&operatorUserId=&validcode=&passwordEncrypt=false";
        RequestBody body = RequestBody.create(MediaType.parse("application/x-www-form-urlencoded;charset=utf-8"),
                params);
        Map<String, String> h = new HashMap<>();
        final Request request = new Request.Builder()
                .url("http://auth.ysu.edu.cn/eportal/InterFace.do?method=login")  //请求的url
                .post(body)//设置请求方式，get()/post()  查看Builder()方法知，在构建时默认设置请求方式为GET
                .build(); //构建一个请求Request对象

        String rep = null;
        //创建/Call
        Call call = okHttpClient.newCall(request);
        //加入队列 异步操作

        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }
        };

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
                    String re = response.body().string();
                    new Thread(new Runnable() {
                        public void run() {
                            textView.post(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        JSONObject jsonObject = new JSONObject(re);
                                      String  result = jsonObject.get("result").toString();
                                        textView.setText(result);
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