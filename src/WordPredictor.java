import java.awt.Dimension;
import java.awt.Font;
import java.awt.TextArea;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Locale;
import javax.swing.JFrame;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Shan
 */
public class WordPredictor extends JFrame {

    private static final Font SINHALA_FONT = new java.awt.Font("Iskoola Pota", 0, 11);

    private TextArea textArea;

    public WordPredictor() {

        this.setTitle("සිංහල වචන අනුමාන කරණය");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        textArea = new TextArea();
        textArea.setPreferredSize(new Dimension(600, 400));
        textArea.setFont(SINHALA_FONT);

        textArea.addKeyListener(new KeyAdapter() {

            @Override
            public void keyTyped(KeyEvent e) {

                super.keyTyped(e); //To change body of generated methods, choose Tools | Templates.
                System.out.println("OK: " + toUnicode(e.getKeyChar()));
                if (e.getKeyChar() == KeyEvent.VK_SPACE) {
                    System.out.println("Space detected");
                } else if (e.getKeyChar() == KeyEvent.VK_PERIOD) {
                    System.out.println("Period Detected");
                }
            }

        });

        this.add(textArea);

        Locale loc = new Locale("si", "LK");//Language, Country
        textArea.setLocale(loc);
        textArea.getInputContext().selectInputMethod(loc);

        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    private static String toUnicode(char ch) {
        return String.format("\\u%04x", (int) ch);
    }

    public static void main(String[] args) {
        WordPredictor wordPredictor = new WordPredictor();
    }

}
