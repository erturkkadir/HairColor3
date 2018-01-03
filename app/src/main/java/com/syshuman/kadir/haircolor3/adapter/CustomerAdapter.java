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
import com.syshuman.kadir.haircolor3.model.Customer;

import java.util.ArrayList;
import java.util.List;



public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.MyViewHolder>  implements Filterable {

    private List<Customer> customerList;
    private List<Customer> customerListFiltered;

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
                    listener.onCustomerSelected(customerListFiltered.get(getAdapterPosition()));
                }
            });
        }
    }


    public CustomerAdapter(Context context, List<Customer> customerList, CustomerAdapterListener listener) {
        this.context = context;
        this.listener = listener;
        this.customerListFiltered = customerList;
        this.customerList = customerList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_customer, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Customer customer = customerList.get(position);
        holder.fname.setText(customer.getFirstName());
        holder.lname.setText(customer.getLastName());
        holder.email.setText(customer.getEmail());

    }

    @Override
    public int getItemCount() {
        return customerList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString = constraint.toString();
                if(charString.isEmpty()) {
                    customerListFiltered = customerList;
                } else {
                    List<Customer> filteredList = new ArrayList<>();
                    for(Customer row : customerList) {
                        if(row.getFirstName().contains(charString.toLowerCase() )) {
                            filteredList.add(row);
                        }
                    }
                    customerListFiltered = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = customerListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                customerListFiltered = (ArrayList<Customer>) results.values;
                notifyDataSetChanged();;
            }
        };
    }

    public interface CustomerAdapterListener {
        void onCustomerSelected(Customer customer);
    }
}

