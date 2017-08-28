package com.syshuman.kadir.haircolor3.model;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.syshuman.kadir.haircolor3.eventbus.MessageEvents;
import com.syshuman.kadir.haircolor3.view.activities.DeviceActivity;
import com.syshuman.kadir.haircolor3.view.activities.MainActivity;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kadir on 2016-12-08.
 */

public class RestServer {



    private String baseUrl  = "http://hcapi.free-estimation.com/";
    private Context context;


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
                                saveUser(data);
                                Intent intent = new Intent(context, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent);
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
                        Toast.makeText(context, "Please double check username and password", Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("um_email", uname);
                params.put("um_upass", upass);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<String, String>();
                return params;
            }

        };

        RequestQueue _requestQueue = Volley.newRequestQueue(context);
        _requestQueue.add(stringRequest);
    }

    public void saveUser(JSONObject data) {
        try {
            JSONObject userm = data.getJSONObject("userm");
            Integer um_no = userm.getInt("um_no");
            Integer cm_no = userm.getInt("cm_no");
            String um_email = userm.getString("um_email");
            String um_uname = userm.getString("um_uname");
            String um_token = userm.getString("um_token");
            SharedPreferences prefs = context.getSharedPreferences("com.syshuman.kadir.socks", context.MODE_PRIVATE);
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
                            String message = json.getString("message");
                            if(message.equals("Success")) {
                                Intent intent = new Intent(context, DeviceActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent);
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
                        Toast.makeText(context, "Please double check username and password", Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
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
                Map<String, String> params = new HashMap<String, String>();
                //params.put("um_token", "");
                //params.put("upass", upass);
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
                            String message = json.getString("message");
                            JSONArray jsonArray = json.getJSONArray("data");

                            if(message.equals("Success")) {
                                Toast.makeText(context, "Color Obtained from server", Toast.LENGTH_LONG).show();

                                List<String> label = new ArrayList<String>();
                                if (jsonArray != null) {
                                    for (int i=0;i<jsonArray.length();i++){
                                        JSONObject inner = new JSONObject(jsonArray.get(i).toString() );
                                        String names = inner.getString("lc_code");
                                        label.add(names);
                                    }
                                }

                                ArrayAdapter<String> rtAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, label);
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
                        Toast.makeText(context, "Please double check service at gelcolor", Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("um_token", "298347decdd47d3ea70361c3acef225a");
                //params.put("upass", upass);
                return params;
            }

        };

        RequestQueue _requestQueue = Volley.newRequestQueue(context);
        _requestQueue.add(stringRequest);
    }



    public void getColor3(final String company, final String catalog, final String zone, final String power,
                         final String r_r, final String r_g, final String r_b, final String r_c,
                         final String g_r, final String g_g, final String g_b, final String g_c,
                         final String b_r, final String b_g, final String b_b, final String b_c,
                         final String a_r, final String a_g, final String a_b, final String a_c
                         ) {

        String url   = baseUrl + "api/v1/users/getcolor3";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                        EventBus.getDefault().post( new MessageEvents.onGetColor("", "") );
                        try {
                            JSONObject json = new JSONObject(response);
                            String statusCode = json.getString("status_code");
                            String message = json.getString("message");
                            JSONObject data = new JSONObject(json.getString("data"));
                            String result = data.getString("lc_color");
                            String zone = data.getString("lc_zone");

                            if(statusCode.equals("200 OK")) {
                                Toast.makeText(context, "Color Obtained from server", Toast.LENGTH_LONG).show();
                                EventBus.getDefault().post( new MessageEvents.onGetColor(result, zone) );
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
                        EventBus.getDefault().post( new MessageEvents.onGetColor("", "") );
                        Toast.makeText(context, "Please double check service at gelcolor3", Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("company", company);
                params.put("catalog", catalog);
                params.put("zone", zone);
                params.put("power", power);

                params.put("r_r", r_r); params.put("r_g", r_g); params.put("r_b", r_b); params.put("r_c", r_c);
                params.put("g_r", g_r); params.put("g_g", g_g); params.put("g_b", g_b); params.put("g_c", g_c);
                params.put("b_r", b_r); params.put("b_g", b_g); params.put("b_b", b_b); params.put("b_c", b_c);
                params.put("a_r", a_r); params.put("a_g", a_g); params.put("a_b", a_b); params.put("a_c", a_c);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("um_token", "298347decdd47d3ea70361c3acef225a");
                //params.put("upass", upass);
                return params;
            }

        };

        RequestQueue _requestQueue = Volley.newRequestQueue(context);
        _requestQueue.add(stringRequest);
    }




    public void train(final Context context,
                      final String r_r, final String r_g, final String r_b, final String r_c,
                      final String g_r, final String g_g, final String g_b, final String g_c,
                      final String b_r, final String b_g, final String b_b, final String b_c,
                      final String a_r, final String a_g, final String a_b, final String a_c,
                      final String company, final String catalog, final String color) {


        String url = "http://hcapi.free-estimation.com/api/v1/users/train3";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject json = new JSONObject(response);
                            String statusCode = json.getString("status_code");
                            String message = json.getString("message");
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
                        Toast.makeText(context, "Please double check username and password", Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
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
                params.put("catalog", catalog);
                params.put("color", color);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("um_token", "6efcfbb2b37a61edd0fce33001aab6cc");
                //params.put("upass", upass);
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
                            String message = json.getString("message");

                            JSONArray jsonArray = json.getJSONArray("data");
                            JSONObject inner = new JSONObject(jsonArray.get(0).toString() );
                            String recipe = inner.getString("cn_recipe");

                            if(message.equals("Success")) {
                                EventBus.getDefault().post( new MessageEvents.onGetRecipe(recipe) );
                                Toast.makeText(context, "Recipe obtained from server...", Toast.LENGTH_LONG).show();
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
                        Toast.makeText(context, "Please double check username and password", Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("zone1", zone1);
                params.put("zone2", zone2);
                params.put("zone3", zone3);
                params.put("target", target);

                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("um_token", "6efcfbb2b37a61edd0fce33001aab6cc");
                //params.put("upass", upass);
                return params;
            }

        };

        RequestQueue _requestQueue = Volley.newRequestQueue(context);
        _requestQueue.add(stringRequest);
    }


    public void getTrainData(String fileName) {


        String url = "http://hcapi.free-estimation.com/api/v1/users/gettraindata";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONObject json = new JSONObject(response);
                            String statusCode = json.getString("status_code");
                            String message = json.getString("message");

                            JSONArray jsonArray = json.getJSONArray("data");
                            JSONObject inner = new JSONObject(jsonArray.get(0).toString() );
                            String recipe = inner.getString("cn_recipe");

                            if(message.equals("Success")) {
                                EventBus.getDefault().post( new MessageEvents.onGetRecipe(recipe) );
                                Toast.makeText(context, "Recipe obtained from server...", Toast.LENGTH_LONG).show();
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
                        Toast.makeText(context, "Please double check username and password", Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();


                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("um_token", "6efcfbb2b37a61edd0fce33001aab6cc");
                //params.put("upass", upass);
                return params;
            }

        };

        RequestQueue _requestQueue = Volley.newRequestQueue(context);
        _requestQueue.add(stringRequest);
    }

}
