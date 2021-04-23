package pl.cookbook.ui;

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
import android.view.View;

import java.util.List;

import pl.cookbook.R;
import pl.cookbook.database.AppDatabase;
import pl.cookbook.database.entities.Recipe;
import pl.cookbook.ui.adapters.RecipesAdapter;

public class MainActivity extends AppCompatActivity implements OnRecipesListItemInteractionListener {

    List<Recipe> recipesList;
    RecipesAdapter recipesAdapter;
    RecyclerView recycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recycler = findViewById(R.id.recipesRecyclerView);
        AppDatabase database = AppDatabase.getInstance(this);
        recipesList = database.recipesDao().getAll();
        recipesAdapter = new RecipesAdapter(recipesList, this);

        setFilter(getIntent());

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recycler.setLayoutManager(gridLayoutManager);
        recycler.setAdapter(recipesAdapter);

        findViewById(R.id.btnAddRecipe).setOnClickListener(v ->  {
            startActivity(new Intent(MainActivity.this, EditAddRecipeActivity.class));
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
        //todo start Activity for recipe
    }
}