package br.com.devzone;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Padrão da tela
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

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

                    // TODO: Redirecionar para página HomeActivity (quando existir)
                    intent = new Intent(getApplicationContext(), WelcomeActivity.class);

                } else {
                    intent = new Intent(getApplicationContext(), LoginActivity.class);
                }

                startActivity(intent);
                finish();
            }
        }, 2000); // 2000ms = 2 segundos
    }
}