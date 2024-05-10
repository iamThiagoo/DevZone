package br.com.devzone;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;
    public List<String> dataList = new ArrayList<>();

    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Resgata instância do Firebasee Auth
        mAuth = FirebaseAuth.getInstance();

        // Seta o nome do usuário na tela de Home
        TextView textUsername = view.findViewById(R.id.textUsername);
        String name = mAuth.getCurrentUser().getDisplayName();
        textUsername.setText(name.split(" ")[0]);


        recyclerView = view.findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        //Chama função para buscar as categorias
        getCategorias();

        return view;
    }

    /**
     * Método responsável por buscar as categorias e criar os card view
     */
    private void getCategorias() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("courses_categories").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            // Limpa a lista antes de adicionar novos dados
                            dataList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Adiciona descrição da categoria no card
                                dataList.add(document.getData().get("name").toString());
                            }
                            // Cria o adaptador com os dados e define no RecyclerView
                            adapter = new RecyclerViewAdapter(getActivity(), dataList);
                            recyclerView.setAdapter(adapter);
                        } else {
                            Log.d("Brito", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}
