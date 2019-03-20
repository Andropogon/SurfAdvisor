package livewind.example.andro.liveWind.Filter;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import livewind.example.andro.liveWind.AboutActivity;
import livewind.example.andro.liveWind.CatalogActivity;
import livewind.example.andro.liveWind.R;

/** An implementation of the View */

public class FilterTripsActivity extends AppCompatActivity
        implements FilterTripsContract.View {

    private FilterTripsContract.Presenter mPresenter;

    //UI properties
    private EditText mCostView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        initViews();

        // Creates presenter
        mPresenter = new FilterTripsPresenter();
    }

    private void initViews() {
        mCostView = (EditText) findViewById(R.id.filter_price_value_edit_text);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_filter, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case livewind.example.andro.liveWind.R.id.action_save:
                mPresenter.saveCost(mCostView.getText().toString());
                mPresenter.sendPreferences();
                Intent intentCatalog = new Intent(FilterTripsActivity.this,CatalogActivity.class);
                startActivity(intentCatalog);
                    return true;
            case android.R.id.home:
                // TODO Add checking unsaved edits if (!mEventHasChanged) {
                    finish();
                    return true;
                }
        return super.onOptionsItemSelected(item);
    }

}