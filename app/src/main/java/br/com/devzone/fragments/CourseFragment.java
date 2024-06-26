package br.com.devzone.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import br.com.devzone.adapters.CourseAdapter;
import br.com.devzone.R;
import br.com.devzone.activities.CourseActivity;
import br.com.devzone.classes.Course;

public class CourseFragment extends Fragment {

    ListView listaCourses;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_course, container, false);
        listaCourses = view.findViewById(R.id.listaCursos);

        // Recuperar a posição do card clicado se foi enviado, se não for enviado
        // significa que ele veio através do botão navigation
        Bundle args = getArguments();

        if (args != null && args.containsKey("position")) {
            int position = args.getInt("position");

            listaCourseCategoria(position, new OnCoursesLoadedListener() {
                @Override
                public void onCoursesLoaded(ArrayList<Course> courses) {
                    // Atualiza a lista de cursos quando a consulta estiver concluída
                    CourseAdapter adapter = new CourseAdapter(requireContext(), courses);
                    listaCourses.setAdapter(adapter);
                }
            });
        } else {
            listaCourse(new OnCoursesLoadedListener() {
                @Override
                public void onCoursesLoaded(ArrayList<Course> courses) {
                    // Atualiza a lista de cursos quando a consulta estiver concluída
                    CourseAdapter adapter = new CourseAdapter(requireContext(), courses);
                    listaCourses.setAdapter(adapter);
                }
            });
        }

        // Adiciona um listener para cada item da lista
        listaCourses.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> curso, View view, int position, long id) {

                // Obter o objeto Course correspondente à posição clicada
                Course clickedCourse = (Course) curso.getItemAtPosition(position);

                // Resgata o ID do curso
                String courseId = clickedCourse.getId();

                // Envia o usuário para a activity do curso escolhido
                Intent intent = new Intent(getActivity(), CourseActivity.class);
                intent.putExtra("course_id", courseId);
                startActivity(intent);
            }
        });

        return view;
    }

    /**
     * Método para carregar os cursos associados à posição do card clicado
     */
    private void listaCourseCategoria(int position, OnCoursesLoadedListener listener) {

        ArrayList<Course> courses = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("courses").whereEqualTo("categoria_id", position).get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {

                            // Obtém o ID do curso para uso posterior
                            String id = document.getId();

                            // Adiciona descrição da categoria no card
                            String nome = document.getData().get("name").toString();

                            // Busca url salva no Firestone
                            String caminhoImagem = document.getData().get("image_url").toString();

                            courses.add(new Course(id, nome, 1, caminhoImagem));
                        }
                        // Chama o listener quando a consulta estiver concluída
                        listener.onCoursesLoaded(courses);
                    } else {
                        Log.d("Error_listaCourseCategoria", "Error getting documents: ", task.getException());
                    }
                }
            });
    }


    /**
     * Método para carregar os todos os cursos
     */
    private void listaCourse(OnCoursesLoadedListener listener) {

        ArrayList<Course> courses = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("courses").get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {

                            // Obtém o ID do curso para uso posterior
                            String id = document.getId();

                            // Adiciona descrição da categoria no card
                            String nome = document.getData().get("name").toString();

                            // Busca url salva no Firestone
                            String caminhoImagem = document.getData().get("image_url").toString();

                            courses.add(new Course(id, nome, 1, caminhoImagem));
                        }
                        // Chama o listener quando a consulta estiver concluída
                        listener.onCoursesLoaded(courses);
                    } else {
                        Log.d("Error_listaCourse", "Error getting documents: ", task.getException());
                    }
                }
            });
    }

    /**
     * Interface para a interface OnCoursesLoadedListener
     */
    interface OnCoursesLoadedListener {
        void onCoursesLoaded(ArrayList<Course> courses);
    }
}
