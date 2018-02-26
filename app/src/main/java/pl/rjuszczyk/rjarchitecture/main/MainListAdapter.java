package pl.rjuszczyk.rjarchitecture.main;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import pl.rjuszczyk.rjarchitecture.R;
import pl.rjuszczyk.rjarchitecture.main.model.MainItem;

public class MainListAdapter extends RecyclerView.Adapter<MainListAdapter.MainItemViewHolder>{

    private List<MainItem> items;

    void setItems(List<MainItem> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public MainItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new MainItemViewHolder(inflater, parent);
    }

    @Override
    public void onBindViewHolder(MainItemViewHolder holder, int position) {
        MainItem item = items.get(position);
        holder.setItem(item);
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    class MainItemViewHolder extends RecyclerView.ViewHolder {

        TextView title;

        MainItemViewHolder(LayoutInflater inflater, ViewGroup parent) {
            this(inflater.inflate(R.layout.row_main_item, parent, false));
        }

        private MainItemViewHolder(View view) {
            super(view);
            title = (TextView) view;
        }

        void setItem(MainItem mainItem) {
            title.setText(mainItem.getName());
        }
    }
}
