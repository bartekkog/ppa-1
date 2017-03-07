package org.ilintar.study;

import com.sun.org.apache.xerces.internal.xs.StringList;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import org.ilintar.study.question.*;
import org.ilintar.study.question.event.RadioQuestionAnswerListener;
import org.ilintar.study.question.event.RadioWithTimeQuestionAnswerListener;
import org.ilintar.study.question.event.WriteQuestionAnswerListener;
import org.ilintar.study.question.event.WriteWithTimeQuestionAnswerListener;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class MainScreenController {

    protected static Map<String, QuestionFactory> factoryMap;

    private AnswerHolder answerHolder = new SimpleAnswerHolder();
    private BufferedReader openedFile;
    private Node currentQuestionComponent;

    private int resultsHandlerCount = 0;

    static {
        factoryMap = new HashMap<>();
        factoryMap.put("radio", new RadioQuestionFactory());
        factoryMap.put("radiowithtime", new RadioWithTimeQuestionFactory());
        factoryMap.put("write", new WriteQuestionFactory());
        factoryMap.put("writewithtime", new WriteWithTimeQuestionFactory());
        // / słowo klucz wskazujace której konkretnie fabryki chcemy użyć
    }

    @FXML
    AnchorPane mainStudy;

    @FXML
    public void startStudy() throws IOException {
        mainStudy.getChildren().clear();
        openedFile = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("StudyDetails.sqf")));
        currentQuestionComponent = readNextQuestionFromFile();
        mainStudy.getChildren().add(currentQuestionComponent);
    }

    private Node readNextQuestionFromFile() throws IOException {
        String questionStartLine = getQuestionStartLine();
        if (questionStartLine != null) {
            String questionType = getQuestionType(questionStartLine);
            String questionId = getQuestionId(questionStartLine);
            List<String> questionLines = readQuestionLines();
            return createQuestion(questionLines, questionType, questionId);
        }
        else {
            //finalScreen();
            saveToFile(); //AB: jak już nie ma następnego pytania -> zapisujemy odpowiedzi do pliku
            return null;
        }
    }


    private void saveToFile() throws IOException {
        File fout = new File("Answers.txt"); //AB: plik tekstowy z odpowiedziami zapisuje się w katalogu projektu
        FileOutputStream fos = new FileOutputStream(fout);

        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
        Collection<Objects> answers = answerHolder.getAnswers();
        for (Object o: answers) {
            bw.write((String) o);
            bw.newLine();
        }
        bw.close();

    }

    private String getQuestionStartLine() throws IOException {
        String currentLine;
        currentLine = openedFile.readLine();
        if (currentLine == null)
            return null;
        if (!currentLine.startsWith("StartQuestion"))
            throw new IllegalArgumentException("Question does not start properly");
        return currentLine;
    }

    private String getQuestionId(String startLine){
        String questionId = null;
        String[] split = startLine.split(" ");
        if (split.length > 1) {
            String[] split2 = split[2].split("=");
            if (split2.length > 1){
                questionId = split2[1];
            }
        }
        return questionId;
    }

    private String getQuestionType(String startLine) {
        String questionType = null;
        String[] split = startLine.split(" ");
        if (split.length > 1) {
            String[] split2 = split[1].split("=");
            if (split2.length > 1) {
                questionType = split2[1];
            }
        }
        if (factoryMap.containsKey(questionType))
            return questionType;
        else
            throw new IllegalArgumentException("InvalidQuestionType");
    }

    private List<String> readQuestionLines() throws IOException {
        List<String> questionLines = new ArrayList<>();
        String currentLine;
        while ((currentLine = openedFile.readLine()) != null) {
            if (currentLine.startsWith("EndQuestion"))
                return questionLines;
            else
                questionLines.add(currentLine.trim());
        }
        throw new IllegalArgumentException("No end-question mark");
    }

    private Node createQuestion(List<String> questionLines, String questionType, String questionId) {
        Question q = factoryMap.get(questionType).createQuestion(questionLines, questionId);
        if(questionType.equals("radio")) {
            q.addQuestionAnsweredListener(new RadioQuestionAnswerListener(answerHolder, this));
        }
        else if (questionType.equals("write")) {
            q.addQuestionAnsweredListener(new WriteQuestionAnswerListener(answerHolder, this));
        }
        else if (questionType.equals("radiowithtime")) {
        	q.addQuestionAnsweredListener(new RadioWithTimeQuestionAnswerListener(answerHolder, this));
        }
        else if (questionType.equals("writewithtime")) {
        	q.addQuestionAnsweredListener(new WriteWithTimeQuestionAnswerListener(answerHolder, this));
        }
        //TODO: dodac nastepne typy pytan, wygenerowalem juz klasy
        return q.getRenderedQuestion();
    }

    public void getNewQuestion() throws IOException {
        mainStudy.getChildren().remove(currentQuestionComponent);
        currentQuestionComponent = readNextQuestionFromFile();
        if (currentQuestionComponent == null) {
            finalScreen();
            return;
        }
        mainStudy.getChildren().add(currentQuestionComponent);
    }

    private void finalScreen(){
        mainStudy.getChildren().add(new Text(50 , 50, "Thank you for participating in this study.\n"));
        Button exitButton = new Button("Exit");
        Button resultsButton = new Button("See results");
        //int resultsHandlerCount = 0;

        exitButton.relocate(mainStudy.getWidth()/2, mainStudy.getHeight()/2);
        resultsButton.relocate(mainStudy.getWidth()/4, mainStudy.getHeight()/2);

        //exitButton.setOnAction(e -> System.out.println());
        exitButton.setOnAction(new EventHandler<javafx.event.ActionEvent>() {   //zamienne z lambdą
            @Override
            public void handle(javafx.event.ActionEvent event) {
                Platform.exit();
            }
        });
        resultsButton.setOnAction(new EventHandler<javafx.event.ActionEvent>() {
            @Override
            public void handle(javafx.event.ActionEvent event) {
                if (resultsHandlerCount < 3) {
                    try {
                        resultsScreen();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    resultsHandlerCount++;
                }
                else if (resultsHandlerCount >= 3) {
                    resultsButton.setText("Them results, in console they are");     //this is supposed to be funny
                    exitButton.setVisible(false);
                }
            }
        });

        try {
            mainStudy.getChildren().add(exitButton);
            mainStudy.getChildren().add(resultsButton);
        } catch(Exception e) {
            e.printStackTrace();
        }

    }


    private void resultsScreen() throws IOException { //Kliknięcie przycisku "See results" otwiera notatnik z zapisanymi wynikami
        File file = new File("Answers.txt");

        if(!Desktop.isDesktopSupported()){
            System.out.println("Desktop is not supported");
            return;
        }

        Desktop desktop = Desktop.getDesktop();
        if(file.exists()) desktop.open(file);
    }


    public void generateStudy(ActionEvent actionEvent) { //TODO: interfejs do generowania .sqf dla naszego programu

        mainStudy.getChildren().clear();
        mainStudy.getChildren().add(new Text(50,50,"It will be done"));



    }
        //TODO: moznaby tez zrobic przycisk "Go back" ktory bylby odziedziczone przez niektóre okna (ale to trzeba zrobic w mainie
        //TODO: uzywajac stage.setScene()?

}

	

