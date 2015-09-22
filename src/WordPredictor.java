import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.awt.BorderLayout;
import javafx.scene.input.KeyCode;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Locale;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JWindow;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;

public class WordPredictor {

    private static final Font SINHALA_FONT = new Font("Iskoola Pota", 0, 18);

    public WordPredictor() {

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("සිංහල වචන අනුමාන කරණය");
        frame.setPreferredSize(new Dimension(600, 400));

        JTextArea textArea = new JTextArea();
        textArea.setWrapStyleWord(true);
        textArea.setFont(SINHALA_FONT);

        //create words for dictionary could also use null as parameter for AutoSuggestor(..,..,null,..,..,..,..) and than call AutoSuggestor#setDictionary after AutoSuggestr insatnce has been created
        ArrayList<String> words = new ArrayList<>();
//        words.add("සිංහල");
//        words.add("වචන");
//        words.add("මම");
//        words.add("අද");
//        words.add("ගෙදර");
//        words.add("යනවා");
//        words.add("අපි");
//        words.add("බස්");
//        words.add("කෝච්චියේ");
//
//        words.add("hello");
//        words.add("heritage");
//        words.add("happiness");
//        words.add("goodbye");
//        words.add("cruel");
//        words.add("car");
//        words.add("war");
//        words.add("will");
//        words.add("world");
//        words.add("wall");

        AutoSuggestor autoSuggestor = new AutoSuggestor(textArea, frame, words, Color.GRAY.brighter(), Color.BLUE, Color.RED, 0.75f) {
            @Override
            boolean wordTyped(String typedWord) {
                System.out.println(typedWord);
                return super.wordTyped(typedWord);//checks for a match in dictionary and returns true or false if found or not
            }

        };

//        JPanel panel = new JPanel();
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(textArea);

//        panel.add(scrollPane);
        frame.getContentPane().setLayout(new BorderLayout());

        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);

        frame.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e); //To change body of generated methods, choose Tools | Templates.
                System.out.println("Window is closing");
                autoSuggestor.saveDictionary();
                autoSuggestor.savePredictor();
            }

        });

        //Setting locale to Sinhala
        Locale loc = new Locale("si", "LK");
        textArea.setLocale(loc);
        textArea.getInputContext().selectInputMethod(loc);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void main(String[] args) {

        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    System.out.println("LnF applied.");
                    break;
                }
            }
        } catch (Exception ex) {
            System.out.println("Exception: " + ex);
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new WordPredictor();
            }
        });
    }
}

class AutoSuggestor {

    private final JTextComponent textComp;
    private final Window container;
    private JPanel suggestionsPanel;
    private JWindow autoSuggestionPopUpWindow;
    private String typedWord;
    private ArrayList<String> dictionary = new ArrayList<>();
    private Predictor predictor;
    private int currentIndexOfSpace, tW, tH;

    private DocumentListener documentListener = new DocumentListener() {
        @Override
        public void insertUpdate(DocumentEvent de) {
            String typed = textComp.getText().substring(de.getOffset());
//            System.out.println("Typed: " + typed);

            if (typed.equals(" ") || typed.equals(".")) {

                updateDictionary();
                if (typed.equals(".")) {
                    updatePredictor();
                }
//                System.out.println("Space or . typed");
                checkAndPredict();
            } else {
                checkForAndShowSuggestions();
            }

        }

        @Override
        public void removeUpdate(DocumentEvent de) {
            checkForAndShowSuggestions();
        }

        @Override
        public void changedUpdate(DocumentEvent de) {
            checkForAndShowSuggestions();
        }
    };

    private void updateDictionary() {

        String text = textComp.getText();

        text = text.replaceAll("[!?(),]", " ").replaceAll("\\s+", " ");

        text = text.replaceAll("\\.", " ").trim();
//        System.out.println("Text: "+text);
        String word = text;

        int lastSpaceLocation = text.lastIndexOf(" ");

        if (lastSpaceLocation > -1) {
            word = text.substring(lastSpaceLocation);
        }

//        int lastPeriodLocation = text.lastIndexOf(".");
//        int lastSpaceLocation = text.lastIndexOf(" ");
//        if (lastPeriodLocation > -1) {
//
//            word = text.substring(lastPeriodLocation);
//
//            lastSpaceLocation = word.lastIndexOf(" ");
//
//            if (lastSpaceLocation > -1) {
//                word = word.substring(lastSpaceLocation);
//            } else {
//                if (text.equals(".")) {
//                    word =
//                }
//            }
//
//
//        } else {
//            if (lastSpaceLocation > -1) {
//                word = text.substring(lastSpaceLocation);
//            }
//        }
//        System.out.println("Checking Last Word..... ");
        System.out.println("Adding last word to the dictionary: " + word.trim());
        addToDictionary(word.trim());
        System.out.println("Dictionary: " + dictionary);

    }

    private void updatePredictor() {
        String text = textComp.getText().trim();

        String sentence = "";

        System.out.println("Text: " + text);
        text = text.substring(0, text.length() - 1);
        System.out.println("Text2: " + text);

        if (text.length() > 0) {

            int lastPeriodLocation = text.trim().lastIndexOf(".");

            if (lastPeriodLocation > -1) {

                sentence = text.substring(lastPeriodLocation + 1);
                System.out.println("Sentence: " + text.substring(lastPeriodLocation + 1));

            } else {
                System.out.println("Sentence: " + text);
                sentence = text;
            }
            predictor.showMap();
            predictor.addSentence(sentence);

            predictor.showMap();

        }

    }

    private final Color suggestionsTextColor;
    private final Color suggestionFocusedColor;

    public AutoSuggestor(JTextComponent textComp, Window mainWindow, ArrayList<String> words, Color popUpBackground, Color textColor, Color suggestionFocusedColor, float opacity) {

        this.textComp = textComp;
        this.suggestionsTextColor = textColor;
        this.container = mainWindow;
        this.suggestionFocusedColor = suggestionFocusedColor;
        this.textComp.getDocument().addDocumentListener(documentListener);

//        textComp.addKeyListener(new KeyListener() {
//            @Override
//            public void keyTyped(KeyEvent keyEvent) {
//                System.out.println("key typed");
////                if(keyEvent.getKeyCode()== KeyEvent.VK_SPACE){
//
////                }
//            }
//
//            @Override
//            public void keyPressed(KeyEvent keyEvent) {
//                System.out.println("key pressed");
//                checkForAndShowSuggestions();
//            }
//
//            @Override
//            public void keyReleased(KeyEvent keyEvent) {
//                System.out.println("key released");
//            }
//        });
        predictor = new Predictor();
        predictor.loadPredictor();

        if (words.isEmpty()) {
            loadDictionary();
        } else {
            setDictionary(words);
        }

        typedWord = "";
        currentIndexOfSpace = 0;
        tW = 0;
        tH = 0;

        autoSuggestionPopUpWindow = new JWindow(mainWindow);
        autoSuggestionPopUpWindow.setOpacity(opacity);

        suggestionsPanel = new JPanel();
        suggestionsPanel.setLayout(new GridLayout(0, 1));
        suggestionsPanel.setBackground(popUpBackground);

        addKeyBindingToRequestFocusInPopUpWindow();
    }

    private void loadDictionary() {
        System.out.println("Dictionary is loading");

        Gson gson = new GsonBuilder().enableComplexMapKeySerialization()
                .setPrettyPrinting().create();

        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader("dictionary.json"));

            Type typeOfHashMap = new TypeToken<ArrayList<String>>() {
            }.getType();
            //convert the json string back to object
            dictionary = gson.fromJson(br, typeOfHashMap);

            String json = gson.toJson(dictionary);

            System.out.println("Dictionary:\n" + json);

        } catch (FileNotFoundException ex) {
            System.out.println("Ex: " + ex);
        }
    }

    public void saveDictionary() {
        System.out.println("Dictionary is saving");

        Gson gson = new GsonBuilder().enableComplexMapKeySerialization()
                .setPrettyPrinting().create();

        String json = gson.toJson(dictionary);

        System.out.println("Dictionary: " + json);
//        FileWriter writer;
        try {
//            writer = new FileWriter("dictionary.json");
//            writer.write(json);
//            writer.close();

            Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("dictionary.json"), "UTF-8"));
            out.write(json);
            out.close();

        } catch (IOException ex) {
            System.out.println("Ex: " + ex);
        }
    }

    public void savePredictor() {

        predictor.savePredictor();
    }

    private void addKeyBindingToRequestFocusInPopUpWindow() {
        textComp.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, true), "Down released");
        textComp.getActionMap().put("Down released", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent ae) {//focuses the first label on popwindow
                for (int i = 0; i < suggestionsPanel.getComponentCount(); i++) {
                    if (suggestionsPanel.getComponent(i) instanceof SuggestionLabel) {
                        ((SuggestionLabel) suggestionsPanel.getComponent(i)).setFocused(true);
                        autoSuggestionPopUpWindow.toFront();
                        autoSuggestionPopUpWindow.requestFocusInWindow();
                        suggestionsPanel.requestFocusInWindow();
                        suggestionsPanel.getComponent(i).requestFocusInWindow();
                        break;
                    }
                }
            }
        });
        suggestionsPanel.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, true), "Down released");
        suggestionsPanel.getActionMap().put("Down released", new AbstractAction() {
            int lastFocusableIndex = 0;

            @Override
            public void actionPerformed(ActionEvent ae) {//allows scrolling of labels in pop window (I know very hacky for now :))

                ArrayList<SuggestionLabel> sls = getAddedSuggestionLabels();
                int max = sls.size();

                if (max > 1) {//more than 1 suggestion
                    for (int i = 0; i < max; i++) {
                        SuggestionLabel sl = sls.get(i);
                        if (sl.isFocused()) {
                            if (lastFocusableIndex == max - 1) {
                                lastFocusableIndex = 0;
                                sl.setFocused(false);
                                autoSuggestionPopUpWindow.setVisible(false);
                                setFocusToTextField();

//                                checkForAndShowSuggestions();//fire method as if document listener change occured and fired it
                                int length = textComp.getText().length() - 1;

                                if (textComp.getText().charAt(length) == ' ' || textComp.getText().charAt(length) == '.') {
                                    checkAndPredict();
                                } else {
                                    checkForAndShowSuggestions();//fire method as if document listener change occured and fired it
                                }

                            } else {
                                sl.setFocused(false);
                                lastFocusableIndex = i;
                            }
                        } else if (lastFocusableIndex <= i) {
                            if (i < max) {
                                sl.setFocused(true);
                                autoSuggestionPopUpWindow.toFront();
                                autoSuggestionPopUpWindow.requestFocusInWindow();
                                suggestionsPanel.requestFocusInWindow();
                                suggestionsPanel.getComponent(i).requestFocusInWindow();
                                lastFocusableIndex = i;
                                break;
                            }
                        }
                    }
                } else {//only a single suggestion was given
                    autoSuggestionPopUpWindow.setVisible(false);
                    setFocusToTextField();

                    int length = textComp.getText().length() - 1;

                    if (textComp.getText().charAt(length) == ' ' || textComp.getText().charAt(length) == '.') {
                        checkAndPredict();
                    } else {
                        checkForAndShowSuggestions();//fire method as if document listener change occured and fired it
                    }
                }
            }
        });
    }

    private void setFocusToTextField() {
        container.toFront();
        container.requestFocusInWindow();
        textComp.requestFocusInWindow();
    }

    public ArrayList<SuggestionLabel> getAddedSuggestionLabels() {
        ArrayList<SuggestionLabel> sls = new ArrayList<>();
        for (int i = 0; i < suggestionsPanel.getComponentCount(); i++) {
            if (suggestionsPanel.getComponent(i) instanceof SuggestionLabel) {
                SuggestionLabel sl = (SuggestionLabel) suggestionsPanel.getComponent(i);
                sls.add(sl);
            }
        }
        return sls;
    }

    private void checkForAndShowSuggestions() {
        typedWord = getCurrentlyTypedWord();

        suggestionsPanel.removeAll();//remove previos words/jlabels that were added

        //used to calcualte size of JWindow as new Jlabels are added
        tW = 0;
        tH = 0;

        boolean added = wordTyped(typedWord);

        if (!added) {
            if (autoSuggestionPopUpWindow.isVisible()) {
                autoSuggestionPopUpWindow.setVisible(false);
            }
        } else {
            showPopUpWindow();
            setFocusToTextField();
        }
    }

    private void checkAndPredict() {

        String key = getCurrentKey();

        System.out.println("Current Key: " + key);

        suggestionsPanel.removeAll();//remove previos words/jlabels that were added

        //used to calcualte size of JWindow as new Jlabels are added
        tW = 0;
        tH = 0;

//        boolean added = wordTyped(typedWord);
        LinkedList<Entry> entries = predictor.predict(key);

        System.out.println("Entries: " + entries);

        if (entries == null) {
            if (autoSuggestionPopUpWindow.isVisible()) {
                autoSuggestionPopUpWindow.setVisible(false);
            }
        } else {

            for (Entry entry : entries) {
                addWordToSuggestions(entry.getWord());
            }

            showPopUpWindow();
            setFocusToTextField();
        }

    }

    protected void addWordToSuggestions(String word) {
        SuggestionLabel suggestionLabel = new SuggestionLabel(word, suggestionFocusedColor, suggestionsTextColor, this);

        calculatePopUpWindowSize(suggestionLabel);

        suggestionsPanel.add(suggestionLabel);
    }

    public String getCurrentKey() {
        String text = textComp.getText().replaceAll("(\\r|\\n)", " ");

        int caretPosition = textComp.getCaretPosition();
//        System.out.println("XX caretPosition: " + caretPosition);

        int lastPeriodPosition = text.lastIndexOf(".");
//        System.out.println("XX lastPeriodPosition: " + lastPeriodPosition);

        if (lastPeriodPosition != -1) {

            if (caretPosition > lastPeriodPosition) {
//                System.out.println("XX Selected Text: " + text.substring(lastPeriodPosition, caretPosition));

                String[] temp = text.substring(lastPeriodPosition).split(" ");

                int size = temp.length;

                if (size >= 2) {
                    String key = temp[size - 2] + " " + temp[size - 1];
//                    System.out.println("Key: " + key);
//                    String predicted = predictor.predict(key);

//                    System.out.println("Predicted: " + predicted);
                    return key;
                }
            } else {
//                System.out.println("XX Selected Text: " + text.substring(lastPeriodPosition, caretPosition));

            }

        } else {
            String[] temp = text.split(" ");

            int size = temp.length;

            if (size >= 2) {
                String key = temp[size - 2] + " " + temp[size - 1];
//                System.out.println("Key: " + key);
//                    String predicted = predictor.predict(key);

//                    System.out.println("Predicted: " + predicted);
                return key;
            }

        }
        return "";
    }

    public String getCurrentlyTypedWord() {//get newest word after last white spaceif any or the first word if no white spaces
        String text = textComp.getText();

//        System.out.println("-----------------------------------------");
//        System.out.println("Text: " + text);
//
//        int caretPosition = textComp.getCaretPosition();
//        System.out.println("XX caretPosition: " + caretPosition);
//
//
//        int lastPeriodPosition = text.lastIndexOf(".");
//        System.out.println("XX lastPeriodPosition: " + lastPeriodPosition);
//        if (lastPeriodPosition != -1) {
//
//            if (caretPosition > lastPeriodPosition) {
////                System.out.println("XX Selected Text: " + text.substring(lastPeriodPosition, caretPosition));
//
//                String[] temp = text.substring(lastPeriodPosition).split(" ");
//
//                int size = temp.length;
//
//                if (size >= 2) {
//                    String key = temp[size - 2] + " " + temp[size - 1];
//                    System.out.println("Key: " + key);
//                    String predicted = predictor.predict(key);
//
//                    System.out.println("Predicted: " + predicted);
//                }
//            } else {
////                System.out.println("XX Selected Text: " + text.substring(lastPeriodPosition, caretPosition));
//
//            }
//
//
//        }
        String wordBeingTyped = "";
        text = text.replaceAll("(\\r|\\n)", " ");//-----------------------XXXXXXXXXXXXXXXXXXXXXXXXXXXXXX REGEX

//        System.out.println("Text2: " + text);
//        System.out.println("++++++++++++++++++++++++++++++++++++++++++++");
        if (text.contains(" ")) {
            int tmp = text.lastIndexOf(" ");
            if (tmp >= currentIndexOfSpace) {
                currentIndexOfSpace = tmp;
                wordBeingTyped = text.substring(text.lastIndexOf(" "));
            }
        } else if (text.contains(".")) {
            int tmp = text.lastIndexOf(".");
            if (tmp >= currentIndexOfSpace) {
                currentIndexOfSpace = tmp;
                wordBeingTyped = text.substring(text.lastIndexOf("."));
            }
        } else {
            wordBeingTyped = text;
        }

//        System.out.println("wordBeingTyped.trim(): " + wordBeingTyped.trim());
        return wordBeingTyped.trim();
    }

    private void calculatePopUpWindowSize(JLabel label) {
        //so we can size the JWindow correctly
        if (tW < label.getPreferredSize().width) {
            tW = label.getPreferredSize().width;
        }
        tH += label.getPreferredSize().height;
    }

    private void showPopUpWindow() {
        autoSuggestionPopUpWindow.getContentPane().add(suggestionsPanel);
//        autoSuggestionPopUpWindow.setMinimumSize(new Dimension(textComp.getWidth(), 30));
        autoSuggestionPopUpWindow.setSize(150, tH);
        autoSuggestionPopUpWindow.setVisible(true);

        int windowX = 0;
        int windowY = 0;

        if (textComp instanceof JTextField) {//calculate x and y for JWindow at bottom of JTextField
            windowX = container.getX() + textComp.getX() + 5;
            if (suggestionsPanel.getHeight() > autoSuggestionPopUpWindow.getMinimumSize().height) {
                windowY = container.getY() + textComp.getY() + textComp.getHeight() + autoSuggestionPopUpWindow.getMinimumSize().height;
            } else {
                windowY = container.getY() + textComp.getY() + textComp.getHeight() + autoSuggestionPopUpWindow.getHeight();
            }
        } else {//calculate x and y for JWindow on any JTextComponent using the carets position
            Rectangle rect = null;
            try {
//                textComp.getCaret().getMagicCaretPosition()
                if (textComp.hasFocus()) {
                    rect = textComp.getUI().modelToView(textComp, textComp.getCaret().getDot());//get carets position
                } else {
                    rect = new Rectangle(100, 100, 10, 10);
                }
            } catch (Exception ex) {
//                ex.printStackTrace();
            }

            try {
                windowX = (int) (rect.getX() + 15) + this.getContainer().getLocation().x;
                windowY = (int) (rect.getY() + (rect.getHeight() * 3)) + this.getContainer().getLocation().y;
//                System.out.println("x: " + windowX);
//                System.out.println("y: " + windowY);
            } catch (Exception e) {
            }

        }

        try {
            //show the pop up
            autoSuggestionPopUpWindow.setLocation(windowX, windowY);
//        autoSuggestionPopUpWindow.setPreferredSize(new Dimension(50, 30));
            autoSuggestionPopUpWindow.revalidate();
            autoSuggestionPopUpWindow.repaint();
        } catch (Exception e) {
            System.out.println("Exception: " + e);
        }

    }

    public void setDictionary(ArrayList<String> words) {
        dictionary.clear();
        if (words == null) {
            return;//so we can call constructor with null value for dictionary without exception thrown
        }
        for (String word : words) {
            dictionary.add(word);
        }
    }

    public JWindow getAutoSuggestionPopUpWindow() {
        return autoSuggestionPopUpWindow;
    }

    public Window getContainer() {
        return container;
    }

    public JTextComponent getTextField() {
        return textComp;
    }

    public void addToDictionary(String word) {

        if (!dictionary.contains(word) && word.length() > 1) {
            dictionary.add(word);
        }
    }

    boolean wordTyped(String typedWord) {

        if (typedWord.isEmpty()) {
            return false;
        }
        //System.out.println("Typed word: " + typedWord);

        boolean suggestionAdded = false;

        for (String word : dictionary) {//get words in the dictionary which we added
            boolean fullymatches = true;
            for (int i = 0; i < typedWord.length(); i++) {//each string in the word
                if (!typedWord.toLowerCase().startsWith(String.valueOf(word.toLowerCase().charAt(i)), i)) {//check for match
                    fullymatches = false;
                    break;
                }
            }
            if (fullymatches) {
                addWordToSuggestions(word);
                suggestionAdded = true;
            }
        }

        System.out.println("suggestionAdded: " + suggestionAdded);

        return suggestionAdded;
    }
}

class SuggestionLabel extends JLabel {

    private boolean focused = false;
    private final JWindow autoSuggestionsPopUpWindow;
    private final JTextComponent textComponent;
    private final AutoSuggestor autoSuggestor;
    private Color suggestionsTextColor, suggestionBorderColor;

    private static final Font SINHALA_FONT = new Font("Iskoola Pota", 0, 18);

    public SuggestionLabel(String string, final Color borderColor, Color suggestionsTextColor, AutoSuggestor autoSuggestor) {
        super(string);

        this.suggestionsTextColor = suggestionsTextColor;
        this.autoSuggestor = autoSuggestor;
        this.textComponent = autoSuggestor.getTextField();
        this.suggestionBorderColor = borderColor;
        this.autoSuggestionsPopUpWindow = autoSuggestor.getAutoSuggestionPopUpWindow();
        this.setFont(SINHALA_FONT);
        initComponent();
    }

    private void initComponent() {
        setFocusable(true);
        setForeground(suggestionsTextColor);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me) {
                super.mouseClicked(me);

                replaceWithSuggestedText();

                autoSuggestionsPopUpWindow.setVisible(false);
            }
        });

        getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, true), "Enter released");
        getActionMap().put("Enter released", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                replaceWithSuggestedText();
                autoSuggestionsPopUpWindow.setVisible(false);
            }
        });

        this.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e); //To change body of generated methods, choose Tools | Templates.
                setBorder(new LineBorder(suggestionBorderColor));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setBorder(null);
            }

        });
    }

    public void setFocused(boolean focused) {
        if (focused) {
            setBorder(new LineBorder(suggestionBorderColor));
        } else {
            setBorder(null);
        }
        repaint();
        this.focused = focused;
    }

    public boolean isFocused() {
        return focused;
    }

    private void replaceWithSuggestedText() {
        String suggestedWord = getText();
        String text = textComponent.getText();
        String typedWord = autoSuggestor.getCurrentlyTypedWord();
        String t = text.substring(0, text.lastIndexOf(typedWord));
        String tmp = t + text.substring(text.lastIndexOf(typedWord)).replace(typedWord, suggestedWord);
        textComponent.setText(tmp + " ");
    }
}
