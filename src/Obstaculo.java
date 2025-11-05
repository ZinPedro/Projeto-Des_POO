package src;
import java.awt.*;

public class Obstaculo extends ObjetoJogo {

    // Guarda a largura da tela para saber quando "dar a volta"
    public int larguraTela;
    
    protected int velocidadeScroll;

    public Obstaculo(int x, int y, int width, int height, int velocidadeX, int velocidadeY, String caminhoImagem, int larguraTela, int velocidadeScroll) {
        super(x, y, width, height);
        this.velocidadeX = velocidadeX;
        this.velocidadeY = velocidadeY;
        this.larguraTela = larguraTela;
        this.velocidadeScroll = velocidadeScroll;
        carregarImagem(caminhoImagem);
    }

    public void setVelocidadeScroll(int velocidadeScroll) {
        this.velocidadeScroll = velocidadeScroll;
    }

    /**
     * Implementação do método abstrato mover()
     */
    @Override
    public void mover() {
        if (!ativo) return; // Se não estiver ativo, não faz nada

        this.x += this.velocidadeX;
        this.y += this.velocidadeY + this.velocidadeScroll;

        // --- Lógica de "Wrap-around" (estilo Frogger) ---
        if (this.x > larguraTela) {
            this.x = -this.width;
        } else if (this.x + this.width < 0) {
            this.x = larguraTela;
        }
        
        if (this.y > 600) {
            this.ativo = false;
        }
    }

    /**
     * Implementação do método abstrato draw()
     */
    @Override
    public void draw(Graphics g) {
        if (!ativo) return; // Se não estiver ativo, não desenha

        if (this.imagem != null) {
            // Desenha a imagem carregada
            g.drawImage(this.imagem, this.x, this.y, this.width, this.height, null);
        } else {
            // Se a imagem falhar ao carregar, desenha um "fallback"
            g.setColor(Color.GRAY);
            g.fillRect(this.x, this.y, this.width, this.height);
            g.setColor(Color.RED);
            g.drawRect(this.x, this.y, this.width, this.height);
            g.drawString("X", x + width/2 - 4, y + height/2 + 5);
        }
    }
}