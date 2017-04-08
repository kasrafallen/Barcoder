package ir.aravas.barcoder;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.ResponseHandlerInterface;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.ByteArrayEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;
import ir.aravas.barcoder.application.Application;
import ir.aravas.barcoder.instance.DataInstance;
import ir.aravas.barcoder.modelazhar.MessageAzharModel;
import ir.aravas.barcoder.modelshahram.MessageModel;
import ir.aravas.barcoder.modelshahram.UserModel;

public class Rest {
    private final static String CONSOLE_AZHAR = "http://vascosmos.com:9100";
    private final static String CONSOLE_SHAHRAM = "http://173.82.125.74:3001/api/v1";

    public final static String TAG = "//REST";

    private static final String APPLICATION_JSON = "application/json";

    public enum Method {
        SIGNUP,
        LOGIN,
        ACTIVATE,
        INVITE,
        SCAN,
        PROFILE
    }

    private static boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public interface CallBack {
        void onResponse(String data);

        void onInternet();

        void onError(String data);

        void onBefore();
    }

    public static void call(final Context context, final Method method, String json, final CallBack callBack) {
        if (!isConnected(context)) {
            Log.d(TAG, "onInternet() returned.");
            callBack.onInternet();
            return;
        }
        callBack.onBefore();

        String url;
        if (Application.IS_AZHAR) {
            url = CONSOLE_AZHAR;
        } else {
            url = CONSOLE_SHAHRAM;
        }
        switch (method) {
            case SIGNUP:
                if (Application.IS_AZHAR) {
                    url = url + "/api/createLead";
                } else {
                    url = url + "/auth/signup";
                }
                break;
            case LOGIN:
                if (Application.IS_AZHAR) {
                    url = url + "/rest/login";
                } else {
                    url = url + "/auth/signin";
                }
                break;
            case ACTIVATE:
                url = url + "/rest/activateUser";
                break;
            case PROFILE:
                if (Application.IS_AZHAR) {
                    url = url + "/api/devices/userId";
                } else {
                    UserModel userModel = new Gson().fromJson(json, UserModel.class);
//                    client.addHeader("Authorization", "Bearer " + userModel.getToken());
                    url = url + "/user/" + userModel.get_id() + "/me";
                }
                break;
            case INVITE:
                UserModel userModel = DataInstance.getUser(context);
//                client.addHeader("Authorization", "Bearer " + userModel.getToken());
                url = url + "/user/" + userModel.get_id() + "/invite";
                break;
            case SCAN:
                if (Application.IS_AZHAR) {
                    url = url + "/api/device";
                } else {
//                    UserModel userModel2 = DataInstance.getUser(context);
//                    client.addHeader("Authorization", "Bearer " + userModel2.getToken());
                    url = url + "/products/" + json + "/barcode";
                }
                break;
        }
        Log.d(TAG, "called: " + url + "  with: " + json);
        if (Application.IS_AZHAR) {
            AsyncHttpClient client = createClient();
            Log.d(TAG, "called: " + url);
            if (method == Method.SIGNUP || method == Method.SCAN) {
                client.put(context, url, createEntity(json), APPLICATION_JSON, generateHandler(callBack));
            } else {
                client.post(context, url, createEntity(json), APPLICATION_JSON, generateHandler(callBack));
            }
        } else {
            RequestQueue queue = Volley.newRequestQueue(context);
            if (method == Method.PROFILE || method == Method.SCAN) {
                StringRequestCustom request = null;
                request = new StringRequestCustom(Request.Method.GET, url
                        , generateHandlerVolley(callBack), generateHandlerVolleyError(callBack)) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        UserModel userModel = DataInstance.getUser(context);
                        params.put("Authorization", "Bearer " + userModel.getToken());
                        return params;
                    }
                };
                request.setShouldCache(false);
                queue.add(request);
//                client.get(url, generateHandler(callBack));
            } else {
                JsonObjectRequestCustom request;
                request = new JsonObjectRequestCustom(Request.Method.POST, url, json
                        , generateHandlerVolleyJson(callBack), generateHandlerVolleyError(callBack)) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        if (method == Rest.Method.INVITE) {
                            Map<String, String> params = new HashMap<>();
                            UserModel userModel = DataInstance.getUser(context);
                            params.put("Authorization", "Bearer " + userModel.getToken());
                            return params;
                        } else {
                            return super.getHeaders();
                        }
                    }
                };
                queue.add(request);
//                client.post(context, url, createEntity(json), APPLICATION_JSON, generateHandler(callBack));
            }
        }
    }

    private static Response.ErrorListener generateHandlerVolleyError(final CallBack callBack) {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error != null && error.networkResponse != null) {
                    Log.d(TAG, "onErrorResponse() returned: " + new String(error.networkResponse.data));
                    onError(error.networkResponse.data, error.getCause(), callBack);
                } else {
                    Log.d(TAG, "onErrorResponse() returned: " + new Gson().toJson(error));
                    onError(null, null, callBack);
                }
            }
        };
    }

    private static Response.Listener<String> generateHandlerVolley(final CallBack callBack) {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                observeResponse(response, callBack);
            }
        };
    }

    private static Response.Listener<JSONObject> generateHandlerVolleyJson(final CallBack callBack) {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String data = null;
                if (response != null) {
                    data = response.toString();
                } else {
                    data = null;
                }
                observeResponse(data, callBack);
            }
        };
    }

    private static void observeResponse(String response, CallBack callBack) {
        Log.d(TAG, "onResponse() returned: " + response);
        byte[] data = null;
        int code = 200;
        if (response != null) {
            try {
                MessageModel messageModel = new Gson().fromJson(response, MessageModel.class);
                if (messageModel != null) {
                    code = messageModel.getStatusCode();
                    if (code == 0) {
                        code = 200;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            data = response.getBytes();
        }
        Rest.onResponse(code, data, callBack, null, null);
    }

    private static ByteArrayEntity createEntity(String json) {
        ByteArrayEntity entity = null;
        try {
            entity = new ByteArrayEntity(json.getBytes("UTF-8"));
            entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, APPLICATION_JSON));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return entity;
    }

    private static ResponseHandlerInterface generateHandler(final CallBack callBack) {
        return new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                onResponse(statusCode, responseBody, callBack, this, headers);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                onError(responseBody, error, callBack);
            }
        };
    }

    private static void onError(byte[] responseBody, Throwable error, CallBack callBack) {
        if (responseBody != null && responseBody.length > 0) {
            Log.d(TAG, "onError() returned: " + new String(responseBody));
            if (Application.IS_AZHAR) {
                try {
                    String res = new String(responseBody);
                    MessageAzharModel model = new Gson().fromJson(res, MessageAzharModel.class);
                    if (model != null && model.getStatus() != null && model.getStatus().equals(MessageAzharModel.STATUS_FAILED)
                            && model.getMessage() != null) {
                        callBack.onError(model.getMessage());
                    } else {
                        callBack.onError(null);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    callBack.onError(null);
                }
            } else {
                try {
                    String res = new String(responseBody);
                    MessageModel model = new Gson().fromJson(res, MessageModel.class);
                    if (model != null && model.getMessage() != null) {
                        callBack.onError(model.getMessage());
                    } else {
                        callBack.onError(null);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    callBack.onError(null);
                }
            }
        } else {
            if (error != null) {
                Log.d(TAG, "onError() returned: " + error.getMessage());
            }
            callBack.onError(null);
        }
    }

    private static void onResponse(int statusCode, byte[] responseBody
            , CallBack callBack, AsyncHttpResponseHandler handler, Header[] headers) {
        if (statusCode == 202) {
            if (handler != null) {
                handler.onFailure(statusCode, headers, responseBody, new Throwable());
            } else {
                onError(responseBody, new Throwable(), callBack);
            }
        } else if (responseBody != null && responseBody.length > 0) {
            Log.d(TAG, "onResponse() returned: " + new String(responseBody));
            callBack.onResponse(new String(responseBody));
        } else if (statusCode == 200) {
            Log.d(TAG, "onResponse() returned: " + null);
            callBack.onResponse(null);
        }
    }

    private static AsyncHttpClient createClient() {
        AsyncHttpClient client = new AsyncHttpClient();
        client.setConnectTimeout(10000);
        client.setResponseTimeout(10000);
        client.setTimeout(10000);
        return client;
    }

    private static class StringRequestCustom extends StringRequest {
        public int statusCode;

        public StringRequestCustom(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
            super(method, url, listener, errorListener);
            setShouldCache(false);
        }

        @Override
        protected Response<String> parseNetworkResponse(NetworkResponse response) {
            statusCode = response.statusCode;
            return super.parseNetworkResponse(response);
        }
    }

    private static class JsonObjectRequestCustom extends JsonObjectRequest {
        public int statusCode;

        public JsonObjectRequestCustom(int method, String url, String requestBody, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
            super(method, url, requestBody, listener, errorListener);
            setShouldCache(false);
        }

        @Override
        protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
            statusCode = response.statusCode;
            return super.parseNetworkResponse(response);
        }
    }
}
