package pl.cookbook.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.List;

import pl.cookbook.R;
import pl.cookbook.database.AppDatabase;
import pl.cookbook.database.entities.Recipe;
import pl.cookbook.ui.adapters.RecipesAdapter;
import pl.cookbook.ui.listeners.OnRecipesListItemInteractionListener;

public class MainActivity extends AppCompatActivity implements OnRecipesListItemInteractionListener {

    private static final int EDIT_ADD_RECIPE_REQUEST_CODE = 1;
    List<Recipe> recipesList;
    RecipesAdapter recipesAdapter;
    RecyclerView recycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recycler = findViewById(R.id.recipesRecyclerView);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recycler.setLayoutManager(gridLayoutManager);

        refresh();

        findViewById(R.id.btnAddRecipe).setOnClickListener(v ->  {
            startActivityForResult(EditAddRecipeActivity.createEditAddRecipeActivityIntent(this, 0), EDIT_ADD_RECIPE_REQUEST_CODE);
        });
    }

    private void setFilter(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            if (query != null && query.length() > 0)
                recipesAdapter.filter(query);
            else
                recipesAdapter.clearFilter();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_options_menu, menu);

        MenuItem searchMenuItem = menu.findItem(R.id.menu_search);
        searchMenuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                recipesAdapter.clearFilter();
                return true;
            }
        });
        SearchView searchView = (SearchView) searchMenuItem.getActionView();

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setFilter(intent);

        super.onNewIntent(intent);
    }

    @Override
    public void onSelectRecipe(Recipe recipe) {
        startActivityForResult(EditAddRecipeActivity.createEditAddRecipeActivityIntent(this, recipe.idRecipe), EDIT_ADD_RECIPE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == EDIT_ADD_RECIPE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                refresh();
            }
        }
    }

    private void refresh() {
        AppDatabase database = AppDatabase.getInstance(this);
        recipesList = database.recipesDao().getAll();
        recipesAdapter = new RecipesAdapter(recipesList, this);
        setFilter(getIntent());
        recycler.setAdapter(recipesAdapter);
    }
}