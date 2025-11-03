package src;
import javax.swing.*;
//import java.awt.*;

public class JogoTravessia extends JFrame {

    public JogoTravessia(){
        setTitle("Nome Jogo");
        setSize(800,600); //tamanho tela
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);  //centraliza
        setResizable(false);  //trava o tamanho

        add(new PainelJogo());  //add o painel principal

        setVisible(true);
    }

    public static void main (String[] args){
        //executaa na thread de interface gráfica
        SwingUtilities.invokeLater(() -> new JogoTravessia());
    }
}