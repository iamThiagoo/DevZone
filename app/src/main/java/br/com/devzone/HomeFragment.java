package br.com.devzone;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements RecyclerViewAdapter.ItemClickListener {

    private RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;
    private List<String> categorias = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

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

        db.collection("courses_categories").orderBy("codigo").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            // Limpa a lista antes de adicionar novos dados
                            categorias.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Adiciona descrição da categoria no card
                                categorias.add(document.getData().get("name").toString());
                            }
                            // Cria o adaptador com os dados e define no RecyclerView
                            adapter = new RecyclerViewAdapter(getActivity(), categorias);
                            adapter.setClickListener(HomeFragment.this); // Configurando o clique
                            recyclerView.setAdapter(adapter);
                        } else {
                            Log.d("Brito", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    /**
    * Método chamado quando um item do RecyclerView é clicado
    */
    @Override
    public void onItemClick(View view, int position) {
        // Criar uma nova instância do fragmento de curso com a posição do card clicado
        CourseFragment courseFragment = new CourseFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("position", position+1);
        courseFragment.setArguments(bundle);

        // Substituir o fragmento atual pelo novo fragmento de curso
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, courseFragment)
                .addToBackStack(null) // Adicionar a transação à pilha de retorno
                .commit();

        // Ação a ser executada quando um item do CardView é clicado for clicado
        String categoriaSelecionada = categorias.get(position);
    }
}

