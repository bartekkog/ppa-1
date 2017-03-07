package org.ilintar.study.question;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import org.ilintar.study.question.event.QuestionAnsweredEventListener;

public class RadioWithTimeQuestion implements Question{

    private final String questionId;
    private VBox vBox;
    private ToggleGroup group;
    private Button nextButton;
    private long start;

    public RadioWithTimeQuestion(VBox vBox, ToggleGroup group, Button nextButton, String questionId) {
        this.vBox = vBox;
        this.group = group;
        this.nextButton = nextButton;
        this.start = System.currentTimeMillis();
        this.questionId = questionId;
        passToggleResult(group, nextButton);
    }

    private void passToggleResult(final ToggleGroup group, final Button nextButton) {
    	String[] answerCodeAndStartTime = new String [3];
    	answerCodeAndStartTime[1] = Long.toString(this.start);
    	
        group.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                //System.out.println(group.getSelectedToggle());
            	answerCodeAndStartTime[0] = group.getSelectedToggle().getUserData().toString();
            	answerCodeAndStartTime[2] = " id:"+questionId;
                nextButton.setUserData(answerCodeAndStartTime);
            }
        });
    }
    
    public long getStart() {
    	return start;
    }

    @Override
    public Node getRenderedQuestion() {
        vBox.getChildren().add(nextButton);
        return vBox;
    }

    @Override
    public String getId() {return vBox.getId();
    }

    @Override
    public void addQuestionAnsweredListener(QuestionAnsweredEventListener listener) {
        nextButton.setOnAction(listener::handleEvent);
    }

    @Override
    public void removeQuestionAnsweredListener(QuestionAnsweredEventListener listener) {

    }
	
}
