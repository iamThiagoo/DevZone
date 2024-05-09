package br.com.devzone;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class NavigationActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private HomeFragment homeFragment;
    private CourseFragment courseFragment;

    private ProfileFragment profileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        // Inicializando os fragments
        homeFragment = new HomeFragment();
        courseFragment = new CourseFragment();
        profileFragment = new ProfileFragment();

        // Configurando o BottomNavigationView
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(navItemSelectedListener);

        // Definindo o fragment inicial
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, homeFragment)
                .commit();

        // Configurando o distintivo para o item "content"
        BadgeDrawable badgeDrawable = bottomNavigationView.getOrCreateBadge(R.id.course);
        badgeDrawable.setVisible(true);
        badgeDrawable.setNumber(8);

        // Card view
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
    }

    private final BottomNavigationView.OnItemSelectedListener navItemSelectedListener =
            new BottomNavigationView.OnItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    int itemId = item.getItemId();
                    if (itemId == R.id.home) {
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, homeFragment).commit();
                        return true;
                    } else if (itemId == R.id.course) {
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, courseFragment).commit();
                        return true;
                    }else if (itemId == R.id.profile) {
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, profileFragment).commit();
                        return true;
                    }
                    return false;
                }
            };
}