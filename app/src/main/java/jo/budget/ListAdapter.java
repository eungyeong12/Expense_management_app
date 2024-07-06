package jo.budget;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ListViewHolder> {
    private ArrayList<Item> itemList;
    private final Context context;
    Util util = new Util();

    public ListAdapter(ArrayList<Item> itemList, Context context) {
        this.itemList = itemList;
        this.context = context;
    }

    @NonNull
    @Override
    public ListAdapter.ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);

        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListAdapter.ListViewHolder holder, int i) {
        Item item = itemList.get(i);

        holder.content.setText(item.getContent());
        holder.expenditure.setText("₩" + util.format(Integer.parseInt(item.getExpenditure().replace(",", ""))));
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM.dd");
        holder.d.setText(dateFormat.format(item.getTimestamp()));

        util.setCategory(item.getCategory(), holder.category);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.position = holder.getBindingAdapterPosition();
                MainActivity.onItemClick = true;
                MainActivity.addItemDialog = new ItemHandler(view.getContext());
                MainActivity.addItemDialog.show();
                MainActivity.addItemDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        MainActivity.onItemClick = false;
                        MainActivity.isDateChange = false;
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return (itemList != null ? itemList.size() : 0);
    }

    public static class ListViewHolder extends  RecyclerView.ViewHolder {
        TextView content;
        TextView expenditure;
        TextView category;
        TextView d;

        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            this.content = itemView.findViewById(R.id.item_content);
            this.expenditure = itemView.findViewById(R.id.item_expenditure);
            this.category = itemView.findViewById(R.id.item_category);
            this.d = itemView.findViewById(R.id.item_date);
        }
    }

    public Item getItem(int position) {
        return itemList.get(position);
    }

    public void remove(int position) {
        Item item = getItem(position);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("date").document(item.getDate())
                .collection("items").document(item.getTimestamp().toString()).delete();


        DocumentReference docRef = db.collection("users")
                .document(MainActivity.mAuth.getCurrentUser().getUid())
                .collection("date")
                .document(item.getDate())
                .collection("monthlyInfo")
                .document(item.getDate());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()) {
                    MonthlyInfo exist = documentSnapshot.toObject(MonthlyInfo.class);
                    exist.setTotal(exist.getTotal()-Integer.parseInt(item.getExpenditure().replace(",","")));
                    util.calCategory2(exist, item);
                    MonthlyInfo monthlyInfo = exist;
                    db.collection("users").document(MainActivity.currentUser.getUid())
                            .collection("date").document(item.getDate())
                            .collection("monthlyInfo").document(item.getDate()).set(monthlyInfo);
                }
            }
        });
    }

}
