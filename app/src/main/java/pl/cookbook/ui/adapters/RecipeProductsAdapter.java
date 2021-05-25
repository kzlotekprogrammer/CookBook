package pl.cookbook.ui.adapters;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import pl.cookbook.R;
import pl.cookbook.database.entities.RecipeProduct;
import pl.cookbook.database.entities.Unit;
import pl.cookbook.ui.listeners.OnRecipeProductListInteractionListener;

public class RecipeProductsAdapter extends RecyclerView.Adapter<RecipeProductsAdapter.ViewHolder> {

    private final Context context;
    private final List<RecipeProduct> recipeProductList;
    private final List<Unit> unitList;
    private final OnRecipeProductListInteractionListener onRecipeProductListInteractionListener;

    public RecipeProductsAdapter(Context context, List<RecipeProduct> recipeProductList, List<Unit> unitList, OnRecipeProductListInteractionListener onRecipeProductListInteractionListener) {
        this.context = context;
        this.recipeProductList = recipeProductList;
        this.unitList = unitList;
        this.onRecipeProductListInteractionListener = onRecipeProductListInteractionListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recylcer_view_item_recipe_product, parent, false);

        return new RecipeProductsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RecipeProduct recipeProduct = recipeProductList.get(position);

        holder.textViewName.setText(recipeProduct.productName);

        UnitsAdapter unitsAdapter = new UnitsAdapter(context, unitList);

        holder.spinnerUnit.setAdapter(unitsAdapter);
        holder.spinnerUnit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                recipeProduct.idUnit = ((Unit) parent.getItemAtPosition(position)).idUnit;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        holder.editTextQuantity.setText(String.valueOf(recipeProduct.quantity));
        holder.editTextQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    recipeProduct.quantity = Integer.parseInt(s.toString());
                } catch (Exception ex) {
                    recipeProduct.quantity = 0;
                    holder.editTextQuantity.setError(context.getString(R.string.invalid_value));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

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
        Spinner spinnerUnit;
        EditText editTextQuantity;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            this.itemView = itemView;
            this.textViewName = itemView.findViewById(R.id.productName);
            this.imageViewDelete = itemView.findViewById(R.id.deleteProduct);
            this.spinnerUnit = itemView.findViewById(R.id.spinnerUnit);
            this.editTextQuantity = itemView.findViewById(R.id.quantity);
        }
    }
}
