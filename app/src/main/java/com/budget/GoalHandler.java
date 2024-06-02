package com.budget;

import static com.budget.MainActivity.currentUser;
import static com.budget.MainActivity.date;
import static com.budget.MainActivity.db;
import static com.budget.MainActivity.mAuth;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class GoalHandler extends Dialog {
    private Context context;
    private TextView d;
    private EditText goal;
    private Button c;
    private Button s;

    private Button del;
    private Util util = new Util();

    public GoalHandler(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCancelable(false);
        setContentView(R.layout.goal);


        goal = findViewById(R.id.goal);
        c = findViewById(R.id.c);
        s = findViewById(R.id.s);
        d = findViewById(R.id.d);
        del = findViewById(R.id.del);

        d.setText(date.getText().toString());
        goal.addTextChangedListener(util.textWatcher(goal));

        getContent();

        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteGoal();
                dismiss();
            }
        });

        s.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!goal.getText().toString().equals("")) {
                    saveGoal();
                    dismiss();
                } else {
                    toast("목표 금액을 설정해주세요");
                }
            }
        });

        c.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    private void getContent() {
        DocumentReference docRef = db.collection("users")
                .document(mAuth.getCurrentUser().getUid())
                .collection("date")
                .document(date.getText().toString())
                .collection("monthlyInfo")
                .document(date.getText().toString());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()) {
                    MonthlyInfo monthlyInfo = documentSnapshot.toObject(MonthlyInfo.class);
                    if(monthlyInfo.getGoal() != -1) {
                        goal.setText(String.valueOf(monthlyInfo.getGoal()));
                        del.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    private void saveGoal() {
        String g = goal.getText().toString();
        db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users")
                .document(mAuth.getCurrentUser().getUid())
                .collection("date")
                .document(date.getText().toString())
                .collection("monthlyInfo")
                .document(date.getText().toString());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()) {
                    MonthlyInfo exist = documentSnapshot.toObject(MonthlyInfo.class);
                    exist.setGoal(Integer.parseInt(g.replace(",","")));
                    MonthlyInfo monthlyInfo = exist;
                    db.collection("users").document(currentUser.getUid())
                            .collection("date").document(date.getText().toString())
                            .collection("monthlyInfo").document(date.getText().toString()).set(monthlyInfo);
                } else {
                    MonthlyInfo monthlyInfo = new MonthlyInfo(0,0,0,0,0,0,0,0,0,0,0);
                    monthlyInfo.setGoal(Integer.parseInt(g.replace(",","")));
                    db.collection("users").document(currentUser.getUid())
                            .collection("date").document(date.getText().toString())
                            .collection("monthlyInfo").document(date.getText().toString()).set(monthlyInfo);
                }
            }
        });
    }

    private void deleteGoal() {
        DocumentReference docRef = db.collection("users")
                .document(mAuth.getCurrentUser().getUid())
                .collection("date")
                .document(date.getText().toString())
                .collection("monthlyInfo")
                .document(date.getText().toString());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()) {
                    MonthlyInfo exist = documentSnapshot.toObject(MonthlyInfo.class);
                    exist.setGoal(-1);
                    MonthlyInfo monthlyInfo = exist;
                    db.collection("users").document(currentUser.getUid())
                            .collection("date").document(date.getText().toString())
                            .collection("monthlyInfo").document(date.getText().toString()).set(monthlyInfo);
                }
            }
        });
    }
    private void toast(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }
}
