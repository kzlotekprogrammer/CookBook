package pl.cookbook.ui.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import pl.cookbook.R;
import pl.cookbook.database.entities.Recipe;
import pl.cookbook.ui.listeners.OnRecipesListItemInteractionListener;

public class RecipesAdapter extends RecyclerView.Adapter<RecipesAdapter.ViewHolder> {
    private final List<Recipe> allRecipesList;
    private final List<Recipe> filteredRecipesList;
    private final OnRecipesListItemInteractionListener onRecipesListItemInteractionListener;

    public RecipesAdapter(List<Recipe> allRecipesList, OnRecipesListItemInteractionListener onRecipesListItemInteractionListener) {
        this.allRecipesList = allRecipesList;
        this.onRecipesListItemInteractionListener = onRecipesListItemInteractionListener;

        filteredRecipesList = new ArrayList<>();
        filteredRecipesList.addAll(allRecipesList);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_item_recipe, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Recipe recipe = filteredRecipesList.get(position);

        // todo zweryfikować zmianę
        /*File imgFile = null;
        if (recipe.imageFileName != null)
            imgFile = new File(holder.itemView.getContext().getFilesDir(), recipe.imageFileName);

        if (imgFile != null && imgFile.exists()) {
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            holder.imageView.setImageBitmap(myBitmap);
            //holder.imageView.setImageURI(Uri.parse(recipe.imageFileName));*/

        if (recipe.imageFileName != null)
            holder.imageView.setImageURI(Uri.parse(recipe.imageFileName));
        else
            holder.imageView.setImageResource(R.drawable.ic_baseline_local_dining_96);


        if (recipe.name.length() > 20)
            holder.textViewName.setText(String.format("%s...", recipe.name.substring(0, 17)));
        else
            holder.textViewName.setText(recipe.name);

        holder.itemView.setOnClickListener(v -> onRecipesListItemInteractionListener.onSelectRecipe(recipe));
    }

    @Override
    public int getItemCount() {
        return filteredRecipesList.size();
    }

    public void clearFilter() {
        filteredRecipesList.clear();
        filteredRecipesList.addAll(allRecipesList);

        notifyDataSetChanged();
    }

    public void filter(String pattern) {
        filteredRecipesList.clear();

        for (Recipe recipe : allRecipesList) {
            if (recipe.name.toLowerCase().contains(pattern.toLowerCase()))
                filteredRecipesList.add(recipe);
        }

        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        View itemView;
        ImageView imageView;
        TextView textViewName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            this.itemView = itemView;
            this.imageView = itemView.findViewById(R.id.recipeImage);
            this.textViewName = itemView.findViewById(R.id.recipeName);
        }
    }
}
