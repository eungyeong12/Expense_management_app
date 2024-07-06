package jo.budget;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;
import static jo.budget.MainActivity.adapter;
import static jo.budget.MainActivity.currentUser;
import static jo.budget.MainActivity.date;
import static jo.budget.MainActivity.db;
import static jo.budget.MainActivity.mAuth;
import static jo.budget.MainActivity.recyclerView;
import static jo.budget.MainActivity.total;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Sorting {
    private Context context;
    private Util util = new Util();
    private GoalHandler goalHandler;

    Sorting(Context context) {
        this.context = context;
        goalHandler = new GoalHandler(context);
    }

    public void list() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        if (currentUser != null) {
            goalHandler.getGoal();
            db.collection("users")
                    .document(mAuth.getCurrentUser().getUid())
                    .collection("date")
                    .document(date.getText().toString())
                    .collection("items")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            ArrayList<Item> itemList = new ArrayList<>();

                            for (QueryDocumentSnapshot document : value) {
                                Log.w(TAG, document.getId() + " => " + document.getData());
                                Item item = document.toObject(Item.class);
                                itemList.add(item);
                            }

                            recyclerView.setHasFixedSize(true);
                            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
                            recyclerView.setLayoutManager(layoutManager);
                            adapter = new ListAdapter(itemList, context);
                            recyclerView.setAdapter(adapter);

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
                                        MonthlyInfo exist = value.toObject(MonthlyInfo.class);
                                        total.setText(util.format(exist.getTotal()) + "원");
                                    } else {
                                        MonthlyInfo monthlyInfo = new MonthlyInfo(-1,0,0,0,0,0,0,0,0,0,0);
                                        total.setText(util.format(monthlyInfo.getTotal()) + "원");
                                    }
                                }
                            });
                            docRef.get().addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(context, "지출 목록을 가져오지 못했습니다", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
        }
    }

    public void list(String category) {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        if(currentUser != null) {
            goalHandler.getGoal();
            db.collection("users")
                    .document(mAuth.getCurrentUser().getUid())
                    .collection("date")
                    .document(date.getText().toString())
                    .collection("items")
                    .whereEqualTo("category", category)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            ArrayList<Item> itemList = new ArrayList<>();

                            for (QueryDocumentSnapshot document : value) {
                                Log.w(TAG, document.getId() + " => " + document.getData());
                                Item item = document.toObject(Item.class);
                                itemList.add(item);
                                Collections.sort(itemList, new Comparator<Item>() {
                                    @Override
                                    public int compare(Item item, Item t1) {
                                        return t1.getTimestamp().compareTo(item.getTimestamp());
                                    }
                                });
                            }

                            recyclerView.setHasFixedSize(true);
                            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
                            recyclerView.setLayoutManager(layoutManager);
                            adapter = new ListAdapter(itemList, context);
                            recyclerView.setAdapter(adapter);

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
                                        setTotal(monthlyInfo, category);
                                    } else {
                                        MonthlyInfo monthlyInfo = new MonthlyInfo(-1,0,0,0,0,0,0,0,0,0,0);
                                        setTotal(monthlyInfo, category);
                                    }
                                }
                            });
                        }
                    });
        }
    }

    private void setTotal(MonthlyInfo monthlyInfo, String category) {
        switch (category) {
            case "식비":
                total.setText(util.format(monthlyInfo.getFood_expenses()) + "원");
                break;
            case "생활비":
                total.setText(util.format(monthlyInfo.getLiving_expenses()) + "원");
                break;
            case "교통비":
                total.setText(util.format(monthlyInfo.getTransportation_expenses()) + "원");
                break;
            case "패션/쇼핑":
                total.setText(util.format(monthlyInfo.getFashion_expenses()) + "원");
                break;
            case "뷰티/미용":
                total.setText(util.format(monthlyInfo.getBeauty_expenses()) + "원");
                break;
            case "문화/여가":
                total.setText(util.format(monthlyInfo.getLeisure_expenses()) + "원");
                break;
            case "의료/건강":
                total.setText(util.format(monthlyInfo.getMedical_expenses()) + "원");
                break;
            case "교육/학습":
                total.setText(util.format(monthlyInfo.getEducational_expenses()) + "원");
                break;
            case "기타":
                total.setText(util.format(monthlyInfo.getOther_expenses()) + "원");
                break;
            default:
                break;
        }
    }

}
