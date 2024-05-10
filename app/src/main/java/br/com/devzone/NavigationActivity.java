package br.com.devzone;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class NavigationActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private HomeFragment homeFragment;
    private CourseFragment courseFragment;

    private ProfileFragment profileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Padrão da tela
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
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