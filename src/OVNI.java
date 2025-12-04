package src;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class OVNI extends Obstaculo {

    private static final int TAMANHO_MINIMO = 40;
    private static final int TAMANHO_MAXIMO = 60;
    private static final int MAX_TIROS_ATIVOS = 3;

    private Random random;
    private int tipo;
    private List<Tiro> tiros;
    private long ultimoTiro;
    private int intervaloEntreTiros;
    private boolean movendoParaDireita;
    private int alturaVoo;

    // Classe interna para representar os tiros
    private class Tiro {
        int x, y;
        int largura, altura;
        Color cor;
        int velocidade;
        boolean ativo;
        boolean paraDireita; // Direção do tiro

        Tiro(int x, int y, Color cor, boolean paraDireita) {
            this.x = x;
            this.y = y;
            this.largura = 20 + random.nextInt(15); // 20-34 pixels de largura
            this.altura = 1 + random.nextInt(2); // 1-2 pixels de altura
            this.cor = cor;
            this.velocidade = 5 + random.nextInt(4); // 5-8 de velocidade
            this.ativo = true;
            this.paraDireita = paraDireita;
        }

        void mover() {
            if (paraDireita) {
                x += velocidade; // Tiro para direita
            } else {
                x -= velocidade; // Tiro para esquerda
            }

            // Remove se sair da tela
            if (x > larguraTela || x + largura < 0) {
                ativo = false;
            }
        }

        void draw(Graphics g) {
            if (!ativo)
                return;

            // Desenha o tiro
            g.setColor(cor);
            g.fillRect(x, y, largura, altura);

            // DEBUG: Mostra hitbox do tiro 
            /*
            g.setColor(Color.WHITE);
            g.drawRect(x, y, largura, altura);
             */

            /*
            // Mostra direção do tiro (setinha)
            g.setColor(Color.YELLOW);
            if (paraDireita) {
                g.drawLine(x + largura, y + altura / 2, x + largura + 5, y + altura / 2);
                g.drawLine(x + largura + 5, y + altura / 2, x + largura + 3, y + altura / 2 - 2);
                g.drawLine(x + largura + 5, y + altura / 2, x + largura + 3, y + altura / 2 + 2);
            } else {
                g.drawLine(x, y + altura / 2, x - 5, y + altura / 2);
                g.drawLine(x - 5, y + altura / 2, x - 3, y + altura / 2 - 2);
                g.drawLine(x - 5, y + altura / 2, x - 3, y + altura / 2 + 2);
            }
            */
        }

        Rectangle getBounds() {
            return new Rectangle(x, y, largura, altura);
        }
    }

    /**
     * Construtor para OVNI com parâmetros específicos
     */
    public OVNI(int x, int y, int tamanho, int velocidadeX, int velocidadeY, int larguraTela, int alturaTela,
            int velocidadeScroll) {
        super(x, y, tamanho, tamanho, velocidadeX, velocidadeY, "imagens/ovni.png", larguraTela, velocidadeScroll);
        this.random = new Random();
        this.tipo = random.nextInt(3);
        this.tiros = new ArrayList<>();
        this.ultimoTiro = System.currentTimeMillis();
        this.intervaloEntreTiros = 1500 + random.nextInt(1500); // 1.5-3 segundos
        this.movendoParaDireita = velocidadeX > 0;
        this.alturaVoo = y;
    }

    /**
     * Construtor para OVNI totalmente aleatório
     */
    public OVNI(int larguraTela, int alturaTela, int velocidadeScroll) {
        super(0, 0, 0, 0, 0, 0, "imagens/ovni.png", larguraTela, velocidadeScroll);
        this.random = new Random();
        this.tiros = new ArrayList<>();
        this.ultimoTiro = System.currentTimeMillis();
        gerarOVNIAleatorio(larguraTela, alturaTela, velocidadeScroll);
    }

    /**
     * Gera um OVNI com propriedades aleatórias
     */
    private void gerarOVNIAleatorio(int larguraTela, int alturaTela, int velocidadeScrollAtual) {
        // Tamanho aleatório entre mínimo e máximo
        int tamanho = random.nextInt(TAMANHO_MAXIMO - TAMANHO_MINIMO + 1) + TAMANHO_MINIMO;
        this.width = tamanho;
        this.height = tamanho / 2; // OVNI é mais largo que alto

        // Decide se vem da esquerda ou direita
        movendoParaDireita = random.nextBoolean();

        if (movendoParaDireita) {
            // Vem da esquerda, vai para direita
            this.x = -tamanho;
            this.velocidadeX = 2 + random.nextInt(2); // 2-3 de velocidade (mais lento)
        } else {
            // Vem da direita, vai para esquerda
            this.x = larguraTela;
            this.velocidadeX = -(2 + random.nextInt(2)); // -2 a -3 de velocidade (mais lento)
        }

        // Altura de voo aleatória (não muito baixo)
        this.alturaVoo = 80 + random.nextInt(150); // Entre 80 e 230 pixels do topo
        this.y = alturaVoo;

        // Velocidade vertical mínima para compensar scroll
        this.velocidadeY = -velocidadeScrollAtual + random.nextInt(3) - 1; // -1, 0, +1

        this.tipo = random.nextInt(3);
        this.intervaloEntreTiros = 1500 + random.nextInt(1500); // 1.5-3 segundos

        // Tenta carregar imagem
        carregarImagem("imagens/ovni.png");
    }

    /**
     * Movimento do OVNI com tiros
     */
    @Override
    public void mover() {
        if (!ativo)
            return;

        // Movimento horizontal
        this.x += this.velocidadeX;

        // Movimento vertical suave (ondulação)
        this.y = alturaVoo + (int) (Math.sin(System.currentTimeMillis() * 0.001) * 8);

        // Adiciona scroll vertical
        this.y += this.velocidadeScroll;

        // Remove OVNI se sair da tela horizontalmente
        if ((movendoParaDireita && this.x > larguraTela) ||
                (!movendoParaDireita && this.x + this.width < 0)) {
            this.ativo = false;
        }

        // Remove OVNI se sair verticalmente
        if (this.y > 600 || this.y + this.height < 0) {
            this.ativo = false;
        }

        // Atira periodicamente
        long tempoAtual = System.currentTimeMillis();
        if (tempoAtual - ultimoTiro > intervaloEntreTiros && tiros.size() < MAX_TIROS_ATIVOS) {
            atirar();
            ultimoTiro = tempoAtual;
            intervaloEntreTiros = 1200 + random.nextInt(1800); // Novo intervalo (1.2-3s)
        }

        // Move e remove tiros inativos
        for (int i = tiros.size() - 1; i >= 0; i--) {
            Tiro tiro = tiros.get(i);
            tiro.mover();
            if (!tiro.ativo) {
                tiros.remove(i);
            }
        }
    }

    /**
     * Cria um novo tiro na mesma direção do OVNI
     */
    private void atirar() {
        // Cores aleatórias para os tiros
        Color[] coresTiros = {
                Color.GREEN,
                Color.CYAN,
                Color.MAGENTA,
                new Color(255, 100, 100), // Vermelho claro
                new Color(100, 255, 100), // Verde claro
                new Color(100, 100, 255), // Azul claro
                new Color(255, 255, 100), // Amarelo
                new Color(255, 150, 50) // Laranja
        };

        Color corTiro = coresTiros[random.nextInt(coresTiros.length)];

        // Posição do tiro (centro do OVNI)
        int tiroX, tiroY;

        if (movendoParaDireita) {
            // Tiro sai da frente (direita) do OVNI
            tiroX = this.x + this.width;
        } else {
            // Tiro sai da frente (esquerda) do OVNI
            tiroX = this.x;
        }

        tiroY = this.y + this.height / 2 - 1; // Centro vertical

        Tiro novoTiro = new Tiro(tiroX, tiroY, corTiro, movendoParaDireita);
        tiros.add(novoTiro);
    }

    /**
     * Desenho do OVNI e seus tiros
     */
    @Override
    public void draw(Graphics g) {
        if (!ativo)
            return;

        // Desenha os tiros primeiro (para ficarem atrás do OVNI)
        for (Tiro tiro : tiros) {
            tiro.draw(g);
        }

        // Aplica espelhamento se OVNI está indo para esquerda
        if (this.imagem != null && !movendoParaDireita) {
            // Desenha imagem espelhada
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.translate(this.x + this.width, this.y);
            g2d.scale(-1, 1);
            g2d.drawImage(this.imagem, 0, 0, this.width, this.height, null);
            g2d.dispose();
        } else if (this.imagem != null) {
            // Desenha imagem normal
            g.drawImage(this.imagem, this.x, this.y, this.width, this.height, null);
        } else {
            // Fallback gráfico - desenha OVNI
            desenharOVNI(g);
        }

        // DEBUG: Mostra hitbox do OVNI 
        /*
        g.setColor(Color.ORANGE);
        g.drawRect(this.x, this.y, this.width, this.height);
         */

        // Mostra direção do OVNI
        /*
        g.setColor(Color.YELLOW);
        if (movendoParaDireita) {
            g.drawString("→", this.x + this.width + 5, this.y + this.height / 2);
        } else {
            g.drawString("←", this.x - 10, this.y + this.height / 2);
        }
        */
    }

    /**
     * Desenha o OVNI como fallback gráfico
     */
    private void desenharOVNI(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        // Corpo principal do OVNI (disco)
        Color[] coresOVNI = {
                new Color(100, 200, 100, 200), // Verde alienígena
                new Color(200, 100, 200, 200), // Roxo
                new Color(100, 100, 255, 200) // Azul
        };

        g2d.setColor(coresOVNI[tipo % coresOVNI.length]);
        g2d.fillOval(this.x, this.y, this.width, this.height);

        // Cúpula superior
        g2d.setColor(new Color(150, 220, 255, 150));
        int cupulaAltura = this.height / 3;
        g2d.fillOval(this.x + this.width / 4, this.y, this.width / 2, cupulaAltura * 2);

        // Luzes/portholes
        g2d.setColor(Color.YELLOW);
        int numLuzes = 4 + tipo;
        int espacamentoLuzes = this.width / (numLuzes + 1);

        for (int i = 1; i <= numLuzes; i++) {
            int luzX = this.x + i * espacamentoLuzes;
            int luzY = this.y + this.height - 10;
            g2d.fillOval(luzX - 3, luzY - 3, 6, 6);
        }

        // Luz principal (na frente)
        g2d.setColor(new Color(0, 255, 255, 150));
        if (movendoParaDireita) {
            // Luz na direita
            int[] luzXPoints = { this.x + this.width, this.x + this.width + 8, this.x + this.width };
            int[] luzYPoints = { this.y + this.height / 2 - 4, this.y + this.height / 2, this.y + this.height / 2 + 4 };
            g2d.fillPolygon(luzXPoints, luzYPoints, 3);
        } else {
            // Luz na esquerda
            int[] luzXPoints = { this.x, this.x - 8, this.x };
            int[] luzYPoints = { this.y + this.height / 2 - 4, this.y + this.height / 2, this.y + this.height / 2 + 4 };
            g2d.fillPolygon(luzXPoints, luzYPoints, 3);
        }

        // Contorno
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(2f));
        g2d.drawOval(this.x, this.y, this.width, this.height);
    }

    /**
     * Verifica colisão com tiros (mas o OVNI não é destruído pelos próprios tiros)
     */
    @Override
    public boolean colisao(ObjetoJogo outro) {
        if (!this.ativo)
            return false;

        // Verifica colisão do OVNI em si
        if (super.colisao(outro)) {
            return true; // Foguete colidiu com o OVNI
        }

        // Verifica colisão com os tiros
        for (Tiro tiro : tiros) {
            if (tiro.ativo && tiro.getBounds().intersects(outro.getBounds())) {
                tiro.ativo = false; // Apenas o tiro some
                return true; // Retorna true para indicar colisão (mas OVNI permanece)
            }
        }

        return false;
    }

    /**
     * Getters para propriedades do OVNI
     */
    public int getQuantidadeTiros() {
        return tiros.size();
    }

    public boolean estaAtirando() {
        return !tiros.isEmpty();
    }

    /**
     * Método estático para criar um OVNI aleatório
     */
    public static OVNI criarAleatorio(int larguraTela, int alturaTela, int velocidadeScroll) {
        return new OVNI(larguraTela, alturaTela, velocidadeScroll);
    }

    /*
     * Método auxiliar para verificar colisão apenas com tiros
     * Retorna true se algum tiro colidiu, mas não destrói o OVNI
     */
    public boolean tiroColidiu(ObjetoJogo outro) {
        for (Tiro tiro : tiros) {
            if (tiro.ativo && tiro.getBounds().intersects(outro.getBounds())) {
                tiro.ativo = false; // Apenas o tiro some
                return true;
            }
        }
        return false;
    }
}