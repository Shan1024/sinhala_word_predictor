import java.awt.Dimension;
import java.awt.Font;
import java.awt.TextArea;
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

        this.add(textArea);

        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    public static void main(String[] args) {
        WordPredictor wordPredictor = new WordPredictor();
    }
}
