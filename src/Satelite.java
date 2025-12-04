package src;

import java.awt.*;
import java.util.Random;

public class Satelite extends Obstaculo {

    private static final int TAMANHO_MINIMO = 30;
    private static final int TAMANHO_MAXIMO = 50;

    private Random random;
    private int tipo; // Para variação visual
    private int anguloRotacao;
    private double velocidadeRotacao;

    /**
     * Construtor para satélite com parâmetros específicos
     */
    public Satelite(int x, int y, int tamanho, int velocidadeX, int velocidadeY, int larguraTela, int alturaTela,
            int velocidadeScroll) {
        super(x, y, tamanho, tamanho, velocidadeX, velocidadeY, "imagens/satelite.png", larguraTela, velocidadeScroll);
        this.random = new Random();
        this.tipo = random.nextInt(3);
        this.anguloRotacao = random.nextInt(360);
        this.velocidadeRotacao = (random.nextDouble() - 0.5) * 3.0; // Rotação entre -1.5 e 1.5 graus por frame
    }

    /**
     * Construtor para satélite totalmente aleatório
     */
    public Satelite(int larguraTela, int alturaTela, int velocidadeScroll) {
        super(0, 0, 0, 0, 0, 0, "imagens/satelite.png", larguraTela, velocidadeScroll);
        this.random = new Random();
        gerarSateliteAleatorio(larguraTela, alturaTela, velocidadeScroll);
    }

    /**
     * Gera um satélite com propriedades aleatórias
     */
    private void gerarSateliteAleatorio(int larguraTela, int alturaTela, int velocidadeScrollAtual) {
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

        // Velocidade horizontal aleatória (-1 a +1) - satélites são mais lentos
        this.velocidadeX = random.nextInt(3) - 1;

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
        this.anguloRotacao = random.nextInt(360);
        this.velocidadeRotacao = (random.nextDouble() - 0.5) * 3.0;

        // Tenta carregar imagem específica baseada no tipo
        carregarImagem("imagens/satelite.png");
    }

    /**
     * Movimento do satélite com rotação
     */
    @Override
    public void mover() {
        if (!ativo)
            return;

        // Movimento nas duas direções (já inclui velocidadeScroll no Y)
        this.x += this.velocidadeX;
        this.y += this.velocidadeY + this.velocidadeScroll;

        // Atualiza rotação
        anguloRotacao += velocidadeRotacao;
        if (anguloRotacao >= 360)
            anguloRotacao -= 360;
        if (anguloRotacao < 0)
            anguloRotacao += 360;

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
     * Desenho do satélite com rotação
     */
    @Override
    public void draw(Graphics g) {
        if (!ativo)
            return;

        Graphics2D g2d = (Graphics2D) g.create();

        // Centraliza a rotação no centro do satélite
        int centroX = this.x + this.width / 2;
        int centroY = this.y + this.height / 2;

        g2d.translate(centroX, centroY);
        g2d.rotate(Math.toRadians(anguloRotacao));
        g2d.translate(-centroX, -centroY);

        if (this.imagem != null) {
            // Desenha a imagem carregada
            g2d.drawImage(this.imagem, this.x, this.y, this.width, this.height, null);
        } else {
            // Fallback gráfico - desenha como cruz
            desenharCruz(g2d);
        }

        // DEBUG: Mostra hitbox do satélite - 
        /* 
        g2d.setColor(Color.YELLOW);
        g2d.drawRect(this.x, this.y, this.width, this.height);
        */
       
        g2d.dispose();
    }

    /**
     * Desenha o satélite como uma cruz (fallback)
     */
    private void desenharCruz(Graphics2D g2d) {
        // Cor base do satélite
        Color[] cores = { Color.CYAN, Color.LIGHT_GRAY, new Color(100, 150, 255) };
        g2d.setColor(cores[tipo % cores.length]);

        // Corpo principal do satélite (círculo central)
        int centroX = this.x + this.width / 2;
        int centroY = this.y + this.height / 2;
        int raio = this.width / 4;

        g2d.fillOval(centroX - raio, centroY - raio, raio * 2, raio * 2);

        // Braços da cruz
        int bracoLargura = this.width / 6;
        int bracoComprimento = this.width / 2;

        // Braço horizontal
        g2d.fillRect(this.x + (this.width - bracoComprimento) / 2,
                centroY - bracoLargura / 2,
                bracoComprimento, bracoLargura);

        // Braço vertical
        g2d.fillRect(centroX - bracoLargura / 2,
                this.y + (this.height - bracoComprimento) / 2,
                bracoLargura, bracoComprimento);

        // Painéis solares (retângulos nas pontas)
        g2d.setColor(new Color(200, 230, 255));
        int painelLargura = this.width / 8;

        // Painéis horizontais
        g2d.fillRect(this.x - painelLargura, centroY - painelLargura / 2,
                painelLargura, painelLargura);
        g2d.fillRect(this.x + this.width, centroY - painelLargura / 2,
                painelLargura, painelLargura);

        // Painéis verticais
        g2d.fillRect(centroX - painelLargura / 2, this.y - painelLargura,
                painelLargura, painelLargura);
        g2d.fillRect(centroX - painelLargura / 2, this.y + this.height,
                painelLargura, painelLargura);

        // Detalhes/antenas
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(2f));

        // Antenas nas pontas
        int antenaComprimento = this.width / 4;
        g2d.drawLine(this.x, centroY, this.x - antenaComprimento, centroY);
        g2d.drawLine(this.x + this.width, centroY, this.x + this.width + antenaComprimento, centroY);
        g2d.drawLine(centroX, this.y, centroX, this.y - antenaComprimento);
        g2d.drawLine(centroX, this.y + this.height, centroX, this.y + this.height + antenaComprimento);

        // Contorno
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(1f));
        g2d.drawOval(this.x, this.y, this.width, this.height);
    }

    /**
     * Getters para propriedades do satélite
     */
    public int getTamanho() {
        return this.width;
    }

    public int getTipo() {
        return this.tipo;
    }

    public int getAnguloRotacao() {
        return this.anguloRotacao;
    }

    public double getVelocidadeRotacao() {
        return this.velocidadeRotacao;
    }

    /**
     * Método estático para criar um satélite aleatório
     */
    public static Satelite criarAleatorio(int larguraTela, int alturaTela, int velocidadeScroll) {
        return new Satelite(larguraTela, alturaTela, velocidadeScroll);
    }
}