package jo.budget;

import static jo.budget.MainActivity.db;
import static jo.budget.MainActivity.mAuth;

import android.content.Context;
import android.graphics.Color;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;

public class BarChart {
    private com.github.mikephil.charting.charts.BarChart barChart;
    private TextView date2;
    private Context context;
    BarChart(com.github.mikephil.charting.charts.BarChart barChart, TextView date2, Context context) {
        this.barChart = barChart;
        this.date2 = date2;
        this.context = context;
        makeBarChart();
    }

    private void makeBarChart() {
        barChart.clear();
        ArrayList<BarEntry> entries = new ArrayList<>();
        BarData barData = new BarData();
        Description description = barChart.getDescription();
        description.setEnabled(false);
        Legend legend = barChart.getLegend();
        legend.setEnabled(false);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisMinimum(1);
        xAxis.setAxisMaximum(12);
        xAxis.setLabelCount(12);
        xAxis.setGranularity(1);

        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setDrawAxisLine(false);
        leftAxis.setAxisMinimum(0);
        YAxis rightAxis = barChart.getAxisRight();
        rightAxis.setDrawAxisLine(false);
        rightAxis.setAxisMinimum(0);

        for(int i=0; i<12; i++) {
            int month = i + 1;
            String date;
            if (i < 10)
                date = date2.getText().toString() + " 0" + month + "월";
            else
                date = date2.getText().toString() + " " + month + "월";

            DocumentReference docRef = db.collection("users")
                    .document(mAuth.getCurrentUser().getUid())
                    .collection("date")
                    .document(date)
                    .collection("monthlyInfo")
                    .document(date);
            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        MonthlyInfo monthlyInfo = documentSnapshot.toObject(MonthlyInfo.class);

                        if (monthlyInfo.getTotal() != 0) {
                            entries.add(new BarEntry(month, monthlyInfo.getTotal() / (float) 10000));
                            BarDataSet barDataSet = new BarDataSet(entries, "지출액");
                            barDataSet.setColor(Color.parseColor("#66BB6A"));
                            barData.addDataSet(barDataSet);
                            barData.setBarWidth(0.5f);
                            barChart.setData(barData);
                            barChart.invalidate();
                            barChart.setTouchEnabled(false);
                        }
                    } else {
                        entries.add(new BarEntry(month, 0));
                        BarDataSet barDataSet = new BarDataSet(entries, "지출액");
                        barDataSet.setColor(Color.parseColor("#66BB6A"));
                        barData.addDataSet(barDataSet);
                        barData.setBarWidth(0.5f);
                        barChart.setData(barData);
                        barChart.invalidate();
                        barChart.setTouchEnabled(false);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(context, "지출 금액을 가져오지 못했습니다", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
