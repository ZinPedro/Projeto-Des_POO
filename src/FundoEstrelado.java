package src;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FundoEstrelado {
    private List<Estrela> estrelas;
    private int larguraTela;
    private int alturaTela;
    private Random random;

    // Cores para diferentes tipos de estrelas
    private Color[] coresEstrelas = {
            Color.WHITE,
            new Color(200, 200, 255), // Azul claro
            new Color(255, 255, 200), // Amarelo claro
            new Color(255, 200, 200) // Vermelho claro
    };

    public FundoEstrelado(int larguraTela, int alturaTela, int quantidadeEstrelas) {
        this.larguraTela = larguraTela;
        this.alturaTela = alturaTela;
        this.random = new Random();
        this.estrelas = new ArrayList<>();

        gerarEstrelas(quantidadeEstrelas);
    }

    private void gerarEstrelas(int quantidade) {
        for (int i = 0; i < quantidade; i++) {
            Estrela estrela = new Estrela();
            estrela.x = random.nextInt(larguraTela);
            estrela.y = random.nextInt(alturaTela);
            estrela.velocidade = 0.5f + random.nextFloat() * 2.0f; // Velocidade base
            estrela.tamanho = random.nextFloat() * 2.0f + 0.5f;
            estrela.cor = coresEstrelas[random.nextInt(coresEstrelas.length)];
            estrela.brilho = 0.7f + random.nextFloat() * 0.3f; // Brilho variável
            estrelas.add(estrela);
        }
    }

    public void atualizar(int velocidadeScroll) {
        for (Estrela estrela : estrelas) {
            // Move a estrela para baixo com velocidade proporcional ao scroll
            estrela.y += estrela.velocidade * (velocidadeScroll * 0.3f);

            // Se a estrela sair da tela, reposiciona no topo
            if (estrela.y > alturaTela) {
                estrela.y = 0;
                estrela.x = random.nextInt(larguraTela);
            }

            // Efeito de piscar suave
            if (random.nextInt(100) < 2) { // 2% de chance por frame
                estrela.brilho = 0.5f + random.nextFloat() * 0.5f;
            }
        }
    }

    public void desenhar(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        // Fundo preto sólido
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, larguraTela, alturaTela);

        // Desenha todas as estrelas
        for (Estrela estrela : estrelas) {
            // Aplica o brilho atual à cor
            Color corComBrilho = new Color(
                    (int) (estrela.cor.getRed() * estrela.brilho),
                    (int) (estrela.cor.getGreen() * estrela.brilho),
                    (int) (estrela.cor.getBlue() * estrela.brilho));

            g2d.setColor(corComBrilho);

            // Desenha a estrela como um pequeno círculo
            int tamanho = (int) estrela.tamanho;
            g2d.fillOval((int) estrela.x, (int) estrela.y, tamanho, tamanho);

            // Para estrelas maiores, adiciona um brilho extra
            if (estrela.tamanho > 1.5f) {
                g2d.setColor(new Color(255, 255, 255, 100));
                g2d.fillOval((int) estrela.x - 1, (int) estrela.y - 1, tamanho + 2, tamanho + 2);
            }
        }

        // Adiciona algumas estrelas cadentes ocasionais
        if (random.nextInt(500) == 0) { // Chance bem baixa
            desenharEstrelaCadente(g2d);
        }
    }

    private void desenharEstrelaCadente(Graphics2D g2d) {
        int x = random.nextInt(larguraTela);
        int comprimento = 20 + random.nextInt(30);

        // Gradiente para o rastro
        for (int i = 0; i < comprimento; i++) {
            float alpha = 1.0f - (float) i / comprimento;
            g2d.setColor(new Color(255, 255, 255, (int) (alpha * 200)));
            g2d.fillRect(x - i, i, 2, 2);
        }
    }

    // Classe interna para representar uma estrela
    private static class Estrela {
        float x, y;
        float velocidade;
        float tamanho;
        Color cor;
        float brilho;
    }
}
