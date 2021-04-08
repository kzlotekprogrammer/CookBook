package pl.cookbook.ui.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.List;

import pl.cookbook.R;
import pl.cookbook.database.entities.Recipe;

public class RecipesAdapter extends RecyclerView.Adapter<RecipesAdapter.ViewHolder> {
    private final List<Recipe> recipesList;

    public RecipesAdapter(List<Recipe> recipesList) {
        this.recipesList = recipesList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_item_recipe, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Recipe recipe = recipesList.get(position);

        File imgFile = null;
        if (recipe.imageFileName != null)
            imgFile = new File(holder.itemView.getContext().getFilesDir(), recipe.imageFileName);

        if (imgFile != null && imgFile.exists()) {
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            holder.imageView.setImageBitmap(myBitmap);
        } else {
            holder.imageView.setImageResource(R.drawable.ic_baseline_local_dining_96);
        }

        holder.textViewName.setText(recipe.name);
    }

    @Override
    public int getItemCount() {
        return recipesList.size();
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
