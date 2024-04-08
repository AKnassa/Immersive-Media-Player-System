package com.meteor.ImmersiveMedia;

import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.Toolbar;  // Make sure to import this
import com.google.android.material.navigationrail.NavigationRailView;
import com.meteor.meteorproject.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Add the toolbar code here
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setTitle("Immersive Reality Video Controller");
        }

        // Initialize your NavigationRailView
        NavigationRailView navigationRailView = findViewById(R.id.navigation_rail);

        navigationRailView.setOnItemSelectedListener(new NavigationRailView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;

                int itemId = item.getItemId();

                if (itemId == R.id.nav_all_devices) {
                    selectedFragment = new AlldevicesFragment();
                } else if (itemId == R.id.nav_calibrate) {
                    selectedFragment = new CalibrateFragment();
                } else if (itemId == R.id.nav_player) {
                    selectedFragment = new PlayerFragment();
                }
                // ... You can add more conditions for other items ...

                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, selectedFragment)
                            .commit();
                }

                return true; // This indicates that you've handled the selection
            }
        });
    }
}
