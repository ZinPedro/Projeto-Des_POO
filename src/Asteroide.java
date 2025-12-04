package src;

import java.awt.*;
import java.util.Random;

public class Asteroide extends Obstaculo {

    private static final int TAMANHO_MINIMO = 20;
    private static final int TAMANHO_MAXIMO = 60;
    // private static final int VELOCIDADE_MAXIMA = 4;

    private Random random;
    private int tipo; // Para variação visual futura

    /**
     * Construtor para asteroide com parâmetros específicos
     */
    public Asteroide(int x, int y, int tamanho, int velocidadeX, int velocidadeY, int larguraTela, int alturaTela,
            int velocidadeScroll) {
        super(x, y, tamanho, tamanho, velocidadeX, velocidadeY, "imagens/asteroide.png", larguraTela, velocidadeScroll);
        this.random = new Random();
        this.tipo = random.nextInt(3);
    }

    /**
     * Construtor para asteroide totalmente aleatório
     */
    public Asteroide(int larguraTela, int alturaTela, int velocidadeScroll) {
        super(0, 0, 0, 0, 0, 0, "imagens/asteroide.png", larguraTela, velocidadeScroll);
        this.random = new Random();
        gerarAsteroideAleatorio(larguraTela, alturaTela, velocidadeScroll);
    }

    /**
     * Gera um asteroide com propriedades aleatórias
     */
    private void gerarAsteroideAleatorio(int larguraTela, int alturaTela, int velocidadeScrollAtual) {
        // Tamanho aleatório entre mínimo e máximo
        int tamanho = random.nextInt(TAMANHO_MAXIMO - TAMANHO_MINIMO + 1) + TAMANHO_MINIMO;
        this.width = tamanho;
        this.height = tamanho;

        // Escolhe um lado da tela para spawnar (0: topo, 1: direita, 2: baixo, 3:
        // esquerda)
        int ladoSpawn = random.nextInt(4);

        // Velocidade base compensa o scroll para ficar "parado"
        int velYBase = -velocidadeScrollAtual;

        // Movimento vertical aleatório (-1, 0, +1) relativo ao scroll
        int movimentoVertical = random.nextInt(3) - 1;
        this.velocidadeY = velYBase + movimentoVertical;

        // Velocidade horizontal aleatória (-2 a +2)
        this.velocidadeX = random.nextInt(5) - 2;

        // Posiciona de acordo com o lado escolhido
        switch (ladoSpawn) {
            case 0: // Topo
                this.x = random.nextInt(larguraTela);
                this.y = -tamanho;
                break;
            case 1: // Direita
                this.x = larguraTela;
                this.y = random.nextInt(alturaTela);
                break;
            case 2: // Baixo
                this.x = random.nextInt(larguraTela);
                this.y = alturaTela;
                break;
            case 3: // Esquerda
                this.x = -tamanho;
                this.y = random.nextInt(alturaTela);
                break;
        }

        this.tipo = random.nextInt(3);

        // Tenta carregar imagem específica baseada no tipo
        carregarImagem("imagens/asteroide.png");
    }

    /**
     * Movimento do asteroide (sobrescreve o método do Obstaculo)
     */
    @Override
    public void mover() {
        if (!ativo)
            return;

        // Movimento nas duas direções (já inclui velocidadeScroll no Y)
        this.x += this.velocidadeX;
        this.y += this.velocidadeY + this.velocidadeScroll;

        // Lógica de "wrap-around" para todas as direções
        if (this.velocidadeX > 0 && this.x > larguraTela) {
            this.x = -this.width;
        } else if (this.velocidadeX < 0 && this.x + this.width < 0) {
            this.x = larguraTela;
        }

        // Para movimento vertical (usa altura da tela do Obstaculo como referência)
        if (this.y > 600) { // 600 = altura assumida da tela
            this.ativo = false;
        } else if (this.y + this.height < 0) {
            this.y = 600;
        }
    }

    /**
     * Desenho do asteroide como círculo
     */
    @Override
    public void draw(Graphics g) {
        if (!ativo)
            return;

        if (this.imagem != null) {
            // Desenha a imagem carregada
            g.drawImage(this.imagem, this.x, this.y, this.width, this.height, null);
        } else {
            // Fallback gráfico - desenha como círculo
            desenharCirculo(g);
        }
    }

    /**
     * Desenha o asteroide como um círculo com detalhes
     */
    private void desenharCirculo(Graphics g) {
        // Cor base do asteroide
        Color[] cores = { Color.GRAY, Color.DARK_GRAY, Color.LIGHT_GRAY };
        g.setColor(cores[tipo % cores.length]);

        // Corpo principal do asteroide
        g.fillOval(this.x, this.y, this.width, this.height);

        // Detalhes/crateras
        g.setColor(Color.BLACK);
        int numCrateras = 2 + random.nextInt(3);
        for (int i = 0; i < numCrateras; i++) {
            int crateraX = this.x + 5 + random.nextInt(this.width - 10);
            int crateraY = this.y + 5 + random.nextInt(this.height - 10);
            int tamanhoCratera = 3 + random.nextInt(5);
            g.fillOval(crateraX, crateraY, tamanhoCratera, tamanhoCratera);
        }

        // Contorno
        g.setColor(Color.BLACK);
        g.drawOval(this.x, this.y, this.width, this.height);
    }

    /**
     * Getters para propriedades do asteroide
     */
    public int getTamanho() {
        return this.width;
    }

    public int getTipo() {
        return this.tipo;
    }

    public double getVelocidade() {
        return Math.sqrt(velocidadeX * velocidadeX + velocidadeY * velocidadeY);
    }

    /**
     * Método estático para criar um asteroide aleatório
     */
    public static Asteroide criarAleatorio(int larguraTela, int alturaTela, int velocidadeScroll) {
        return new Asteroide(larguraTela, alturaTela, velocidadeScroll);
    }
}