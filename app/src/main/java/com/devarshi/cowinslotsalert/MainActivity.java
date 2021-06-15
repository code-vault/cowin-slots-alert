package com.devarshi.cowinslotsalert;

import androidx.annotation.ArrayRes;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    TextView textView;
    Spinner stateSpinner, districtSpinner;
    ArrayAdapter<CharSequence> stateAdapter, districtAdapter;
    RequestQueue queue;
    String GET_STATES_URL;
    String GET_DISTRICTS_URL;
    List<StateModel> states;
    List<DistrictModel> districts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        getStateList();
    }

    public void init() {
        GET_STATES_URL = this.getResources().getString(R.string.get_states_url);
        GET_DISTRICTS_URL = this.getResources().getString(R.string.get_districts_url);
        queue = Volley.newRequestQueue(this);
    }

    public void getStateList() {
        JsonObjectRequest stateListRequest = new JsonObjectRequest(Request.Method.GET, GET_STATES_URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONArray statesArray = response.optJSONArray("states");
                        states = new ArrayList<>();
                        for(int i = 0; i < statesArray.length(); i++) {
                            JSONObject stateObj =  statesArray.optJSONObject(i);
                            int stateId = stateObj.optInt("state_id");
                            String stateName = stateObj.optString("state_name");
                            StateModel state = new StateModel(stateId, stateName);
                            states.add(state);
                            setStateSpinner();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                textView.setText("That didn't work!");
            }
        });
        queue.add(stateListRequest);
    }

    public void getDistrictList(int stateId) {
        GET_DISTRICTS_URL = String.format(GET_DISTRICTS_URL, stateId);
        JsonObjectRequest districtListRequest = new JsonObjectRequest(Request.Method.GET, GET_DISTRICTS_URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONArray districtsArray = response.optJSONArray("districts");
                        districts = new ArrayList<>();
                        for(int i = 0; i < districtsArray.length(); i++) {
                            JSONObject districtObj =  districtsArray.optJSONObject(i);
                            int districtId = districtObj.optInt("district_id");
                            String districtName = districtObj.optString("district_name");
                            DistrictModel district = new DistrictModel(districtId, districtName);
                            districts.add(district);
                            setDistrictSpinner();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                textView.setText("That didn't work!");
            }
        });
        queue.add(districtListRequest);
    }

    public void setStateSpinner(){
        stateSpinner = (Spinner) findViewById(R.id.state_dropdown);
        textView= (TextView) findViewById(R.id.textView);

        List<String> stateNames = new ArrayList<>();
        for(StateModel state : states){
            stateNames.add(state.getStateName());
        }

        stateAdapter = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, stateNames);
        stateAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        stateSpinner.setAdapter(stateAdapter);
//        stateSpinner.setOnItemSelectedListener(this);
    }

    public void setDistrictSpinner() {
        districtSpinner = (Spinner) findViewById(R.id.district_dropdown);
        textView= (TextView) findViewById(R.id.textView);

        List<String> districtNames = new ArrayList<>();
        for(DistrictModel district : districts){
            districtNames.add(district.getDistrictName());
        }

        districtAdapter = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, districtNames);
        districtAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        districtSpinner.setAdapter(districtAdapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
       String selectedState = parent.getItemAtPosition(pos).toString();
       textView.setText(selectedState);
       int selectedStateId = 100;
       for(StateModel state: states){
           if(state.getStateName().equalsIgnoreCase(selectedState)){
               selectedStateId = state.getStateId();
               break;
           }
       }
       getDistrictList(selectedStateId);
       System.out.println(selectedState);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}