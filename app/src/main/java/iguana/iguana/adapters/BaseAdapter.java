package iguana.iguana.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by moritz on 19.08.17.
 */



public abstract class BaseAdapter<T> extends RecyclerView.Adapter<BaseAdapter.ViewHolder> {
    protected List<T> items;
    private Context context;
    private OnViewHolderClick<T> listener;
    private OnViewHolderLongClick<T> long_listener;

    protected boolean rev;
    protected String order;

    public interface OnViewHolderClick<T> {
        void onClick(View view, int position, T item);
    }

    public interface OnViewHolderLongClick<T> {
        boolean onLongClick(View view, int position, T item);
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private Map<Integer, View> views;

        public ViewHolder(View view, OnViewHolderClick listener, OnViewHolderLongClick long_listener) {
            super(view);
            views = new HashMap<>();
            views.put(0, view);

            if (listener != null)
                view.setOnClickListener(this);

            if (long_listener != null)
                view.setOnLongClickListener(this);


        }

        @Override
        public void onClick(View view) {
            if (listener != null)
                listener.onClick(view, getAdapterPosition(), getItem(getAdapterPosition()));
        }

        public boolean onLongClick(View view) {
            if (listener != null)
                return long_listener.onLongClick(view, getAdapterPosition(), getItem(getAdapterPosition()));
            return false;
        }

        public void initViewList(int[] idList) {
            for (int id : idList)
                initViewById(id);
        }




        public void initViewById(int id) {
            View view = (getView() != null ? getView().findViewById(id) : null);
            if (view != null)
                views.put(id, view);
        }

        public View getView() {
            return getView(0);
        }

        public View getView(int id) {
            if (views.containsKey(id))
                return views.get(id);
            else
                initViewById(id);

            return views.get(id);
        }
    }

    public void clear() {
        this.items.clear();
        notifyDataSetChanged();
    }

    public void reverse() {
        Collections.reverse(this.items);
    }

    public void toggleReverse() {
        this.rev = !this.rev;
    }

    public void set_order(String order) {
        this.order = order;
    }

    public String get_order() {
        return this.order;
    }

    public void set_reverse(boolean rev) {
        this.rev = rev;
    }

    public Boolean get_reverse() {
        return this.rev;
    }

    protected abstract View createView(Context context, ViewGroup viewGroup, int viewType);

    protected abstract void bindView(T item, BaseAdapter.ViewHolder viewHolder) throws ParseException;

    public BaseAdapter(Context context) {
        this(context, null, null);
    }

    public  BaseAdapter(Context context, OnViewHolderClick<T> listener, OnViewHolderLongClick<T> long_listener) {
        super();
        this.context = context;
        this.listener = listener;
        this.long_listener = long_listener;
        this.items = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        return new ViewHolder(createView(context, viewGroup, viewType), listener, long_listener);
    }

    @Override
    public void onBindViewHolder(BaseAdapter.ViewHolder holder, int position)  {

        try {
            bindView(getItem(position), holder);
        } catch (ParseException e) {
                e.printStackTrace();
            }

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public T getItem(int index) {
        return ((items != null && index < items.size()) ? items.get(index) : null);
    }

    public Context getContext() {
        return context;
    }

    public void setList(List<T> list) {
        items = list;
    }

    public List<T> getList() {
        return items;
    }

    public void setClickListener(OnViewHolderClick listener) {
        this.listener = listener;
    }
    public void setLongClickListener(OnViewHolderLongClick listener) { this.long_listener = listener; }


    public void addAll(List<T> list) {
        items.addAll(list);
    }

    public void reset() {
        items.clear();
        notifyDataSetChanged();
    }
}
