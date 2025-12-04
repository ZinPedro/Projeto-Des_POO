package src;

import java.awt.*;

import javax.swing.ImageIcon;

public abstract class ObjetoJogo {
    // atributos protegidos (acessiveis pelas classes filhas)
    protected int x, y; // coordenadas
    protected int width, height; // tamanho
    protected int velocidadeX, velocidadeY; // movimento
    protected Image imagem; // aparencia
    protected boolean ativo; // estado

    // contrutor
    public ObjetoJogo(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.velocidadeX = 0;
        this.velocidadeY = 0;
        this.ativo = true;
    }

    // Métodos Abstratos (Implementação Obrigatória nos Filhos)
    public abstract void mover(); // atualiza a posição/logica do objeto

    public abstract void draw(Graphics g); // desenhar objeto na tela

    // Métodos Concretos
    public Rectangle getBounds() { // limites de colisão
        return new Rectangle(x, y, width, height);
    }

    public boolean colisao(ObjetoJogo outro) { // verifica colisao
        if (!this.ativo || !outro.ativo)
            return false; // caso algum nao esteja ativo
        return getBounds().intersects(outro.getBounds()); // se getBounds do objeto intersectar o getBonds do outro,
                                                          // retorna true
    }

    public boolean estaForaDaTela(int screenWidth, int screenHeight) { // verifica saida da tela (true se está fora)
        return x + width < 0 ||
                x > screenWidth ||
                y + height < 0 ||
                y > screenHeight;
    }

    // Getters e Setters

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getVelocidadeX() {
        return velocidadeX;
    }

    public int getVelocidadeY() {
        return velocidadeY;
    }

    public Image getImagem() {
        return imagem;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setVelocidadeX(int velocidadeX) {
        this.velocidadeX = velocidadeX;
    }

    public void setVelocidadeY(int velocidadeY) {
        this.velocidadeY = velocidadeY;
    }

    public void setImagem(Image imagem) {
        this.imagem = imagem;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    // Metodo carregar imagem (com fallback (se nao carregar))
    protected void carregarImagem(String caminho) {
        try {
            ImageIcon icon = new ImageIcon(caminho);
            imagem = icon.getImage();
        } catch (Exception e) {
            System.out.println("Erro ao carregar a imagem: " + caminho);
            imagem = null;
        }
    }
}
