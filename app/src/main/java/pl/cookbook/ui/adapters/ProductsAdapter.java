package pl.cookbook.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import pl.cookbook.R;
import pl.cookbook.database.entities.Product;
import pl.cookbook.ui.listeners.OnProductsListItemInteractionListener;

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ViewHolder> {
    private final List<Product> allProductsList;
    private final List<Product> filteredProductsList;
    private final OnProductsListItemInteractionListener onProductsListItemInteractionListener;

    public ProductsAdapter(List<Product> allProductsList, OnProductsListItemInteractionListener onProductsListItemInteractionListener) {
        this.allProductsList = allProductsList;
        this.onProductsListItemInteractionListener = onProductsListItemInteractionListener;

        filteredProductsList = new ArrayList<>();
        filteredProductsList.addAll(allProductsList);
    }

    @NonNull
    @Override
    public ProductsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_item_product, parent, false);

        return new ProductsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductsAdapter.ViewHolder holder, int position) {
        Product product = filteredProductsList.get(position);

        holder.textViewName.setText(product.name);

        holder.textViewName.setOnClickListener(v -> onProductsListItemInteractionListener.onSelectProduct(product));

        holder.imageViewDelete.setOnClickListener(v -> onProductsListItemInteractionListener.onDeleteProduct(product));
    }

    @Override
    public int getItemCount() {
        return filteredProductsList.size();
    }

    public void clearFilter() {
        filteredProductsList.clear();
        filteredProductsList.addAll(allProductsList);

        notifyDataSetChanged();
    }

    public void filter(String pattern) {
        filteredProductsList.clear();

        for (Product product : allProductsList) {
            if (product.name.toLowerCase().contains(pattern.toLowerCase()))
                filteredProductsList.add(product);
        }

        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        View itemView;
        TextView textViewName;
        ImageView imageViewDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            this.itemView = itemView;
            this.textViewName = itemView.findViewById(R.id.productName);
            this.imageViewDelete = itemView.findViewById(R.id.deleteProduct);
        }
    }
}
