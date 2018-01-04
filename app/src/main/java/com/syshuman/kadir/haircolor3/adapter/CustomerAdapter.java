package com.syshuman.kadir.haircolor3.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.syshuman.kadir.haircolor3.R;
import com.syshuman.kadir.haircolor3.model.CustM;

import java.util.ArrayList;
import java.util.List;



public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.MyViewHolder>  implements Filterable {

    private List<CustM> custMList;
    private List<CustM> custMListFiltered;

    private CustomerAdapterListener listener;
    private Context context;



    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView fname, lname, email;

        public MyViewHolder(View view) {
            super(view);
            fname = (TextView) view.findViewById(R.id.rvFirstName);
            lname = (TextView) view.findViewById(R.id.rvLastName);
            email = (TextView) view.findViewById(R.id.rvEmail);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onCustomerSelected(custMListFiltered.get(getAdapterPosition()));
                }
            });
        }
    }


    public CustomerAdapter(Context context, List<CustM> custMList, CustomerAdapterListener listener) {
        this.context = context;
        this.listener = listener;
        this.custMListFiltered = custMList;
        this.custMList = custMList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_customer, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        CustM custM = custMList.get(position);
        holder.fname.setText(custM.getCm_fname());
        holder.lname.setText(custM.getCm_lname());
        holder.email.setText(custM.getCm_email());

    }

    @Override
    public int getItemCount() {
        return custMList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString = constraint.toString();
                if(charString.isEmpty()) {
                    custMListFiltered = custMList;
                } else {
                    List<CustM> filteredList = new ArrayList<>();
                    for(CustM row : custMList) {
                        if(row.getCm_fname().contains(charString.toLowerCase() )) {
                            filteredList.add(row);
                        }
                    }
                    custMListFiltered = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = custMListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                custMListFiltered = (ArrayList<CustM>) results.values;
                notifyDataSetChanged();;
            }
        };
    }

    public interface CustomerAdapterListener {
        void onCustomerSelected(CustM custM);
    }
}

