package pl.cookbook.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import pl.cookbook.R;
import pl.cookbook.database.entities.RecipeProduct;
import pl.cookbook.ui.OnRecipeProductListInteractionListener;

public class RecipeProductsAdapter extends RecyclerView.Adapter<RecipeProductsAdapter.ViewHolder> {

    List<RecipeProduct> recipeProductList;
    OnRecipeProductListInteractionListener onRecipeProductListInteractionListener;

    public RecipeProductsAdapter(Context context, List<RecipeProduct> recipeProductList, OnRecipeProductListInteractionListener onRecipeProductListInteractionListener) {
        this.recipeProductList = recipeProductList;
        this.onRecipeProductListInteractionListener = onRecipeProductListInteractionListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_item_product, parent, false);

        return new RecipeProductsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RecipeProduct recipeProduct = recipeProductList.get(position);

        holder.textViewName.setText(recipeProduct.productName);

        holder.imageViewDelete.setOnClickListener(v -> onRecipeProductListInteractionListener.onDeleteRecipeProduct(recipeProduct));
    }

    @Override
    public int getItemCount() {
        return recipeProductList.size();
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
