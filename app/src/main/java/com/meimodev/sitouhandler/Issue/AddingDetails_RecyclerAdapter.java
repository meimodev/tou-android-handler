package com.meimodev.sitouhandler.Issue;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.meimodev.sitouhandler.CustomWidget.CustomEditText;
import com.meimodev.sitouhandler.R;

import java.util.ArrayList;

import butterknife.ButterKnife;

public class AddingDetails_RecyclerAdapter extends RecyclerView.Adapter<AddingDetails_RecyclerAdapter.MyViewHolder> {

    private final String TAG = "RecyAdap_AddDetails :";
    private Context context;
    private ArrayList<AddingDetails_RecyclerModel> items;


//    @BindView(R.id.recyclerItem_detail)
//    @BindView(R.id.recyclerItem_amount)
//    @BindView(R.id.recyclerItem_delete)

    public AddingDetails_RecyclerAdapter(Context ctx, ArrayList<AddingDetails_RecyclerModel> items) {
        this.context = ctx;
        this.items = items;
    }

    @NonNull
    @Override
    public AddingDetails_RecyclerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_item_adding_details, parent, false);
        ButterKnife.bind(this, view);
        return new MyViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        EditText etDetail;
        CustomEditText etAmount;
        Button btnDelete;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            etDetail = itemView.findViewById(R.id.recyclerItem_detail);
            etAmount = itemView.findViewById(R.id.recyclerItem_amount);
            btnDelete = itemView.findViewById(R.id.recyclerItem_delete);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        AddingDetails_RecyclerModel model = items.get(holder.getAdapterPosition());
        holder.etAmount.setAsThousandSeparator();

        holder.etAmount.setOnFocusChangeListener((view, b) -> {
            if (!b) {
                model.setDetails(String.valueOf(holder.etDetail.getText()));
                model.setAmount(String.valueOf(holder.etAmount.getText()));
            }
        });
        holder.etDetail.setOnFocusChangeListener((view, b) -> {
            if (!b) {
                model.setDetails(String.valueOf(holder.etDetail.getText()));
                model.setAmount(String.valueOf(holder.etAmount.getText()));
            }
        });

//        if (holder.getAdapterPosition() != items.size() - 1)
//            holder.btnDelete.setVisibility(View.GONE);

//        try {
//            if (!model.getDetails().isEmpty()) holder.etDetail.setText(model.getDetails());
//            if (!model.getAmount().isEmpty()) holder.etAmount.setText(model.getAmount());

//        } catch (NullPointerException e) {
//            Log.e(TAG, "onBindViewHolder: NPE catch");
//        }

        holder.btnDelete.setOnClickListener(view -> {
            Log.e(TAG, "onBindViewHolder: Test Delete Button POSITION: " + position);
            Log.e(TAG, "onBindViewHolder: Test Delete Button GET ADAPTER POSITION: " + holder.getAdapterPosition());
            Log.e(TAG, "onBindViewHolder: Test Delete Button GET  POSITION: " + holder.getPosition());
            Log.e(TAG, "onBindViewHolder: Test Delete Button GET LAYOUT POSITION: " + holder.getLayoutPosition());
            Log.e(TAG, "onBindViewHolder: Test Delete Button GET OLD POSITION: " + holder.getOldPosition());

//            holder.etDetail.setText("");
//            holder.etAmount.setText("");

            items.remove(holder.getAdapterPosition());
//            notifyDataSetChanged();

            if (onRecyclerItemDeleteListener != null)
                onRecyclerItemDeleteListener.delete();
            if (onRecyclerItemDeleteDetailListener != null)
                onRecyclerItemDeleteDetailListener.deleteDetails(holder.getAdapterPosition());
            if (onRecyclerItemCreateListener != null)
                onRecyclerItemCreateListener.createItem(holder.getAdapterPosition());
        });
    }

    //    Listener Section
    private OnRecyclerItemOperationListener.DeleteListener onRecyclerItemDeleteListener;
    private OnRecyclerItemOperationListener.DeleteDetailsListener onRecyclerItemDeleteDetailListener;
    private OnRecyclerItemOperationListener.CreateItemListener onRecyclerItemCreateListener;

    public void setOnRecyclerItemDeleteListener(OnRecyclerItemOperationListener.DeleteListener onRecyclerItemDeleteListener) {
        this.onRecyclerItemDeleteListener = onRecyclerItemDeleteListener;
    }

    public void setOnRecyclerItemDeleteDetailListener(OnRecyclerItemOperationListener.DeleteDetailsListener onRecyclerItemDeleteDetailListener) {
        this.onRecyclerItemDeleteDetailListener = onRecyclerItemDeleteDetailListener;
    }

    public void setOnRecyclerItemCreateListener(OnRecyclerItemOperationListener.CreateItemListener onRecyclerItemCreateListener) {
        this.onRecyclerItemCreateListener = onRecyclerItemCreateListener;
    }

    public void setItems(ArrayList<AddingDetails_RecyclerModel> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    private void deleteItem(int modelId) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getId() == modelId) {
                items.remove(i);
                notifyItemRemoved(i);
                break;
            }
        }
    }

    public void addItem(AddingDetails_RecyclerModel model) {
        items.add(model);
        notifyItemInserted(getItemCount());
    }

    public ArrayList<AddingDetails_RecyclerModel> getItems() {
        return items;
    }


}
