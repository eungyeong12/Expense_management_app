package com.budget;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;


public class MainActivity extends AppCompatActivity {
    static FirebaseAuth mAuth;
    static FirebaseUser currentUser;
    static FirebaseFirestore db;
    private ImageView left;
    private ImageView right;
    static protected TextView date;
    static RecyclerView recyclerView;
    static TextView total;
    static Spinner spinner2;
    private String category;
    static ListAdapter adapter;
    private FloatingActionButton add;
    static TextView goalText;
    private Button setGoalButton;
    private GoalHandler setGoal;
    static ItemHandler addItemDialog;
    static boolean onItemClick = false;
    static boolean isDateChange = false;
    static int position = -1;
    private ImageView st;
    Util util = new Util();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if(currentUser == null) {
            mAuth.signInAnonymously();
        }

        left = findViewById(R.id.left);
        right = findViewById(R.id.right);
        date = findViewById(R.id.date);
        recyclerView = findViewById(R.id.recyclerView);
        total = findViewById(R.id.total);
        spinner2 = findViewById(R.id.spinner2);
        add = findViewById(R.id.add);
        setGoalButton = findViewById(R.id.setGoalButton);
        goalText = findViewById(R.id.goalText);
        st = findViewById(R.id.st);

        Sorting sort = new Sorting(MainActivity.this);
        new SwipeToDelete();

        date.setText(util.getTime());
        date.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                sort.list();
            }
        });

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                com.budget.DatePicker d = new com.budget.DatePicker();
                d.setListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        Calendar cal = Calendar.getInstance();
                        cal.set(year, month, day);
                        date.setText(util.translateDateFormat(cal.getTime()));
                        spinner2.setSelection(0);
                        sort.list();
                    }
                });
                d.show(getSupportFragmentManager(), "DatePicker");
            }
        });

        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                date.setText(util.getLastMonth(date));
                spinner2.setSelection(0);
                sort.list();
            }
        });

        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                date.setText(util.getNextMonth(date));
                spinner2.setSelection(0);
                sort.list();
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItem();
            }
        });

        setGoalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setGoal = new GoalHandler(MainActivity.this);
                setGoal.show();
                setGoal.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        util.getGoal();
                    }
                });
            }
        });

        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
               category = adapterView.getItemAtPosition(i).toString();
               if(!category.equals("전체")) sort.list(category);
               else sort.list();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        st.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, StatsActivity.class);
                startActivity(intent);
            }
        });
    }

    private void addItem() {
        addItemDialog = new ItemHandler(MainActivity.this);
        addItemDialog.show();
        addItemDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                onItemClick = false;
                isDateChange = false;
            }
        });
    }

}