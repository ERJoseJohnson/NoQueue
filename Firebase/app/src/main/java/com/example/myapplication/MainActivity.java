package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {
    //Debug TAG
    private static final String TAG = "FirebaseApp";
    public final static String ORDER1 = "ORDER1";
    public final static String ORDER2 = "ORDER2";
    public final static String ORDER3 = "ORDER3";
    public final static String PRICE1 = "PRICE1";
    public final static String PRICE2 = "PRICE2";
    public final static String PRICE3 = "PRICE3";
    String order1;
    String order2;
    String order3;
    String price1;
    String price2;
    String price3;

    TextView ChickenChopTextView;
    TextView FriesTextView;
    TextView NuggetsTextView;

    Button ChickenChopPrice;
    Button FriesPrice;
    Button NuggetsPrice;

    DatabaseReference mRootRef= FirebaseDatabase.getInstance().getReference();   //Gives you the root of the JSON tree
    //     Creates a location of menu underneath the roots, which can receive a value
    DatabaseReference mfoodRef = mRootRef.child("Menu");
    DatabaseReference mcustomerRef = mRootRef.child("CustomerList");
    DatabaseReference mWesternStall = mRootRef.child("WesternOrderQueue");
    DatabaseReference mconditionRef = mRootRef.child("condition");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ChickenChopTextView = (TextView) findViewById(R.id.ChickenChopTextView);
        FriesTextView = (TextView) findViewById(R.id.FriesTextView);
        NuggetsTextView = (TextView) findViewById(R.id.NuggetsTextView);

        ChickenChopPrice = (Button) findViewById(R.id.ChickenChopButton);
        FriesPrice = (Button) findViewById(R.id.FriesButton);
        NuggetsPrice = (Button) findViewById(R.id.NuggetsButton);

        Button cart = findViewById(R.id.Cart);
        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Cart.class);
                intent.putExtra(ORDER1, order1);
                intent.putExtra(PRICE1, price1);
                intent.putExtra(ORDER2, order2);
                intent.putExtra(PRICE2, price2);
                intent.putExtra(ORDER3, order3);
                intent.putExtra(PRICE3, price3);
                startActivity(intent);
            }
        });

        final Button ChickenChopButton = findViewById(R.id.ChickenChopButton);
        ChickenChopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView ChickenChopTextView = findViewById(R.id.ChickenChopTextView);
                order1 = ChickenChopTextView.getText().toString();
                price1 = ChickenChopPrice.getText().toString();
                //TextView textViewTry = findViewById(R.id.Try);
                //textViewTry.setText(price1);

            }
        });

        final Button FriesButton = findViewById(R.id.FriesButton);
        FriesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView FriesTextView = findViewById(R.id.FriesTextView);
                order2= FriesTextView.getText().toString();
                price2 = FriesPrice.getText().toString();

            }
        });

        final Button NuggetsButton = findViewById(R.id.NuggetsButton);
        NuggetsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView NuggetsTextView = findViewById(R.id.NuggetsTextView);
                order3 = NuggetsTextView.getText().toString();
                price3 = NuggetsPrice.getText().toString();

            }
        });

        Menu mexican_chop = new Menu("Mexican Chicken Chop",4.5, "001");
        mfoodRef.child("Menu").setValue(mexican_chop);
        Menu aglio_olio = new Menu("Aglio Olio", 4.5, "002");
        mfoodRef.child("Menu1").setValue(aglio_olio);
        Menu fish_chips = new Menu("Fish and Chips",5.5,"003");
        mfoodRef.child("Menu2").setValue(fish_chips);

        //Creating orders from the menu for the customer details for CUSTOMER LIST
        OrderDetails order1 = new OrderDetails(mexican_chop.getFoodCode(),1,false);
        ArrayList<OrderDetails> customer1Orders= new ArrayList<OrderDetails>();
        customer1Orders.add(order1);

        OrderDetails order2 = new OrderDetails(aglio_olio.getFoodCode(),2,true);
        customer1Orders.add(order2);

        OrderDetails order3 = new OrderDetails(aglio_olio.getFoodCode(),3,true);
        customer1Orders.add(order3);

        //Populating the database
        //Create a customer class
        CustomerDetails customer1 = new CustomerDetails(1,customer1Orders);
        mcustomerRef.child("Customer1").child("ID").setValue(customer1.getCustomerID());        //Add their ID, can be used for authentication if we take it further
        //Ordercode is used as the child header
        for (OrderDetails order: customer1Orders){
            mcustomerRef.child("Customer1").child(Integer.toString(order.getOrderCode())).setValue(order);
        }

        //Creating Western Order queue child database
        for (OrderDetails Orders : customer1Orders){
            if (Orders.getOrderStatus()==false){
                mWesternStall.child("Order to complete").setValue(Orders);
            }
        }

        //For updating the price list and menu for the day.
        mfoodRef.addValueEventListener(new ValueEventListener() {

            //Will run everytime there is an update to the condition value in the database
            //So this will run when the .setValue function runs in the button onClickListener classes
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataSnapshot foodmenu1 = dataSnapshot.child("Menu");
                Menu text1 = foodmenu1.getValue(Menu.class);             //Retrieves the value of menu in the database
                ChickenChopTextView.setText(text1.getFoodName());
                ChickenChopPrice.setText(Double.toString(text1.getFoodPrice()));
                //ChickenChopButton.setText("gasdg");

                DataSnapshot foodmenu2 = dataSnapshot.child("Menu1");
                Menu text2 = foodmenu2.getValue(Menu.class);
                FriesTextView.setText(text2.getFoodName());
                FriesPrice.setText(Double.toString(text2.getFoodPrice()));
                //FriesButton.setText("fklasjdf");

                DataSnapshot foodmenu3 = dataSnapshot.child("Menu2");
                Menu text3 = foodmenu3.getValue(Menu.class);
                NuggetsTextView.setText(text3.getFoodName());
                NuggetsPrice.setText(Double.toString(text3.getFoodPrice()));
                //NuggetsButton.setText("flksjdlfkj");

            }

            // In case we run into any errors
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
//    @Override
//    protected void onStart() {
//        super.onStart();
////        ChickenChopTextView.setText("Okaylo");
//        //Attach the eventListener to the condition child to see if there are any changes.
//        mfoodRef.addValueEventListener(new ValueEventListener() {
//
//            //Will run everytime there is an update to the condition value in the database
//            //So this will run when the .setValue function runs in the button onClickListener classes
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                Log.i(TAG,"onDataChange : " + dataSnapshot);
//                DataSnapshot foodmenu1 = dataSnapshot.child("Menu1");
//                Menu text1 = foodmenu1.getValue(Menu.class);
//                ChickenChopTextView.setText(text1.foodName);
//
////                ChickenChopTextView.setText(Long.toString(dataSnapshot.getChildrenCount()));
////                DataSnapshot foodmenu1 = dataSnapshot.child("Menu");
////                Menu menu1 = foodmenu1.getValue(Menu.class);             //Retrieves the value of condition in the database
////                DataSnapshot foodmenu2 = dataSnapshot.child("Menu1");
////                Menu menu2 = foodmenu2.getValue(Menu.class);
////                ChickenChopTextView.setText(menu1.foodName);
////                FriesTextView.setText(menu2.foodName);
//
//            }
//
//            // In case we run into any errors
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }
}


