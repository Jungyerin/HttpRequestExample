package com.jx372.httprequestexample.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jx372.httprequestexample.R;
import com.jx372.httprequestexample.core.domain.GuestBook;
import com.jx372.httprequestexample.network.SafeAsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onFetchGuestbookClick(View view) {
        new FetchGuestbookListAsyncTask().execute();

    }


    //통신 결과를 담을 result클래스
    private class JSONResultFetchGuestbookList{
        private String result;
        private String message;
        private List<GuestBook> data;

        public String getResult() {
            return result;
        }

        public void setResult(String result) {
            this.result = result;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public List<GuestBook> getData() {
            return data;
        }

        public void setData(List<GuestBook> data) {
            this.data = data;
        }
    }

    /*통신 내부 클래스 하나당 api하나 */
    private class FetchGuestbookListAsyncTask extends SafeAsyncTask<List<GuestBook>>{

        @Override
        public List<GuestBook> call() throws Exception {
            //1. 요청세팅
            String url = "http://192.168.1.39:8088/mysite03/guestbook/api/list";
            HttpRequest request = HttpRequest.get(url);

            //"name=ddd&no=1"
            //request.contentType(HttpRequest.CONTENT_TYPE_FORM);

            //"{name:ddd,no:1}"
            //request.contentType(HttpRequest.CONTENT_TYPE_JSON);

            request.accept(HttpRequest.CONTENT_TYPE_JSON);
            request.connectTimeout(3000);
            request.readTimeout(3000);

            //2. 요청
            int responseCode = request.code();

            //3. 응답처리
            if(responseCode != HttpURLConnection.HTTP_OK){
                throw new RuntimeException("HTTP Response Error:"+responseCode);
            }
//            BufferedReader br = request.bufferedReader();
//            String json = "";
//            String line=null;
//
//            while((line=br.readLine())!=null){
//                json += line;
//            }
//
//            br.close();
            //4. gson을 사용한 객체 생성
            Reader reader = request.bufferedReader();
            JSONResultFetchGuestbookList jsonResult = new GsonBuilder().create().fromJson(reader, JSONResultFetchGuestbookList.class);

            //5.결과 에러 체크
            if("fail".equals(jsonResult.getResult())){
                throw new RuntimeException(jsonResult.getMessage());
            }
            return jsonResult.getData();
        }

        @Override
        protected void onException(Exception e) throws RuntimeException {
            Log.e("FetchGuestbookAsyncTask","Exception:"+e);
            super.onException(e);
        }

        @Override
        protected void onSuccess(List<GuestBook> list) throws Exception {
            super.onSuccess(list);

            //결과 처리
            for(GuestBook guestBook : list) {
                System.out.println(list);
            }
        }
    }
}
