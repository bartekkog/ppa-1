package org.ilintar.study.question;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

import java.util.List;


/**
 * Created by mariusz on 2016-12-29.
 */
public class WriteWithTimeQuestionFactory implements QuestionFactory {
    @Override
    public Question createQuestion(List<String> lines, String questionId) {
    	VBox question = new VBox();
        String questionText = lines.get(0);

        TextArea textArea = new TextArea();
        textArea.setMaxHeight(10);              

        question.getChildren().add(new Label(questionText));
        question.getChildren().add(textArea);

        Button nextButton = createNextButton();
        return new WriteWithTimeQuestion(question, textArea, nextButton, questionId);
    }

    private Button createNextButton() {
        Button nextButton = new Button("Next");
        return nextButton;
    }
}
