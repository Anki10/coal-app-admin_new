package com.anova.indiaadmin;


import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.anova.indiaadmin.database.AppDatabase;
import com.anova.indiaadmin.database.AppDatabaseResponse;
import com.anova.indiaadmin.database.customers.CustomerDBHelper;
import com.anova.indiaadmin.database.customers.CustomerEntity;
import com.anova.indiaadmin.database.samplecustomer.SampleCustomerDBHelper;
import com.anova.indiaadmin.database.samplecustomer.SampleCustomerEntity;
import com.anova.indiaadmin.network.AppNetworkResponse;
import com.anova.indiaadmin.network.Volley;
import com.anova.indiaadmin.utils.Constants;
import com.anova.indiaadmin.utils.PreferenceHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orhanobut.logger.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


/**
 * A simple {@link Fragment} subclass.
 */
public class CustomerFragment extends Fragment implements AppNetworkResponse, AppDatabaseResponse, AdapterView.OnItemSelectedListener {


    @BindView(R.id.customerName)
    Spinner customerName;
    @BindView(R.id.fsaNumber)
    EditText fsaNumber;
    @BindView(R.id.totalNumberTruck)
    EditText totalNumberTruck;
    @BindView(R.id.totalNumberTruckSampled)
    EditText totalNumberTruckSampled;
    Unbinder unbinder;
    @BindView(R.id.doDetails)
    EditText doDetails;

    @BindView(R.id.declaredGrade)
    EditText declaredGrade;

    int declaredGradeInt=0;

    Bundle bundle;
    String returnJson, editMode, sampleID = "", liftType;

    ArrayAdapter<String> customerNameAdapter ;
    List<String> customerNameList = new ArrayList<String>();
    List<CustomerEntity> customerList;
    List<SampleCustomerEntity> sampleCustomerEntityArrayList = new ArrayList<>();

    String session;
    final String TAG = "CustomerFrag";
    boolean editModeBoolean;
    JSONObject response;

    HomeActivity mCallback;
    @BindView(R.id.fsaLayout)
    LinearLayout fsaLayout;

    @BindView(R.id.ll_main_container)
    LinearLayout llMainContainer;

    String quantityLiftedPattern= "[0-9]{1,5}+(\\.[0-9]{1,2})?";
    String truckWagon="Truck";

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.customerName:{
                String item = parent.getItemAtPosition(position).toString();

                if (!item.equals("Name of the Customer")){
                    ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.colorDarkGrey));
                }

                if(position<=0){
                    fsaNumber.setText("");
                } else {
                    fsaNumber.setText(customerList.get(position - 1).getFsaNo());
                }
                break;
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public interface OpenChallanInterface {
        public void openChallanFrag(String i);
    }

    @TargetApi(23)
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.e(TAG,"onAttach context");
        try {
            mCallback = (HomeActivity) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement IFragmentToActivity");
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.e(TAG,"onAttach activity");
        try {
            mCallback = (HomeActivity) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement IFragmentToActivity");
        }
    }

    @Override
    public void onDetach() {
        Log.e(TAG,"onDeatach");
        mCallback = null;
        super.onDetach();
    }

    public CustomerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_customer, container, false);
        unbinder = ButterKnife.bind(this, view);
        Log.e(TAG,"onCreate");
        bundle = getArguments();

        liftType = mCallback.liftType;
        fsaNumber.setEnabled(false);
        customerName.setOnItemSelectedListener(this);


        /*if (bundle != null) {
            editMode = bundle.getString("editMode");
            if (editMode.equals("true")) {
                returnJson = bundle.getString("returnJson");
            }

            if(bundle.containsKey("liftType"))
            {
                liftType=bundle.getString("liftType");
            }

            if(bundle.containsKey("sampleId"))
            {
                sampleID=bundle.getString("sampleId");
            }
            Log.e("customerFrag", editMode + " : " + returnJson+" : "+liftType+" : "+sampleID);
        }*/

      /*  declaredGrade.addTextChangedListener(new TextWatcher(){
            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start, int count, int after){}

            public void onTextChanged(CharSequence s, int start, int before, int count){
                String strEnteredVal = declaredGrade.getText().toString();

                if(!strEnteredVal.equals("")){
                    int num=Integer.parseInt(strEnteredVal);
                    if(num<17){
                        declaredGrade.setText(""+num);
                    }else{
                        Toast.makeText(mCallback,"Declared Grade should be less 17",Toast.LENGTH_SHORT).show();
                        declaredGrade.setText("");
                    }
                }
            }});*/

        //declaredGrade.setFilters(new InputFilter[]{ new InputFilterMinMax("1", "17")});


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        customerNameList.clear();

        Log.e(TAG,"onResume");
        setAdapter();
        checkSession();

//        if (editMode.equals("true")) {
//            Log.e(TAG,"onResume 1");
//            getData();
//        }else
//        {
//            Log.e(TAG,"onResume 2");
//            setVisibilty();
//        }
    }

    private void setVisibilty() {
        Log.e(TAG,"setVisibilty");
        switch (liftType) {
            case "Road":
                fsaLayout.setVisibility(View.VISIBLE);
                doDetails.setHint("DO Detail");
                truckWagon="Truck";
              // totalNumberTruck.setHint("Total no. of "+truckWagon);
              //  totalNumberTruckSampled.setHint("Total no. of "+truckWagon+" Sampled");

                break;

            case "Rail":
                fsaLayout.setVisibility(View.GONE);
                doDetails.setHint("RR number");
                doDetails.setInputType(InputType.TYPE_CLASS_NUMBER);
                truckWagon="Wagons";
              //  totalNumberTruck.setHint("Total no. of Wagons");
               // totalNumberTruckSampled.setHint("Total no. of Wagons Sampled");

                break;

            default:
                fsaLayout.setVisibility(View.VISIBLE);
                doDetails.setHint("DO Detail");
                truckWagon="Truck";
               // totalNumberTruck.setHint("Total no. of Truck");
              //  totalNumberTruckSampled.setHint("Total no. of Truck Sampled");
                break;
        }

          totalNumberTruck.setHint("Total no. of "+truckWagon);
          totalNumberTruckSampled.setHint("Total no. of "+truckWagon+" Sampled");
    }

    private void getData(JSONObject dbJsonObject) {
        Log.d(TAG,"getData");
        /*"customer_name": "",
                "fsa_number": "",
                "do_details": "",
                "declared_grade": "",
                "total_trucks": "0",
                "total_trucks_sampled": "0",*/


        try {
            sampleID = String.valueOf(PreferenceHelper.getInteger(Constants.SP_KEY_CURRENT_COLLECTION_ID));
            int spinnerPosition = customerNameAdapter.getPosition(dbJsonObject.getString("customerName"));
            CustomerEntity currentCustomer = customerList.get(spinnerPosition-1);
            customerName.setSelection(spinnerPosition);
            if(mCallback.liftType!=null && mCallback.liftType.length()>0){
                liftType = mCallback.liftType;
            } else {
                liftType = dbJsonObject.getString("liftingType");
            }
            switch (liftType) {
                case "Road":
                    fsaLayout.setVisibility(View.VISIBLE);
                    if (currentCustomer.getFsaNo()!=null) {
                        fsaNumber.setText(currentCustomer.getFsaNo());
                    }

                    doDetails.setHint("DO Detail");
                    truckWagon="Truck";
                   // totalNumberTruck.setHint("Total no. of Truck");
                   // totalNumberTruckSampled.setHint("Total no. of Truck Sampled");
                    break;

                case "Rail":
                    fsaLayout.setVisibility(View.GONE);
                    doDetails.setHint("RR number");
                    doDetails.setInputType(InputType.TYPE_CLASS_NUMBER);
                    truckWagon="Wagons";
                   // totalNumberTruck.setHint("Total no. of Wagons");
                   // totalNumberTruckSampled.setHint("Total no. of Wagons Sampled");
                    break;

                default:
                    fsaLayout.setVisibility(View.VISIBLE);
                    if (currentCustomer.getFsaNo()!=null) {
                        fsaNumber.setText(currentCustomer.getFsaNo());
                    }
                    doDetails.setHint("DO Detail");
                    truckWagon="Truck";
                    //totalNumberTruck.setHint("Total no. of Truck");
                   // totalNumberTruckSampled.setHint("Total no. of Truck Sampled");
                    break;
            }
            totalNumberTruck.setHint("Total no. of "+truckWagon);
            totalNumberTruckSampled.setHint("Total no. of "+truckWagon+" Sampled");


            /*if (jsonObject.getString("fsa_number") != null) {
                int spinnerPosition = fsaNumberAdapter.getPosition(jsonObject.getString("fsa_number"));
                fsaNumber.setSelection(spinnerPosition);
            }*/
            if(!dbJsonObject.isNull("doDetails"))
            {
                Log.d("do_details","check");
                doDetails.setText(dbJsonObject.getString("doDetails"));
            }else
            {
                    Log.d("do_details","not_check");
            }

            if(!dbJsonObject.isNull("declaredGrade"))
            {
                Log.d("declaredGrade","check");
                declaredGrade.setText(dbJsonObject.getString("declaredGrade"));
            }
            else
            {
                Log.d("declared","not_check");
            }


            if (!dbJsonObject.isNull("totalVehicles") ) {

                totalNumberTruck.setText(dbJsonObject.getString("totalVehicles"));
            }

            if (!dbJsonObject.isNull("totalVehiclesSampled")) {
               totalNumberTruckSampled.setText(dbJsonObject.getString("totalVehiclesSampled"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    public void setAdapter() {
        try {
            customerNameList.add("Name of the Customer");
            CustomerDBHelper customerDBHelper = new CustomerDBHelper(AppDatabase.getAppDatabase(getActivity()), this);
            customerDBHelper.getAllCustomers(Constants.SELECT_ALL_CUSTOMERS);
            customerNameAdapter = new ArrayAdapter<String>(getActivity(), R.layout.simple_spinner_item, customerNameList);
            customerNameAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
            customerName.setAdapter(customerNameAdapter);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
     }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    @OnClick({R.id.addCustomer, R.id.submit, R.id.Do_add})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.addCustomer:
                break;
            case R.id.submit:
                if (validationCheck()) {
                    save();
                }
                break;
        }
    }

    private boolean validationCheck() {
        //TODO REMOVE this code
        Log.e("yo","yo");

            if (declaredGrade.getText().length() != 0) {
                declaredGradeInt = Integer.parseInt(declaredGrade.getText().toString());
            }

            if (customerName.getSelectedItemId() == 0) {
                Toast.makeText(getActivity(), "Select Customer Name", Toast.LENGTH_SHORT).show();
            } else if (doDetails.getText().length() == 0) {
                Toast.makeText(getActivity(), "Enter Do Details", Toast.LENGTH_SHORT).show();
            } else if(declaredGrade.getText().length() == 0){
                Toast.makeText(getActivity(), "Enter Declared Grade", Toast.LENGTH_SHORT).show();
            } else if(totalNumberTruck.getText().length() == 0){
                Toast.makeText(getActivity(), "Enter Total no. of Truck", Toast.LENGTH_SHORT).show();
            } else if(totalNumberTruckSampled.getText().length() == 0){
                Toast.makeText(getActivity(), "Enter Total no. of Truck Sampled", Toast.LENGTH_SHORT).show();
            } else if (declaredGradeInt > 17) {
                Toast.makeText(getActivity(), "Declared Grade should be less then 17", Toast.LENGTH_SHORT).show();
                declaredGrade.setText("");
                declaredGradeInt = 0;
            } else if(Integer.parseInt(String.valueOf(totalNumberTruckSampled.getText())) >= Integer.parseInt(String.valueOf(totalNumberTruck.getText()))){
                Toast.makeText(getActivity(), "Total no. of Truck Sampled should be less than Total no. of Trucks", Toast.LENGTH_SHORT).show();
            } else if (liftType.equalsIgnoreCase("Road") && totalNumberTruck.getText().length() > 4) {
                Toast.makeText(getActivity(), "Total number of trucks should be less than or equal to 4 digits", Toast.LENGTH_SHORT).show();
            } else if (liftType.equalsIgnoreCase("Road") && totalNumberTruckSampled.getText().length() > 3) {
                Toast.makeText(getActivity(), "Total number of trucks sampled should be less than or equal to 3 digits", Toast.LENGTH_SHORT).show();
            }


       /* else if (Integer.parseInt(totalNumberTruck.getText().toString())>Integer.parseInt(totalNumberTruckSampled.getText().toString())) {
            Toast.makeText(getActivity(), "Sampled "+truckWagon+" should be less then Total number of "+truckWagon, Toast.LENGTH_SHORT).show();
            totalNumberTruckSampled.setText("");
        }else if(Integer.parseInt(totalNumberTruckSampled.getText().toString())>6){
            Toast.makeText(getActivity(), "Sampled "+truckWagon+" upto 6 only", Toast.LENGTH_SHORT).show();
        }*/
            else {
                return true;
            }
            return false;

    }


    private void save() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("sample_id", sampleID);
            jsonObject.put("lifting_type", liftType);
            jsonObject.put("customer_name", customerName.getSelectedItem());
            jsonObject.put("fsa_number", fsaNumber.getText().toString());
            jsonObject.put("do_details", doDetails.getText().toString());
            jsonObject.put("declared_grade", declaredGrade.getText().toString());
            jsonObject.put("total_trucks", totalNumberTruck.getText().toString());
            jsonObject.put("total_trucks_sampled", totalNumberTruckSampled.getText().toString());


            SampleCustomerEntity sampleCustomerEntity = new SampleCustomerEntity();
            int spinnerPosition = customerNameAdapter.getPosition(String.valueOf(customerName.getSelectedItem()));
            sampleCustomerEntity.setLocalSampleId(Integer.parseInt(sampleID));
            sampleCustomerEntity.setCustomerId(Integer.parseInt(customerList.get(spinnerPosition - 1).getUserId()));
            sampleCustomerEntity.setCustomerName(customerList.get(spinnerPosition - 1).getUserName());
            sampleCustomerEntity.setLiftingType(liftType);
            sampleCustomerEntity.setMore_details(doDetails.getText().toString());
            sampleCustomerEntity.setDeclaredGrade(declaredGrade.getText().toString());
            sampleCustomerEntity.setTotalVehicles(totalNumberTruck.getText().toString());
            sampleCustomerEntity.setTotalVehiclesSampled(totalNumberTruckSampled.getText().toString());

            sampleCustomerEntityArrayList.add(sampleCustomerEntity);

            SampleCustomerDBHelper sampleCustomerDBHelper = new SampleCustomerDBHelper(AppDatabase.getAppDatabase(getActivity()),this);
            sampleCustomerDBHelper.deleteSampleCustomersByLocalSampleId(Integer.parseInt(sampleID),Constants.DELETE_ALL_SAMPLE_CUSTOMERS_SAMPLE_ID);
//
//            String currentLocalSampleId = String.valueOf(PreferenceHelper.getInteger(Constants.SP_KEY_CURRENT_COLLECTION_ID));
//            if()

            Log.e("json", jsonObject.toString());
//            Volley volley = Volley.getInstance();
//            volley.postSession(Constants.apiSamplingCollection, jsonObject, this, ((App) getActivity().getApplicationContext()).getSessionID(), Constants.REQ_POST_SAMPLING_COllECTION);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onDatabaseResSuccess(JSONObject jsonObject, int reqCode) {
        switch (reqCode){
            case Constants.SELECT_ALL_CUSTOMERS:{
                try {
                    if (jsonObject != null) {
                        Logger.json(jsonObject.toString());
                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<CustomerEntity>>() {
                        }.getType();
                        customerList = gson.fromJson(jsonObject.getJSONArray("data").toString(), listType);
                        for (CustomerEntity customer : customerList) {
                            customerNameList.add(customer.getUserName());
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            }

            case Constants.SELECT_SAMPLE_CUSTOMER_BY_ID:{
                Logger.d(jsonObject);
                if(jsonObject!=null && jsonObject.length()>0) {
                    getData(jsonObject);
                }
                break;
            }

            case Constants.DELETE_ALL_SAMPLE_CUSTOMERS_SAMPLE_ID:{
                SampleCustomerDBHelper sampleCustomerDBHelper = new SampleCustomerDBHelper(AppDatabase.getAppDatabase(getActivity()), this);
                sampleCustomerDBHelper.insertAll(sampleCustomerEntityArrayList, Constants.INSERT_SAMPLE_CUSTOMER_LIST);
                break;
            }

            case Constants.INSERT_SAMPLE_CUSTOMER_LIST:{
                Logger.d(jsonObject);
                Toast.makeText(getActivity(),"Customer Detail Saved",Toast.LENGTH_SHORT).show();
                Log.e(TAG,"sampleID "+sampleID);
                try {
                    mCallback.openChallanFrag(sampleID);
                    // Toast.makeText(getActivity(), jsonObject.getString("data"), Toast.LENGTH_SHORT).show();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    @Override
    public void onResSuccess(JSONObject jsonObject, int reqCode) {
        Log.e("ResSuccess", jsonObject + " : " + reqCode);
        switch (reqCode) {
            case Constants.REQ_POST_SAMPLING_COllECTION:
                Toast.makeText(getActivity(),"Customer Detail Saved",Toast.LENGTH_SHORT).show();
                Log.e(TAG,"sampleID "+sampleID);
                try {
                    mCallback.openChallanFrag(sampleID);
                    // Toast.makeText(getActivity(), jsonObject.getString("data"), Toast.LENGTH_SHORT).show();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                break;

            case Constants.REQ_POST_CHECK_LOGIN:
                Log.i(TAG,"2");
                try {
                    Log.i(TAG,"3");
                    response=jsonObject;
                    editModeBoolean = response.getBoolean("editMode");
                    if(editModeBoolean)
                    {
                        returnJson=response.getString("return");
                        editMode="true";
//                        getData();
                        Log.e(TAG,Boolean.toString(editModeBoolean)+" : "+returnJson);
                    }
                    else
                    {
                        setVisibilty();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                break;
        }
    }

    @Override
    public void onResFailure(String errCode, String errMsg, int reqCode, JSONObject jsonObject) {
        Log.e("ResFailure", reqCode + " : " + errMsg);
        //Toast.makeText(getActivity(), errMsg, Toast.LENGTH_SHORT).show();
        switch (reqCode) {

            case Constants.REQ_POST_CHECK_LOGIN:
                Intent in = new Intent(getActivity().getApplicationContext(), LoginActivity.class);
                startActivity(in);
                in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                getActivity().finish();
                break;

            case Constants.REQ_POST_SAMPLING_COllECTION:
                if(isJSONValid(errMsg))
                {
                    try {
                        JSONObject jsonObject1= new JSONObject(errMsg);
                        if(jsonObject1.has("errors"))
                        {
                            JSONObject jsonObject2= jsonObject1.getJSONObject("errors");
                            if(jsonObject2.has("sample_id"))
                            {
                                Toast.makeText(getActivity(),jsonObject2.getString("sample_id"),Toast.LENGTH_SHORT).show();
                            }
                            if(jsonObject2.has("lifting_type"))
                            {
                                Toast.makeText(getActivity(),jsonObject2.getString("lifting_type"),Toast.LENGTH_SHORT).show();
                            }
                            if(jsonObject2.has("customer_name"))
                            {
                                Toast.makeText(getActivity(),jsonObject2.getString("customer_name"),Toast.LENGTH_SHORT).show();
                            }
                            if(jsonObject2.has("fsa_number"))
                            {
                               fsaNumber.setText("");
                                Toast.makeText(getActivity(),jsonObject2.getString("fsa_number"),Toast.LENGTH_SHORT).show();
                            }
                            if(jsonObject2.has("do_details"))
                            {
                               doDetails.setText("");
                                Toast.makeText(getActivity(),jsonObject2.getString("do_details"),Toast.LENGTH_SHORT).show();
                            }
                            if(jsonObject2.has("declared_grade"))
                            {
                               declaredGrade.setText("");
                                Toast.makeText(getActivity(),jsonObject2.getString("declared_grade"),Toast.LENGTH_SHORT).show();
                            }
                            if(jsonObject2.has("total_trucks"))
                            {
                                totalNumberTruck.setText("");
                                Toast.makeText(getActivity(),jsonObject2.getString("total_trucks"),Toast.LENGTH_SHORT).show();
                            }
                            if(jsonObject2.has("total_trucks_sampled"))
                            {
                                totalNumberTruckSampled.setText("");
                                Toast.makeText(getActivity(),jsonObject2.getString("total_trucks_sampled"),Toast.LENGTH_SHORT).show();
                            }

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
                else
                {
                    try{
                  Toast.makeText(getActivity(),errMsg,Toast.LENGTH_SHORT).show();
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
                break;


        }
    }

    public boolean isJSONValid(String test) {
        try {
            new JSONObject(test);
        } catch (JSONException ex) {
            // edited, to include @Arthur's comment
            // e.g. in case JSONArray is valid as well...
            try {
                new JSONArray(test);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }

    private void checkSession() {
        if(PreferenceHelper.contains("session"))
        {
            session = (PreferenceHelper.getString("session", "NA"));
            Log.i(TAG,"session "+ session);
            if(PreferenceHelper.getInteger(Constants.SP_KEY_CURRENT_COLLECTION_ID,0) > 0){
                sampleID = String.valueOf(PreferenceHelper.getInteger(Constants.SP_KEY_CURRENT_COLLECTION_ID));
                getSampleDataFromDB(PreferenceHelper.getInteger(Constants.SP_KEY_CURRENT_COLLECTION_ID));
            }
//            getDataFromSession(session);
        }
        else
        {
            Log.i(TAG,"session NA");
            Toast.makeText(getActivity(),"Please Login",Toast.LENGTH_SHORT).show();
            Intent in = new Intent(getActivity().getApplicationContext(), LoginActivity.class);
            startActivity(in);
            in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            getActivity().finish();
        }
    }

    private void getSampleDataFromDB(int localSampleId) {
        SampleCustomerDBHelper sampleCustomerDBHelper = new SampleCustomerDBHelper(AppDatabase.getAppDatabase(getActivity()), this);
        sampleCustomerDBHelper.getSamplCustomerByLocalSampleId(localSampleId, Constants.SELECT_SAMPLE_CUSTOMER_BY_ID);
    }

    private void getDataFromSession(String session) {
        Log.i(TAG,"1");
        Volley volley=Volley.getInstance();
        JSONObject jsonObject= null;
        volley.postSession(Constants.apiCheckLogin,jsonObject,this,session,Constants.REQ_POST_CHECK_LOGIN);
    }
}
