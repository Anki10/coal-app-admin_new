package com.anova.indiaadmin.adapters;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.anova.indiaadmin.CustomerListFragment;
import com.anova.indiaadmin.R;
import com.anova.indiaadmin.database.AppDatabase;
import com.anova.indiaadmin.database.AppDatabaseResponse;
import com.anova.indiaadmin.database.customers.CustomerDBHelper;
import com.anova.indiaadmin.database.customers.CustomerEntity;
import com.anova.indiaadmin.database.samplecustomer.MoredetailsEntity;
import com.anova.indiaadmin.database.samplecustomer.SampleCustomerEntity;
import com.anova.indiaadmin.utils.Constants;
import com.anova.indiaadmin.utils.DecimalInputTextWatcher;
import com.anova.indiaadmin.utils.FormatConversionHelper;
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

import butterknife.BindView;
import butterknife.ButterKnife;

public class CustomerFormListAdapter extends RecyclerView.Adapter<CustomerFormListAdapter.CustomerFormViewHolder> implements AppDatabaseResponse {

    private static final String TAG = CustomerFormListAdapter.class.getSimpleName();
    public List<SampleCustomerEntity> sampleCustomerEntityList;
    private List<CustomerEntity> allCustomerList;
    private List<String> customerNameList;
    private List<String>NameList;
    private List<String> customerIdList;
    private String liftType;
    private String sampleID;
    private Context context;
    private boolean allowMoreCustomer;
    private boolean allowMoreGradle;
    private String globalDeclaredGrade;
    public ArrayList<MoredetailsEntity> dolist;
    public ArrayList<MoredetailsEntity> singledolist = new ArrayList<>();
    public ArrayList<MoredetailsEntity> fliterdolist = new ArrayList<>();
    public ArrayList<MoredetailsEntity> fixeddolist = new ArrayList<>();
    public ArrayList<MoredetailsEntity> remaningdolist = new ArrayList<>();
    private SampleCustomerEntity currentSampleCustomer;
    ArrayAdapter<String> auctionTypeAdapter;
    MoredetailsEntity obj;
    private String details = "", quantity = "", rr_number,fsa_number = "",rr_date;
    List<String> auctionTypeList = new ArrayList<String>();
    private CustomerListFragment fragment;
    List<CustomerEntity> customerList;
    private String Subsidery;
    private String mode;
    private String auction_type, customer_name, RR_details;
    private int auction_type_pos, customer_name_pos;

    public ArrayList<MoredetailsEntity> morelist;

    public Boolean AdaperCall = true;

    private int customer_position = 0;

    public ArrayAdapter<String> NameAdapter;

    public ArrayAdapter<String> customerNameAdapter;


    private String AuctionType;

    int pos;

    Calendar myCalendar = Calendar.getInstance();

    int day = 0, month = 0, year = 0;

    String getDate, selectedDate;


    public CustomerFormListAdapter(CustomerListFragment fragment, List<SampleCustomerEntity> sampleCustomerEntityList, List<CustomerEntity> allCustomerList, List<String> customerNameList, List<String> customerIdList, String liftType, String sampleID, String auction_type, Context context, boolean allowMoreCustomer, boolean moreGradle, String Subsidery) {
        this.fragment = fragment;
        this.sampleCustomerEntityList = sampleCustomerEntityList;
        this.allCustomerList = allCustomerList;
        this.customerIdList = customerIdList;
        this.customerNameList = customerNameList;
        this.liftType = liftType;
        this.sampleID = sampleID;
        this.context = context;
        this.allowMoreCustomer = allowMoreCustomer;
        this.allowMoreGradle = moreGradle;
        this.Subsidery = Subsidery;
        this.AuctionType = auction_type;


    }

    @Override
    public CustomerFormViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());
        View v = inflater.inflate(R.layout.item_row_customer_form, parent, false);
        // set the view's size, margins, paddings and layout parameters

        CustomerFormViewHolder vh = new CustomerFormViewHolder(v, liftType, customerNameList);

        vh.setIsRecyclable(false);

        dolist = new ArrayList<>();


        return vh;
    }

    @Override
    public void onBindViewHolder(final CustomerFormViewHolder holder, final int position) {
        Log.e(TAG, "onBindViewHolder: position : " + position);

        final int currentViewPosition = position;
        currentSampleCustomer = sampleCustomerEntityList.get(position);
        currentSampleCustomer.setLiftingType(liftType);
        currentSampleCustomer.setLocalSampleId(Integer.parseInt(sampleID));
        holder.quantity.addTextChangedListener(new DecimalInputTextWatcher(holder.quantity, 3));

        pos = position;

        NameList = new ArrayList<>();


        customer_position = sampleCustomerEntityList.size();

        try {
            if (sampleCustomerEntityList.get(0).getMore_details() != null) {
                JSONArray jsonArray1 = new JSONArray(sampleCustomerEntityList.get(0).getMore_details());

                JSONObject object = jsonArray1.getJSONObject(0);

                String do_details = object.getString("do_details");

                rr_number = do_details;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


        if (position == 0) {
            holder.tv_challan_code.setText("Challan Code : A");
            currentSampleCustomer.setChallanCode("A");
        }
        if (position == 1) {
            holder.tv_challan_code.setText("Challan Code : B");
            currentSampleCustomer.setChallanCode("B");
        }
        if (position == 2) {
            holder.tv_challan_code.setText("Challan Code : C");
            currentSampleCustomer.setChallanCode("C");
        }
        if (position == 3) {
            holder.tv_challan_code.setText("Challan Code : D");
            currentSampleCustomer.setChallanCode("D");
        }
        if (position == 4) {
            holder.tv_challan_code.setText("Challan Code : E");
            currentSampleCustomer.setChallanCode("E");
        }
        if (position == 5) {
            holder.tv_challan_code.setText("Challan Code : F");
            currentSampleCustomer.setChallanCode("F");
        }
        if (position == 6) {
            holder.tv_challan_code.setText("Challan Code : G");
            currentSampleCustomer.setChallanCode("G");
        }
        if (position == 7) {
            holder.tv_challan_code.setText("Challan Code : H");
            currentSampleCustomer.setChallanCode("H");
        }
        if (position == 8) {
            holder.tv_challan_code.setText("Challan Code : I");
            currentSampleCustomer.setChallanCode("I");
        }
        if (position == 9) {
            holder.tv_challan_code.setText("Challan Code : J");
            currentSampleCustomer.setChallanCode("J");
        }
        if (position == 10) {
            holder.tv_challan_code.setText("Challan Code : K");
            currentSampleCustomer.setChallanCode("K");
        }
        if (position == 11) {
            holder.tv_challan_code.setText("Challan Code : L");
            currentSampleCustomer.setChallanCode("L");
        }
        if (position == 12) {
            holder.tv_challan_code.setText("Challan Code : M");
            currentSampleCustomer.setChallanCode("M");
        }
        if (position == 13) {
            holder.tv_challan_code.setText("Challan Code : N");
            currentSampleCustomer.setChallanCode("N");
        }
        if (position == 14) {
            holder.tv_challan_code.setText("Challan Code : o");
            currentSampleCustomer.setChallanCode("O");
        }
        if (position == 15) {
            holder.tv_challan_code.setText("Challan Code : P");
            currentSampleCustomer.setChallanCode("p");
        }
        if (position == 16) {
            holder.tv_challan_code.setText("Challan Code : Q");
            currentSampleCustomer.setChallanCode("Q");
        }
        if (position == 17) {
            holder.tv_challan_code.setText("Challan Code : R");
            currentSampleCustomer.setChallanCode("R");
        }
        if (position == 18) {
            holder.tv_challan_code.setText("Challan Code : S");
            currentSampleCustomer.setChallanCode("S");
        }
        if (position == 19) {
            holder.tv_challan_code.setText("Challan Code : T");
            currentSampleCustomer.setChallanCode("T");
        }
        if (position == 20) {
            holder.tv_challan_code.setText("Challan Code : U");
            currentSampleCustomer.setChallanCode("U");
        }
        if (position == 21) {
            holder.tv_challan_code.setText("Challan Code : V");
            currentSampleCustomer.setChallanCode("V");
        }
        if (position == 22) {
            holder.tv_challan_code.setText("Challan Code : W");
            currentSampleCustomer.setChallanCode("W");
        }
        if (position == 23) {
            holder.tv_challan_code.setText("Challan Code : X");
            currentSampleCustomer.setChallanCode("X");
        }
        if (position == 24) {
            holder.tv_challan_code.setText("Challan Code : Y");
            currentSampleCustomer.setChallanCode("Y");
        }
        if (position == 25) {
            holder.tv_challan_code.setText("Challan Code : Z");
            currentSampleCustomer.setChallanCode("Z");
        }
        if (position == 26) {
            holder.tv_challan_code.setText("Challan Code : A1");
            currentSampleCustomer.setChallanCode("A1");
        }
        if (position == 27) {
            holder.tv_challan_code.setText("Challan Code : B1");
            currentSampleCustomer.setChallanCode("B1");
        }
        if (position == 28) {
            holder.tv_challan_code.setText("Challan Code : C1");
            currentSampleCustomer.setChallanCode("C1");
        }
        if (position == 29) {
            holder.tv_challan_code.setText("Challan Code : D1");
            currentSampleCustomer.setChallanCode("D1");
        }
        if (position == 30) {
            holder.tv_challan_code.setText("Challan Code : E1");
            currentSampleCustomer.setChallanCode("E1");
        }
        if (position == 31) {
            holder.tv_challan_code.setText("Challan Code : F1");
            currentSampleCustomer.setChallanCode("F1");
        }
        if (position == 32) {
            holder.tv_challan_code.setText("Challan Code : G1");
            currentSampleCustomer.setChallanCode("G1");
        }
        if (position == 33) {
            holder.tv_challan_code.setText("Challan Code : H1");
            currentSampleCustomer.setChallanCode("H1");
        }
        if (position == 34) {
            holder.tv_challan_code.setText("Challan Code : I1");
            currentSampleCustomer.setChallanCode("I1");
        }
        if (position == 35) {
            holder.tv_challan_code.setText("Challan Code : J1");
            currentSampleCustomer.setChallanCode("J1");
        }
        if (position == 36) {
            holder.tv_challan_code.setText("Challan Code : K1");
            currentSampleCustomer.setChallanCode("K1");
        }
        if (position == 37) {
            holder.tv_challan_code.setText("Challan Code : L1");
            currentSampleCustomer.setChallanCode("L1");
        }
        if (position == 38) {
            holder.tv_challan_code.setText("Challan Code : M1");
            currentSampleCustomer.setChallanCode("M1");
        }
        if (position == 39) {
            holder.tv_challan_code.setText("Challan Code : N1");
            currentSampleCustomer.setChallanCode("N1");
        }
        if (position == 40) {
            holder.tv_challan_code.setText("Challan Code : O1");
            currentSampleCustomer.setChallanCode("O1");
        }
        if (position == 41) {
            holder.tv_challan_code.setText("Challan Code : P1");
            currentSampleCustomer.setChallanCode("p1");
        }
        if (position == 42) {
            holder.tv_challan_code.setText("Challan Code : Q1");
            currentSampleCustomer.setChallanCode("Q1");
        }
        if (position == 43) {
            holder.tv_challan_code.setText("Challan Code : R1");
            currentSampleCustomer.setChallanCode("R1");
        }
        if (position == 44) {
            holder.tv_challan_code.setText("Challan Code : S1");
            currentSampleCustomer.setChallanCode("S1");
        }
        if (position == 45) {
            holder.tv_challan_code.setText("Challan Code : T1");
            currentSampleCustomer.setChallanCode("T1");
        }
        if (position == 46) {
            holder.tv_challan_code.setText("Challan Code : U1");
            currentSampleCustomer.setChallanCode("U1");
        }
        if (position == 47) {
            holder.tv_challan_code.setText("Challan Code : V1");
            currentSampleCustomer.setChallanCode("V1");
        }
        if (position == 48) {
            holder.tv_challan_code.setText("Challan Code : W1");
            currentSampleCustomer.setChallanCode("W1");
        }
        if (position == 49) {
            holder.tv_challan_code.setText("Challan Code : X1");
            currentSampleCustomer.setChallanCode("X1");
        }
        if (position == 50) {
            holder.tv_challan_code.setText("Challan Code : Y1");
            currentSampleCustomer.setChallanCode("Y1");
        }
        if (position == 51) {
            holder.tv_challan_code.setText("Challan Code : Z1");
            currentSampleCustomer.setChallanCode("Z1");
        }

        holder.auctionType_spinner.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ((TextView) holder.auctionType_spinner.getSelectedView()).setTextColor(Color.BLACK);
            }
        });


        if (!getFromPrefs(Constants.declared_grade).equalsIgnoreCase("null")){
            holder.declaredGrade.setText(getFromPrefs(Constants.declared_grade));
        }else {
            holder.declaredGrade.setText("");
        }

     //   setCustomerData(currentViewPosition);

   /*     details = holder.doDetails.getText().toString();
        quantity = holder.quantity.getText().toString();*/

        auctionTypeList.clear();
        auctionTypeList.add("Auction Type");
        auctionTypeList.add("Linkage");
        auctionTypeList.add("Special Forwad E-auction");
        auctionTypeList.add("Spot");
        auctionTypeList.add("Exclusive auction");
        auctionTypeList.add("Shakti");
        auctionTypeAdapter = new ArrayAdapter<String>(context, R.layout.simple_spinner_item, auctionTypeList);
        auctionTypeAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        holder.auctionType_spinner.setAdapter(auctionTypeAdapter);

        if (getArrayList(currentSampleCustomer.getAuction_type()) != null){

            customerNameList = getArrayList(currentSampleCustomer.getAuction_type());

            customerNameAdapter = new ArrayAdapter<String>(context, R.layout.simple_spinner_item, customerNameList);
            customerNameAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
            holder.customerName.setAdapter(customerNameAdapter);
        }else {
            customerNameAdapter = new ArrayAdapter<String>(context, R.layout.simple_spinner_item, customerNameList);
            customerNameAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
            holder.customerName.setAdapter(customerNameAdapter);
        }

        final AdapterView.OnItemSelectedListener auction_listener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                currentSampleCustomer = sampleCustomerEntityList.get(currentViewPosition);

                currentSampleCustomer.setAuction_type(auctionTypeList.get(i));

                sampleCustomerEntityList.set(currentViewPosition, currentSampleCustomer);

                CustomerDBHelper customerDBHelper = new CustomerDBHelper(AppDatabase.getAppDatabase(context), fragment);
                customerDBHelper.getFilteredCustomers(Constants.SELECT_ALL_CUSTOMERS, Subsidery, currentSampleCustomer.getAuction_type());

                auction_type_pos = i;

                customerNameAdapter = new ArrayAdapter<String>(context, R.layout.simple_spinner_item, fragment.customerNameList);
                customerNameAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
                holder.customerName.setAdapter(customerNameAdapter);

                ((TextView) adapterView.getChildAt(0)).setTextColor(Color.BLACK);

                if (currentViewPosition == 0) {
                    auction_type = auctionTypeList.get(position);
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        };

        holder.auctionType_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                currentSampleCustomer = sampleCustomerEntityList.get(currentViewPosition);
                if (currentSampleCustomer.getAuction_type() != null){
                    ((TextView) adapterView.getChildAt(0)).setTextColor(Color.BLACK);
                }
                holder.auctionType_spinner.setOnItemSelectedListener(auction_listener);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                System.out.printf("noting select");
            }
        });

  /*      holder.auctionType_spinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                return false;
            }
        });*/
       final AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                currentSampleCustomer = sampleCustomerEntityList.get(currentViewPosition);

                try {
                    currentSampleCustomer.setCustomerId(Integer.parseInt(fragment.customerIdList.get(i)));
                    currentSampleCustomer.setCustomerName(fragment.customerNameList.get(i));


                    sampleCustomerEntityList.set(currentViewPosition, currentSampleCustomer);

                    ((TextView) adapterView.getChildAt(0)).setTextColor(Color.BLACK);

                    customer_name_pos = position;

                    if (currentViewPosition == 0) {
                        customer_name = customerNameList.get(position);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        };

       holder.customerName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
           @Override
           public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

               currentSampleCustomer = sampleCustomerEntityList.get(currentViewPosition);


               if (currentSampleCustomer.getCustomerName() != null){

                   ((TextView) adapterView.getChildAt(0)).setTextColor(Color.BLACK);
               }



               holder.customerName.setOnItemSelectedListener(listener);
           }

           @Override
           public void onNothingSelected(AdapterView<?> adapterView) {

           }
       });

       holder.customerName.setOnTouchListener(new View.OnTouchListener() {
           @Override
           public boolean onTouch(View view, MotionEvent motionEvent) {
               currentSampleCustomer = sampleCustomerEntityList.get(currentViewPosition);

               CustomerDBHelper customerDBHelper = new CustomerDBHelper(AppDatabase.getAppDatabase(context), fragment);
               customerDBHelper.getFilteredCustomers(Constants.SELECT_ALL_CUSTOMERS, Subsidery, currentSampleCustomer.getAuction_type());
               return false;
           }
       });


       /* holder.customerName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {
                switch (parent.getId()) {
                    case R.id.customerName: {
                        if (position <= 0) {

                        } else {
                        *//*    System.out.println("xxx" + customerIdList.get(position));
                            System.out.println("xxx" + customerNameList.get(position));*//*



                        }
                        break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
*/

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //              currentSampleCustomer.setDoDetails(holder.doDetails.getText().toString());

                try {
                    currentSampleCustomer = sampleCustomerEntityList.get(currentViewPosition);

                    currentSampleCustomer.setDeclaredGrade(holder.declaredGrade.getText().toString());
                    globalDeclaredGrade = holder.declaredGrade.getText().toString();
                    currentSampleCustomer.setTotalVehicles(holder.totalNumberTruck.getText().toString());
                    currentSampleCustomer.setTotalVehiclesSampled(holder.totalNumberTruckSampled.getText().toString());


                    fsa_number = holder.fsaNumber.getText().toString();
                    details = holder.doDetails.getText().toString();
                    quantity = holder.quantity.getText().toString();


                    singledolist.clear();
                    dolist.clear();

                    if (mode.equalsIgnoreCase("Road")) {
                        if (fsa_number.length() > 0  && details.length() > 0 && quantity.length() > 0) {
                            MoredetailsEntity obj = new MoredetailsEntity();

                            obj.setFsa_number(fsa_number);
                            obj.setDo_details(details);
                            obj.setQuantity(quantity);

                            singledolist.add(obj);

                            for (int i = 0; i < fliterdolist.size(); i++) {
                                MoredetailsEntity pojo = fliterdolist.get(i);
                                if (currentViewPosition == pojo.getPosition()) {
                                    remaningdolist.add(pojo);
                                }
                            }

                            dolist.addAll(remaningdolist);

                            dolist.add(obj);

                            Gson gson = new Gson();
                            String json = gson.toJson(dolist);

                            currentSampleCustomer.setMore_details(json);

                            remaningdolist.clear();

                        }
                    } else if (mode.equalsIgnoreCase("Rail")) {
                        if (fsa_number.length() > 0 || details.length() > 0 || quantity.length() > 0) {
                            MoredetailsEntity obj = new MoredetailsEntity();

                            obj.setFsa_number(fsa_number);
                            obj.setDo_details(details);
                            obj.setQuantity(quantity);

                            singledolist.add(obj);

                            dolist.addAll(fliterdolist);

                            dolist.add(obj);

                            Gson gson = new Gson();
                            String json = gson.toJson(dolist);

                            currentSampleCustomer.setMore_details(json);

                        }
                    }

                    if (currentSampleCustomer.getMore_details() != null) {
                        sampleCustomerEntityList.set(position, currentSampleCustomer);
                    } else {

                        MoredetailsEntity obj = new MoredetailsEntity();

                        obj.setFsa_number("0");
                        obj.setDo_details("0");
                        obj.setQuantity("0");

                        singledolist.add(obj);

                        dolist.add(obj);

                        Gson gson = new Gson();
                        String json = gson.toJson(dolist);

                        currentSampleCustomer.setMore_details(json);

                        sampleCustomerEntityList.set(position, currentSampleCustomer);
                    }


                    rr_number = holder.doDetails.getText().toString();
                } catch (Exception e) {
                    e.printStackTrace();
                }



        /*        if (holder.doDetails.getText().toString().length() > 0 & holder.quantity.getText().toString().length() > 0){
                    obj = new MoredetailsEntity();

                    obj.setDo_details(holder.doDetails.getText().toString());
                    obj.setQuantity(holder.quantity.getText().toString());

                }*/

            }
        };

        if (currentSampleCustomer.getAuction_type() != null) {
            int spinnerPosition = customerNameAdapter.getPosition(currentSampleCustomer.getAuction_type());
            holder.auctionType_spinner.setSelection(spinnerPosition);
        }


        if (currentSampleCustomer.getCustomerName() != null) {
     /*       int spinnerPosition = holder.customerNameAdapter.getPosition(currentSampleCustomer.getCustomerName());
//            CustomerEntity currentCustomer = allCustomerList.get(spinnerPosition - 1);
            holder.customerName.setSelection(spinnerPosition);*/


            switch (liftType) {
                case "Road": {
                    holder.fsaLayout.setVisibility(View.VISIBLE);
                  /*  if (currentSampleCustomer.getFsaNo() != null) {
                        holder.fsaNumber.setText(currentSampleCustomer.getFsaNo());
                    }*/
                    holder.doDetails.setHint("DO Detail");
                    holder.truckWagon = "Truck";

                    holder.addCustomer.setText("ADD CUSTOMER");
                    break;
                }
                case "Rail":
                    holder.fsaLayout.setVisibility(View.GONE);
                    holder.doDetails.setHint("RR number");
                    holder.doDetails.setInputType(InputType.TYPE_CLASS_NUMBER);
                    holder.truckWagon = "Wagons";


                    holder.addCustomer.setText("Add new grade sample");
                    break;

                default:
                    holder.fsaLayout.setVisibility(View.VISIBLE);
                  /*  if (currentCustomer.getFsaNo() != null) {
                        holder.fsaNumber.setText(currentCustomer.getFsaNo());
                    }*/
                    holder.doDetails.setHint("DO Detail");
                    holder.truckWagon = "Truck";
                    break;
            }
            holder.totalNumberTruck.setHint("Total no. of " + holder.truckWagon);
            holder.totalNumberTruckSampled.setHint("Total no. of " + holder.truckWagon + " Sampled");
        }

       /* if (currentSampleCustomer.getDoDetails() != null) {
            holder.doDetails.setText(currentSampleCustomer.getDoDetails());
        }*/

     /*   if (position == 0){
            RR_details = details;

        }
*/

        if (currentSampleCustomer.getDeclaredGrade() != null) {
            holder.declaredGrade.setText(currentSampleCustomer.getDeclaredGrade());
        }
        if (position == 0) {
            globalDeclaredGrade = holder.declaredGrade.getText().toString();
        }
        if (mode.equalsIgnoreCase("Road")) {
            if (position > 0) {
                holder.declaredGrade.setEnabled(false);
                holder.declaredGrade.setText(globalDeclaredGrade);
            }
        }


        if (mode.equalsIgnoreCase("Rail")) {
            if (position > 0) {

                holder.doDetails.setText(rr_number);
                //            holder.doDetails.setEnabled(false);

            }
            if (position > 0) {
                holder.auctionType_spinner.setSelection(auction_type_pos);
                //               holder.auctionType_spinner.setEnabled(false);
            }

            if (position > 0) {
                holder.customerName.setSelection(customer_name_pos);
                //              holder.customerName.setEnabled(false);
            }

            if (position > 0){
                if (sampleCustomerEntityList.get(0).getRrdate() != null){
                    holder.dateRR.setText(sampleCustomerEntityList.get(0).getRrdate());

                    holder.dateRR.setTextColor(context.getResources().getColor(R.color.colorDarkGrey));
                }else {
                    holder.dateRR.setText("RR Date (optional)");

                    holder.dateRR.setTextColor(context.getResources().getColor(R.color.colorLineGrey));
                }

                currentSampleCustomer = sampleCustomerEntityList.get(currentViewPosition);

                currentSampleCustomer.setDeclaredGrade(holder.declaredGrade.getText().toString());

                currentSampleCustomer.setRrdate(holder.dateRR.getText().toString());

                sampleCustomerEntityList.set(currentViewPosition, currentSampleCustomer);


            }
        }



        int pos_length = sampleCustomerEntityList.size();

        if (mode.equalsIgnoreCase("Road")) {
            if (AdaperCall) {
                for (int i = 0; i < sampleCustomerEntityList.size(); i++) {
                    if (sampleCustomerEntityList.get(i).getMore_details() != null) {
                        dolist.clear();
                        try {
                            JSONArray jsonArray1 = new JSONArray(sampleCustomerEntityList.get(i).getMore_details());

                            for (int j = 0; j < jsonArray1.length(); j++) {

                                JSONObject object = jsonArray1.getJSONObject(j);

                                String fsa_number = object.getString("fsa_number");
                                String do_details = object.getString("do_details");
                                String quantity = object.getString("quantity");


                            holder.fsaNumber.setText(fsa_number);
                            holder.doDetails.setText(do_details);
                            holder.quantity.setText(quantity);

                                MoredetailsEntity obj = new MoredetailsEntity();

                                obj.setFsa_number(fsa_number);
                                obj.setDo_details(do_details);
                                obj.setQuantity(quantity);
                                obj.setPosition(i);

                                fliterdolist.add(obj);
                            }
                            System.out.println("xxxx");
                            if (pos_length == i + 1) {
                                AdaperCall = false;
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        AdaperCall = false;
                    }

                }

            }
        } else if (mode.equalsIgnoreCase("Rail")) {

            if (currentSampleCustomer.getMore_details() != null) {
                dolist.clear();
                try {
                    JSONArray jsonArray1 = new JSONArray(currentSampleCustomer.getMore_details());

                    JSONObject object = jsonArray1.getJSONObject(0);

                    String fsa_number = object.getString("fsa_number");
                    String rail_details = object.getString("do_details");
                    String quantity_details = object.getString("quantity");


                    holder.fsaNumber.setText(fsa_number);
                    holder.doDetails.setText(rail_details);
                    holder.quantity.setText(quantity_details);

                /*        if (i == 0){
                            holder.doDetails.setText(rail_details);
                            holder.quantity.setText(quantity_details);
                        }else {
                            holder.doDetails.setText(rail_details);
                            holder.quantity.setText(quantity_details);
                        }*/

                    System.out.println("xxxx");
                      /*  if (pos_length == i + 1) {
                            AdaperCall = false;
                        }*/

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }



 /*       if(currentSampleCustomer.getAuction_type() != null){

        }

        if (currentSampleCustomer.getCustomerName() != null){

        }

        */



        if (currentSampleCustomer.getAuction_type() != null) {
            int spinnerPosition = auctionTypeAdapter.getPosition(currentSampleCustomer.getAuction_type());
            holder.auctionType_spinner.setSelection(spinnerPosition);

     /*       holder.auctionType_spinner.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    ((TextView) holder.auctionType_spinner.getSelectedView()).setTextColor(Color.BLACK);
                }
            });
*/
        }

        if (currentSampleCustomer.getCustomerName() != null) {

            int spinnerPosition = customerNameAdapter.getPosition(currentSampleCustomer.getCustomerName());
            holder.customerName.setSelection(spinnerPosition);

  //          holder.customerName.setOnItemSelectedListener(null);

      /*      holder.customerName.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    ((TextView) holder.customerName.getSelectedView()).setTextColor(Color.BLACK);
                }
            });*/



 //           ((TextView) holder.customerName.getSelectedView()).setTextColor(Color.BLACK);

        }

        if (currentSampleCustomer.getRrdate() != null){
            holder.dateRR.setText(currentSampleCustomer.getRrdate());

            holder.dateRR.setTextColor(context.getResources().getColor(R.color.colorDarkGrey));
        }

          if (currentSampleCustomer.getMore_details() != null) {

            try {
                JSONArray jsonArray1 = new JSONArray(currentSampleCustomer.getMore_details());

                for (int i = 0; i < jsonArray1.length(); i++) {

                    JSONObject object = jsonArray1.getJSONObject(i);

                    String fsa_number = object.getString("fsa_number");
                    String do_details = object.getString("do_details");
                    String quantity = object.getString("quantity");

                    holder.fsaNumber.setText(fsa_number);
                    holder.doDetails.setText(do_details);
                    holder.quantity.setText(quantity);

                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

   /*     if (currentSampleCustomer.getMore_details() != null){
         holder.fsaNumber.setText(dolist.get(0).getQuantity());
        }*/

        if (currentSampleCustomer.getTotalVehicles() != null) {
            holder.totalNumberTruck.setText(currentSampleCustomer.getTotalVehicles());
        }

        if (currentSampleCustomer.getTotalVehiclesSampled() != null) {
            holder.totalNumberTruckSampled.setText(currentSampleCustomer.getTotalVehiclesSampled());
        }

        if (allowMoreCustomer) {
            holder.tv_challan_code.setVisibility(View.VISIBLE);
        } else if (allowMoreGradle) {
            holder.tv_challan_code.setVisibility(View.VISIBLE);
        } else {
            holder.tv_challan_code.setVisibility(View.GONE);
        }


        if (allowMoreCustomer && position == sampleCustomerEntityList.size() - 1) {

            holder.addCustomer.setVisibility(View.VISIBLE);
        } else {
            holder.addCustomer.setVisibility(View.GONE);

        }
        if (allowMoreGradle && position == sampleCustomerEntityList.size() - 1) {
            holder.addGradle.setVisibility(View.VISIBLE);

        } else {
            holder.addGradle.setVisibility(View.GONE);
            //
        }
        holder.declaredGrade.addTextChangedListener(textWatcher);
        holder.doDetails.addTextChangedListener(textWatcher);
        holder.quantity.addTextChangedListener(textWatcher);
        holder.totalNumberTruck.addTextChangedListener(textWatcher);
        holder.totalNumberTruckSampled.addTextChangedListener(textWatcher);
    /*    holder.doDetails.addTextChangedListener(textWatcher);
        holder.quantity.addTextChangedListener(textWatcher);*/


        holder.addCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

    //            setData("add_customer",position);
                if (add(sampleCustomerEntityList.size(), new SampleCustomerEntity())) {
                    holder.addCustomer.setVisibility(View.GONE);
                }


            }
        });

        holder.addGradle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //              setData("add_customer",position);
                if (add(sampleCustomerEntityList.size(), new SampleCustomerEntity())) {
                    holder.addGradle.setVisibility(View.GONE);
                }

            }
        });

        holder.Do_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //           setData("add_view",position);

                try {
                    MoredetailsEntity pojo = singledolist.get(0);

                    pojo.setPosition(currentViewPosition);

                    fliterdolist.add(pojo);

                    holder.fsaNumber.setText("");
                    holder.doDetails.setText("");
                    holder.quantity.setText("");
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });



        holder.Do_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int pos = position;

                if (sampleCustomerEntityList.get(pos).getMore_details() != null && sampleCustomerEntityList.get(pos).getMore_details().length() > 0) {
                    DoDialog(sampleCustomerEntityList.get(pos).getMore_details(), pos);
                } else {
                    Toast.makeText(context, "There is no DO information for this customer", Toast.LENGTH_LONG).show();
                }
            }
        });

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

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

                holder.dateRR.setText(selectedDate);
                holder.dateRR.setTextColor(context.getResources().getColor(R.color.colorDarkGrey));

                currentSampleCustomer = sampleCustomerEntityList.get(currentViewPosition);

                currentSampleCustomer.setRrdate(holder.dateRR.getText().toString());

                sampleCustomerEntityList.set(currentViewPosition, currentSampleCustomer);

                rr_date = holder.dateRR.getText().toString();

            }

        };

        holder.dateRR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog =
                        new DatePickerDialog(context, R.style.DateDialogTheme,
                                date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                                myCalendar.get(Calendar.DAY_OF_MONTH));
                Calendar c = Calendar.getInstance();


                c.setTime(new Date());
                datePickerDialog.getDatePicker().setMaxDate(c.getTime().getTime());

                c.add(Calendar.MONTH, -3);

                datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
                datePickerDialog.show();
            }
        });

        holder.iv_cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sampleCustomerEntityList.size() == 1) {
                    Toast.makeText(context, "You can't remove", Toast.LENGTH_LONG).show();
                } else {
                    remove(position);
                    if (mode.equalsIgnoreCase("Rail")) {
                        holder.addGradle.setVisibility(View.VISIBLE);
                    } else {
                        holder.addCustomer.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

    }


    @Override
    public int getItemCount() {
        return sampleCustomerEntityList.size();
    }

    public Boolean add(int position, SampleCustomerEntity item) {
        if (sampleCustomerEntityList.get(position - 1).getAuction_type() == null) {
            Toast.makeText(context, "Select auction type", Toast.LENGTH_SHORT).show();
        }/*else if (sampleCustomerEntityList.get(position-1).getCustomerName() == null){
            Toast.makeText(context, "Select Customer Name", Toast.LENGTH_SHORT).show();
        }*//* else if (mode.equalsIgnoreCase("Road") && sampleCustomerEntityList.get(position - 1).getMore_details() == null) {
            Toast.makeText(context, "Enter Do Details", Toast.LENGTH_SHORT).show();
        }*/ else if (sampleCustomerEntityList.get(position - 1).getDeclaredGrade() == null || sampleCustomerEntityList.get(position - 1).getDeclaredGrade().length() == 0) {
            Toast.makeText(context, "Enter Declared Grade", Toast.LENGTH_SHORT).show();
        } else if (sampleCustomerEntityList.get(position - 1).getTotalVehicles() == null || sampleCustomerEntityList.get(position - 1).getTotalVehicles().length() == 0) {
            Toast.makeText(context, "Enter Total no. of Truck", Toast.LENGTH_SHORT).show();
        } else if (sampleCustomerEntityList.get(position - 1).getTotalVehiclesSampled() == null || sampleCustomerEntityList.get(position - 1).getTotalVehiclesSampled().length() == 0) {
            Toast.makeText(context, "Enter Total no. of Truck Sampled", Toast.LENGTH_SHORT).show();
        } else if (Integer.parseInt(sampleCustomerEntityList.get(position - 1).getDeclaredGrade()) > 17) {
            Toast.makeText(context, "Declared Grade should be less then 17", Toast.LENGTH_SHORT).show();
        } else if (Integer.parseInt(sampleCustomerEntityList.get(position - 1).getTotalVehiclesSampled()) > Integer.parseInt(sampleCustomerEntityList.get(position - 1).getTotalVehicles())) {
            Toast.makeText(context, "Total no. of Truck Sampled should be less than Total no. of Trucks", Toast.LENGTH_SHORT).show();
        } else if (liftType.equalsIgnoreCase("Road") && sampleCustomerEntityList.get(position - 1).getTotalVehicles().length() > 4) {
            Toast.makeText(context, "Total number of trucks should be less than or equal to 3 digits", Toast.LENGTH_SHORT).show();
        } else {

            sampleCustomerEntityList.add(position, item);
            notifyItemInserted(position);

            //         fliterdolist.clear();
            singledolist.clear();

            return true;
        }

        return false;

    }


    public void remove(int position) {

        try {

            int id = sampleCustomerEntityList.get(position).getId();

            if (fragment.idlist.contains(id)) {
                fragment.CustomerDelete(id);
            }

            sampleCustomerEntityList.remove(position);
            notifyItemRemoved(position);

            notifyDataSetChanged();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public List<SampleCustomerEntity> getPreparedData() {
        return sampleCustomerEntityList;
    }

    public void setDeleteList(ArrayList<MoredetailsEntity> list, int pos) {
        Gson gson = new Gson();
        String json = gson.toJson(list);

        currentSampleCustomer = sampleCustomerEntityList.get(pos);

        currentSampleCustomer.setMore_details(json);

        sampleCustomerEntityList.set(pos, currentSampleCustomer);
    }

/*    public void setMoreDetails(){
        if (dolist.size() == 0) {
            dolist.clear();
            try {
                JSONArray jsonArray1 = new JSONArray(currentSampleCustomer.getMore_details());

                for (int i = 0; i < jsonArray1.length(); i++) {

                    JSONObject object = jsonArray1.getJSONObject(i);

                    String do_details = object.getString("do_details");
                    String quantity = object.getString("quantity");

                    MoredetailsEntity obj = new MoredetailsEntity();

                    obj.setDo_details(do_details);
                    obj.setQuantity(quantity);

                    dolist.add(obj);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else {
                Gson gson = new Gson();
                String json = gson.toJson(dolist);

                currentSampleCustomer.setMore_details(json);
        }
    }*/

    public void setCustomerData(int position){
        currentSampleCustomer = sampleCustomerEntityList.get(position);

        MoredetailsEntity obj = new MoredetailsEntity();

        obj.setFsa_number(fsa_number);
        obj.setDo_details(details);
        obj.setQuantity(quantity);

        singledolist.add(obj);

        dolist.add(obj);

        Gson gson = new Gson();
        String json = gson.toJson(dolist);

        currentSampleCustomer.setMore_details(json);

        sampleCustomerEntityList.set(position, currentSampleCustomer);
    }



   public void setCustomerName(){

   }

    public void setData(String from, int position) {
        dolist.clear();
        if (from.equalsIgnoreCase("customer")) {
            if (details.length() > 0 && quantity.length() > 0 && dolist.size() == 1) {
                dolist.clear();
            }
        }

        if (from.equalsIgnoreCase("customer")) {
            if (details.length() > 0 && quantity.length() > 0) {
                MoredetailsEntity obj = new MoredetailsEntity();

                obj.setDo_details(details);
                obj.setQuantity(quantity);

                dolist.add(obj);

                Gson gson = new Gson();
                String json = gson.toJson(dolist);

                currentSampleCustomer.setMore_details(json);

            } else {
                //              currentSampleCustomer.setMore_details(null);
            }
        } else {
            currentSampleCustomer = sampleCustomerEntityList.get(position);

            JSONArray jsonArray1 = null;
            try {
                if (currentSampleCustomer.getMore_details() != null) {
                    jsonArray1 = new JSONArray(currentSampleCustomer.getMore_details());

                    for (int i = 0; i < jsonArray1.length(); i++) {

                        JSONObject object = jsonArray1.getJSONObject(i);

                        String do_details = object.getString("do_details");
                        String quantity = object.getString("quantity");

                        MoredetailsEntity obj = new MoredetailsEntity();

                        obj.setDo_details(do_details);
                        obj.setQuantity(quantity);

                        dolist.add(obj);
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


            if (details.length() > 0 && quantity.length() > 0) {
                MoredetailsEntity obj = new MoredetailsEntity();

                obj.setDo_details(details);
                obj.setQuantity(quantity);

                dolist.add(obj);

                Gson gson = new Gson();
                String json = gson.toJson(dolist);

                currentSampleCustomer.setMore_details(json);

            }
            sampleCustomerEntityList.set(position, currentSampleCustomer);
        }


        if (from.equalsIgnoreCase("add_customer")) {
            dolist.clear();
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
                        customerList = gson.fromJson(jsonObject.getJSONArray("data").toString(), listType);
                        for (CustomerEntity customer : customerList) {
                            customerNameList.add(customer.getUserName());
                            customerIdList.add(customer.getUserId());
                        }
                     /*   gotAllCustomerList = true;
                        setCustomerFormListAdapter();*/
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }


    public class CustomerFormViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.customerName)
        public Spinner customerName;
        @BindView(R.id.fsaNumber)
        public EditText fsaNumber;
        @BindView(R.id.fsaLayout)
        public LinearLayout fsaLayout;
        @BindView(R.id.doDetails)
        public EditText doDetails;
        @BindView(R.id.declaredGrade)
        public EditText declaredGrade;
        @BindView(R.id.totalNumberTruck)
        public EditText totalNumberTruck;
        @BindView(R.id.totalNumberTruckSampled)
        public EditText totalNumberTruckSampled;
        @BindView(R.id.addCustomer)
        public Button addCustomer;
        @BindView(R.id.addGradle)
        public Button addGradle;
        @BindView(R.id.Do_add)
        ImageView Do_add;
        @BindView(R.id.Do_view)
        ImageView Do_view;
        @BindView(R.id.quantity)
        EditText quantity;
        @BindView(R.id.iv_cross)
        ImageView iv_cross;
        @BindView(R.id.tv_challan_code)
        TextView tv_challan_code;

        @BindView(R.id.dateRR)
        TextView dateRR;

        @BindView(R.id.ll_multiple)
        LinearLayout ll_multiple;

        @BindView(R.id.ll_rrDate)
        LinearLayout ll_rrDate;

        @BindView(R.id.ll_rrPath)
        LinearLayout ll_rrPath;

        @BindView(R.id.auctionType_spinner)
        Spinner auctionType_spinner;

        String liftType;
        String truckWagon = "Truck";


        private List<String> customerNameList;
        View item_view;

        public CustomerFormViewHolder(View itemView, String liftType, List<String> customerNameList) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.liftType = liftType;
            this.customerNameList = customerNameList;
            setVisibilty();
    //        setCustomerAdapter();
            this.item_view = itemView;
        }

        private void setCustomerAdapter(){
            customerNameAdapter = new ArrayAdapter<String>(context, R.layout.simple_spinner_item, customerNameList);
            customerNameAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
            customerName.setAdapter(customerNameAdapter);
        }


        private void setVisibilty() {
            addCustomer.setVisibility(View.GONE);
            switch (liftType) {
                case "Road":
                    fsaLayout.setVisibility(View.VISIBLE);
                    doDetails.setHint("DO Detail");
                    truckWagon = "Truck";
                    // totalNumberTruck.setHint("Total no. of "+truckWagon);
                    //  totalNumberTruckSampled.setHint("Total no. of "+truckWagon+" Sampled");

                    addGradle.setVisibility(View.GONE);
                    addCustomer.setVisibility(View.VISIBLE);

                    ll_rrDate.setVisibility(View.GONE);

                    ll_rrPath.setVisibility(View.GONE);

                    mode = "Road";

                    break;

                case "Rail":
                    fsaLayout.setVisibility(View.GONE);
                    doDetails.setHint("RR number");
                    doDetails.setInputType(InputType.TYPE_CLASS_NUMBER);

                    ll_multiple.setVisibility(View.INVISIBLE);

                    truckWagon = "Wagons";

                    mode = "Rail";

                    addCustomer.setVisibility(View.GONE);

                    addGradle.setVisibility(View.VISIBLE);

                    ll_rrDate.setVisibility(View.VISIBLE);

                    ll_rrPath.setVisibility(View.VISIBLE);

                    //  totalNumberTruck.setHint("Total no. of Wagons");
                    // totalNumberTruckSampled.setHint("Total no. of Wagons Sampled");

                    break;

                default:
                    fsaLayout.setVisibility(View.VISIBLE);
                    doDetails.setHint("DO Detail");
                    truckWagon = "Truck";
                    // totalNumberTruck.setHint("Total no. of Truck");
                    //  totalNumberTruckSampled.setHint("Total no. of Truck Sampled");
                    break;
            }

            totalNumberTruck.setHint("Total no. of " + truckWagon);
            totalNumberTruckSampled.setHint("Total no. of " + truckWagon + " Sampled");


        }

      /*  public void setCustomerAdapter() {

        }*/
    }

    private void DoDialog(String list, int pos) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.do_details_dialog);
        dialog.setTitle(" ");

        RecyclerView mRecyclerView;
        RecyclerView.LayoutManager mLayoutManager;
        DoDetailsAdapter adapter;

        ImageView iv_cross_details = (ImageView) dialog.findViewById(R.id.iv_cross_details);

        iv_cross_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        mRecyclerView = (RecyclerView) dialog.findViewById(R.id.do_details_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(context);
        mRecyclerView.setLayoutManager(mLayoutManager);

        morelist = new ArrayList<>();

        try {
            JSONArray jsonArray1 = new JSONArray(list);

            for (int i = 0; i < jsonArray1.length(); i++) {

                JSONObject object = jsonArray1.getJSONObject(i);

                String fsa_number = object.getString("fsa_number");
                String do_details = object.getString("do_details");
                String quantity = object.getString("quantity");

                MoredetailsEntity obj = new MoredetailsEntity();

                obj.setFsa_number(fsa_number);
                obj.setDo_details(do_details);
                obj.setQuantity(quantity);

                morelist.add(obj);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // specify an adapter (see also next example)
        adapter = new DoDetailsAdapter(CustomerFormListAdapter.this, context, morelist, pos);
        mRecyclerView.setAdapter(adapter);

        dialog.show();

    }



    public ArrayList<String> getArrayList(String key){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = prefs.getString(key, null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public String getFromPrefs(String key) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.PREF_NAME, context.MODE_PRIVATE);
        return prefs.getString(key, "");
    }

}


