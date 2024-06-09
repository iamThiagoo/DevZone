package br.com.devzone.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import br.com.devzone.R;

import br.com.devzone.classes.Question;

public class QuestionnaireAdapter extends RecyclerView.Adapter<QuestionnaireAdapter.QuestionnaireViewHolder> {

    private List<Question> questionList;
    private Map<Integer, String> answersMap = new HashMap<>();

    public QuestionnaireAdapter(List<Question> questionList) {
        this.questionList = questionList;
    }

    @NonNull
    @Override
    public QuestionnaireViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_questionnaire, parent, false);
        return new QuestionnaireViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionnaireViewHolder holder, int position) {
        Question question = questionList.get(position);
        holder.bind(question, position);
    }

    @Override
    public int getItemCount() {
        return questionList.size();
    }

    public Map<Integer, String> getAnswersMap() {
        return answersMap;
    }

    public class QuestionnaireViewHolder extends RecyclerView.ViewHolder {
        private TextView questionText;
        private RadioGroup answerGroup;

        public QuestionnaireViewHolder(@NonNull View itemView) {
            super(itemView);
            questionText = itemView.findViewById(R.id.question_text);
            answerGroup = itemView.findViewById(R.id.answer_group);
        }

        public void bind(Question question, int position) {
            questionText.setText(question.getQuestionText());
            answerGroup.removeAllViews();

            for (String answer : question.getAnswers()) {
                RadioButton radioButton = new RadioButton(itemView.getContext());
                radioButton.setText(answer);
                answerGroup.addView(radioButton);
            }

            answerGroup.setOnCheckedChangeListener((group, checkedId) -> {
                RadioButton radioButton = group.findViewById(checkedId);
                if (radioButton != null) {
                    answersMap.put(position, radioButton.getText().toString());
                }
            });
        }
    }
}
