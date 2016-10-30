package cz.petrkubes.payuback.Api;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.lang.reflect.Array;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class ApiRestClient {
    private static final String BASE_URL = "http://192.168.2.71/payuback-api/www/api/";

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler, ArrayList<cz.petrkubes.payuback.Structs.Header> headers) {

        for (cz.petrkubes.payuback.Structs.Header header : headers) {
            client.addHeader(header.name, header.key);
        }

        client.get(getAbsoluteUrl(url), params, responseHandler);
    }


    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}
