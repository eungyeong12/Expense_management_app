package jo.budget;

import java.util.Date;

public class Item {
    private String content;
    private String expenditure;
    private String date;
    private String category;
    private Date timestamp;

    public Item() {}

    public Item(String content, String expenditure, String date, String category, Date timestamp) {
        this.content = content;
        this.expenditure = expenditure;
        this.date = date;
        this.category = category;
        this.timestamp = timestamp;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getExpenditure() {
        return expenditure;
    }

    public void setExpenditure(String expenditure) {
        this.expenditure = expenditure;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
