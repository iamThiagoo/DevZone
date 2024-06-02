package br.com.devzone.activities;

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
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import br.com.devzone.R;

public class RegisterActivity extends AppCompatActivity {

    private EditText txtNome;
    private EditText txtEmail;
    private EditText txtSenha;

    private Button btnRegistrar;
    private Button btnLogin;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Padrão da tela
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_register);

        // Campos do formulário
        txtNome = findViewById(R.id.txtNome);
        txtEmail = findViewById(R.id.txtEmail);
        txtSenha = findViewById(R.id.txtSenha);

        // Botões disponíveis para usuário
        btnRegistrar = findViewById(R.id.btnRegistrar);
        btnLogin = findViewById(R.id.btnLogin);

        // Ação para botão de registro
        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processNewUser();
            }
        });

        // Ação para botão de login
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
    protected void processNewUser() {
        // Verifica os campos do cadastro estão preenchidos corretamente
        if (fieldsAreNotEmpty() && fieldsAreValid()) {

            // Resgata os valores dos campos
            String nome = String.valueOf(txtNome.getText());
            String email = String.valueOf(txtEmail.getText()).toLowerCase();
            String senha = String.valueOf(txtSenha.getText());

            // Resgata instância do Firebasee Auth
            mAuth = FirebaseAuth.getInstance();

            // Cria novo usuário no Firebase Authentication
            mAuth.createUserWithEmailAndPassword(email, senha).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Resgata o objeto do usuário recém criado
                        FirebaseUser user = mAuth.getCurrentUser();

                        // Atualiza nome do usuário no perfil dentro do Firebase Authentication
                        processNewUsername(user, nome);

                    } else {
                        processAuthError(task.getException());
                    }
                }
            });
        }
    }


    /**
     * Método que atualiza o nome do usuário no seu perfil
     * dentro do Firebase
     *
     * Modo de uso dos dados inseridos no perfil -> https://firebase.google.com/docs/auth/android/manage-users?hl=pt#java_1
     */
    protected void processNewUsername(FirebaseUser user, String name) {

        // Definir o nome de exibição do usuário
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();

        user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Intent intent = new Intent(getApplicationContext(), NavigationActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }


    /**
     * Método que processa o erro de registro do usuário no
     * Firebase Authentication
     */
    protected void processAuthError(Exception error) {
        String message;

        // Cria mensagem de erro caso já exista usuário com este e-mail
        if (error instanceof FirebaseAuthUserCollisionException) {
            message = "Já existe uma conta com este e-mail!";

        } else {
            // Lança mensagem genérica
            message = "Parece que algo deu errado durante o seu cadastro. Pedimos que tente o envio novamente e se persistir o erro, entre em contato com o nosso suporte.";
        }

        // Exibe alert bacana ao usuário, informando que deu ruim ao cadastrá-lo no sistema
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(RegisterActivity.this)
                .setTitle("Opss... Ocorreu um erro!")
                .setMessage(message)
                .setPositiveButton("Ok, entendido!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                });
        builder.show();
    }


    /*
     * Método que verifica se os valores nos campos não estão vazios
     */
    protected boolean fieldsAreNotEmpty() {

        // Verifica se nome está vazio
        if (TextUtils.isEmpty(txtNome.getText().toString())) {
            txtNome.setError("Por favor, insira seu nome!");
            txtNome.requestFocus();
            return false;
        }

        // Verifica se e-mail está vazio
        if (TextUtils.isEmpty(txtEmail.getText().toString())) {
            txtEmail.setError("Por favor, insira seu e-mail!");
            txtEmail.requestFocus();
            return false;
        }

        // Verifica se senha está vazia
        if (TextUtils.isEmpty(txtSenha.getText().toString())) {
            txtSenha.setError("Por favor, insira sua senha!");
            txtSenha.requestFocus();
            return false;
        }

        return true;
    }


    /*
     * Método que realiza validações extras nos campos de cadastro
     */
    protected boolean fieldsAreValid() {

        // E-mail deve ser válido
        if (!Patterns.EMAIL_ADDRESS.matcher(txtEmail.getText().toString()).matches()) {
            txtEmail.setError("O e-mail deve ser válido!");
            txtEmail.requestFocus();
            return false;
        }

        // Senha deve ter pelo menos 8 caracteres (Firebase exige)
        if (txtSenha.getText().toString().length() < 8) {
            txtSenha.setError("A senha deve conter pelo menos 8 caracteres!");
            txtSenha.requestFocus();
            return false;
        }

        return true;
    }
}