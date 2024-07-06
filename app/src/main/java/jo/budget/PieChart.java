package jo.budget;

import static jo.budget.MainActivity.db;
import static jo.budget.MainActivity.mAuth;

import android.content.Context;
import android.graphics.Color;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;

public class PieChart {
    private com.github.mikephil.charting.charts.PieChart pieChart;
    private TextView date3;
    private Context context;

    private int[] colorArray = new int[] {
            Color.parseColor("#9CCC65"), Color.parseColor("#26C6DA"), Color.parseColor("#7E57C2"),
            Color.parseColor("#FF7043"), Color.parseColor("#EC407A"), Color.parseColor("#EF5350"),
            Color.parseColor("#FFCA28"), Color.parseColor("#78909C"), Color.parseColor("#BDBDBD")
    };

    PieChart(com.github.mikephil.charting.charts.PieChart pieChart, TextView date3, Context context) {
        this.pieChart = pieChart;
        this.date3 = date3;
        this.context = context;
        makePieChart();
    }

    private void makePieChart() {
        pieChart.clear();
        ArrayList<PieEntry> entries = new ArrayList<>();
        pieChart.setUsePercentValues(false);
        pieChart.getDescription().setEnabled(false);
        pieChart.getLegend().setEnabled(false);
        pieChart.setDrawHoleEnabled(false);
        DocumentReference docRef = db.collection("users")
                .document(mAuth.getCurrentUser().getUid())
                .collection("date")
                .document(date3.getText().toString())
                .collection("monthlyInfo")
                .document(date3.getText().toString());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    MonthlyInfo monthlyInfo = documentSnapshot.toObject(MonthlyInfo.class);
                    if(monthlyInfo.getFood_expenses() != 0) {
                        entries.add(new PieEntry(monthlyInfo.getFood_expenses(), "식비"));
                        drawPieChart(entries);
                    }
                    if(monthlyInfo.getLiving_expenses() != 0) {
                        entries.add(new PieEntry(monthlyInfo.getLiving_expenses(), "생활비"));
                        drawPieChart(entries);
                    }
                    if(monthlyInfo.getTransportation_expenses() != 0) {
                        entries.add(new PieEntry(monthlyInfo.getTransportation_expenses(), "교통비"));
                        drawPieChart(entries);
                    }
                    if(monthlyInfo.getFashion_expenses() != 0) {
                        entries.add(new PieEntry(monthlyInfo.getFashion_expenses(), "패션/쇼핑"));
                        drawPieChart(entries);
                    }
                    if(monthlyInfo.getBeauty_expenses() != 0) {
                        entries.add(new PieEntry(monthlyInfo.getBeauty_expenses(), "뷰티/미용"));
                        drawPieChart(entries);
                    }
                    if(monthlyInfo.getLeisure_expenses() != 0) {
                        entries.add(new PieEntry(monthlyInfo.getLeisure_expenses(), "문화/여가"));
                        drawPieChart(entries);
                    }
                    if(monthlyInfo.getMedical_expenses() != 0) {
                        entries.add(new PieEntry(monthlyInfo.getMedical_expenses(), "의료/건강"));
                        drawPieChart(entries);
                    }
                    if(monthlyInfo.getEducational_expenses() != 0) {
                        entries.add(new PieEntry(monthlyInfo.getEducational_expenses(), "교육/학습"));
                        drawPieChart(entries);
                    }
                    if(monthlyInfo.getOther_expenses() != 0) {
                        entries.add(new PieEntry(monthlyInfo.getOther_expenses(), "기타"));
                        drawPieChart(entries);
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "지출 금액을 가져오지 못했습니다", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void drawPieChart(ArrayList<PieEntry> entries) {
        PieDataSet dataSet = new PieDataSet(entries, "카테고리별 지출액");
        dataSet.setColors(colorArray);
        PieData data = new PieData(dataSet);
        data.setValueTextSize(10f);
        data.setValueTextColor(Color.YELLOW);
        pieChart.setData(data);
        pieChart.invalidate();
        pieChart.setTouchEnabled(false);
    }
}
