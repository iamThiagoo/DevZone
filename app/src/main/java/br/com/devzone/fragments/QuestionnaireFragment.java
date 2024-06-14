package br.com.devzone.fragments;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.devzone.R;
import br.com.devzone.adapters.QuestionnaireAdapter;
import br.com.devzone.classes.Question;


public class QuestionnaireFragment extends Fragment {

    private String courseId;

    public QuestionnaireFragment(String courseId) {
       this.courseId = courseId;
    }
    private RecyclerView questionnaireRecyclerView;
    private QuestionnaireAdapter adapter;
    private List<Question> questionList;
    private Button btnEnviarResposta;

    private TextView txtPerguntasNaoEncontradas;

    private FirebaseFirestore db;

    private FirebaseAuth mauth;

    private String idVideo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_questionnaire, container, false);

        questionnaireRecyclerView = view.findViewById(R.id.questionnaire_recycler_view);
        btnEnviarResposta = view.findViewById(R.id.btnEnviarResposta);
        txtPerguntasNaoEncontradas = view.findViewById(R.id.txtPerguntasNaoEncontradas);
        questionnaireRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        btnEnviarResposta.setOnClickListener(v -> {
            Map<Integer, String> answers = adapter.getAnswersMap();
            int correctCount = 0;
            for (Map.Entry<Integer, String> entry : answers.entrySet()) {
                int questionIndex = entry.getKey();
                String userAnswer = entry.getValue();
                String correctAnswer = questionList.get(questionIndex).getCorrectAnswer();
                if (userAnswer.equals(correctAnswer)) {
                    correctCount++;
                }
            }

            processaRespostaUsuario(questionList.size(), correctCount);
            Toast.makeText(getContext(), "Respostas corretas: " + correctCount + " out of " + questionList.size(), Toast.LENGTH_LONG).show();
        });

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
        getIdFromQuestion(idVideoQuestoes -> {
            idVideo = idVideoQuestoes;
            verificarExistenciaRegistro(questionarioRespondido -> {
                if(!questionarioRespondido) {
                    db.collection("course_form_questions").whereEqualTo("course_form_id",
                            idVideoQuestoes).get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (!task.getResult().isEmpty()) {
                                questionnaireRecyclerView.setVisibility(View.VISIBLE);
                                btnEnviarResposta.setVisibility(View.VISIBLE);
                                txtPerguntasNaoEncontradas.setVisibility(View.GONE);
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    List<String> answers = (List<String>) document.get("options");
                                    String correctAnswer = document.getString("correct_response");
                                    questionList.add(new Question(document.getString("name"), answers, correctAnswer));
                                }
                                // Carregar as perguntas
                                adapter = new QuestionnaireAdapter(questionList);
                                questionnaireRecyclerView.setAdapter(adapter);
                            } else {
                                questionnaireRecyclerView.setVisibility(View.GONE);
                                btnEnviarResposta.setVisibility(View.GONE);
                                txtPerguntasNaoEncontradas.setVisibility(View.VISIBLE);
                            }
                        }else {
                            Log.d("Error_question", "Error getting documents: ", task.getException());
                        }

                    });
                }else{
                    questionnaireRecyclerView.setVisibility(View.GONE);
                    txtPerguntasNaoEncontradas.setText("Atividade já respondida!");
                    txtPerguntasNaoEncontradas.setVisibility(View.VISIBLE);
                    btnEnviarResposta.setVisibility(View.GONE);
                }

        });
        });
    }

    // Defini a interface de callback
    public interface FirestoreCallback {
        void onCallback(String idVideoQuestoes);
    }

    // Método que usa o callback para retornar o resultado assíncrono
    private void getIdFromQuestion(FirestoreCallback firestoreCallback) {

        db.collection("user_courses").whereEqualTo("userId", mauth.getUid())
                .whereEqualTo("courseId", courseId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        if (!task.getResult().isEmpty()) {
                            DocumentSnapshot document = task.getResult().getDocuments().get(0);
                            String options = document.getString("currentCourseVideoId");
                            firestoreCallback.onCallback(options != null ? options : "");
                        } else {
                            firestoreCallback.onCallback("");
                        }
                    } else {
                        firestoreCallback.onCallback("");
                    }
                });
    }

    public interface FirestoreCallbackQuestion {
        void onCallback2(Boolean questionarioRespondido);
    }

    private void verificarExistenciaRegistro(FirestoreCallbackQuestion firestoreCallback2) {
        Log.d("britooo", courseId);
        db.collection("user_course_result_forms").whereEqualTo("user_id", mauth.getUid())
                .whereEqualTo("course_id", courseId).whereEqualTo("course_id_video", idVideo)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            firestoreCallback2.onCallback2(true);
                        } else {
                            firestoreCallback2.onCallback2(false);
                        }
                    } else {
                        firestoreCallback2.onCallback2(false);
                    }
                });
    }

    /**
     * Método que salva as resposta do cliente
     */

    private void processaRespostaUsuario(int totalQuestoes, int totalAcertos){
        // Cria estrutura para gravar os dados
        Map<String, Object> result = new HashMap<>();
        result.put("correct_questions", totalAcertos);
        result.put("course_id", courseId);
        result.put("questions", totalQuestoes);
        result.put("course_id_video", idVideo);
        result.put("user_id", mauth.getUid());

        db.collection("user_course_result_forms")
                .add(result)
                .addOnSuccessListener(new OnSuccessListener() {
                    @Override
                    public void onSuccess(Object documentReference) {
                        questionnaireRecyclerView.setVisibility(View.GONE);
                        txtPerguntasNaoEncontradas.setText("Respostas enviadas com sucesso!");
                        txtPerguntasNaoEncontradas.setVisibility(View.VISIBLE);
                        btnEnviarResposta.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Firestore", "Erro ao adicionar documento", e);
                    }
                });




    }
}
