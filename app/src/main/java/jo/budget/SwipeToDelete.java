package jo.budget;

import android.graphics.Canvas;
import android.graphics.Color;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class SwipeToDelete {
    private Util util = new Util();
    SwipeToDelete() {
        delete();
    }

    private void delete() {
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int p = viewHolder.getLayoutPosition();
                switch (direction) {
                    case ItemTouchHelper.LEFT:
                        Item deleteItem = MainActivity.adapter.getItem(p);
                        MainActivity.adapter.remove(p);

                        Snackbar.make(MainActivity.recyclerView, "",  Snackbar.LENGTH_LONG)
                                .setBackgroundTint(Color.parseColor("#ECEFF1"))
                                .setAction("복구", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        MainActivity.db = FirebaseFirestore.getInstance();
                                        MainActivity.db.collection("users").document(MainActivity.currentUser.getUid())
                                                .collection("date").document(deleteItem.getDate())
                                                .collection("items").document(deleteItem.getTimestamp().toString()).set(deleteItem);
                                        DocumentReference docRef = MainActivity.db.collection("users")
                                                .document(MainActivity.mAuth.getCurrentUser().getUid())
                                                .collection("date")
                                                .document(deleteItem.getDate())
                                                .collection("monthlyInfo")
                                                .document(deleteItem.getDate());
                                        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                if(documentSnapshot.exists()) {
                                                    MonthlyInfo exist = documentSnapshot.toObject(MonthlyInfo.class);
                                                    exist.setTotal(exist.getTotal()+Integer.parseInt(deleteItem.getExpenditure().replace(",","")));

                                                    util.calCategory1(exist, deleteItem);

                                                    MonthlyInfo monthlyInfo = exist;
                                                    MainActivity.db.collection("users").document(MainActivity.currentUser.getUid())
                                                            .collection("date").document(deleteItem.getDate())
                                                            .collection("monthlyInfo").document(deleteItem.getDate()).set(monthlyInfo);
                                                } else {
                                                    MonthlyInfo monthlyInfo = new MonthlyInfo(-1,0,0,0,0,0,0,0,0,0,0);
                                                    monthlyInfo.setTotal(Integer.parseInt(deleteItem.getExpenditure().replace(",","")));
                                                    util.calCategory3(monthlyInfo, deleteItem);
                                                    MainActivity.db.collection("users").document(MainActivity.currentUser.getUid())
                                                            .collection("date").document(deleteItem.getDate())
                                                            .collection("monthlyInfo").document(deleteItem.getDate()).set(monthlyInfo);
                                                }
                                            }
                                        });
                                    }
                                }).show();
                        break;
                }
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftBackgroundColor(Color.parseColor("#FF6347"))
                        .addSwipeLeftActionIcon(R.drawable.baseline_delete_24)
                        .addSwipeLeftLabel("삭제")
                        .setSwipeLeftLabelColor(Color.WHITE)
                        .create()
                        .decorate();
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }).attachToRecyclerView(MainActivity.recyclerView);
    }

}
