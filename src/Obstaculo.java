package src;
import java.awt.*;

public class Obstaculo extends ObjetoJogo {

    // Guarda a largura da tela para saber quando "dar a volta"
    private int larguraTela;

    /**
     * Construtor do Obstáculo
     * @param x Posição inicial X
     * @param y Posição inicial Y
     * @param width Largura
     * @param height Altura
     * @param velocidadeX Velocidade e direção (positiva = direita, negativa = esquerda)
     * @param caminhoImagem Caminho para a imagem (ex: "imagens/asteroide.png")
     * @param larguraTela A largura total da tela do jogo
     */
    public Obstaculo(int x, int y, int width, int height, int velocidadeX, String caminhoImagem, int larguraTela) {
        // Chama o construtor da classe pai (ObjetoJogo)
        super(x, y, width, height);
        
        // Define a velocidade horizontal (velocidadeY será 0 por padrão)
        this.velocidadeX = velocidadeX;
        this.larguraTela = larguraTela;

        // Tenta carregar a imagem (método herdado de ObjetoJogo)
        carregarImagem(caminhoImagem);
    }

    /**
     * Implementação do método abstrato mover()
     */
    @Override
    public void mover() {
        if (!ativo) return; // Se não estiver ativo, não faz nada

        // Atualiza a posição X baseado na velocidade
        this.x += this.velocidadeX;

        // --- Lógica de "Wrap-around" (estilo Frogger) ---

        // 1. Se está se movendo para a DIREITA (velocidade positiva)
        // e a posição X ultrapassou a borda da tela...
        if (this.velocidadeX > 0 && this.x > larguraTela) {
            // ...reinicia a posição para a esquerda, fora da tela.
            this.x = -this.width; // Começa logo antes da borda esquerda
        } 
        
        // 2. Se está se movendo para a ESQUERDA (velocidade negativa)
        // e o objeto desapareceu completamente à esquerda...
        else if (this.velocidadeX < 0 && this.x + this.width < 0) {
            // ...reinicia a posição para a direita, fora da tela.
            this.x = larguraTela; // Começa logo após a borda direita
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