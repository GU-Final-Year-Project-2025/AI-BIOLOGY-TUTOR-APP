package com.example.aibiotutor;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class Quiz extends AppCompatActivity {

    private TextView timerTextView, questionTextView, backgroundTextView;
    private RadioGroup optionsGroup;
    private Button nextButton, quitButton;
    private ProgressBar progressBarView;

    private CountDownTimer countDownTimer;
    private final long totalTimeMillis = 10 * 60 * 1000L; // 10 minutes

    private int score = 0;
    private int currentQuestionIndex = 0;

    private List<ShuffledQuestion> quizQuestions;

    // Question structure
    private static class Question {
        String background;
        String question;
        String[] options;
        int correctAnswerIndex;

        Question(String background, String question, String[] options, int correctAnswerIndex) {
            this.background = background;
            this.question = question;
            this.options = options;
            this.correctAnswerIndex = correctAnswerIndex;
        }
    }

    // Structure after shuffling options
    private static class ShuffledQuestion {
        String background;
        String question;
        List<String> options;
        int correctAnswerIndex;

        ShuffledQuestion(Question q) {
            this.background = q.background;
            this.question = q.question;

            List<String> optionList = new ArrayList<>();
            Collections.addAll(optionList, q.options);
            String correctAnswer = q.options[q.correctAnswerIndex];

            Collections.shuffle(optionList);

            this.options = optionList;
            this.correctAnswerIndex = optionList.indexOf(correctAnswer);
        }
    }

    private final Question[] originalQuestions = new Question[]{
            new Question("Cellular respiration occurs in the mitochondria and involves energy production.",
                    "What is the site of cellular respiration?",
                    new String[]{"Mitochondria", "Chloroplast", "Ribosome", "Nucleus"}, 0),
            new Question("Chloroplasts are organelles involved in photosynthesis in plants.",
                    "Which pigment captures light energy in photosynthesis?",
                    new String[]{"Chlorophyll", "Carotene", "Xanthophyll", "Hemoglobin"}, 0),
            new Question("DNA stores genetic information in living organisms.",
                    "What shape is the DNA molecule?",
                    new String[]{"Double helix", "Single strand", "Triple helix", "Circular ring"}, 0),
            new Question("Proteins are made up of amino acids linked together.",
                    "Which organelle is responsible for protein synthesis?",
                    new String[]{"Ribosome", "Golgi apparatus", "Lysosome", "Nucleus"}, 0),
            new Question("The cell membrane controls what enters and leaves the cell.",
                    "What type of molecule primarily makes up the cell membrane?",
                    new String[]{"Phospholipids", "Carbohydrates", "Proteins", "Nucleic acids"}, 0),
            new Question("The circulatory system transports blood throughout the body.",
                    "Which organ pumps blood in the human body?",
                    new String[]{"Heart", "Lungs", "Kidneys", "Liver"}, 0),
            new Question("Plants take in carbon dioxide and release oxygen during photosynthesis.",
                    "What gas do plants absorb from the atmosphere?",
                    new String[]{"Carbon dioxide", "Oxygen", "Nitrogen", "Methane"}, 0),
            new Question("Enzymes speed up chemical reactions in the body.",
                    "What type of biological molecule are enzymes?",
                    new String[]{"Proteins", "Lipids", "Carbohydrates", "Nucleic acids"}, 0),
            new Question("The nervous system sends signals throughout the body.",
                    "Which cells transmit nerve impulses?",
                    new String[]{"Neurons", "Glial cells", "Muscle cells", "Epithelial cells"}, 0),
            new Question("Mitosis is a type of cell division for growth and repair.",
                    "How many daughter cells are produced after mitosis?",
                    new String[]{"Two", "One", "Four", "Eight"}, 0),
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        timerTextView = findViewById(R.id.timerTextView);
        progressBarView = findViewById(R.id.progressBar);
        questionTextView = findViewById(R.id.questionTextView);
        backgroundTextView = findViewById(R.id.backgroundTextView);
        optionsGroup = findViewById(R.id.optionsGroup);
        nextButton = findViewById(R.id.nextButton);
        quitButton = findViewById(R.id.quitButton);

        nextButton.setEnabled(false);

        // Shuffle questions and their options
        quizQuestions = new ArrayList<>();
        for (Question q : originalQuestions) {
            quizQuestions.add(new ShuffledQuestion(q));
        }
        Collections.shuffle(quizQuestions);

        loadQuestion();

        optionsGroup.setOnCheckedChangeListener((group, checkedId) -> nextButton.setEnabled(true));

        nextButton.setOnClickListener(v -> {
            checkAnswerAndUpdateScore();
            currentQuestionIndex++;

            if (currentQuestionIndex < quizQuestions.size()) {
                loadQuestion();
            } else {
                endQuiz();
            }
        });

        quitButton.setOnClickListener(v -> finish());

        startTimer();
    }

    private void loadQuestion() {
        nextButton.setEnabled(false);
        optionsGroup.clearCheck();

        ShuffledQuestion q = quizQuestions.get(currentQuestionIndex);
        backgroundTextView.setText("Background: " + q.background);
        questionTextView.setText("Question: " + q.question);

        for (int i = 0; i < optionsGroup.getChildCount(); i++) {
            RadioButton rb = (RadioButton) optionsGroup.getChildAt(i);
            if (i < q.options.size()) {
                rb.setText(q.options.get(i));
                rb.setVisibility(View.VISIBLE);
            } else {
                rb.setVisibility(View.GONE);
            }
        }

        int progressPercent = (int) (((float) currentQuestionIndex / quizQuestions.size()) * 100);
        progressBarView.setProgress(progressPercent);
    }

    private void checkAnswerAndUpdateScore() {
        int selectedId = optionsGroup.getCheckedRadioButtonId();
        if (selectedId != -1) {
            View selectedRadioButton = optionsGroup.findViewById(selectedId);
            int selectedIndex = optionsGroup.indexOfChild(selectedRadioButton);
            if (selectedIndex == quizQuestions.get(currentQuestionIndex).correctAnswerIndex) {
                score++;
            }
        }
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(totalTimeMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int minutes = (int) (millisUntilFinished / 1000) / 60;
                int seconds = (int) (millisUntilFinished / 1000) % 60;
                timerTextView.setText(String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds));
            }

            @Override
            public void onFinish() {
                timerTextView.setText("00:00");
                endQuiz();
            }
        };
        countDownTimer.start();
    }

    private void endQuiz() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        if (score >= 5) {
            // Launch game
            new AlertDialog.Builder(this)
                    .setTitle("Quiz Finished")
                    .setMessage("Your score is " + score + ". Well done! Let's play a game.")
                    .setPositiveButton("Play Game", (dialog, which) -> {
                        Intent intent = new Intent(Quiz.this, ReactionGame.class);
                        startActivity(intent);
                        finish();
                    })
                    .setCancelable(false)
                    .show();
        } else {
            // Repeat quiz
            new AlertDialog.Builder(this)
                    .setTitle("Quiz Finished")
                    .setMessage("Your score is " + score + ". Let's try again.")
                    .setPositiveButton("Retry Quiz", (dialog, which) -> {
                        Collections.shuffle(quizQuestions);
                        score = 0;
                        currentQuestionIndex = 0;
                        loadQuestion();
                        startTimer();
                    })
                    .setCancelable(false)
                    .show();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}
