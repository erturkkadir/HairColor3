package com.syshuman.kadir.haircolor3.model;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.syshuman.kadir.haircolor3.R;
import com.syshuman.kadir.haircolor3.eventbus.BoardingEvents;
import com.syshuman.kadir.haircolor3.eventbus.MessageEvents;
import com.syshuman.kadir.haircolor3.view.activities.MainActivity;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RestServer {

    private String baseUrl  = "http://hcapi.free-estimation.com/";
    private Context context;
    private String um_token = "298347decdd47d3ea70361c3acef225a";

    public RestServer(Context context) {
        this.context = context;
    }

    public void login(final String uname, final String upass) {
        String url = this.baseUrl + "api/v1/users/login";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject json = new JSONObject(response);
                            String statusCode = json.getString("status_code");
                            String message = json.getString("message");
                            JSONObject data = json.getJSONObject("data");
                            if(statusCode.equals("200 OK")) {
                                EventBus.getDefault().post(new BoardingEvents.onLoginSuccess("OK"));
                                saveUser(data);

                            } else {
                                EventBus.getDefault().post(new BoardingEvents.onLoginFailed(statusCode.toString()));
                                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "Please double check username and password (login)", Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("um_email", uname);
                params.put("um_upass", upass);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("um_token", um_token);
                return params;
            }

        };

        RequestQueue _requestQueue = Volley.newRequestQueue(context);
        _requestQueue.add(stringRequest);
    }

    private void saveUser(JSONObject data) {
        try {
            JSONObject userm = data.getJSONObject("userm");
            Integer um_no = userm.getInt("um_no");
            Integer cm_no = userm.getInt("cm_no");
            String um_email = userm.getString("um_email");
            String um_uname = userm.getString("um_uname");
            String um_token = userm.getString("um_token");
            SharedPreferences prefs = context.getSharedPreferences("com.syshuman.kadir.socks", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("um_no", um_no);
            editor.putInt("cm_no", cm_no);
            editor.putString("um_email", um_email);
            editor.putString("um_uname", um_uname);
            editor.putString("um_token", um_token);
            editor.apply();

        } catch(JSONException e) {
            e.printStackTrace();
        }
    }

    public void register(final Context context, final String uname, final String upass, final String fname, final String lname, final String devId, final int tz) {

        String url   = baseUrl + "api/v1/users/register"; // ?um_email=" + uname + "&um_upass=" + upass;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject json = new JSONObject(response);
                            String statusCode = json.getString("status_code");
                            if(!statusCode.equals("200 OK")) {
                                Toast.makeText(context, "Http communication failure " + statusCode, Toast.LENGTH_LONG).show();
                                EventBus.getDefault().post(new BoardingEvents.onRegistrationFailed("Communication Error"));
                                return;
                            }
                            String message = json.getString("message");
                            if(message.equals("Success")) {
                                EventBus.getDefault().post(new BoardingEvents.onRegisterSuccess("OK"));
                            } else {
                                EventBus.getDefault().post(new BoardingEvents.onRegistrationFailed(statusCode));
                                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "POST api/v1/users/register failed", Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("um_uname", uname);
                params.put("um_upass", upass);
                params.put("um_fname", fname);
                params.put("um_lname", lname);
                params.put("um_devid", devId);
                params.put("um_devtype", "android");
                params.put("um_timezone", String.valueOf(tz));
                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("um_token", um_token);
                return params;
            }

        };

        RequestQueue _requestQueue = Volley.newRequestQueue(context);
        _requestQueue.add(stringRequest);
    }

    public void getNames(final Context context,  final Spinner spColor) {
        String url   = baseUrl + "api/v1/users/getnames";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject json = new JSONObject(response);
                            String statusCode = json.getString("status_code");
                            if(!statusCode.equals("200 OK")) {
                                Toast.makeText(context, "Http Communication Error" + statusCode, Toast.LENGTH_LONG ).show();
                                return;
                            }
                            String message = json.getString("message");
                            JSONArray jsonArray = json.getJSONArray("data");

                            if(message.equals("Success")) {
                                Toast.makeText(context, "Color Obtained from server", Toast.LENGTH_LONG).show();

                                List<String> label = new ArrayList<>();
                                if (jsonArray != null) {
                                    for (int i=0;i<jsonArray.length();i++){
                                        JSONObject inner = new JSONObject(jsonArray.get(i).toString() );
                                        String names = inner.getString("lc_code");
                                        label.add(names);
                                    }
                                }

                                ArrayAdapter<String> rtAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, label);
                                rtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spColor.setAdapter(rtAdapter);
                            } else {
                                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "POST api/v1/users/getnames failed", Toast.LENGTH_LONG).show();
                    }
                }){

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("um_token", um_token);
                return params;
            }

        };

        RequestQueue _requestQueue = Volley.newRequestQueue(context);
        _requestQueue.add(stringRequest);
    }

    public void getColor3(final String r_r, final String r_g, final String r_b, final String r_c,
                          final String g_r, final String g_g, final String g_b, final String g_c,
                          final String b_r, final String b_g, final String b_b, final String b_c,
                          final String a_r, final String a_g, final String a_b, final String a_c,
                          final String company, final String catalog, final String zone, final int pow  ) {

        String url   = baseUrl + "api/v1/users/getcolor3";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject json = new JSONObject(response);
                            String statusCode = json.getString("status_code");
                            if(!statusCode.equals("200 OK")) {
                                Toast.makeText(context, "Http Communication Error" + statusCode, Toast.LENGTH_LONG ).show();
                                return;
                            }
                            String message = json.getString("message");
                            JSONArray jsonArray = json.getJSONArray("data");
                            if(message.equals("Success")) {
                                if (jsonArray != null) {
                                    EventBus.getDefault().post(new MessageEvents.onGetColor(jsonArray, zone));
                                    Toast.makeText(context, "Color Obtained from server", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(context, "data is null from server", Toast.LENGTH_LONG).show();
                                }
                            }else {
                                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "POST api/v1/users/getcolor3 failed", Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("r_r", r_r);
                params.put("r_g", r_g);
                params.put("r_b", r_b);
                params.put("r_c", r_c);

                params.put("g_r", g_r);
                params.put("g_g", g_g);
                params.put("g_b", g_b);
                params.put("g_c", g_c);

                params.put("b_r", b_r);
                params.put("b_g", b_g);
                params.put("b_b", b_b);
                params.put("b_c", b_c);

                params.put("a_r", a_r);
                params.put("a_g", a_g);
                params.put("a_b", a_b);
                params.put("a_c", a_c);

                params.put("company", company);
                params.put("category", catalog);
                params.put("zone", zone);
                params.put("power", String.valueOf(pow));

                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("um_token", um_token);
                return params;
            }

        };

        RequestQueue _requestQueue = Volley.newRequestQueue(context);
        _requestQueue.add(stringRequest);
    }

    public void train3(final Context context,
                      final String r_r, final String r_g, final String r_b, final String r_c,
                      final String g_r, final String g_g, final String g_b, final String g_c,
                      final String b_r, final String b_g, final String b_b, final String b_c,
                      final String a_r, final String a_g, final String a_b, final String a_c,
                      final String pow,
                      final String company, final String category, final String series, final String color) {


        String url = "http://hcapi.free-estimation.com/api/v1/users/train3";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject json = new JSONObject(response);
                            String statusCode = json.getString("status_code");
                            if(!statusCode.equals("200 OK")) {
                                Toast.makeText(context, "Http Communication Error" + statusCode, Toast.LENGTH_LONG ).show();
                                return;
                            }
                            String message = json.getString("message");
                            EventBus.getDefault().post(new MessageEvents.onTrainingComplete(message));
                            if(message.equals("Success")) {
                                Toast.makeText(context, "Training data saved at server...Please try other colors", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "POST http://hcapi.free-estimation.com/api/v1/users/train3 Failed ", Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("r_r", r_r);
                params.put("r_g", r_g);
                params.put("r_b", r_b);
                params.put("r_c", r_c);

                params.put("g_r", g_r);
                params.put("g_g", g_g);
                params.put("g_b", g_b);
                params.put("g_c", g_c);

                params.put("b_r", b_r);
                params.put("b_g", b_g);
                params.put("b_b", b_b);
                params.put("b_c", b_c);

                params.put("a_r", a_r);
                params.put("a_g", a_g);
                params.put("a_b", a_b);
                params.put("a_c", a_c);

                params.put("pow", pow);
                params.put("company", company);
                params.put("category", category);
                params.put("series", series);
                params.put("color", color);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("um_token", um_token);
                return params;
            }

        };

        RequestQueue _requestQueue = Volley.newRequestQueue(context);
        _requestQueue.add(stringRequest);
    }


    public void getRecipe(final String zone1, final String zone2, final String zone3, final String target) {

        String url = "http://hcapi.free-estimation.com/api/v1/users/getrecipe";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONObject json = new JSONObject(response);
                            String statusCode = json.getString("status_code");
                            if(!statusCode.equals("200 OK")) {
                                Toast.makeText(context, "Http Communication Error" + statusCode, Toast.LENGTH_LONG ).show();
                                return;
                            }
                            String message = json.getString("message");
                            JSONArray jsonArray = json.getJSONArray("data");
                            JSONObject inner = new JSONObject(jsonArray.get(0).toString() );
                            String recipe = inner.getString("cn_recipe");

                            if(message.equals("Success")) {
                                EventBus.getDefault().post( new MessageEvents.onGetRecipe(recipe) );
                                Toast.makeText(context, "Recipe is available. Success...", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "POST api/v1/users/getrecipe failed", Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("zone1", zone1);
                params.put("zone2", zone2);
                params.put("zone3", zone3);
                params.put("target", target);

                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("um_token", um_token);
                return params;
            }

        };

        RequestQueue _requestQueue = Volley.newRequestQueue(context);
        _requestQueue.add(stringRequest);
    }

    public void getTrainedData() {

        String url = "http://hcapi.free-estimation.com/api/v1/users/getdata3";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject json = new JSONObject(response);
                            String statusCode = json.getString("status_code");
                            if(!statusCode.equals("200 OK")) {
                                Toast.makeText(context, "Http Communication Error" + statusCode, Toast.LENGTH_LONG ).show();
                                return;
                            }
                            String message = json.getString("message");
                            JSONArray jsonArray = json.getJSONArray("data");
                            if(message.equals("Success")) {
                                EventBus.getDefault().post( new MessageEvents.onTrainedData(jsonArray) );
                                Toast.makeText(context, "Train data fetched from server...", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "POST api/v1/users/getdata3 is failed " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }){

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("um_token", um_token);
                return params;
            }

        };

        RequestQueue _requestQueue = Volley.newRequestQueue(context);
        _requestQueue.add(stringRequest);
    }

    public void getCategory(final Context context, final String company, final Spinner spTCatalog) {
        String url   = baseUrl + "api/v1/users/getcategory";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject json = new JSONObject(response);
                            String statusCode = json.getString("status_code");
                            if(!statusCode.equals("200 OK")) {
                                Toast.makeText(context, "Http Communication Error" + statusCode, Toast.LENGTH_LONG ).show();
                                return;
                            }
                            String message = json.getString("message");
                            JSONArray jsonArray = json.getJSONArray("data");

                            if(message.equals("Success")) {
                                List<String> label = new ArrayList<>();
                                if (jsonArray != null) {
                                    for (int i=0;i<jsonArray.length();i++){
                                        JSONObject inner = new JSONObject(jsonArray.get(i).toString() );
                                        String names = inner.getString("cn_category");
                                        label.add(names);
                                    }
                                }
                                ArrayAdapter<String> rtAdapter = new ArrayAdapter<>(context, R.layout.simple_spinner, label);
                                rtAdapter.setDropDownViewResource(R.layout.simple_spinner);
                                spTCatalog.setAdapter(rtAdapter);

                            } else {
                                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "api/v1/users/getcategory is failed " + error.toString(), Toast.LENGTH_LONG).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("company", company);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("um_token", um_token);
                return params;
            }

        };

        RequestQueue _requestQueue = Volley.newRequestQueue(context);
        _requestQueue.add(stringRequest);
    }

    public void getSeries(final Context context, final String company, final String category, final Spinner spTSeries) {
        String url   = baseUrl + "api/v1/users/getseries";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject json = new JSONObject(response);
                            String statusCode = json.getString("status_code");
                            if(!statusCode.equals("200 OK")) {
                                Toast.makeText(context, "Http Communication Error" + statusCode, Toast.LENGTH_LONG ).show();
                                return;
                            }
                            String message = json.getString("message");
                            JSONArray jsonArray = json.getJSONArray("data");

                            if(message.equals("Success")) {
                                List<String> label = new ArrayList<>();
                                if (jsonArray != null) {
                                    for (int i=0;i<jsonArray.length();i++){
                                        JSONObject inner = new JSONObject(jsonArray.get(i).toString() );
                                        String names = inner.getString("cn_series");
                                        label.add(names);
                                    }
                                }

                                ArrayAdapter<String> rtAdapter = new ArrayAdapter<>(context, R.layout.simple_spinner, label);
                                rtAdapter.setDropDownViewResource(R.layout.simple_spinner);
                                spTSeries.setAdapter(rtAdapter);
                            } else {
                                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "api/v1/users/getseries is failed " + error.toString(), Toast.LENGTH_LONG).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("company", company);
                params.put("category", category);

                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("um_token", um_token);
                return params;
            }

        };

        RequestQueue _requestQueue = Volley.newRequestQueue(context);
        _requestQueue.add(stringRequest);
    }

    public void getColorList(final Context context, final String company, final String category, final String series, final Spinner spTColor) {
        String url   = baseUrl + "api/v1/users/getcolorlist";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject json = new JSONObject(response);
                            String statusCode = json.getString("status_code");
                            if(!statusCode.equals("200 OK")) {
                                Toast.makeText(context, "Http Communication Error" + statusCode, Toast.LENGTH_LONG ).show();
                                return;
                            }
                            String message = json.getString("message");
                            JSONArray jsonArray = json.getJSONArray("data");

                            if(message.equals("Success")) {
                                List<String> label = new ArrayList<>();
                                if (jsonArray != null) {
                                    for (int i=0;i<jsonArray.length();i++){
                                        JSONObject inner = new JSONObject(jsonArray.get(i).toString() );
                                        String names = inner.getString("cn_color");
                                        label.add(names);
                                    }
                                }

                                ArrayAdapter<String> rtAdapter = new ArrayAdapter<>(context, R.layout.simple_spinner, label);
                                rtAdapter.setDropDownViewResource(R.layout.simple_spinner);
                                spTColor.setAdapter(rtAdapter);
                            } else {
                                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "api/v1/users/getcolorlist is failed " + error.toString(), Toast.LENGTH_LONG).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("company", company);
                params.put("category", category);
                params.put("series", series);

                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("um_token", um_token);
                return params;
            }

        };

        RequestQueue _requestQueue = Volley.newRequestQueue(context);
        _requestQueue.add(stringRequest);
    }

    public void clearTrainData() {

        String url = "http://hcapi.free-estimation.com/api/v1/users/cleartraindata";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONObject json = new JSONObject(response);
                            String statusCode = json.getString("status_code");
                            if(!statusCode.equals("200 OK")) {
                                Toast.makeText(context, "Http Communication Error" + statusCode, Toast.LENGTH_LONG ).show();
                                return;
                            }
                            String message = json.getString("message");
                            if(message.equals("Success")) {
                                Toast.makeText(context, "Train data on the server is erased...", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, " api/v1/users/cleartraindata failed", Toast.LENGTH_LONG).show();
                    }
                }){

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("um_token", um_token);
                return params;
            }
        };

        RequestQueue _requestQueue = Volley.newRequestQueue(context);
        _requestQueue.add(stringRequest);
    }


    public void reTrain3() {

        String url = "http://hcapi.free-estimation.com/api/v1/users/retrain3";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject json = new JSONObject(response);
                            String statusCode = json.getString("status_code");
                            if(!statusCode.equals("200 OK")) {
                                Toast.makeText(context, "Http Communication Error" + statusCode, Toast.LENGTH_LONG ).show();
                                return;
                            }
                            String message = json.getString("message");
                            if(message.equals("Success")) {
                                Toast.makeText(context, "Re Trained 3 is success...", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "POST api/v1/users/getdata3 is failed " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }){

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("um_token", um_token);
                return params;
            }

        };

        RequestQueue _requestQueue = Volley.newRequestQueue(context);
        _requestQueue.add(stringRequest);
    }
}
