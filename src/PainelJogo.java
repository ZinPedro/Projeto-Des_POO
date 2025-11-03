package src;
import javax.swing.*;
import java.awt.*;

public class PainelJogo extends JPanel{
    
    private Image fundo;

    public PainelJogo(){
        setFocusable(true); //permmite capturar tecllas
        setDoubleBuffered(true); //suaviza a animação

        fundo = new ImageIcon("imagens/fundo-teste.jpg").getImage();  //imagem de fundo 
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Desenha o fundo
        g.drawImage(fundo, 0, 0, getWidth(), getHeight(), this);

        // Texto temporário (pra ver que está rodando)
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Travessia Espacial - Base Pronta!", 250, 300);
    }
}