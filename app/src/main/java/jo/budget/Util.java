package jo.budget;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;

public class Util {
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy년 MM월");
    private SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy.MM.dd(E)", Locale.KOREA);
    private SimpleDateFormat dateFormat3 = new SimpleDateFormat("yyyy년");
    private DecimalFormat decimalFormat = new DecimalFormat("#,###");
    private String result="";

    Util() {

    }

    public void calCategory1(MonthlyInfo exist, Item item) {
        switch (item.getCategory()) {
            case "식비":
                exist.setFood_expenses(exist.getFood_expenses() + Integer.parseInt(item.getExpenditure().replace(",","")));
                break;
            case "생활비":
                exist.setLiving_expenses(exist.getLiving_expenses() + Integer.parseInt(item.getExpenditure().replace(",","")));
                break;
            case "교통비":
                exist.setTransportation_expenses(exist.getTransportation_expenses() + Integer.parseInt(item.getExpenditure().replace(",","")));
                break;
            case "패션/쇼핑":
                exist.setFashion_expenses(exist.getFashion_expenses() + Integer.parseInt(item.getExpenditure().replace(",","")));
                break;
            case "뷰티/미용":
                exist.setBeauty_expenses(exist.getBeauty_expenses() + Integer.parseInt(item.getExpenditure().replace(",","")));
                break;
            case "문화/여가":
                exist.setLeisure_expenses(exist.getLeisure_expenses() + Integer.parseInt(item.getExpenditure().replace(",","")));
                break;
            case "의료/건강":
                exist.setMedical_expenses(exist.getMedical_expenses() + Integer.parseInt(item.getExpenditure().replace(",","")));
                break;
            case "교육/학습":
                exist.setEducational_expenses(exist.getEducational_expenses() + Integer.parseInt(item.getExpenditure().replace(",","")));
                break;
            case "기타":
                exist.setOther_expenses(exist.getOther_expenses() + Integer.parseInt(item.getExpenditure().replace(",","")));
                break;
            default:
                break;
        }
    }
    public void calCategory2(MonthlyInfo exist, Item item) {
        switch (item.getCategory()) {
            case "식비":
                exist.setFood_expenses(exist.getFood_expenses() - Integer.parseInt(item.getExpenditure().replace(",","")));
                break;
            case "생활비":
                exist.setLiving_expenses(exist.getLiving_expenses() - Integer.parseInt(item.getExpenditure().replace(",","")));
                break;
            case "교통비":
                exist.setTransportation_expenses(exist.getTransportation_expenses() - Integer.parseInt(item.getExpenditure().replace(",","")));
                break;
            case "패션/쇼핑":
                exist.setFashion_expenses(exist.getFashion_expenses() - Integer.parseInt(item.getExpenditure().replace(",","")));
                break;
            case "뷰티/미용":
                exist.setBeauty_expenses(exist.getBeauty_expenses() - Integer.parseInt(item.getExpenditure().replace(",","")));
                break;
            case "문화/여가":
                exist.setLeisure_expenses(exist.getLeisure_expenses() - Integer.parseInt(item.getExpenditure().replace(",","")));
                break;
            case "의료/건강":
                exist.setMedical_expenses(exist.getMedical_expenses() - Integer.parseInt(item.getExpenditure().replace(",","")));
                break;
            case "교육/학습":
                exist.setEducational_expenses(exist.getEducational_expenses() - Integer.parseInt(item.getExpenditure().replace(",","")));
                break;
            case "기타":
                exist.setOther_expenses(exist.getOther_expenses() - Integer.parseInt(item.getExpenditure().replace(",","")));
                break;
            default:
                break;
        }
    }

    public void calCategory3(MonthlyInfo monthlyInfo, Item item) {
        switch (item.getCategory()) {
            case "식비":
                monthlyInfo.setFood_expenses(Integer.parseInt(item.getExpenditure().replace(",","")));
                break;
            case "생활비":
                monthlyInfo.setLiving_expenses(Integer.parseInt(item.getExpenditure().replace(",","")));
                break;
            case "교통비":
                monthlyInfo.setTransportation_expenses(Integer.parseInt(item.getExpenditure().replace(",","")));
                break;
            case "패션/쇼핑":
                monthlyInfo.setFashion_expenses(Integer.parseInt(item.getExpenditure().replace(",","")));
                break;
            case "뷰티/미용":
                monthlyInfo.setBeauty_expenses(Integer.parseInt(item.getExpenditure().replace(",","")));
                break;
            case "문화/여가":
                monthlyInfo.setLeisure_expenses(Integer.parseInt(item.getExpenditure().replace(",","")));
                break;
            case "의료/건강":
                monthlyInfo.setMedical_expenses(Integer.parseInt(item.getExpenditure().replace(",","")));
                break;
            case "교육/학습":
                monthlyInfo.setEducational_expenses(Integer.parseInt(item.getExpenditure().replace(",","")));
                break;
            case "기타":
                monthlyInfo.setOther_expenses(Integer.parseInt(item.getExpenditure().replace(",","")));
                break;
            default:
                break;
        }
    }

    public TextWatcher textWatcher(EditText editText) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!TextUtils.isEmpty(charSequence.toString()) && !charSequence.toString().equals(result)){
                    result = decimalFormat.format(Double.parseDouble(charSequence.toString().replaceAll(",","").replace("원","")));
                    editText.setText(result);
                    editText.setSelection(result.length());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };
    }

    public void setCategory(String categoryText, TextView category) {
        category.setText(categoryText);
        switch (categoryText) {
            case "식비":
                category.setBackgroundResource(R.drawable.bg_1);
                break;
            case "생활비":
                category.setBackgroundResource(R.drawable.bg_2);
                break;
            case "교통비":
                category.setBackgroundResource(R.drawable.bg_3);
                break;
            case "패션/쇼핑":
                category.setBackgroundResource(R.drawable.bg_4);
                break;
            case "뷰티/미용":
                category.setBackgroundResource(R.drawable.bg_5);
                break;
            case "문화/여가":
                category.setBackgroundResource(R.drawable.bg_6);
                break;
            case "의료/건강":
                category.setBackgroundResource(R.drawable.bg_7);
                break;
            case "교육/학습":
                category.setBackgroundResource(R.drawable.bg_8);
                break;
            case "기타":
                category.setBackgroundResource(R.drawable.bg_9);
                break;
            default:
                break;
        }
    }

    public String getCurrentDay() {
        Calendar cal = Calendar.getInstance();
        return dateFormat2.format(cal.getTime());
    }

    public String translateDateFormat(Date date) {
        return dateFormat.format(date);
    }

    public String translateDateFormat2(Date date) {
        return dateFormat2.format(date);
    }

    public String getDate(TextView dateText) {
        String s = dateText.getText().toString();
        StringTokenizer st = new StringTokenizer(s, ".");
        String year = st.nextToken();
        String month = st.nextToken();
        return year + "년 " + month + "월";
    }

    public Date getDate2(TextView dateText) {
        String s = dateText.getText().toString();
        StringTokenizer st = new StringTokenizer(s, ".");
        int year = Integer.parseInt(st.nextToken());
        int month = Integer.parseInt(st.nextToken()) - 1;
        st = new StringTokenizer(st.nextToken(), "(");
        int day = Integer.parseInt(st.nextToken());
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        return calendar.getTime();
    }

    public String getTime() {
        Calendar cal = Calendar.getInstance();
        return dateFormat.format(cal.getTime());
    }

    public String getLastMonth(TextView dateText) {
        Calendar cal = getCurrentMonth(dateText);
        cal.add(cal.MONTH, -1);
        return dateFormat.format(cal.getTime());
    }

    public String getNextMonth(TextView dateText) {
        Calendar cal = getCurrentMonth(dateText);
        cal.add(cal.MONTH, +1);
        return dateFormat.format(cal.getTime());
    }

    public Calendar getCurrentMonth(TextView dateText) {
        String currentDate = dateText.getText().toString();
        StringTokenizer st = new StringTokenizer(currentDate, " ");
        String yearS = st.nextToken();
        String monthS = st.nextToken();
        Calendar cal = Calendar.getInstance();
        int year = Integer.parseInt(yearS.substring(0,4));
        int month = Integer.parseInt(monthS.substring(0,2)) - 1;
        cal.set(year, month,1);

        return cal;
    }

    public String getTime2() {
        Calendar cal = Calendar.getInstance();
        return dateFormat3.format(cal.getTime());
    }

    public String getLastYear(TextView dateText) {
        Calendar cal = getCurrentYear(dateText);
        cal.add(cal.YEAR, -1);
        return dateFormat3.format(cal.getTime());
    }

    public String getNextYear(TextView dateText) {
        Calendar cal = getCurrentYear(dateText);
        cal.add(cal.YEAR, +1);
        return dateFormat3.format(cal.getTime());
    }

    public Calendar getCurrentYear(TextView dateText) {
        String currentYear = dateText.getText().toString().replace("년","");
        Calendar cal = Calendar.getInstance();
        cal.set(Integer.parseInt(currentYear), 1,1);

        return cal;
    }

    public String format(int val) {
        DecimalFormat df = new DecimalFormat("###,###");
        return df.format(val);
    }
}
