package org.ilintar.study.question.event;

import java.io.IOException;

import org.ilintar.study.MainScreenController;
import org.ilintar.study.question.AnswerHolder;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;

public class RadioWithTimeQuestionAnswerListener implements QuestionAnsweredEventListener {
	
	private final AnswerHolder answerHolder;
    MainScreenController mainScreenController;


    public RadioWithTimeQuestionAnswerListener(AnswerHolder answerHolder, MainScreenController mainScreenController) {
        this.answerHolder = answerHolder;
        this.mainScreenController = mainScreenController;
    }

    
    @Override
    public void handleEvent(ActionEvent event) {
        String[] answerInfo = (String[]) ((Button) event.getSource()).getUserData();
        if (answerInfo != null) {
        	long answerStart = Long.parseLong(answerInfo[1]);
            long answerTimeMillisec = System.currentTimeMillis() - answerStart;
            double answerTimeSec = (((double) answerTimeMillisec) / 1000);
        	String finalAnswer = (answerInfo[0] + " (" + Double.toString(answerTimeSec) + " sec)" +answerInfo[2]);
        	answerHolder.putAnswer(finalAnswer);
            //System.out.println("answerCode w handleEvent Radio = " + answerCode);
            try {
                mainScreenController.getNewQuestion();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            System.out.println("DupaRadio!");

        }
    }
}
