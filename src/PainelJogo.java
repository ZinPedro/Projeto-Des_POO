package src;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*; 

public class PainelJogo extends JPanel implements KeyListener{
    
    private FundoEstrelado fundoEstrelado; // Substitui a imagem de fundo
    private Foguete foguete;
    private GerenciadorDificuldade gerenciadorDificuldade;
    private int velocidadeScroll;
    private java.util.List<Obstaculo> obstaculos;
    private Timer gameTimer;

    public PainelJogo(){
        setFocusable(true);
        setDoubleBuffered(true);
        addKeyListener(this);

        // Inicializa gerenciador de dificuldade
        gerenciadorDificuldade = new GerenciadorDificuldade();
        velocidadeScroll = gerenciadorDificuldade.getVelocidadeAtual();

        // Inicializa o fundo estrelado (800x600 é o tamanho da tela, 200 estrelas)
        fundoEstrelado = new FundoEstrelado(800, 600, 200);
        
        foguete = new Foguete(400,500);

        // Lista de obstáculos
        obstaculos = new java.util.ArrayList<>();

        //Inicia game loop
        iniciarGameLoop();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Desenha o fundo estrelado
        fundoEstrelado.desenhar(g);

        // Desenha foguete
        foguete.draw(g);

        // Desenha Obstaculos  
        for (Obstaculo obstaculo : obstaculos) {
            obstaculo.draw(g);

            // DEBUG: Mostrar posição dos asteroides (opcional)
            g.setColor(Color.GREEN);
            g.drawString("A:" + obstaculo.getX() + "," + obstaculo.getY(), obstaculo.getX(), obstaculo.getY() - 10);
        }

        // HUD
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Velocidade: " + velocidadeScroll, 10, 20);
        g.drawString("Tempo: " + gerenciadorDificuldade.getTempoJogado() + "s", 10, 40);
        g.drawString("Vidas: " + foguete.getVidas(), 10, 60);
        g.drawString("Nível: " + gerenciadorDificuldade.getNivelDificuldade(), 10, 80);
        g.drawString("Asteroides: " + obstaculos.size(), 10, 100);
    }

    private void iniciarGameLoop() {
        gameTimer = new Timer(16, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                atualizar();
            }
        });
        gameTimer.start();
    }

    public void atualizar() {
        // Atualiza dificuldade
        gerenciadorDificuldade.atualizar();
        velocidadeScroll = gerenciadorDificuldade.getVelocidadeAtual();
        
        // Atualiza o fundo com a velocidade atual
        fundoEstrelado.atualizar(velocidadeScroll);
        
        // Move foguete
        foguete.mover();
        
        // Move obstáculos com velocidade atual
        for (Obstaculo obstaculo : obstaculos) {
            obstaculo.setVelocidadeScroll(velocidadeScroll);
            obstaculo.mover();
        }

        int tamanhoAntes = obstaculos.size();
        obstaculos.removeIf(obstaculo -> !obstaculo.isAtivo());
        if (tamanhoAntes != obstaculos.size()) {
            //System.out.println("❌ Obstáculos REMOVIDOS: " + (tamanhoAntes - obstaculos.size()));
        }
        
        // Gera novos obstáculos
        gerarObstaculos();
        
        // Verifica colisões
        verificarColisoes();
        
        repaint();
    }

    private void gerarObstaculos() {
        double chance = 0.02 + (velocidadeScroll * 0.005);
        if (Math.random() < chance) {
            Asteroide asteroide = new Asteroide(800, 600, velocidadeScroll);
            obstaculos.add(asteroide);
            //System.out.println("✅ Asteroide GERADO! Total: " + obstaculos.size());
        }
    }

    private void verificarColisoes() {
        for (Obstaculo obstaculo : obstaculos) {
            if (foguete.colisao(obstaculo)) {
                foguete.perderVida();
                obstaculo.setAtivo(false);
                
                if (!foguete.estaAtivo()) {
                    gameOver();
                }
            }
        }
    }

    private void gameOver() {
        gameTimer.stop();
        System.out.println("Game Over! Pontuação: " + foguete.getPontuacao());
    }

     // Métodos KeyListener (mantenha os mesmos)
     @Override
     public void keyPressed(KeyEvent e) {
         int keyCode = e.getKeyCode();
         
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
         // Método obrigatório mas não utilizado
     }
}