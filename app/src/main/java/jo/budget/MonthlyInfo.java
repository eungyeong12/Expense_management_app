package jo.budget;

public class MonthlyInfo {
    private int goal;
    private int total;
    private int food_expenses;
    private int living_expenses;
    private int transportation_expenses;
    private int fashion_expenses;
    private int beauty_expenses;
    private int leisure_expenses;
    private int medical_expenses;
    private int educational_expenses;
    private int other_expenses;

    public MonthlyInfo() {

    }

    public MonthlyInfo(int goal, int total, int food_expenses, int living_expenses, int transportation_expenses, int fashion_expenses, int beauty_expenses, int leisure_expenses, int medical_expenses, int educational_expenses, int other_expenses) {
        this.goal = goal;
        this.total = total;
        this.food_expenses = food_expenses;
        this.living_expenses = living_expenses;
        this.transportation_expenses = transportation_expenses;
        this.fashion_expenses = fashion_expenses;
        this.beauty_expenses = beauty_expenses;
        this.leisure_expenses = leisure_expenses;
        this.medical_expenses = medical_expenses;
        this.educational_expenses = educational_expenses;
        this.other_expenses = other_expenses;
    }

    public int getGoal() {
        return goal;
    }

    public void setGoal(int goal) {
        this.goal = goal;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getFood_expenses() {
        return food_expenses;
    }

    public void setFood_expenses(int food_expenses) {
        this.food_expenses = food_expenses;
    }

    public int getLiving_expenses() {
        return living_expenses;
    }

    public void setLiving_expenses(int living_expenses) {
        this.living_expenses = living_expenses;
    }

    public int getTransportation_expenses() {
        return transportation_expenses;
    }

    public void setTransportation_expenses(int transportation_expenses) {
        this.transportation_expenses = transportation_expenses;
    }

    public int getFashion_expenses() {
        return fashion_expenses;
    }

    public void setFashion_expenses(int fashion_expenses) {
        this.fashion_expenses = fashion_expenses;
    }

    public int getBeauty_expenses() {
        return beauty_expenses;
    }

    public void setBeauty_expenses(int beauty_expenses) {
        this.beauty_expenses = beauty_expenses;
    }

    public int getLeisure_expenses() {
        return leisure_expenses;
    }

    public void setLeisure_expenses(int leisure_expenses) {
        this.leisure_expenses = leisure_expenses;
    }

    public int getMedical_expenses() {
        return medical_expenses;
    }

    public void setMedical_expenses(int medical_expenses) {
        this.medical_expenses = medical_expenses;
    }

    public int getEducational_expenses() {
        return educational_expenses;
    }

    public void setEducational_expenses(int educational_expenses) {
        this.educational_expenses = educational_expenses;
    }

    public int getOther_expenses() {
        return other_expenses;
    }

    public void setOther_expenses(int other_expenses) {
        this.other_expenses = other_expenses;
    }
}
