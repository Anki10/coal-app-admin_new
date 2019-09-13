package com.anova.indiaadmin;


import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.anova.indiaadmin.database.AppDatabase;
import com.anova.indiaadmin.database.AppDatabaseResponse;
import com.anova.indiaadmin.database.area.Area;
import com.anova.indiaadmin.database.area.SubsidiaryAreaHelper;
import com.anova.indiaadmin.database.location.AreaLocationHelper;
import com.anova.indiaadmin.database.location.Location;
import com.anova.indiaadmin.database.samples.SampleDBHelper;
import com.anova.indiaadmin.database.samples.SampleEntity;
import com.anova.indiaadmin.database.subsidiary.Subsidiary;
import com.anova.indiaadmin.database.subsidiary.SubsidiaryHelper;
import com.anova.indiaadmin.network.AppNetworkResponse;
import com.anova.indiaadmin.network.Volley;
import com.anova.indiaadmin.utils.Constants;
import com.anova.indiaadmin.utils.DecimalFilter;
import com.anova.indiaadmin.utils.FormatConversionHelper;
import com.anova.indiaadmin.utils.PreferenceHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orhanobut.logger.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


/**
 * A simple {@link Fragment} subclass.
 */
public class SampleDetailsFragment extends Fragment implements AdapterView.OnItemSelectedListener, AppNetworkResponse, AppDatabaseResponse {


    @BindView(R.id.subsidiaryName)
    Spinner subsidiaryName;
    Unbinder unbinder;
    @BindView(R.id.areaName)
    Spinner areaName;
    @BindView(R.id.locationName)
    Spinner locationName;
    @BindView(R.id.dateCollection)
    TextView dateCollection;
    @BindView(R.id.liftingType)
    Spinner liftingType;
    @BindView(R.id.auctionType_spinner)
    Spinner auctionType_spinner;
    @BindView(R.id.totalQuantityLifted)
    EditText totalQuantityLifted;
    @BindView(R.id.totalQuantitySampled)
    EditText totalQuantitySampled;

    String getDate, selectedDate, sampleID = "";
    String editMode = "false";
    Calendar calendar;
    int day = 0, month = 0, year = 0;
    Calendar myCalendar = Calendar.getInstance();
    HomeActivity mCallback;
    @BindView(R.id.typeSampling)
    Spinner typeSampling;

    private String subsidery;

    Double lifted_value;

    JSONObject response;
    JSONArray responseData;

    private String location_name;


    Bundle bundle;
    String returnJson, areaReturn, locationReturn;
    ArrayAdapter<String> liftingTypeAdapter, auctionTypeAdapter, typeSamplingAdapter, subsidiaryAdapter, areaNameAdapter, locationNameAdapter;
    List<String> subsidiaryNameList = new ArrayList<String>();
    List<String> areaNameList = new ArrayList<String>();
    List<String> locationNameList = new ArrayList<String>();
    List<String> locationCodeList = new ArrayList<>();
    List<String> typeSamplingList = new ArrayList<String>();
    List<String> liftingTypeList = new ArrayList<String>();
    List<Subsidiary> subsidiaryList;
    List<Area> areaList;
    List<Location> locationsList;
    List<Location> selectlocationlist;

    private String location_code;
    private String declared_grade;

    private String submit_status = "no";


    private DecimalFilter filter_decimal;

    boolean firstTime = false;
    boolean subsidiaryCheck = false, areaCheck = false;

    String quantityLiftedPattern = "[0-9]{1,5}(\\.[0-9]{1,3})?";
    boolean editModeBoolean;

    private String savedTypeSampling;
    private boolean overRideSamplingTypeIssue = false;


    public interface OpenCustomerInterface {
        public void openCustomerFrag(String i, boolean firstTimeCheck, String liftType, boolean deleteExistingCustomers);
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
        mCallback = null;
        super.onDetach();
    }

    public SampleDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sample_details, container, false);
        unbinder = ButterKnife.bind(this, view);

        totalQuantitySampled.addTextChangedListener(new DecimalInputTextWatcher(totalQuantitySampled, 3));

        bundle = getArguments();
        Log.e(TAG, "onCreate");

        /*if(bundle!=null)
        {
            editMode=bundle.getString("editMode");
            if(editMode.equals("true")) {
                returnJson = bundle.getString("returnJson");
            }
            Log.e("data",editMode+" : "+returnJson);
        }*/
//
        //totalQuantityLifted.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(5,2)});


        return view;

    }


    private void getData(JSONObject responseJSON) {
        Logger.json(responseJSON.toString());
       /* "sample_id": "4",
                "sample_user_id": "1",
                "subsidiary_name": "Business Services",
                "area": "Business Services",
                "location": "Business Services",
                "date_of_collection": "2018-02-24",
                "lifting_type": "Business Services",
                "auction_type": "Business Services",
                "quantity_lifted": "5.00",
                "quantity_sampled": "5.00",
                "sampling_type": "base",*/
        try {
            SampleEntity sampleEntity = new Gson().fromJson(responseJSON.toString(), new TypeToken<SampleEntity>() {
            }.getType());
            sampleID = String.valueOf(PreferenceHelper.getInteger(Constants.SP_KEY_CURRENT_COLLECTION_ID));
            getDate = getFromPrefs(Constants.date);
            areaReturn = responseJSON.getString("area");
            locationReturn = responseJSON.getString("location");
            selectedDate = getDate;
            if (!selectedDate.isEmpty()){
                dateCollection.setText(getDate);
                dateCollection.setTextColor(getResources().getColor(R.color.colorDarkGrey));
            }


            totalQuantitySampled.setText(responseJSON.getString("quantitySampled"));

            totalQuantityLifted.setText(responseJSON.getString("quantityLifted"));
            mCallback.challanNumber = sampleEntity.getChallanNumber();
            mCallback.qciNumber = sampleEntity.getQciNumber();

            subsidery = responseJSON.getString("subsidiaryName");

            if (responseJSON.getString("subsidiaryName") != null) {
                int spinnerPosition = subsidiaryAdapter.getPosition(responseJSON.getString("subsidiaryName"));
                subsidiaryName.setSelection(spinnerPosition);
                if (subsidiaryList != null && spinnerPosition > 0) {
                    getAreaList(subsidiaryList.get(spinnerPosition - 1).getId());
                }
            }

            if (responseJSON.getString("location") != null){
                location_name = responseJSON.getString("location");


            }

            if (responseJSON.getString("liftingType") != null) {
                int spinnerPosition = liftingTypeAdapter.getPosition(responseJSON.getString("liftingType"));
                liftingType.setSelection(spinnerPosition);

            }

            String lifting_type = responseJSON.getString("liftingType");

            typeSamplingList.clear();
            typeSamplingList.add("Type of Sampling");
       /* typeSamplingList.add("Gross");
        typeSamplingList.add("Individual");*/
            System.out.println("xxx" + liftingType.getSelectedItemId());
            if (lifting_type.equalsIgnoreCase("Rail")) {
                //      typeSamplingList.add("Gross");
                typeSamplingList.add("Individual");
                typeSamplingList.add("Multigrade sampling");
            } else {
                typeSamplingList.add("Gross");
                typeSamplingList.add("Individual");
            }


            typeSamplingAdapter = new ArrayAdapter<String>(getActivity(), R.layout.simple_spinner_item, typeSamplingList);
            typeSamplingAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);


            if (responseJSON.getString("samplingType") != null) {
                final int spinnerPosition = typeSamplingAdapter.getPosition(responseJSON.getString("samplingType"));
                typeSampling.setSelection(spinnerPosition);
                View v = typeSampling.getSelectedView();
                ((TextView)v).setTextColor(getResources().getColor(R.color.colorDarkGrey));


                //             savedTypeSampling = responseJSON.getString("samplingType");
            }

            Log.e("sample_data_2", sampleID);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

                subsidiaryCheck = false;
                areaCheck = false;
        try {
            mCallback = (HomeActivity) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement IFragmentToActivity");
        }

        Log.e(TAG, "onResume");
        setAdapters();
        checkSession();

        if (editMode.equals("true")) {
            firstTime = true;
            subsidiaryCheck = true;
            areaCheck = true;
            //getData();
        }

    }

    public void setAdapters() {
        Log.e(TAG, "setAdapter");
        subsidiaryNameList.clear();
        subsidiaryNameList.add("Name of Subsidiary");
        SubsidiaryHelper.ManageSubsidiaries selectAllSubsidiaries = new SubsidiaryHelper.ManageSubsidiaries(AppDatabase.getAppDatabase(getActivity()), this, Constants.SELECT_ALL_SUBSIDIARIES, null, AppDatabase.SELECT_ACTION);
        selectAllSubsidiaries.execute();
        subsidiaryAdapter = new ArrayAdapter<String>(getActivity(), R.layout.simple_spinner_item, subsidiaryNameList);
        subsidiaryAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        subsidiaryName.setAdapter(subsidiaryAdapter);
        subsidiaryName.setOnItemSelectedListener(this);

        areaNameList.clear();

        areaNameList.add("Name of Area");
        areaNameAdapter = new ArrayAdapter<String>(getActivity(), R.layout.simple_spinner_item, areaNameList);
        areaNameAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        areaName.setAdapter(areaNameAdapter);
        areaName.setOnItemSelectedListener(this);

        locationNameList.clear();
        locationNameList.add("Name of Location");
        locationNameAdapter = new ArrayAdapter<String>(getActivity(), R.layout.simple_spinner_item, locationNameList);
        locationNameAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        locationName.setAdapter(locationNameAdapter);
        locationName.setOnItemSelectedListener(this);

        liftingTypeList.clear();
        liftingTypeList.add("Mode");
        liftingTypeList.add("Road");
        liftingTypeList.add("Rail");
        liftingTypeAdapter = new ArrayAdapter<String>(getActivity(), R.layout.simple_spinner_item, liftingTypeList);
        liftingTypeAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        liftingType.setAdapter(liftingTypeAdapter);
        liftingType.setOnItemSelectedListener(this);

  /*      auctionTypeList.clear();
        auctionTypeList.add("Auction Type");
        auctionTypeList.add("Linkage");
        auctionTypeList.add("Special Forwad E-auction");
        auctionTypeList.add("Spot");
        auctionTypeList.add("Exclusive auction");
        auctionTypeList.add("Shakti");
        auctionTypeAdapter = new ArrayAdapter<String>(getActivity(), R.layout.simple_spinner_item, auctionTypeList);
        auctionTypeAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        auctionType_spinner.setAdapter(auctionTypeAdapter);
        auctionType_spinner.setOnItemSelectedListener(this);*/

        typeSamplingList.clear();
        typeSamplingList.add("Type of Sampling");
       /* typeSamplingList.add("Gross");
        typeSamplingList.add("Individual");*/
        System.out.println("xxx" + liftingType.getSelectedItemId());
        if (liftingType.getSelectedItemId() == 1) {
            //      typeSamplingList.add("Gross");
            typeSamplingList.add("Individual");
            typeSamplingList.add("Multigrade sampling");
        } else {
            typeSamplingList.add("Gross");
            typeSamplingList.add("Individual");
        }


        typeSamplingAdapter = new ArrayAdapter<String>(getActivity(), R.layout.simple_spinner_item, typeSamplingList);
        typeSamplingAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        typeSampling.setAdapter(typeSamplingAdapter);
        typeSampling.setOnItemSelectedListener(this);


    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        try {
            String item = parent.getItemAtPosition(position).toString();
            switch (parent.getId()) {
                case R.id.subsidiaryName:

                    if (!item.equals("Name of Subsidiary")) {
                        ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.colorDarkGrey));
                    }

                    if (subsidiaryList == null || position <= 0) {
                        break;
                    }
                    int selectedSubsidiaryId = subsidiaryList.get(position - 1).getId();
                    String name = subsidiaryList.get(position - 1).getName();


                    if (subsidery != null){
                        if (subsidery.equalsIgnoreCase(name)){
                            subsidiaryCheck = false;
                        }else {
                            subsidiaryCheck = true;
                        }
                    }else {
                        subsidiaryCheck = true;
                    }
                    Log.e(TAG, "spinner clicked : " + item + " : (sub_check = " + Boolean.toString(subsidiaryCheck) + ") : (volleyCheck = " + "( : (areaCheck = " + Boolean.toString(areaCheck));
                    if (mCallback.isNew || subsidiaryCheck) {
                        if (!item.equals("Name of Subsidiary")) {
                            getAreaList(selectedSubsidiaryId);
                            areaCheck = true;

                        }
                    } else {
                       subsidiaryCheck = true;
                    }
                    break;

                case R.id.areaName:

                    if (!item.equals("Name of Area")) {
                        ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.colorDarkGrey));
                    }

                    if (areaList == null || position <= 0) {
                        break;
                    }
                    int selectedAreaId = areaList.get(position - 1).getId();
                    Log.e(TAG, "spinner clicked : " + item + " : (sub_check = " + Boolean.toString(subsidiaryCheck) + ") : (volleyCheck = " + "( : (areaCheck = " + Boolean.toString(areaCheck));
                    if (mCallback.isNew || areaCheck) {
                        if (!item.equals("Name of Area")) {
                            getLocationList(selectedAreaId);
                        }
                    } else {
                        areaCheck = true;
                    }
                    break;

                case R.id.locationName:
                    if (!item.equals("Name of Location")){
                        ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.colorDarkGrey));
                    }


                    break;

                case R.id.liftingType:
                    if (!item.equals("Mode")){
                        ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.colorDarkGrey));
                    }

                        typeSamplingList.clear();

                        System.out.println("xxx" + liftingType.getSelectedItemId());
                        if (liftingType.getSelectedItemId() == 2) {
                            //      typeSamplingList.add("Gross");
                            typeSamplingList.add("Type of Sampling");
                            typeSamplingList.add("Individual");
                            typeSamplingList.add("Multigrade sampling");
                            totalQuantityLifted.setEnabled(true);
                            //                  totalQuantityLifted.setCursorVisible(false);

                        } else if (liftingType.getSelectedItemId() == 0) {
                            typeSamplingList.add("Type of Sampling");
                            typeSamplingList.add("Gross");
                            typeSamplingList.add("Individual");
                            totalQuantityLifted.setFocusable(true);
                        } else if (liftingType.getSelectedItemId() == 1) {
                            typeSamplingList.add("Type of Sampling");
                            typeSamplingList.add("Gross");
                            typeSamplingList.add("Individual");
                            totalQuantityLifted.setEnabled(false);

                    }

                    typeSamplingAdapter.notifyDataSetChanged();

                    break;

                case R.id.typeSampling:
                    if (!item.equals("Type of Sampling")){
                        ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.colorDarkGrey));
                    }

                    break;

                case R.id.auctionType_spinner:

                    if (!item.equals("Auction Type")){
                        ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.colorDarkGrey));
                    }

                    System.out.println("xxx auction");
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void getAreaList(int subsidiaryId) {
        areaNameList.clear();
        areaNameList.add("Name of Area");
        areaName.setSelection(0);
        locationNameList.clear();
        locationNameList.add("Name of Location");
//        locationName.setSelection(0);

        SubsidiaryAreaHelper.ManageSubsidiaryArea getSubsidiaryAreas = new SubsidiaryAreaHelper.ManageSubsidiaryArea(AppDatabase.getAppDatabase(getActivity()), this, Constants.SELECT_AREA_BY_SUBSIDIARY, null, subsidiaryId, SubsidiaryAreaHelper.SELECT_BY_SUBSIDIARY_ACTION);
        getSubsidiaryAreas.execute();
    }

    private void getLocationList(int areaId) {
        locationNameList.clear();
        locationNameList.add("Name of Location");
   //     locationName.setSelection(0);
        AreaLocationHelper.ManageAreaLocation getAreaLocations = new AreaLocationHelper.ManageAreaLocation(AppDatabase.getAppDatabase(getActivity()), this, Constants.SELECT_LOCATIONS_BY_AREA, null, areaId, AreaLocationHelper.SELECT_BY_AREA_ACTION);
        getAreaLocations.execute();

        locationCodeList.add("Name of code");
    }


    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    @Override
    public void onStop() {
        super.onStop();

        try {
            String sub_name = ((String) subsidiaryName.getSelectedItem());
            String area_name = ((String) areaName.getSelectedItem());
            String location = ((String) locationName.getSelectedItem());

            if (submit_status.equalsIgnoreCase("No")){
                if (!sub_name.equalsIgnoreCase("Name of Subsidiary") && !area_name.equalsIgnoreCase("Name of Area") && !location.equalsIgnoreCase("Name of Location")){
                    sendSampling();
                }

                mCallback.isNew = false;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @OnClick({R.id.dateCollection, R.id.submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.dateCollection:
                DatePickerDialog datePickerDialog =
                        new DatePickerDialog(getActivity(), R.style.DateDialogTheme, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                                myCalendar.get(Calendar.DAY_OF_MONTH));
                Calendar c = Calendar.getInstance();


                c.setTime(new Date());
                datePickerDialog.getDatePicker().setMaxDate(c.getTime().getTime());

                c.add(Calendar.MONTH, -3);

                datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
                datePickerDialog.show();
                break;
            case R.id.submit:
                if (validationCheck()) {
                    submit_status = "yes";
                    sendSampling();

                    mCallback.isNew = false;
                }
                break;
        }
    }

   /* public void saveSample(){
        saveIntoPrefs(Constants.SubsidiaryName, (String) subsidiaryName.getSelectedItem());
        saveIntoPrefs(Constants.Area_Location, (String) areaName.getSelectedItem());
        saveIntoPrefs(Constants.Location_code,location_code);
        saveIntoPrefs(Constants.date, selectedDate);

        saveIntoPrefs(Constants.LOCAlSAVE,"No");
    }*/
    public void sendSampling() {
        //TODO save this in local db along with updatedAt and stop fetching data from session
        try {
            if (savedTypeSampling != null && savedTypeSampling.equalsIgnoreCase("gross") && !((String) typeSampling.getSelectedItem()).equalsIgnoreCase("gross")) {
                if (overRideSamplingTypeIssue) {
                    //do nothing and let it delete all customers
                } else {
                    new AlertDialog.Builder(getActivity())
                            .setTitle("Warning")
                            .setMessage("You have changed sampling type from Gross to Individual, This will delete existing saved customers")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    overRideSamplingTypeIssue = true;
                                }
                            })
                            .setNegativeButton(android.R.string.no, null).show();
                    return;
                }
            }

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("sample_id", sampleID);
            jsonObject.put("subsidiary", subsidiaryName.getSelectedItem());
            jsonObject.put("area", areaName.getSelectedItem());
            jsonObject.put("location", locationName.getSelectedItem());
            jsonObject.put("collection_date", selectedDate);
            jsonObject.put("lifting_type", liftingType.getSelectedItem());
            jsonObject.put("auction_type", auctionType_spinner.getSelectedItem());
            jsonObject.put("lifted_quantity", totalQuantityLifted.getText().toString());
            jsonObject.put("sampled_quantity", totalQuantitySampled.getText().toString());
            jsonObject.put("sampling_type", typeSampling.getSelectedItem());
            jsonObject.put("editMode", Boolean.valueOf(editMode));

            SampleEntity sampleEntity = new SampleEntity();
            sampleEntity.setSubsidiaryName((String) subsidiaryName.getSelectedItem());
            sampleEntity.setArea((String) areaName.getSelectedItem());
            sampleEntity.setLocation((String) locationName.getSelectedItem());

            sampleEntity.setIsPrimary("false");
            sampleEntity.setCollectionDate(selectedDate);
            sampleEntity.setLiftingType((String) liftingType.getSelectedItem());
            sampleEntity.setAuctionType((String) auctionType_spinner.getSelectedItem());
            sampleEntity.setQuantityLifted(totalQuantityLifted.getText().toString());
            sampleEntity.setQuantitySampled(totalQuantitySampled.getText().toString());
            sampleEntity.setSamplingType((String) typeSampling.getSelectedItem());
            mCallback.samplingType = (String) typeSampling.getSelectedItem();
            mCallback.subsidiary = sampleEntity.getSubsidiaryName();
            mCallback.area = sampleEntity.getArea();
            mCallback.location = sampleEntity.getLocation();
    /*        if (sampleEntity.getAuctionType().equalsIgnoreCase("Special Forwad E-auction")) {
                mCallback.auction_type = sampleEntity.getAuctionType();
            } else {
                mCallback.auction_type = sampleEntity.getAuctionType() + " Auction";
            }*/

            for (int i=0;i<locationsList.size();i++){
                if (((String) locationName.getSelectedItem()).equalsIgnoreCase(locationsList.get(i).getName())){
                    location_code = locationsList.get(i).getLocation_code();
                    declared_grade = locationsList.get(i).getDeclared_grade();
                }
            }

            sampleEntity.setLocation_code(location_code);


            saveIntoPrefs(Constants.SubsidiaryName, sampleEntity.getSubsidiaryName());
            saveIntoPrefs(Constants.Area_Location, sampleEntity.getLocation());
            saveIntoPrefs(Constants.Location_code,location_code);
            saveIntoPrefs(Constants.declared_grade,declared_grade);
            saveIntoPrefs(Constants.date, sampleEntity.getCollectionDate());

            if (sampleID != null && sampleID.length() > 0) {
                sampleEntity.setLocalSampleId(Integer.parseInt(sampleID));
                SampleDBHelper sampleDBHelper = new SampleDBHelper(AppDatabase.getAppDatabase(getActivity()), this);
                sampleDBHelper.updateSample(sampleEntity, Constants.UPDATE_SAMPLE);
            } else {
                SampleDBHelper sampleDBHelper = new SampleDBHelper(AppDatabase.getAppDatabase(getActivity()), this);
                sampleDBHelper.insertSample(sampleEntity, Constants.INSERT_SAMPLE);
            }

            Log.e("json", jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private boolean validationCheck() {

        if (liftingType.getSelectedItemId() == 1) {
            try {
                if (subsidiaryName.getSelectedItemId() == 0) {
                    Toast.makeText(getActivity(), "Select Subsidiary Name", Toast.LENGTH_SHORT).show();
                } else if (areaName.getSelectedItemId() == 0) {
                    Toast.makeText(getActivity(), "Select Area Name", Toast.LENGTH_SHORT).show();
                } else if (locationName.getSelectedItemId() == 0) {
                    Toast.makeText(getActivity(), "Select Location Name", Toast.LENGTH_SHORT).show();
                } else if (selectedDate == null) {
                    Toast.makeText(getActivity(), "Select Date of Collection", Toast.LENGTH_SHORT).show();
                } else if (liftingType.getSelectedItemId() == 0) {
                    Toast.makeText(getActivity(), "Select Mode", Toast.LENGTH_SHORT).show();
                } /*else if (auctionType_spinner.getSelectedItemId() == 0) {
                    Toast.makeText(getActivity(), "Select Auction Type", Toast.LENGTH_SHORT).show();
                }*/ else if (totalQuantitySampled.getText().length() == 0) {
                    Toast.makeText(getActivity(), "Enter Total Quantity Sampled", Toast.LENGTH_SHORT).show();
                } else if (!(totalQuantitySampled.getText().toString()).matches(quantityLiftedPattern)) {
                    Toast.makeText(getActivity(), "Sample Quantity should 5 digit upto 3 decimal place only", Toast.LENGTH_SHORT).show();
                    totalQuantitySampled.setText("");
                }

                else if (!isValidFloat(totalQuantitySampled.getText().toString())) {
                    Toast.makeText(getActivity(), "Total Quantity Sampled input is invalid", Toast.LENGTH_SHORT).show();
                } else if (typeSampling.getSelectedItemId() == 0) {
                    Toast.makeText(getActivity(), "Select Type Sampling", Toast.LENGTH_SHORT).show();
                } else {
                    return true;
                }
                return false;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        } else {

            try {
                if (totalQuantityLifted.getText().toString().length() > 0){
                    lifted_value = (Double.parseDouble(totalQuantityLifted.getText().toString()) * 1000);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            try {
                if (subsidiaryName.getSelectedItemId() == 0) {
                    Toast.makeText(getActivity(), "Select Subsidiary Name", Toast.LENGTH_SHORT).show();
                } else if (areaName.getSelectedItemId() == 0) {
                    Toast.makeText(getActivity(), "Select Area Name", Toast.LENGTH_SHORT).show();
                } else if (locationName.getSelectedItemId() == 0) {
                    Toast.makeText(getActivity(), "Select Location Name", Toast.LENGTH_SHORT).show();
                } else if (selectedDate == null) {
                    Toast.makeText(getActivity(), "Select Date of Collection", Toast.LENGTH_SHORT).show();
                } else if (liftingType.getSelectedItemId() == 0) {
                    Toast.makeText(getActivity(), "Select Mode", Toast.LENGTH_SHORT).show();
                } /*else if (auctionType_spinner.getSelectedItemId() == 0) {
                    Toast.makeText(getActivity(), "Select Auction Type", Toast.LENGTH_SHORT).show();
                }*/ /*else if (totalQuantityLifted.getText().length() == 0) {
                    Toast.makeText(getActivity(), "Enter Total Quantity Lifted", Toast.LENGTH_SHORT).show();

                } *//*else if (!(totalQuantityLifted.getText().toString()).matches(quantityLiftedPattern)) {
                    Toast.makeText(getActivity(), "Quantity Lifted should 5 digit upto 2 decimal place only", Toast.LENGTH_SHORT).show();
                    totalQuantityLifted.setText("");
                } else if (!isValidFloat(totalQuantityLifted.getText().toString())) {
                    Toast.makeText(getActivity(), "Total Quantity Lifted input is invalid", Toast.LENGTH_SHORT).show();
                }*/ else if (totalQuantitySampled.getText().length() == 0) {
                    Toast.makeText(getActivity(), "Enter Total Quantity Sampled", Toast.LENGTH_SHORT).show();
                } else if (!(totalQuantitySampled.getText().toString()).matches(quantityLiftedPattern)) {
                    Toast.makeText(getActivity(), "Sample Quantity should 5 digit upto 2 decimal place only", Toast.LENGTH_SHORT).show();
                    totalQuantitySampled.setText("");
                } else if (!isValidFloat(totalQuantitySampled.getText().toString())) {
                    Toast.makeText(getActivity(), "Total Quantity Sampled input is invalid", Toast.LENGTH_SHORT).show();
                }  else if (typeSampling.getSelectedItemId() == 0) {
                    Toast.makeText(getActivity(), "Select Type Sampling", Toast.LENGTH_SHORT).show();
                }/* else if (totalQuantityLifted.getText().toString().length() > 0 && lifted_value < Double.parseDouble(totalQuantitySampled.getText().toString())) {
                    Toast.makeText(getActivity(), "Sample Quantity should be less than or equal to Quantity Lifted", Toast.LENGTH_SHORT).show();
                    totalQuantitySampled.setText("");
                }*/
                else {
                    return true;
                }
                return false;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
    }

    public boolean isValidFloat(String value) {
        try {
            float temp = Float.valueOf(value);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {

            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            year = year;
            month = monthOfYear + 1;
            day = dayOfMonth;

            selectedDate = FormatConversionHelper.getFormatedDateTime(day + "-" + month + "-" + year, "dd-MM-yyyy", "dd-MM-yyyy");
            Log.e("date", "selectDate after change " + selectedDate);

            dateCollection.setText(selectedDate);
            dateCollection.setTextColor(getResources().getColor(R.color.colorDarkGrey));
        }

    };

    @Override
    public void onDatabaseResSuccess(JSONObject jsonObject, int reqCode) {
        switch (reqCode) {
            case Constants.SELECT_ALL_SUBSIDIARIES: {
                try {
                    if (jsonObject != null) {
                        Logger.json(jsonObject.toString());
                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<Subsidiary>>() {
                        }.getType();
                        subsidiaryList = gson.fromJson(jsonObject.getJSONArray("data").toString(), listType);
                        for (Subsidiary subsidiary : subsidiaryList) {
                            subsidiaryNameList.add(subsidiary.getName());
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            }
            case Constants.SELECT_AREA_BY_SUBSIDIARY: {
                try {
                    if (jsonObject != null) {
                        Logger.json(jsonObject.toString());
                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<Area>>() {
                        }.getType();
                        areaList = gson.fromJson(jsonObject.getJSONArray("data").toString(), listType);
                        for (Area area : areaList) {
                            areaNameList.add(area.getName());
                        }
                        areaNameAdapter.notifyDataSetChanged();
                        if (areaReturn != null && areaReturn.length() > 0) {
                            int spinnerPosition = areaNameAdapter.getPosition(areaReturn);
                            areaReturn = null;
                            areaName.setSelection(spinnerPosition);
                            if (areaList != null && spinnerPosition > 0) {
                                getLocationList(areaList.get(spinnerPosition - 1).getId());
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            }
            case Constants.SELECT_LOCATIONS_BY_AREA: {
                try {
                    if (jsonObject != null) {
                        Logger.json(jsonObject.toString());
                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<Location>>() {
                        }.getType();
                        locationsList = gson.fromJson(jsonObject.getJSONArray("data").toString(), listType);
                        for (Location location : locationsList) {
                            locationNameList.add(location.getName());

                            locationCodeList.add(location.getLocation_code());
                        }
                        locationNameAdapter.notifyDataSetChanged();

                        if (locationReturn != null && locationReturn.length() > 0) {
                            int spinnerPosition = locationNameAdapter.getPosition(locationReturn);
                            locationReturn = null;
                            locationName.setSelection(spinnerPosition);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }

            case Constants.INSERT_SAMPLE: {
                if (jsonObject != null) {

                        try {
                            sampleID = String.valueOf(jsonObject.getLong("sampleId"));
                            PreferenceHelper.putInteger(Constants.SP_KEY_CURRENT_COLLECTION_ID, Integer.valueOf(sampleID));
                            //                    Toast.makeText(getActivity(), "Sample Detail Saved at " + sampleID, Toast.LENGTH_SHORT).show();
                            if (submit_status.equalsIgnoreCase("yes")) {
                                mCallback.openCustomerFrag(sampleID, firstTime, liftingType.getSelectedItem().toString(), overRideSamplingTypeIssue);
                            }Log.e("sampleID_sa", sampleID + " : " + Boolean.valueOf(firstTime));

                        } catch (JSONException e) {
                            e.printStackTrace();

                    }

                }
                break;
            }

            case Constants.UPDATE_SAMPLE: {
                //         Toast.makeText(mCallback, "Sample data updated", Toast.LENGTH_SHORT).show();
                Log.e("sampleID_sa", sampleID + " : " + Boolean.valueOf(firstTime));
                if (submit_status.equalsIgnoreCase("yes")){
                    try {
                        mCallback.openCustomerFrag(sampleID, firstTime, liftingType.getSelectedItem().toString(), overRideSamplingTypeIssue);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

            }

            case Constants.SELECT_SAMPLE_BY_ID: {
                if (jsonObject != null) {
                    Logger.d(jsonObject);
                    getData(jsonObject);
                }
            }
        }
    }

    @Override
    public void onResSuccess(JSONObject jsonObject, int reqCode) {
        Log.e("ResSuccess", jsonObject + " : " + reqCode);
        switch (reqCode) {
            case Constants.REQ_POST_CHECK_LOGIN:

                try {
                    response = jsonObject;
                    editModeBoolean = response.getBoolean("editMode");
                    if (editModeBoolean) {
                        returnJson = response.getString("return");
                        editMode = "true";
//                        getData();
                        Log.e(TAG, Boolean.toString(editModeBoolean) + " : " + returnJson);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }


    @Override
    public void onResFailure(String errCode, String errMsg, int reqCode, JSONObject jsonObject) {
        Log.e("ResFailure", reqCode + " : " + errMsg);
        Toast.makeText(getActivity(), errMsg, Toast.LENGTH_SHORT).show();
        switch (reqCode) {

            case Constants.REQ_POST_CHECK_LOGIN:
                Intent in = new Intent(getActivity().getApplicationContext(), LoginActivity.class);
                startActivity(in);
                in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                getActivity().finish();
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

    String session;
    final String TAG = "SampleDetailFrag";

    private void checkSession() {
        if (PreferenceHelper.contains("session")) {
            session = (PreferenceHelper.getString("session", "NA"));
            Log.i(TAG, "session " + session);
            if (PreferenceHelper.getInteger(Constants.SP_KEY_CURRENT_COLLECTION_ID, 0) > 0) {
                sampleID = String.valueOf(PreferenceHelper.getInteger(Constants.SP_KEY_CURRENT_COLLECTION_ID));
                getSampleDataFromDB(PreferenceHelper.getInteger(Constants.SP_KEY_CURRENT_COLLECTION_ID));
            }
//            getDataFromSession(session);
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
        SampleDBHelper sampleDBHelper = new SampleDBHelper(AppDatabase.getAppDatabase(getActivity()), this);
        sampleDBHelper.getSampleById(localSampleId, Constants.SELECT_SAMPLE_BY_ID);
    }

    //TODO don't get the data from session, get the selected sampling record data from local DB
    private void getDataFromSession(String session) {
        Volley volley = Volley.getInstance();
        JSONObject jsonObject = null;
        volley.postSession(Constants.apiCheckLogin, jsonObject, this, session, Constants.REQ_POST_CHECK_LOGIN);
    }

    public void saveIntoPrefs(String key, String value) {
        SharedPreferences prefs = getActivity().getSharedPreferences(Constants.PREF_NAME, getActivity().MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString(key, value);
        edit.commit();
    }

    public String getFromPrefs(String key) {
        SharedPreferences prefs = getActivity().getSharedPreferences(Constants.PREF_NAME, getActivity().MODE_PRIVATE);
        return prefs.getString(key, "");
    }

    public class DecimalInputTextWatcher implements TextWatcher {

        private String mPreviousValue;
        private int mCursorPosition;
        private boolean mRestoringPreviousValueFlag;
        private int mDigitsAfterZero;
        private EditText mEditText;

        public DecimalInputTextWatcher(EditText editText, int digitsAfterZero) {
            mDigitsAfterZero = digitsAfterZero;
            mEditText = editText;
            mPreviousValue = "";
            mRestoringPreviousValueFlag = false;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            if (!mRestoringPreviousValueFlag) {
                mPreviousValue = s.toString();
                mCursorPosition = mEditText.getSelectionStart();
            }
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (!mRestoringPreviousValueFlag) {

                if (!isValid(s.toString())) {
                    mRestoringPreviousValueFlag = true;
                    restorePreviousValue();
                }

            } else {
                mRestoringPreviousValueFlag = false;
            }
        }

        private void restorePreviousValue() {
            mEditText.setText(mPreviousValue);
            mEditText.setSelection(mCursorPosition);
        }

        private boolean isValid(String s) {
            Pattern patternWithDot = Pattern.compile("[0-9]*((\\.[0-9]{0," + mDigitsAfterZero + "})?)||(\\.)?");
            Pattern patternWithComma = Pattern.compile("[0-9]*((,[0-9]{0," + mDigitsAfterZero + "})?)||(,)?");

            Matcher matcherDot = patternWithDot.matcher(s);
            Matcher matcherComa = patternWithComma.matcher(s);

            return matcherDot.matches() || matcherComa.matches();
        }
    }


}

