package jo.budget;

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
    private FloatingActionButton addButton;
    static TextView goalText;
    private Button goalButton;
    private GoalHandler setGoal;
    static ItemHandler addItemDialog;
    static boolean onItemClick = false;
    static boolean isDateChange = false;
    static int position = -1;
    private ImageView statsButton;
    Util util = new Util();
    Sorting sort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if(currentUser == null) {
            signInAnonymously();
        }

        left = findViewById(R.id.left);
        right = findViewById(R.id.right);
        date = findViewById(R.id.date);
        recyclerView = findViewById(R.id.recyclerView);
        total = findViewById(R.id.total);
        spinner2 = findViewById(R.id.spinner2);
        addButton = findViewById(R.id.add);
        goalButton = findViewById(R.id.goalButton);
        goalText = findViewById(R.id.goalText);
        statsButton = findViewById(R.id.st);

        sort = new Sorting(MainActivity.this);
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


        setDateClick(date);
        setPreviousMonthButtonClick(left);
        setNextMonthButtonClick(right);
        setAddButtonClick(addButton);
        setGoalButtonClick(goalButton);
        setSpinnerSelected(spinner2);
        setStatsButtonClick(statsButton);
    }

    private void signInAnonymously() {
        mAuth.signInAnonymously();
    }

    private void setDateClick(TextView date) {
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                jo.budget.DatePicker d = new jo.budget.DatePicker();
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
    }

    private void setPreviousMonthButtonClick(ImageView left) {
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                date.setText(util.getLastMonth(date));
                spinner2.setSelection(0);
                sort.list();
            }
        });
    }
    private void setNextMonthButtonClick(ImageView right) {
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                date.setText(util.getNextMonth(date));
                spinner2.setSelection(0);
                sort.list();
            }
        });
    }
    private void setAddButtonClick(FloatingActionButton addButton) {
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        });
    }

    private void setGoalButtonClick(Button goalButton) {
        goalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setGoal = new GoalHandler(MainActivity.this);
                setGoal.show();
                setGoal.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        setGoal.getGoal();
                    }
                });
            }
        });
    }

    private void setSpinnerSelected(Spinner spinner2) {
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
    }

    private void setStatsButtonClick(ImageView statsButton) {
        statsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, StatsActivity.class);
                startActivity(intent);
            }
        });
    }

}