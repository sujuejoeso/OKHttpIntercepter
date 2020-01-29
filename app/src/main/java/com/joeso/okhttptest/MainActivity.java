package com.joeso.okhttptest;

import androidx.appcompat.app.AppCompatActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import org.json.JSONObject;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;

import okio.BufferedSource;

public class MainActivity extends AppCompatActivity {

    TextView tvResult;
    EditText txtUrl;
    Button bnGet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtUrl=findViewById(R.id.txt_url);
        tvResult=findViewById(R.id.tv_result);
        bnGet=findViewById(R.id.bn_get);
        bnGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MyAsyncTask().execute(txtUrl.getText().toString());
            }
        });
    }

    private class MyAsyncTask extends AsyncTask <String,Object,String>{
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new LoggerInterceptor()).addInterceptor(new MyInterceptor()).build();
        String outcome;

        @Override
        protected String doInBackground(String... urls) {
            Map map = new HashMap();
            map.put("code", "1111");
            map.put("phone", "0405060781");
            JSONObject jsonObject = new JSONObject(map);
            String json = jsonObject.toString();

            Request request = new Request.Builder()
                    .url("http://fitstop.pixelforcesystems.com.au/api/v1/auth/sign_out")
                    .post(RequestBody.create(json,MediaType.parse("application/json") ))
                    .build();
            try {
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (IOException e) {
                return e.getMessage();
            }
        }
        @Override
        protected void onPostExecute(String result) {
            tvResult.setText(result);
        }
    }
}

/**
 *  A simple intercepter for tesing
 */
class MyInterceptor implements Interceptor {
    private String newUrl="http://fitstop.pixelforcesystems.com.au/api/v1/auth/sign_in";

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder requestBuilder = chain.request().newBuilder();
        requestBuilder.url(newUrl);
        FormBody.Builder bodyBuilder = new FormBody.Builder();
        bodyBuilder.add("code","2222");
        bodyBuilder.add("phone","0405060782");
        FormBody newBody = bodyBuilder.build();
        requestBuilder.post(newBody);
        return chain.proceed(requestBuilder.build());//returns a response
    }
}

//==================================================================================================

class LoggerInterceptor implements Interceptor {
    public static final String TAG = "jjjj";

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();  //chain.request()截获 request
        printRequestMessage(request);
        Response response = chain.proceed(request); //chain.proceed(request)方法截获 response
        printResponseMessage(response);
        return response;
    }

    /**
     Print request
     */
    private void printRequestMessage(Request request) {
        if (request == null) {
            return;
        }
        Log.e(TAG, "-----------------------Request intercepted-----------------------");
        Log.e(TAG, "Url : " + request.url().url().toString());
        Log.e(TAG, "Method: " + request.method());
        Log.e(TAG, "Heads : " + request.headers());
        RequestBody requestBody = request.body();
        if (requestBody == null) {
            return;
        }
        try {
            request.body().toString();
            Buffer bufferedSink = new Buffer();
            requestBody.writeTo(bufferedSink);
            Charset charset = requestBody.contentType().charset();
            charset = charset == null ? Charset.forName("utf-8") : charset;
            Log.e(TAG, "Params: " + bufferedSink.readString(charset));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
        Print Response
     */
    private void printResponseMessage(Response response) {
        if (response == null) {
            Log.e(TAG, "======================== Respond = null ========================");
            return;
        }
        Log.e(TAG, "===========================Respond intercepted=========================");
        ResponseBody responseBody = response.body();
        long contentLength = responseBody.contentLength();
        BufferedSource source = responseBody.source();
        try {
            source.request(Long.MAX_VALUE); // Buffer the entire body.
        } catch (IOException e) {
            e.printStackTrace();
        }
        Buffer buffer = source.buffer();
        Charset charset=Charset.defaultCharset();
        MediaType contentType = responseBody.contentType();
        if (contentType != null) {
            charset = contentType.charset(Charset.forName("utf-8"));
        }
        if (contentLength != 0) {
            String result = buffer.clone().readString(charset);
            Log.e(TAG, "========================response headers ========================");
            Log.e(TAG, "Head: " + response.headers());
            Log.e(TAG, "=========================response body ==========================");
            Log.e(TAG, "body: " + result);
        }
    }
}