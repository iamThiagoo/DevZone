package br.com.devzone.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import br.com.devzone.R;
import java.util.ArrayList;
import java.util.List;
import br.com.devzone.adapters.QuestionnaireAdapter;
import br.com.devzone.classes.Question;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;


public class QuestionnaireFragment extends Fragment {
    private RecyclerView questionnaireRecyclerView;
    private QuestionnaireAdapter adapter;
    private List<Question> questionList;
    private Button btnEnviarResposta;

    private TextView txtPerguntasNaoEncontradas;

    private FirebaseFirestore db;

    private FirebaseAuth mauth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_questionnaire, container, false);

        questionnaireRecyclerView = view.findViewById(R.id.questionnaire_recycler_view);
        btnEnviarResposta = view.findViewById(R.id.btnEnviarResposta);
        txtPerguntasNaoEncontradas = view.findViewById(R.id.txtPerguntasNaoEncontradas);
        questionnaireRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Inicialização do Firestore e referência à coleção
        db = FirebaseFirestore.getInstance();
        mauth = FirebaseAuth.getInstance();

        loadQuestions();

        return view;
    }

    /**
     * Método que processa as perguntas
     */
    private void loadQuestions() {
        questionList = new ArrayList<>();

        getIdFomQuestion(idVideoQuestoes -> {
            db.collection("course_form_questions").whereEqualTo("course_form_id",
                            idVideoQuestoes)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (!task.getResult().isEmpty()) {
                                questionnaireRecyclerView.setVisibility(View.VISIBLE);
                                btnEnviarResposta.setVisibility(View.VISIBLE);
                                txtPerguntasNaoEncontradas.setVisibility(View.GONE);
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    List<String> answers = (List<String>) document.get("options");
                                    String correctAnswer = document.getString("correct_answer");
                                    questionList.add(new Question(document.getString("name"), answers, correctAnswer));
                                }
                                // Carregar as perguntas
                                adapter = new QuestionnaireAdapter(questionList);
                                questionnaireRecyclerView.setAdapter(adapter);
                            }else{
                                questionnaireRecyclerView.setVisibility(View.GONE);
                                btnEnviarResposta.setVisibility(View.GONE);
                                txtPerguntasNaoEncontradas.setVisibility(View.VISIBLE);
                            }
                        } else {
                            Log.d("Error_question", "Error getting documents: ", task.getException());
                        }
                    });
        });
    }

    // Defini a interface de callback
    public interface FirestoreCallback {
        void onCallback(String idVideoQuestoes);
    }

    // Método que usa o callback para retornar o resultado assíncrono
    private void getIdFomQuestion(FirestoreCallback firestoreCallback) {
        db.collection("user_course_videos").whereEqualTo("user_id", mauth.getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        if (!task.getResult().isEmpty()) {
                            DocumentSnapshot document = task.getResult().getDocuments().get(0);
                            String options = document.getString("course_video_id");
                            firestoreCallback.onCallback(options != null ? options : "");
                        } else {
                            firestoreCallback.onCallback("");
                        }
                    } else {
                        firestoreCallback.onCallback("");
                    }
                });
    }
}
