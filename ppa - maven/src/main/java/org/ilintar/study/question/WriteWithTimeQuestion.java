package org.ilintar.study.question;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import org.ilintar.study.question.event.QuestionAnsweredEventListener;

public class WriteWithTimeQuestion implements Question{

    private final String questionId;
    private VBox vBox;
    private TextArea textArea;
    private Button nextButton;
    private long start;

    public WriteWithTimeQuestion(VBox vBox, TextArea textArea, Button nextButton, String questionId) {
        this.vBox = vBox;
        this.textArea = textArea;
        this.nextButton = nextButton;
        this.start = System.currentTimeMillis();
        this.questionId = questionId;
        passWriteResult(textArea, nextButton);
    }

    private void passWriteResult(final TextArea textArea, final Button nextButton) {
    	String[] answerAndStartTime = new String [3];
    	answerAndStartTime[1] = Long.toString(this.start);
    	
        textArea.textProperty().addListener(new ChangeListener<String>() {              
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) { 
            	answerAndStartTime[0] = textArea.getText();
            	answerAndStartTime[2] = " id:"+questionId;
                //System.out.println(text);
                nextButton.setUserData(answerAndStartTime);
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
    public String getId() {
        return vBox.getId();
    }

    @Override
    public void addQuestionAnsweredListener(QuestionAnsweredEventListener listener) {
        nextButton.setOnAction(listener::handleEvent);
    }

    @Override
    public void removeQuestionAnsweredListener(QuestionAnsweredEventListener listener) {

    }
	
}
