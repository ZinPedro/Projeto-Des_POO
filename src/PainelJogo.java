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
    private Font fontePausa;
    
    // Interface para comunicação com o menu principal
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

        // HUD
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Velocidade: " + velocidadeScroll, 10, 20);
        g.drawString("Tempo: " + gerenciadorDificuldade.getTempoJogado() + "s", 10, 40);
        g.drawString("Vidas: " + foguete.getVidas(), 10, 60);
        g.drawString("Nível: " + gerenciadorDificuldade.getNivelDificuldade(), 10, 80);
        g.drawString("Asteroides: " + obstaculos.size(), 10, 100);

        // Se o jogo estiver pausado, mostra a mensagem
        if (pausado) {
            g.setColor(new Color(255, 255, 255, 200));
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

        // Se o foguete não estiver ativo (game over)
        if (!foguete.estaAtivo()) {
            g.setColor(new Color(0, 0, 0, 180));
            g.fillRect(0, 0, getWidth(), getHeight());
            
            g.setColor(Color.RED);
            g.setFont(fontePausa);
            String gameOver = "GAME OVER";
            FontMetrics fm = g.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(gameOver)) / 2;
            int y = getHeight() / 2 - 50;
            g.drawString(gameOver, x, y);
            
            g.setFont(new Font("Arial", Font.PLAIN, 24));
            g.setColor(Color.WHITE);
            String pontuacao = "Pontuação: " + foguete.getPontuacao();
            FontMetrics fm2 = g.getFontMetrics();
            int x2 = (getWidth() - fm2.stringWidth(pontuacao)) / 2;
            int y2 = getHeight() / 2 + 20;
            g.drawString(pontuacao, x2, y2);
            
            String tempo = "Tempo: " + gerenciadorDificuldade.getTempoJogado() + "s";
            int x3 = (getWidth() - fm2.stringWidth(tempo)) / 2;
            int y3 = getHeight() / 2 + 60;
            g.drawString(tempo, x3, y3);
            
            String voltarMenu = "ESC para voltar ao menu";
            int x4 = (getWidth() - fm2.stringWidth(voltarMenu)) / 2;
            int y4 = getHeight() / 2 + 100;
            g.drawString(voltarMenu, x4, y4);
        }
    }

    private void iniciarGameLoop() {
        gameTimer = new Timer(16, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!pausado && foguete.estaAtivo()) {
                    atualizar();
                }
                repaint();
            }
        });
        gameTimer.start();
    }

    public void atualizar() {
        gerenciadorDificuldade.atualizar();
        velocidadeScroll = gerenciadorDificuldade.getVelocidadeAtual();
        
        fundoEstrelado.atualizar(velocidadeScroll);
        foguete.mover();
        
        for (Obstaculo obstaculo : obstaculos) {
            obstaculo.setVelocidadeScroll(velocidadeScroll);
            obstaculo.mover();
        }

        obstaculos.removeIf(obstaculo -> !obstaculo.isAtivo());
        
        gerarObstaculos();
        verificarColisoes();
    }

    private void gerarObstaculos() {
        double chance = 0.02 + (velocidadeScroll * 0.005);
        if (Math.random() < chance) {
            Asteroide asteroide = new Asteroide(800, 600, velocidadeScroll);
            obstaculos.add(asteroide);
        }
    }

    private void verificarColisoes() {
        for (Obstaculo obstaculo : obstaculos) {
            if (foguete.colisao(obstaculo)) {
                foguete.perderVida();
                obstaculo.setAtivo(false);
            }
        }
    }

    public void pausarOuContinuar() {
        pausado = !pausado;
        if (pausado) {
            System.out.println("Jogo pausado");
        } else {
            System.out.println("Jogo continuado");
        }
    }

    public void resetarJogo() {
        foguete = new Foguete(400, 500);
        gerenciadorDificuldade.reset();
        velocidadeScroll = gerenciadorDificuldade.getVelocidadeAtual();
        obstaculos.clear();
        pausado = false;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        
        if (keyCode == KeyEvent.VK_P) {
            pausarOuContinuar();
            return;
        }
        
        if (keyCode == KeyEvent.VK_ESCAPE) {
            if (listener != null) {
                listener.voltarAoMenu();
            }
            return;
        }
        
        if (pausado || !foguete.estaAtivo()) {
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
    public void keyTyped(KeyEvent e) {}
}