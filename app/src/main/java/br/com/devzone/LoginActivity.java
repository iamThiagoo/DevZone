package br.com.devzone;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private Button btnLogin;
    private Button btnRegistrarLogin;
    private EditText edtEmail;
    private EditText edtSenha;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Padrão da tela
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);

        // Campos do formulário
        edtEmail = findViewById(R.id.txtEmailLogin);
        edtSenha = findViewById(R.id.txtSenhaLogin);

        // Botões disponíveis para usuário
        btnLogin = findViewById(R.id.btnLogin);
        btnRegistrarLogin = findViewById(R.id.btnRegistrarLogin);

        // Ação para botão de login
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processLogin();
            }
        });

        // Ação para tela de cadastro
        btnRegistrarLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(i);
                finish();
            }
        });
    }


    /**
     * Método responsável por processar o login do usuário
     */
    protected void processLogin() {
        // Verifica os campos do login estão preenchidos corretamente
        if (fieldsAreNotEmpty() && fieldsAreValid()) {

            // Resgata os valores dos campos
            String email = String.valueOf(edtEmail.getText()).toLowerCase();
            String senha = String.valueOf(edtSenha.getText());

            // Resgata instância do Firebasee Auth
            mAuth = FirebaseAuth.getInstance();

            // Tenta realizar o login com o Firebase
            mAuth.signInWithEmailAndPassword(email, senha).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {

                        // TODO: Redirecionar para página HomeActivity (quando existir)
                        Intent intent = new Intent(getApplicationContext(), WelcomeActivity.class);
                        startActivity(intent);
                        finish();

                    } else {
                        // Exibe alert bacana ao usuário, informando que deu ruim ao cadastrá-lo no sistema
                        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(LoginActivity.this)
                            .setTitle("Opss... Erro no Login.")
                            .setMessage("Verifique novamente os campos e tente novamente!")
                            .setPositiveButton("Ok, entendido!", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    return;
                                }
                            });
                        builder.show();
                    }
                }
            });
        }
    }


    /**
     * Método que verifica se os valores nos campos não estão vazios
     */
    protected boolean fieldsAreNotEmpty() {

        // Verifica se nome está vazio
        if (TextUtils.isEmpty(edtEmail.getText().toString())) {
            edtEmail.setError("Por favor, insira seu e-mail!");
            edtEmail.requestFocus();
            return false;
        }

        // Verifica se e-mail está vazio
        if (TextUtils.isEmpty(edtSenha.getText().toString())) {
            edtSenha.setError("Por favor, insira sua senha!");
            edtSenha.requestFocus();
            return false;
        }

        return true;
    }


    /**
     * Método que realiza validações extras nos campos de login
     */
    protected boolean fieldsAreValid() {

        // E-mail deve ser válido
        if (!Patterns.EMAIL_ADDRESS.matcher(edtEmail.getText().toString()).matches()) {
            edtEmail.setError("O e-mail deve ser válido!");
            edtEmail.requestFocus();
            return false;
        }

        return true;
    }

}