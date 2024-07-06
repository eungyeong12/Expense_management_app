package jo.budget;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;

public class StatsActivity extends AppCompatActivity {
    private ImageView left2;
    private TextView date2;
    private ImageView right2;
    private ImageView back;
    private ImageView left3;
    private TextView date3;
    private ImageView right3;
    private BarChart barChart;
    private PieChart pieChart;

    Util util = new Util();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);
        left2 = findViewById(R.id.left2);
        date2 = findViewById(R.id.date2);
        right2 = findViewById(R.id.right2);
        back = findViewById(R.id.back);
        left3 = findViewById(R.id.left3);
        date3 = findViewById(R.id.date3);
        right3 = findViewById(R.id.right3);
        barChart = findViewById(R.id.chart1);
        pieChart = findViewById(R.id.chart2);
        date2.setText(util.getTime2());
        date3.setText(util.getTime());

        setStats();
        setBackButtonClick(back);
        setPreviousYearButtonClick(left2);
        setNextYearButtonClick(right2);
        setPreviousMonthButtonClick(left3);
        setNextMonthButtonClick(right3);
    }

    private void setStats() {
        new jo.budget.BarChart(barChart, date2, StatsActivity.this);
        new jo.budget.PieChart(pieChart, date3, StatsActivity.this);
    }

    private void setPreviousYearButtonClick(ImageView left2) {
        left2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                date2.setText(util.getLastYear(date2));
                new jo.budget.BarChart(barChart, date2, StatsActivity.this);
            }
        });
    }

    private void setNextYearButtonClick(ImageView right2) {
        right2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                date2.setText(util.getNextYear(date2));
                new jo.budget.BarChart(barChart, date2, StatsActivity.this);
            }
        });
    }

    private void setPreviousMonthButtonClick(ImageView left3) {
        left3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                date3.setText(util.getLastMonth(date3));
                new jo.budget.PieChart(pieChart, date3, StatsActivity.this);
            }
        });
    }

    private void setNextMonthButtonClick(ImageView right3) {
        right3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                date3.setText(util.getNextMonth(date3));
                new jo.budget.PieChart(pieChart, date3, StatsActivity.this);
            }
        });
    }

    private void setBackButtonClick(ImageView back) {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StatsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
