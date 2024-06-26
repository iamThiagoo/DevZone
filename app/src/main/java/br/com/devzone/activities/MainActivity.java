package br.com.devzone.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import br.com.devzone.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Padrão da tela
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        // Carregar a animação de fadeIn na logo
        Animation fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        ImageView imageView = findViewById(R.id.logo);
        imageView.startAnimation(fadeInAnimation);

        // Splash screen
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                // Resgata instância do Firebase Auth
                FirebaseAuth mAuth = FirebaseAuth.getInstance();

                // Resgata possível usuário logado
                FirebaseUser user = mAuth.getCurrentUser();

                // Receberá intent correta
                Intent intent;

                // Verifica se possível usuário logado para redirecioná-lo para página correta
                if (user != null) {
                    intent = new Intent(getApplicationContext(), NavigationActivity.class);

                } else {
                    intent = new Intent(getApplicationContext(), LoginActivity.class);
                }

                startActivity(intent);
                finish();
            }
        }, 2000); // 2000ms = 2 segundos
    }
}