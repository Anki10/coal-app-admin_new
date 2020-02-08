package com.anova.indiaadmin;


import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.anova.indiaadmin.adapters.CustomerFormListAdapter;
import com.anova.indiaadmin.database.AppDatabase;
import com.anova.indiaadmin.database.AppDatabaseResponse;
import com.anova.indiaadmin.database.customers.CustomerDBHelper;
import com.anova.indiaadmin.database.customers.CustomerEntity;
import com.anova.indiaadmin.database.samplecustomer.SampleCustomerDBHelper;
import com.anova.indiaadmin.database.samplecustomer.SampleCustomerEntity;
import com.anova.indiaadmin.database.samples.SampleDBHelper;
import com.anova.indiaadmin.database.samples.SampleEntity;
import com.anova.indiaadmin.utils.Constants;
import com.anova.indiaadmin.utils.PreferenceHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orhanobut.logger.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


/**
 * A simple {@link Fragment} subclass.
 */
public class CustomerListFragment extends Fragment implements AppDatabaseResponse {

    private static final int DELETE_ALL_SAMPLE_CUSTOMERS_SAMPLE_ID_AND_LOAD_EMPTY_ARRAY = 1001;
    Bundle bundle;
    String returnJson, editMode, sampleID = "", liftType;

    ArrayAdapter<String> customerNameAdapter;
    public ArrayList<String> customerNameList = new ArrayList<String>();
    public ArrayList<String> customerIdList = new ArrayList<>();
    List<CustomerEntity> customerList;
    public List<SampleCustomerEntity> sampleCustomerEntityArrayList = new ArrayList<>();
    List<SampleCustomerEntity> toBeSavedSampleCustomerList = new ArrayList<>();
    String session;
    final String TAG = "CustomerFrag";
    boolean editModeBoolean;
    JSONObject response;
    String lifting_type = "";

    private String submit_status = "no";

    HomeActivity mCallback;

    String quantityLiftedPattern = "[0-9]{1,5}+(\\.[0-9]{1,2})?";
    String truckWagon = "Truck";

    @BindView(R.id.rvCustomerFormList)
    RecyclerView rvCustomerFormList;
    @BindView(R.id.submit)
    Button submit;
    Unbinder unbinder;

    private String data_submit = "no";

    public ArrayList<Integer> idlist = new ArrayList<>();

    int id;

    private String auction_type;

    CustomerFormListAdapter customerFormListAdapter;
    boolean gotAllCustomerList = false, gotSampleCustomerList = false;
    private LinearLayoutManager layoutManager;
    private RecyclerView.SmoothScroller smoothScroller;

    Double qu = 0.0;
    Double qu1 = 0.0;
    Double sample = 0.0;

    private JSONObject object;

    public String cutomer_staus = "no";


    public interface OpenChallanInterface {
        public void openChallanFrag(String i);
    }

    @TargetApi(23)
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.e(TAG, "onAttach context");
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
        Log.e(TAG, "onAttach activity");
        try {
            mCallback = (HomeActivity) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement IFragmentToActivity");
        }
    }

    @Override
    public void onDetach() {
        Log.e(TAG, "onDeatach");
        mCallback = null;
        super.onDetach();
    }

    public CustomerListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_customer, container, false);
        unbinder = ButterKnife.bind(this, view);

        Log.e(TAG, "onCreate");
        bundle = getArguments();


        customerNameList.add("Name of the Customer");

/*        SampleDBHelper sampleDBHelper = new SampleDBHelper(AppDatabase.getAppDatabase(getActivity()), this);
        sampleDBHelper.getSampleById(PreferenceHelper.getInteger(Constants.SP_KEY_CURRENT_COLLECTION_ID), Constants.SELECT_SAMPLE_BY_ID);


            CustomerDBHelper customerDBHelper = new CustomerDBHelper(AppDatabase.getAppDatabase(getActivity()), this);
            customerDBHelper.getFilteredCustomers(Constants.SELECT_ALL_CUSTOMERS, mCallback.subsidiary, mCallback.auction_type);*/


        smoothScroller = new LinearSmoothScroller(getActivity()) {
            @Override
            protected int getVerticalSnapPreference() {
                return LinearSmoothScroller.SNAP_TO_START;
            }
        };
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "onResume");

        liftType = mCallback.liftType;

        gotAllCustomerList = false;

        SampleDBHelper sampleDBHelper = new SampleDBHelper(AppDatabase.getAppDatabase(getActivity()), this);
        sampleDBHelper.getSampleById(PreferenceHelper.getInteger(Constants.SP_KEY_CURRENT_COLLECTION_ID), Constants.SELECT_SAMPLE_BY_ID);

        CustomerDBHelper customerDBHelper = new CustomerDBHelper(AppDatabase.getAppDatabase(getActivity()), this);
        customerDBHelper.getFilteredCustomers(Constants.SELECT_ALL_CUSTOMERS, mCallback.subsidiary, mCallback.auction_type);

        checkSession();


        rvCustomerFormList.setHasFixedSize(true);

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    @OnClick({R.id.submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.submit:
 //               customerFormListAdapter.setData("customer",0);
                toBeSavedSampleCustomerList = customerFormListAdapter.getPreparedData();
                SampleListupdate(object);
                Logger.d("debug data");
                for (int i = 0; i < toBeSavedSampleCustomerList.size(); i++) {
                    if (!validationCheck(toBeSavedSampleCustomerList.get(i))) {
                        smoothScroller.setTargetPosition(i);
                        layoutManager.startSmoothScroll(smoothScroller);


                        return;
                    }
                }
                data_submit = "yes";
                submit_status = "yes";

               /* SampleCustomerDBHelper sampleCustomerDBHelper = new SampleCustomerDBHelper(AppDatabase.getAppDatabase(getActivity()), this);
                sampleCustomerDBHelper.insertAll(toBeSavedSampleCustomerList, Constants.INSERT_SAMPLE_CUSTOMER_LIST);*/


                save();
                break;
        }
    }


    @Override
    public void onStop() {
        super.onStop();
        if (data_submit.equalsIgnoreCase("no")) {
            insetData();
        }

    }

    private void insetData() {
        try {
//            customerFormListAdapter.setData("customer",0);
            toBeSavedSampleCustomerList = customerFormListAdapter.getPreparedData();

            for (int i = 0; i < toBeSavedSampleCustomerList.size(); i++) {
                if (toBeSavedSampleCustomerList.get(i).getAuction_type() != null && toBeSavedSampleCustomerList.get(i).getCustomerName() != null) {

                } else {
                    toBeSavedSampleCustomerList.remove(i);
                }
            }

            SampleListupdate(object);

            save();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private boolean validationCheck(SampleCustomerEntity sampleCustomerEntity) {
        int declaredGradeInt = 0;


        if (sampleCustomerEntity.getDeclaredGrade() != null && sampleCustomerEntity.getDeclaredGrade().length() != 0) {
            declaredGradeInt = Integer.parseInt(sampleCustomerEntity.getDeclaredGrade());
        }

        if (sampleCustomerEntity.getAuction_type() == null) {
            Toast.makeText(getActivity(), "Select auction type", Toast.LENGTH_SHORT).show();
        } else if (sampleCustomerEntity.getCustomerId() == 0) {
            Toast.makeText(getActivity(), "Select Customer Name", Toast.LENGTH_SHORT).show();
        } /*else if (lifting_type.equalsIgnoreCase("Road") && sampleCustomerEntity.getMore_details() == null) {
            Toast.makeText(getActivity(), "Enter Do Details", Toast.LENGTH_SHORT).show();
        }*/ else if (sampleCustomerEntity.getDeclaredGrade() == null || sampleCustomerEntity.getDeclaredGrade().length() == 0) {
            Toast.makeText(getActivity(), "Enter Declared Grade", Toast.LENGTH_SHORT).show();
        } else if (sampleCustomerEntity.getTotalVehicles() == null || sampleCustomerEntity.getTotalVehicles().length() == 0) {
            Toast.makeText(getActivity(), "Enter Total no. of Truck", Toast.LENGTH_SHORT).show();
        } else if (sampleCustomerEntity.getTotalVehiclesSampled() == null || sampleCustomerEntity.getTotalVehiclesSampled().length() == 0) {
            Toast.makeText(getActivity(), "Enter Total no. of Truck Sampled", Toast.LENGTH_SHORT).show();
        } else if (declaredGradeInt > 17) {
            Toast.makeText(getActivity(), "Declared Grade should be less then 17", Toast.LENGTH_SHORT).show();
        } else if (Integer.parseInt(sampleCustomerEntity.getTotalVehiclesSampled()) > Integer.parseInt(sampleCustomerEntity.getTotalVehicles())) {
            Toast.makeText(getActivity(), "Total no. of Truck Sampled should be less than Total no. of Trucks", Toast.LENGTH_SHORT).show();
        } else if (liftType.equalsIgnoreCase("Road") && sampleCustomerEntity.getTotalVehicles().length() > 4) {
            Toast.makeText(getActivity(), "Total number of trucks should be less than or equal to 4 digits", Toast.LENGTH_SHORT).show();
        } else if (liftType.equalsIgnoreCase("Road") && sampleCustomerEntity.getTotalVehiclesSampled().length() > 3) {
            Toast.makeText(getActivity(), "Total number of trucks sampled should be less than or equal to 3 digits", Toast.LENGTH_SHORT).show();
        } /*else if (lifting_type.equalsIgnoreCase("Road") && qu1 * 1000 < sample) {
            Toast.makeText(getActivity(), "Sample Quantity should be less than or equal to Quantity Lifted", Toast.LENGTH_SHORT).show();
        }*//*else if (lifting_type.equalsIgnoreCase("Rail") && Integer.parseInt(sampleCustomerEntity.getTotalVehicles()) > 60){
            Toast.makeText(getActivity(), "Total number of wagons should be less than 60", Toast.LENGTH_SHORT).show();
        }else if (lifting_type.equalsIgnoreCase("Rail") && Integer.parseInt(sampleCustomerEntity.getTotalVehiclesSampled()) > 6){
            Toast.makeText(getActivity(), "Total number of wagons sampled should be less than 6", Toast.LENGTH_SHORT).show();
        }*//*else if (lifting_type.equalsIgnoreCase("Rail") && sampleCustomerEntity.getMore_details() == null){
            Toast.makeText(getActivity(),"Enter RR number or Quantity",Toast.LENGTH_LONG).show();
        }*/

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

    public void saveIntoPrefs(String key, String value) {
        SharedPreferences prefs = getActivity().getSharedPreferences("com.COAL.prefs", getActivity().MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString(key, value);
        edit.commit();
    }

    public String getFromPrefs(String key) {
        SharedPreferences prefs = getActivity().getSharedPreferences("com.COAL.prefs", getActivity().MODE_PRIVATE);
        return prefs.getString(key, "");
    }

    private void save() {
        if (idlist.size() == 0) {
            SampleCustomerDBHelper sampleCustomerDBHelper = new SampleCustomerDBHelper(AppDatabase.getAppDatabase(getActivity()), this);
            sampleCustomerDBHelper.insertAll(toBeSavedSampleCustomerList, Constants.INSERT_SAMPLE_CUSTOMER_LIST);
        } else {


            SampleCustomerEntity pojo = null;

            for (int i = 0; i < toBeSavedSampleCustomerList.size(); i++) { // 1,2,3,4,5

                pojo = toBeSavedSampleCustomerList.get(i);


                if (idlist.contains(pojo.getId())) {

                    pojo.setId(idlist.get(i));

                    toBeSavedSampleCustomerList.set(i, pojo);

                    SampleCustomerDBHelper sampleCustomerDBHelper = new SampleCustomerDBHelper(AppDatabase.getAppDatabase(getActivity()), this);
                    sampleCustomerDBHelper.updateAll(pojo, Constants.INSERT_SAMPLE_CUSTOMER_LIST);


                } else {
                    SampleCustomerDBHelper sampleCustomerDBHelper = new SampleCustomerDBHelper(AppDatabase.getAppDatabase(getActivity()), this);
                    sampleCustomerDBHelper.insertSample(pojo, Constants.INSERT_SAMPLE_CUSTOMER_LIST);
                }
            }
        }

    }

    @Override
    public void onDatabaseResSuccess(JSONObject jsonObject, int reqCode) {
        switch (reqCode) {
            case Constants.SELECT_ALL_CUSTOMERS: {
                try {
                    if (jsonObject != null) {
                        Logger.json(jsonObject.toString());
                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<CustomerEntity>>() {
                        }.getType();
                        customerNameList.clear();
                        customerIdList.clear();

                        customerList = gson.fromJson(jsonObject.getJSONArray("data").toString(), listType);


                        //                  Collections.sort(customerList, String.CASE_INSENSITIVE_ORDER);
                        for (CustomerEntity customer : customerList) {
                            customerNameList.add(customer.getUserName());
                            customerIdList.add(customer.getUserId());
                        }
                        customerIdList.add(0, "");
                        customerNameList.add(0, "Name of the Customer");

    //                    customerFormListAdapter.notifyDataSetChanged();

                        saveArrayList(customerNameList,customerList.get(0).getAuctionType());

                        if (customerNameList.size() > 1) {
                            if (!gotAllCustomerList) {

                                gotAllCustomerList = true;

                                setCustomerFormListAdapter();
                            }
                        } else {
                            //                       setCustomerFormListAdapter();
                        }


                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }

            case Constants.SELECT_SAMPLE_CUSTOMER_BY_ID: {
                Logger.d(jsonObject);
                try {
                    if (jsonObject != null) {
                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<SampleCustomerEntity>>() {
                        }.getType();
                        sampleCustomerEntityArrayList = gson.fromJson(jsonObject.getJSONArray("data").toString(), listType);
                        for (int i = 0; i < sampleCustomerEntityArrayList.size(); i++) {
                            SampleCustomerEntity pojo = sampleCustomerEntityArrayList.get(i);
                            idlist.add(i, pojo.getId());

                            auction_type = sampleCustomerEntityArrayList.get(i).getAuction_type();
                        }

                        gotSampleCustomerList = true;

                        if (auction_type != null) {
                            CustomerDBHelper customerDBHelper = new CustomerDBHelper(AppDatabase.getAppDatabase(getActivity()), this);
                            customerDBHelper.getFilteredCustomers(Constants.SELECT_ALL_CUSTOMERS, mCallback.subsidiary, auction_type);
                        } else {
                            gotAllCustomerList = true;

                            setCustomerFormListAdapter();
                        }

                        id = sampleCustomerEntityArrayList.get(0).getId();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }

            case DELETE_ALL_SAMPLE_CUSTOMERS_SAMPLE_ID_AND_LOAD_EMPTY_ARRAY: {
                SampleCustomerDBHelper sampleCustomerDBHelper = new SampleCustomerDBHelper(AppDatabase.getAppDatabase(getActivity()), this);
                sampleCustomerDBHelper.getSamplCustomerByLocalSampleId(Integer.parseInt(sampleID), Constants.SELECT_SAMPLE_CUSTOMER_BY_ID);
                break;
            }

            case Constants.DELETE_ALL_SAMPLE_CUSTOMERS_SAMPLE_ID: {
                SampleCustomerDBHelper sampleCustomerDBHelper = new SampleCustomerDBHelper(AppDatabase.getAppDatabase(getActivity()), this);
                sampleCustomerDBHelper.insertAll(toBeSavedSampleCustomerList, Constants.INSERT_SAMPLE_CUSTOMER_LIST);
                break;
            }

            case Constants.SELECT_SAMPLE_BY_ID: {
                if (jsonObject != null) {
                    Logger.d(jsonObject);
                    object = jsonObject;
                }
                break;
            }


            case Constants.INSERT_SAMPLE_CUSTOMER_LIST: {
                Logger.d(jsonObject);

                if (submit_status.equalsIgnoreCase("yes")) {

                    try {
                        Toast.makeText(getActivity(), "Customer Detail Saved", Toast.LENGTH_SHORT).show();
                        mCallback.openChallanFrag(sampleID);
                        // Toast.makeText(getActivity(), jsonObject.getString("data"), Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }


                break;
            }
        }
    }

    private void SampleListupdate(JSONObject responseJSON) {
        SampleEntity sampleEntity = new Gson().fromJson(responseJSON.toString(), new TypeToken<SampleEntity>() {
        }.getType());

        lifting_type = sampleEntity.getLiftingType();

        try {

            sampleID = String.valueOf(PreferenceHelper.getInteger(Constants.SP_KEY_CURRENT_COLLECTION_ID));
            sample = Double.parseDouble(sampleEntity.getQuantitySampled());

            qu1 = 0.0;

            try {
                for (SampleCustomerEntity sampleCustomerEntity : toBeSavedSampleCustomerList) {
                    JSONArray jsonArray1 = new JSONArray(sampleCustomerEntity.getMore_details());

                    for (int i = 0; i < jsonArray1.length(); i++) {

                        JSONObject object = jsonArray1.getJSONObject(i);

                        String do_details = object.getString("do_details");
                        String quantity = object.getString("quantity");

                        qu = Double.parseDouble(quantity);

                        qu1 = qu1 + qu;

                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

            DecimalFormat numberFormat = new DecimalFormat("#.00");

            String value = numberFormat.format(qu1);

            sampleEntity.setQuantityLifted(value);

            if (sampleID != null && sampleID.length() > 0) {
                sampleEntity.setLocalSampleId(Integer.parseInt(sampleID));
                SampleDBHelper sampleDBHelper = new SampleDBHelper(AppDatabase.getAppDatabase(getActivity()), this);
                sampleDBHelper.updateSample(sampleEntity, Constants.INSERT_SAMPLE_CUSTOMER_LIST);
            }

            System.out.println("xxxx" + sampleEntity);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setCustomerFormListAdapter() {
        if (gotAllCustomerList && gotSampleCustomerList) {
            if (sampleCustomerEntityArrayList.size() == 0) {
                sampleCustomerEntityArrayList.add(new SampleCustomerEntity());
            }
            boolean allowMoreCustomers = false;
            if (mCallback.samplingType != null) {
                if (mCallback.samplingType.equalsIgnoreCase("gross")) {
                    allowMoreCustomers = true;
                }
            }

            boolean allowGradle = false;
            if (mCallback.samplingType != null) {
                if (mCallback.samplingType.equalsIgnoreCase("Multigrade sampling")) {
                    allowGradle = true;
                }
            }

            customerFormListAdapter = new CustomerFormListAdapter(this, sampleCustomerEntityArrayList, customerList, customerNameList, customerIdList, liftType, sampleID, auction_type, getActivity(), allowMoreCustomers, allowGradle, mCallback.subsidiary);
            layoutManager = new LinearLayoutManager(getActivity());
            rvCustomerFormList.setLayoutManager(layoutManager);
            rvCustomerFormList.setAdapter(customerFormListAdapter);

        }
    }

    public void CustomerDelete(int sample_id) {
        SampleCustomerDBHelper sampleCustomerDBHelper = new SampleCustomerDBHelper(AppDatabase.getAppDatabase(getActivity()), this);
        sampleCustomerDBHelper.deleteSampleCustomersByLocalSampleId(sample_id, DELETE_ALL_SAMPLE_CUSTOMERS_SAMPLE_ID_AND_LOAD_EMPTY_ARRAY);
    }


    private void checkSession() {
        if (PreferenceHelper.contains("session")) {
            session = (PreferenceHelper.getString("session", "NA"));
            Log.i(TAG, "session " + session);
            if (PreferenceHelper.getInteger(Constants.SP_KEY_CURRENT_COLLECTION_ID, 0) > 0) {
                sampleID = String.valueOf(PreferenceHelper.getInteger(Constants.SP_KEY_CURRENT_COLLECTION_ID));
                if (mCallback.deleteExistingCustomers) {
                    SampleCustomerDBHelper sampleCustomerDBHelper = new SampleCustomerDBHelper(AppDatabase.getAppDatabase(getActivity()), this);
                    sampleCustomerDBHelper.deleteSampleCustomersByLocalSampleId(Integer.parseInt(sampleID), DELETE_ALL_SAMPLE_CUSTOMERS_SAMPLE_ID_AND_LOAD_EMPTY_ARRAY);
                } else {
                    getSampleDataFromDB(PreferenceHelper.getInteger(Constants.SP_KEY_CURRENT_COLLECTION_ID));
                }
            }
        } else {
            Log.i(TAG, "session NA");
            Toast.makeText(getActivity(), "Please Login", Toast.LENGTH_SHORT).show();
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

    public void saveArrayList(ArrayList<String> list, String key){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(key, json);
        editor.apply();     // This line is IMPORTANT !!!
    }

}
