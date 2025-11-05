package src;
import java.awt.*;
import java.awt.image.BufferedImage;

import javax.swing.*;    

public class Foguete extends ObjetoJogo{
    //atributos foguete
    private int vidas;
    private int pontuacao;
    private int velocidadeMovimento;
    private boolean movCima, movBaixo, movEsq, movDir;

    //p/ pequena animação
    private long ultimoMovimento;
    private boolean frameAlternado;
    private boolean usandooFallback;

    //Construtor
    public Foguete(int x, int y){
        super(x,y,40,60); //super classe pai (ObjetoJogo)

        this.vidas = 3;
        this.pontuacao = 0;
        this.velocidadeMovimento = 5;

        //inicializa os estados de movimento
        this.movCima = false;
        this.movBaixo = false;
        this.movEsq = false;
        this.movDir = false;

        this.ultimoMovimento = System.currentTimeMillis();
        this.frameAlternado = false;
        this.usandooFallback = false;

        carregarImagem();

    }

    //Método mover
    @Override
    public void mover(){
        if(movEsq){
            x -= velocidadeMovimento;
        }
        if(movDir){
            x += velocidadeMovimento;
        }
        if(movCima){
            y += velocidadeMovimento;
        }
        if(movBaixo){
            y -= velocidadeMovimento;
        }

        manterDentroTela();

        // Animação simples (alternar frame a cada 200ms)
        long tempoAtual = System.currentTimeMillis();
        if (tempoAtual - ultimoMovimento > 200) {
            frameAlternado = !frameAlternado;
            ultimoMovimento = tempoAtual;
        }

        if(usandooFallback){
            criarImagemFallback();
        }
    }

    private void manterDentroTela(){
        int larguraTela = 800;
        int alturaTela = 600;

        if(x < 0){
            x = 0;
        }
        if(x > larguraTela - width){
            x = larguraTela - width;
        }

        if(y < 0){
            y = 0;
        }
        if (y > alturaTela - height){
            y = alturaTela - height;
        }
    }

    private void carregarImagem(){
        try { //tenta carregar imagem fogute
            ImageIcon icon = new ImageIcon("imagem/Foguete.png");
            imagem = icon.getImage();   

            // Verifica se a imagem foi carregada corretamente
            if (imagem.getWidth(null) <= 0 || imagem.getHeight(null) <= 0) {
                throw new Exception("Imagem inválida");
            }

            usandoFallback = false;

        } catch (Exception e) {
            System.out.println("Erro ao carregar imagem do foguete, usando FallBack: " + e.getMessage());
            usandoFallback = true;
            criarImagemFallback();
        }
    }

    private void criarImagemFallback() {
        // Cria uma imagem simples como fallback
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();

        // Suavização
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Corpo do foguete (triângulo)
        g2d.setColor(Color.WHITE);
        int[] xPoints = {width/2, width/4, 3*width/4};
        int[] yPoints = {10, height - 10, height - 10};
        g2d.fillPolygon(xPoints, yPoints, 3);
        
        // Chamas (alternando para animação)
         g2d.setColor(frameAlternado ? Color.ORANGE : Color.YELLOW);
        int[] chamaXPoints = {width/2, width/3, 2*width/3};
        int[] chamaYPoints = {height - 10, height + 5, height + 5};
        g2d.fillPolygon(chamaXPoints, chamaYPoints, 3);
        
        g2d.dispose();
        imagem = img;
    }

    @Override
    public void draw (Graphics g){
        if (!ativo) return;

        //desenha imagem do foguete (carregada ou Fallback)
        if (imagem != null){
            g.drawImage(imagem, x, y, width, height, null);
        }

        //mostrar HitBoox (Debug)
        g.setColor(Color.red);
        g.drawRect(x, y, width, height);
    }

    //Getters e Setters

    public int getVidas(){ return vidas; }
    public int getPontuacao(){ return pontuacao; }

    public void setVidas(int vidas){ this.vidas = vidas; }
    public void setPontuacao(int pontuacao){ this.pontuacao = pontuacao; }

    //aumento de pontos
    public void addPontuacao(int pontos){
        this.pontuacao += pontos;
    }    

    //Perder vidas
    public void perderVida(){
        this.vidas--;
        if(vidas <= 0){
            ativo = false;
        }
    }

    //Verificar se esta ativos (se tem vidas)
    public boolean estaAtivo(){
        return ativo && vidas > 0;
    }

    //Getters p/ estados de movimetno (debug) e Setters p/ Controles de movimento por teclado
    public boolean isMovCima(){ return movCima; }
    public boolean isMovBaixo(){ return movBaixo; }
    public boolean isMovEsq(){ return movEsq; }
    public boolean isMovDir(){ return movDir; }

    public void setMovCima(boolean movendo){
        this.movCima = movendo;
    }

    public void setMovBaixo(boolean movendo){
        this.movBaixo = movendo;
    }

    public void setMovEsq(boolean movendo){
        this.movEsq = movendo;
    }

    public void setMovDir(boolean movendo){
        this.movDir = movendo;
    }
}
