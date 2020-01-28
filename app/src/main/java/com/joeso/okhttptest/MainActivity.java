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

import okhttp3.Call;
import okhttp3.Callback;
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
    String url="https://raw.github.com/square/okhttp/master/README.md";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtUrl=findViewById(R.id.txt_url);
        txtUrl.setText(url);
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
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new LoggerInterceptor()).build();
        String outcome;

        @Override
        protected String doInBackground(String... urls) {
            Map map = new HashMap();
            map.put("code", "1111");
            map.put("phone", "0405060781");
            JSONObject jsonObject = new JSONObject(map);
            String json = jsonObject.toString();

            Request request = new Request.Builder()
                    .url("http://fitstop.pixelforcesystems.com.au/api/v1/auth/sign_in")
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

class LoggerInterceptor implements Interceptor {

    public static final String TAG = "jjjj";

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        printRequestMessage(request);
        Response response = chain.proceed(request);
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
        Log.e(TAG, "Url : " + request.url().url().toString());
        Log.e(TAG, "Method: " + request.method());
        Log.e(TAG, "Heads : " + request.headers());
        RequestBody requestBody = request.body();
        if (requestBody == null) {
            return;
        }
        try {
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
            return;
        }
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

            Log.e(TAG, "Head: " + response.headers());
            Log.e(TAG, "body: " + result);
        }
    }
}



//class InterceptRequest implements Interceptor {
//
//    private static final String NEW_URL = "http://www.google.com";
//    @Override
//    public Response intercept(Chain chain) throws IOException {
//
//        Request.Builder requestBuilder = chain.request().newBuilder();
//        //adding a header to the original request
//        //requestBuilder.addHeader("joe","Intercepted");
//        //changing the URL
//        requestBuilder.url(NEW_URL);
//        //returns a response
//        return chain.proceed(requestBuilder.build());
//    }
//}