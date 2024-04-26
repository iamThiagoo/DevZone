package br.com.devzone;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;

public class RegisterActivity extends AppCompatActivity {

    Button btnRegistrar;
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_register);

        btnRegistrar = findViewById(R.id.btnRegistrar);
        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processNewRegister();
            }
        });

        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    /**
     * Método que processará o novo registro do usuário
     */
    protected void processNewRegister() {
        if (fieldsIsNotEmpty()) {
            EditText txtNome = findViewById(R.id.txtNome);
            String nome = txtNome.getText().toString();

            EditText txtEmail = findViewById(R.id.txtEmail);
            String email = txtNome.getText().toString();

            EditText txtSenha = findViewById(R.id.txtSenha);
            String senha = txtNome.getText().toString();

        }
    }


    /*
     * Método que verifica se os valores nos campos não estão vazios
     */
    protected boolean fieldsIsNotEmpty() {
        try {
            EditText txtNome = findViewById(R.id.txtNome);
            String nome = txtNome.getText().toString();
            if (nome.isEmpty()) {
                throw new Exception("Você esqueceu de preencher o campo de \"Nome\"!");
            }

            EditText txtEmail = findViewById(R.id.txtEmail);
            String email = txtEmail.getText().toString();
            if (email.isEmpty()) {
                throw new Exception("Você esqueceu de preencher o campo de \"Email\"!");
            }

            EditText txtSenha = findViewById(R.id.txtSenha);
            String senha = txtSenha.getText().toString();
            if (senha.isEmpty()) {
                throw new Exception("Você esqueceu de preencher o campo de \"Senha\"!");
            }

            return true;

        } catch (Exception e) {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(RegisterActivity.this)
                    .setTitle("Opss... Você esqueceu de algo!")
                    .setMessage(e.getMessage())
                    .setPositiveButton("Ok, obrigado!", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            return;
                        }
                    });
            builder.show();
            return false;
        }
    }
}