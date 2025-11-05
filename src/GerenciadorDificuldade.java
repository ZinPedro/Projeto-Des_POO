package src;

public class GerenciadorDificuldade {
    private int velocidadeBase = 3;        // Velocidade inicial
    private int velocidadeAtual = 3;       // Velocidade atual
    private long tempoInicio;              // Quando começou o jogo
    private int intervaloAumento = 30000;  // 30 segundos entre aumentos
    private int incrementoVelocidade = 1;  // Aumenta 1 ponto
    private long ultimoAumento;            // Último aumento

    public GerenciadorDificuldade() {
        this.tempoInicio = System.currentTimeMillis();
        this.ultimoAumento = tempoInicio;
    }

    public void atualizar() {
        long tempoAtual = System.currentTimeMillis();
        
        // Aumenta velocidade a cada 30 segundos
        if (tempoAtual - ultimoAumento > intervaloAumento) {
            velocidadeAtual += incrementoVelocidade;
            ultimoAumento = tempoAtual;
            System.out.println("Dificuldade aumentada! Velocidade: " + velocidadeAtual);
        }
    }

    public int getVelocidadeAtual() {
        return velocidadeAtual;
    }
    
    public int getTempoJogado() {
        return (int)((System.currentTimeMillis() - tempoInicio) / 1000);
    }
    
    public void reset() {
        velocidadeAtual = velocidadeBase;
        tempoInicio = System.currentTimeMillis();
        ultimoAumento = tempoInicio;
    }
    
    // Getter para debug/info
    public int getNivelDificuldade() {
        return velocidadeAtual - velocidadeBase + 1;
    }
}
