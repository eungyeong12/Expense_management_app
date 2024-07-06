package jo.budget;

import static jo.budget.MainActivity.adapter;
import static jo.budget.MainActivity.currentUser;
import static jo.budget.MainActivity.db;
import static jo.budget.MainActivity.isDateChange;
import static jo.budget.MainActivity.mAuth;
import static jo.budget.MainActivity.onItemClick;
import static jo.budget.MainActivity.position;
import static jo.budget.MainActivity.spinner2;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Date;

public class ItemHandler extends Dialog {

    private Context context;
    private TextView dateText;
    private EditText editContent;
    private EditText editPrice;
    private Button cancel;
    private Button save;
    private Button delete;
    static Spinner spinner;
    private Item updateItem;
    private Date existDate;
    private Util util = new Util();

    public ItemHandler(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCancelable(false);
        setContentView(R.layout.add_item);

        dateText = findViewById(R.id.dd);
        editContent = findViewById(R.id.content);
        editPrice = findViewById(R.id.goal);
        cancel = findViewById(R.id.c);
        save = findViewById(R.id.s);
        delete = findViewById(R.id.delete);
        spinner = findViewById(R.id.spinner);

        editPrice.addTextChangedListener(util.textWatcher(editPrice));

        delete.setVisibility(View.INVISIBLE);
        dateText.setText(util.getCurrentDay());

        if(onItemClick) {
            getContent();
        }

        dateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinner.setSelection(0);
                editContent.setText("");
                editPrice.setText("");
                dismiss();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if(currentUser != null) {
                    String content = editContent.getText().toString();
                    String price = editPrice.getText().toString();
                    if(!content.equals("")) {
                        if(!price.equals("")) {
                            if(!spinner.getSelectedItem().toString().equals("선택")) {
                                db = FirebaseFirestore.getInstance();
                                String d = util.getDate(dateText);
                                Date date = util.getDate2(dateText);
                                MainActivity.date.setText(d);
                                if(onItemClick) {
                                    boolean isNeedSort = updateItem(content, price, d, date);
                                    if(!isNeedSort) return;
                                }
                                saveItem(content, price, d, date);
                            } else {
                                toast("카테고리를 선택해주세요");
                            }

                        } else {
                            toast("금액을 입력해주세요");
                        }
                    } else {
                        toast("내용을 입력해주세요");
                    }

                }
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("삭제하시겠습니까?")
                                .setPositiveButton("예", new OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        deleteItem();
                                        toast("삭제되었습니다");
                                        dismiss();
                                    }
                                }).setNegativeButton("아니오", new OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                                .show();
            }
        });
    }

    private void getContent() {
        delete.setVisibility(View.VISIBLE);
        updateItem = adapter.getItem(position);
        dateText.setText(util.translateDateFormat2(updateItem.getTimestamp()));
        editContent.setText(updateItem.getContent());
        editPrice.setText(updateItem.getExpenditure());
        int p = getSpinnerPosition(updateItem.getCategory());
        spinner.setSelection(p);
        existDate = updateItem.getTimestamp();
    }

    private void deleteItem() {
        db.collection("users").document(currentUser.getUid())
                .collection("date").document(updateItem.getDate())
                .collection("items").document(updateItem.getTimestamp().toString()).delete();
        DocumentReference docRef = db.collection("users")
                .document(mAuth.getCurrentUser().getUid())
                .collection("date")
                .document(updateItem.getDate())
                .collection("monthlyInfo")
                .document(updateItem.getDate());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()) {
                    MonthlyInfo exist = documentSnapshot.toObject(MonthlyInfo.class);
                    exist.setTotal(exist.getTotal()-Integer.parseInt(updateItem.getExpenditure().replace(",","")));
                    util.calCategory2(exist, updateItem);
                    MonthlyInfo monthlyInfo = exist;
                    db.collection("users").document(currentUser.getUid())
                            .collection("date").document(updateItem.getDate())
                            .collection("monthlyInfo").document(updateItem.getDate()).set(monthlyInfo);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                toast("지출 내역을 삭제하지 못했습니다");
            }
        });
    }

    private boolean updateItem(String content, String price, String d, Date date) {
        if(!isDateChange) date = updateItem.getTimestamp();
        if(content.equals(updateItem.getContent()) && price.equals(updateItem.getExpenditure()) &&
                d.equals(updateItem.getDate()) && spinner.getSelectedItem().toString().equals(updateItem.getCategory())
                && !isDateChange) {
            dismiss();
            return false;
        }
        db.collection("users").document(currentUser.getUid())
                .collection("date").document(updateItem.getDate())
                .collection("items").document(updateItem.getTimestamp().toString()).delete();
        return true;
    }

    private void saveItem(String content, String price, String d, Date date) {
        Item item = new Item(content, price, d, spinner.getSelectedItem().toString(), date);
        db.collection("users").document(currentUser.getUid())
                .collection("date").document(d)
                .collection("items").document(item.getTimestamp().toString()).set(item);

        if(onItemClick) {
            if(updateItem.getDate().equals(d)) {
                DocumentReference docRef = db.collection("users")
                        .document(mAuth.getCurrentUser().getUid())
                        .collection("date")
                        .document(updateItem.getDate())
                        .collection("monthlyInfo")
                        .document(updateItem.getDate());
                docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()) {
                            MonthlyInfo exist = documentSnapshot.toObject(MonthlyInfo.class);
                            exist.setTotal(exist.getTotal()-Integer.parseInt(updateItem.getExpenditure().replace(",",""))+Integer.parseInt(price.replace(",","")));
                            util.calCategory2(exist, updateItem);
                            util.calCategory1(exist, item);
                            MonthlyInfo monthlyInfo = exist;
                            db.collection("users").document(currentUser.getUid())
                                    .collection("date").document(updateItem.getDate())
                                    .collection("monthlyInfo").document(updateItem.getDate()).set(monthlyInfo);
                            toast("수정되었습니다");
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        toast("지출 내역을 수정하지 못했습니다");
                    }
                });
            } else {
                DocumentReference docRef = db.collection("users")
                        .document(mAuth.getCurrentUser().getUid())
                        .collection("date")
                        .document(updateItem.getDate())
                        .collection("monthlyInfo")
                        .document(updateItem.getDate());
                docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()) {
                            MonthlyInfo exist = documentSnapshot.toObject(MonthlyInfo.class);
                            exist.setTotal(exist.getTotal()-Integer.parseInt(updateItem.getExpenditure().replace(",","")));
                            util.calCategory2(exist, updateItem);
                            MonthlyInfo monthlyInfo = exist;
                            db.collection("users").document(currentUser.getUid())
                                    .collection("date").document(updateItem.getDate())
                                    .collection("monthlyInfo").document(updateItem.getDate()).set(monthlyInfo);
                        }
                    }
                });
                docRef = db.collection("users")
                        .document(mAuth.getCurrentUser().getUid())
                        .collection("date")
                        .document(d)
                        .collection("monthlyInfo")
                        .document(d);
                docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()) {
                            MonthlyInfo exist = documentSnapshot.toObject(MonthlyInfo.class);
                            exist.setTotal(exist.getTotal()+Integer.parseInt(price.replace(",","")));
                            util.calCategory1(exist, item);

                            MonthlyInfo monthlyInfo = exist;
                            db.collection("users").document(currentUser.getUid())
                                    .collection("date").document(d)
                                    .collection("monthlyInfo").document(d).set(monthlyInfo);
                        } else {
                            MonthlyInfo monthlyInfo = new MonthlyInfo(-1,0,0,0,0,0,0,0,0,0,0);
                            monthlyInfo.setTotal(Integer.parseInt(price.replace(",","")));
                            util.calCategory3(monthlyInfo, item);
                            db.collection("users").document(currentUser.getUid())
                                    .collection("date").document(d)
                                    .collection("monthlyInfo").document(d).set(monthlyInfo);
                        }
                        toast("수정되었습니다");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        toast("지출 내역을 수정하지 못했습니다");
                    }
                });
            }
        } else {
            DocumentReference docRef = db.collection("users")
                    .document(mAuth.getCurrentUser().getUid())
                    .collection("date")
                    .document(d)
                    .collection("monthlyInfo")
                    .document(d);
            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if(documentSnapshot.exists()) {
                        MonthlyInfo exist = documentSnapshot.toObject(MonthlyInfo.class);
                        exist.setTotal(exist.getTotal()+Integer.parseInt(price.replace(",","")));
                        util.calCategory1(exist, item);

                        MonthlyInfo monthlyInfo = exist;
                        db.collection("users").document(currentUser.getUid())
                                .collection("date").document(d)
                                .collection("monthlyInfo").document(d).set(monthlyInfo);
                    } else {
                        MonthlyInfo monthlyInfo = new MonthlyInfo(-1,0,0,0,0,0,0,0,0,0,0);
                        monthlyInfo.setTotal(Integer.parseInt(price.replace(",","")));
                        util.calCategory3(monthlyInfo, item);
                        db.collection("users").document(currentUser.getUid())
                                .collection("date").document(d)
                                .collection("monthlyInfo").document(d).set(monthlyInfo);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    toast("지출 내역을 저장하지 못했습니다");
                }
            });
        }
        editContent.setText("");
        editPrice.setText("");
        spinner.setSelection(0);
        spinner2.setSelection(0);
        dismiss();
    }

    private void showDatePicker() {
        Calendar c = Calendar.getInstance();
        if(existDate != null) {
            c.setTime(existDate);
        }
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(android.widget.DatePicker datePicker, int year, int month, int day) {
                        isDateChange = true;
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, month, day);
                        dateText.setText(util.translateDateFormat2(calendar.getTime()));
                    }
                }, year, month, day);
        datePickerDialog.show();
    }

    private int getSpinnerPosition(String category) {
        switch (category) {
            case "식비":
                return 1;
            case "생활비":
                return 2;
            case "교통비":
                return 3;
            case "패션/쇼핑":
                return 4;
            case "뷰티/미용":
                return 5;
            case "문화/여가":
                return 6;
            case "의료/건강":
                return 7;
            case "교육/학습":
                return 8;
            case "기타":
                return 9;
            default:
                return -1;
        }
    }

    private void toast(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }
}