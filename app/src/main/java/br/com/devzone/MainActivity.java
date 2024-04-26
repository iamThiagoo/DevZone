package br.com.devzone;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                // TODO: Verificar se usuário estiver logado ou não
                //       Se estiver, envia para a home
                //       Caso contrário, envia para o login

                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);

                startActivity(intent);
                finish();
            }
        }, 2000); // 2000ms = 2 segundos
    }
}