package jo.budget;

import static jo.budget.MainActivity.currentUser;
import static jo.budget.MainActivity.date;
import static jo.budget.MainActivity.db;
import static jo.budget.MainActivity.goalText;
import static jo.budget.MainActivity.mAuth;

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
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

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
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                toast("지출 목표를 가져오지 못했습니다");
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
                    MonthlyInfo monthlyInfo = new MonthlyInfo(-1,0,0,0,0,0,0,0,0,0,0);
                    monthlyInfo.setGoal(Integer.parseInt(g.replace(",","")));
                    db.collection("users").document(currentUser.getUid())
                            .collection("date").document(date.getText().toString())
                            .collection("monthlyInfo").document(date.getText().toString()).set(monthlyInfo);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                toast("지출 목표를 저장하지 못했습니다");
            }
        });
        getGoal();
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
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                toast("지출 목표를 삭제하지 못했습니다");
            }
        });
        getGoal();
    }

    public void getGoal() {
        DocumentReference docRef = db.collection("users")
                .document(mAuth.getCurrentUser().getUid())
                .collection("date")
                .document(date.getText().toString())
                .collection("monthlyInfo")
                .document(date.getText().toString());
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value.exists()) {
                    MonthlyInfo monthlyInfo = value.toObject(MonthlyInfo.class);
                    if(monthlyInfo.getGoal() != -1) {
                        goalText.setText(util.format(monthlyInfo.getGoal()) + "원");
                    } else {
                        goalText.setText("");
                    }
                } else {
                    new MonthlyInfo(-1,0,0,0,0,0,0,0,0,0,0);
                    goalText.setText("");
                }
            }
        });
    }

    private void toast(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }
}
