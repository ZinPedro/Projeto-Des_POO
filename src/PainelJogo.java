package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class PainelJogo extends JPanel implements KeyListener {

    private FundoEstrelado fundoEstrelado;
    private Foguete foguete;
    private GerenciadorDificuldade gerenciadorDificuldade;
    private int velocidadeScroll;
    private java.util.List<Obstaculo> obstaculos;
    private Timer gameTimer;
    private boolean pausado = false;
    private boolean gameOver = false;
    private Font fontePausa;
    private int pontuacaoFinal = 0;
    private int tempoFinal = 0;

    public interface JogoListener {
        void voltarAoMenu();
    }

    private JogoListener listener;

    public PainelJogo() {
        setFocusable(true);
        setDoubleBuffered(true);
        addKeyListener(this);

        gerenciadorDificuldade = new GerenciadorDificuldade();
        velocidadeScroll = gerenciadorDificuldade.getVelocidadeAtual();

        fundoEstrelado = new FundoEstrelado(800, 600, 200);
        foguete = new Foguete(400, 500);
        obstaculos = new java.util.ArrayList<>();

        fontePausa = new Font("Arial", Font.BOLD, 48);

        iniciarGameLoop();
    }

    public void setJogoListener(JogoListener listener) {
        this.listener = listener;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        fundoEstrelado.desenhar(g);
        foguete.draw(g);

        for (Obstaculo obstaculo : obstaculos) {
            obstaculo.draw(g);
        }

        // HUD - Interface do jogador
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Velocidade: " + velocidadeScroll, 10, 20);
        g.drawString("Tempo: " + gerenciadorDificuldade.getTempoJogado() + "s", 10, 40);
        g.drawString("Vidas: " + foguete.getVidas(), 10, 60);
        g.drawString("Nível: " + gerenciadorDificuldade.getNivelDificuldade(), 10, 80);
        g.drawString("Pontuação: " + foguete.getPontuacao(), 10, 100);

        // Contar asteroides e satélites separadamente
        int asteroides = 0;
        int satelites = 0;
        for (Obstaculo obstaculo : obstaculos) {
            if (obstaculo instanceof Asteroide) {
                asteroides++;
            } else if (obstaculo instanceof Satelite) {
                satelites++;
            }
        }
        g.drawString("Asteroides: " + asteroides, 10, 120);
        g.drawString("Satélites: " + satelites, 10, 140);
        
        // Se o jogo estiver pausado, mostra a mensagem
        if (pausado && !gameOver) {
            g.setColor(new Color(0, 0, 0, 180));
            g.fillRect(0, 0, getWidth(), getHeight());

            g.setColor(Color.BLUE);
            g.setFont(fontePausa);
            String textoPausa = "JOGO PAUSADO";
            FontMetrics fm = g.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(textoPausa)) / 2;
            int y = getHeight() / 2 - 50;
            g.drawString(textoPausa, x, y);

            g.setFont(new Font("Arial", Font.PLAIN, 24));
            g.setColor(Color.WHITE);
            String continuar = "Pressione P para continuar";
            FontMetrics fm2 = g.getFontMetrics();
            int x2 = (getWidth() - fm2.stringWidth(continuar)) / 2;
            int y2 = getHeight() / 2 + 20;
            g.drawString(continuar, x2, y2);

            String voltarMenu = "ESC para voltar ao menu";
            int x3 = (getWidth() - fm2.stringWidth(voltarMenu)) / 2;
            int y3 = getHeight() / 2 + 60;
            g.drawString(voltarMenu, x3, y3);
        }

        // Se o jogo acabou (game over)
        if (gameOver) {
            g.setColor(new Color(0, 0, 0, 200));
            g.fillRect(0, 0, getWidth(), getHeight());

            g.setColor(Color.RED);
            g.setFont(fontePausa);
            String gameOverText = "GAME OVER";
            FontMetrics fm = g.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(gameOverText)) / 2;
            int y = getHeight() / 2 - 120;
            g.drawString(gameOverText, x, y);

            g.setFont(new Font("Arial", Font.BOLD, 32));
            g.setColor(Color.YELLOW);
            String pontuacao = "Pontuação: " + pontuacaoFinal;
            FontMetrics fm2 = g.getFontMetrics();
            int x2 = (getWidth() - fm2.stringWidth(pontuacao)) / 2;
            int y2 = getHeight() / 2 - 60;
            g.drawString(pontuacao, x2, y2);

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.PLAIN, 24));

            // CORREÇÃO: Usar FontMetrics da fonte atual (24) para tempo
            String tempo = "Tempo: " + tempoFinal + " segundos";
            FontMetrics fmTempo = g.getFontMetrics(); // Isso pega a fonte atual (24)
            int x3 = (getWidth() - fmTempo.stringWidth(tempo)) / 2;
            int y3 = getHeight() / 2;
            g.drawString(tempo, x3, y3);

            // CORREÇÃO: Usar FontMetrics da fonte atual (24) para nível
            String nivel = "Nível alcançado: " + gerenciadorDificuldade.getNivelDificuldade();
            FontMetrics fmNivel = g.getFontMetrics(); // Recalcular para a string atual
            int x4 = (getWidth() - fmNivel.stringWidth(nivel)) / 2;
            int y4 = getHeight() / 2 + 40;
            g.drawString(nivel, x4, y4);

            g.setFont(new Font("Arial", Font.PLAIN, 20));
            g.setColor(new Color(180, 180, 255));
            String voltarMenu = "Pressione ESC para voltar ao menu";
            FontMetrics fm4 = g.getFontMetrics();
            int x5 = (getWidth() - fm4.stringWidth(voltarMenu)) / 2;
            int y5 = getHeight() / 2 + 90;
            g.drawString(voltarMenu, x5, y5);

            String jogarNovamente = "Pressione ESPAÇO para jogar novamente";
            int x6 = (getWidth() - fm4.stringWidth(jogarNovamente)) / 2;
            int y6 = getHeight() / 2 + 120;
            g.drawString(jogarNovamente, x6, y6);
        }
    }

    private void iniciarGameLoop() {
        gameTimer = new Timer(16, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!pausado && !gameOver) {
                    atualizar();
                }
                repaint();
            }
        });
        gameTimer.start();
    }

    public void atualizar() {
        if (gameOver)
            return;

        // Atualiza dificuldade
        gerenciadorDificuldade.atualizar();
        velocidadeScroll = gerenciadorDificuldade.getVelocidadeAtual();

        // Atualiza pontuação baseada no tempo e nível
        atualizarPontuacao();

        // Atualiza o fundo com a velocidade atual
        fundoEstrelado.atualizar(velocidadeScroll);

        // Move foguete
        foguete.mover();

        // Move obstáculos com velocidade atual
        for (Obstaculo obstaculo : obstaculos) {
            obstaculo.setVelocidadeScroll(velocidadeScroll);
            obstaculo.mover();
        }

        // Remove obstáculos inativos
        obstaculos.removeIf(obstaculo -> !obstaculo.isAtivo());

        // Gera novos obstáculos
        gerarObstaculos();

        // Verifica colisões
        verificarColisoes();
    }

    private void atualizarPontuacao() {
        if (gameOver)
            return;

        // Pontuação aumenta com o tempo e nível
        int tempo = gerenciadorDificuldade.getTempoJogado();
        int nivel = gerenciadorDificuldade.getNivelDificuldade();

        // Fórmula de pontuação:
        // - Base: 10 pontos por segundo
        // - Bônus: 100 pontos por nível
        // - Multiplicador: nível atual
        int pontuacaoBase = tempo * 10;
        int bonusNivel = nivel * 100;
        int pontuacaoTotal = (pontuacaoBase + bonusNivel) * Math.max(1, nivel / 2);

        foguete.setPontuacao(pontuacaoTotal);

        // Pontuação adicional por sobrevivência em níveis altos
        if (nivel >= 5) {
            foguete.addPontuacao(nivel * 20); // Bônus de 20 pontos por nível acima de 5
        }
    }

    private void gerarObstaculos() {
        double chanceAsteroide = 0.02 + (velocidadeScroll * 0.005);
        double chanceSatelite = 0.01 + (velocidadeScroll * 0.003); // Satélites são mais raros

        if (Math.random() < chanceAsteroide) {
            Asteroide asteroide = new Asteroide(800, 600, velocidadeScroll);
            obstaculos.add(asteroide);
        }

        if (Math.random() < chanceSatelite) {
            Satelite satelite = new Satelite(800, 600, velocidadeScroll);
            obstaculos.add(satelite);
        }
    }

    private void verificarColisoes() {
        for (Obstaculo obstaculo : obstaculos) {
            if (foguete.colisao(obstaculo)) {
                foguete.perderVida();
                obstaculo.setAtivo(false);

                // Adiciona pequena pontuação por desviar (após perder vida)
                foguete.addPontuacao(50);

                if (!foguete.estaAtivo()) {
                    gameOver();
                }
            }
        }
    }

    private void gameOver() {
        gameOver = true;
        pontuacaoFinal = foguete.getPontuacao();
        tempoFinal = gerenciadorDificuldade.getTempoJogado();

        // Para o gerenciador de dificuldade
        gerenciadorDificuldade.parar();

        System.out.println("Game Over! Pontuação: " + pontuacaoFinal + " | Tempo: " + tempoFinal + "s");
    }

    public void pausarOuContinuar() {
        if (!gameOver) {
            pausado = !pausado;
            if (pausado) {
                System.out.println("Jogo pausado");
            } else {
                System.out.println("Jogo continuado");
            }
        }
    }

    public void resetarJogo() {
        foguete = new Foguete(400, 500);
        gerenciadorDificuldade.reset();
        velocidadeScroll = gerenciadorDificuldade.getVelocidadeAtual();
        obstaculos.clear();
        pausado = false;
        gameOver = false;
        pontuacaoFinal = 0;
        tempoFinal = 0;
        foguete.setPontuacao(0);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();

        if (keyCode == KeyEvent.VK_P && !gameOver) {
            pausarOuContinuar();
            return;
        }

        if (keyCode == KeyEvent.VK_ESCAPE) {
            if (listener != null) {
                listener.voltarAoMenu();
            }
            return;
        }

        if (keyCode == KeyEvent.VK_SPACE && gameOver) {
            resetarJogo();
            return;
        }

        if (pausado || gameOver) {
            return;
        }

        switch (keyCode) {
            case KeyEvent.VK_UP:
            case KeyEvent.VK_W:
                foguete.setMovCima(true);
                break;
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_S:
                foguete.setMovBaixo(true);
                break;
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_A:
                foguete.setMovEsq(true);
                break;
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_D:
                foguete.setMovDir(true);
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();

        switch (keyCode) {
            case KeyEvent.VK_UP:
            case KeyEvent.VK_W:
                foguete.setMovCima(false);
                break;
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_S:
                foguete.setMovBaixo(false);
                break;
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_A:
                foguete.setMovEsq(false);
                break;
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_D:
                foguete.setMovDir(false);
                break;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }
}