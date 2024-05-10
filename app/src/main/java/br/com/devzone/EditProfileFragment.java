package br.com.devzone;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class EditProfileFragment extends Fragment {

    private TextView edtName;
    private TextView edtEmail;
    private TextView edtActualPassword;
    private TextView edtNewPassword;
    private Button btnUpdateData;
    private Button btnDeleteAccount;
    private FirebaseAuth mAuth;

    public EditProfileFragment() {}

    public static EditProfileFragment newInstance() {

        EditProfileFragment fragment = new EditProfileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Resgata instância do Firebasee Auth
        mAuth = FirebaseAuth.getInstance();

        // Insere o nome do magnata
        edtName = getView().findViewById(R.id.edtName);
        edtName.setText(mAuth.getCurrentUser().getDisplayName());

        // Insere o email do magnata (esse não pode ser alterado, apenas visualização)
        edtEmail = getView().findViewById(R.id.edtEmail);
        edtEmail.setText(mAuth.getCurrentUser().getEmail());
        edtEmail.setEnabled(false);

        // Campos disponíveis para preencher
        edtActualPassword = getView().findViewById(R.id.edtActualPassword);
        edtNewPassword = getView().findViewById(R.id.edtNewPassword);

        // Botões disponíveis para o usuário
        btnUpdateData = getView().findViewById(R.id.btnUpdateData);
        btnDeleteAccount  = getView().findViewById(R.id.btnDeleteAccount);

        // Ação para botão de atualizar dados
        btnUpdateData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processUpdate();
            }
        });

        // Ação para botão de deletar conta
        btnDeleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processDeleteAccount();
            }
        });
    }


    /**
     * Método que controla o fluxo de atualização de dados do usuário
     */
    public boolean processUpdate() {

        // Resgata o usuário atual
        FirebaseUser user = mAuth.getCurrentUser();

        String name = edtName.getText().toString();
        String actualPassword = edtActualPassword.getText().toString();
        String newPassword = edtNewPassword.getText().toString();

        // Nenhum campo está preenchido
        if (name.isEmpty() && actualPassword.isEmpty() && newPassword.isEmpty()) {
            edtName.setError("Preencha um dos campos para atualizar!");
            edtName.requestFocus();
            return false;
        }

        // Se apenas a atual senha estiver vazia
        if (actualPassword.isEmpty() && !newPassword.isEmpty()) {
            edtActualPassword.setError("Por favor, insira sua senha atual!");
            edtActualPassword.requestFocus();
            return false;

        }
        // Se apenas a nova senha estiver vazia
        else if (!actualPassword.isEmpty() && newPassword.isEmpty()) {
            edtNewPassword.setError("Por favor, insira sua nova senha!");
            edtNewPassword.requestFocus();
            return false;

        }
        // Se ambos os campos de senhas tiverem valor, faz a validação para alterar senha
        else if (!actualPassword.isEmpty() && !newPassword.isEmpty()) {

            // Senha deve ter pelo menos 8 caracteres (Firebase exige)
            if (edtNewPassword.getText().toString().length() < 8) {
                edtNewPassword.setError("A nova senha deve conter pelo menos 8 caracteres!");
                edtNewPassword.requestFocus();
                return false;
            }

            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), edtActualPassword.getText().toString());

            // Reautentica o usuário para verificar se a senha que ele digitou, está ou não correta
            user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        user.updatePassword(edtNewPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(requireActivity(), "Senha alterada com sucesso!", Toast.LENGTH_LONG).show();
                                } else {
                                    Log.d("Update password failed", "Falha ao atualizar a senha: " + task.getException().getMessage());
                                }
                            }
                        });
                    } else {
                        // Senha atual incorreta
                        edtActualPassword.setError("Senha atual incorreta!");
                        edtActualPassword.requestFocus();
                    }
                }
            });
        }

        // Se o nome não estiver vazio
        if (!name.isEmpty()) {
            // Se o nome digitado for diferente do atual, atualiza
            if (!name.equals(user.getDisplayName())) {
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(edtName.getText().toString())
                        .build();

                user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(requireActivity(), "Nome alterado com sucesso!", Toast.LENGTH_LONG).show();
                        } else {
                            Log.d("Fullname update failed", "Falha ao atualizar o nome do usuário: " + task.getException().getMessage());
                        }
                    }
                });
            }
        }

        return true;
    }


    /**
     * Método que controla o fluxo de exclusão de conta
     */
    public void processDeleteAccount() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity())
        .setTitle("Tem certeza que deseja deletar sua conta?")
        .setMessage("Essa ação não poderá ser desfeita e todos os seus dados serão apagados!")
        .setPositiveButton("Deletar conta", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final FirebaseUser user = mAuth.getCurrentUser();
                user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            // Finaliza sessão atual
                            mAuth.signOut();

                            // Mostra mensagem
                            Toast.makeText(requireActivity(), "Conta deletada com sucesso!", Toast.LENGTH_LONG).show();

                            // Volta para tela de login
                            Intent intent = new Intent(getActivity(), LoginActivity.class);
                            startActivity(intent);
                            getActivity().finish();
                        }
                    }
                });
            }
        })
        .setNegativeButton("Cancelar", null);

        builder.show();
    }
}