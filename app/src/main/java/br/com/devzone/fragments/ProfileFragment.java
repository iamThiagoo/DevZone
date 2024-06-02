package br.com.devzone.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

import br.com.devzone.R;
import br.com.devzone.activities.LoginActivity;

public class ProfileFragment extends Fragment {

    private Button btnSignOut;
    private LinearLayout btnMyData;
    private FirebaseAuth mAuth;

    public ProfileFragment() {}

    public static ProfileFragment newInstance() {

        ProfileFragment fragment = new ProfileFragment();
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Resgata instância do Firebasee Auth
        mAuth = FirebaseAuth.getInstance();

        // Realiza a substituição de mensagem de boas vindas com nome do usuário
        TextView tvWelcome = getView().findViewById(R.id.txtHelloUser);
        String name = mAuth.getCurrentUser().getDisplayName();
        tvWelcome.setText("Olá, " + name + "!");

        // Botões disponíveis para o usuário
        btnSignOut = getView().findViewById(R.id.btnSignOut);
        btnMyData  = getView().findViewById(R.id.btnMyData);

        // Resgata instância do Firebasee Auth
        mAuth = FirebaseAuth.getInstance();

        // Ação para tela de edição de dados
        btnMyData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditProfileFragment editProfileFragment = new EditProfileFragment();
                requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, editProfileFragment).commit();
            }
        });

        // Ação para ação de logout
        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    mAuth.signOut();
                    Thread.sleep(1000);

                    // Mostra mensagem de logout
                    Toast.makeText(requireActivity(), "Você saiu com sucesso!", Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);

                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}