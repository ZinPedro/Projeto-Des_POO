package src;

public class GerenciadorDificuldade {
    private double velocidadeBase = 3.0;
    private double velocidadeAtual = 3.0;
    private long tempoInicio;
    private int intervaloAumento = 1000;  // MUDOU: 1 segundo entre verificações
    private double incrementoVelocidade = 0.033;
    private long ultimoAumento;
    
    public GerenciadorDificuldade() {
        this.tempoInicio = System.currentTimeMillis();
        this.ultimoAumento = tempoInicio;
    }
    
    public void atualizar() {
        long tempoAtual = System.currentTimeMillis();
        
        // Aumenta suavemente a cada 1 segundo
        if (tempoAtual - ultimoAumento > intervaloAumento) {
            velocidadeAtual += incrementoVelocidade;
            ultimoAumento = tempoAtual;
            
            // Debug opcional (mostra a cada ~5 segundos)
            if (getTempoJogado() % 5 == 0) {
                System.out.println("Velocidade: " + String.format("%.2f", velocidadeAtual) + 
                                 " | Nível: " + getNivelDificuldade());
            }
        }
    }
    
    // Resto do código permanece igual...
    public int getVelocidadeAtual() {
        return (int) Math.floor(velocidadeAtual);
    }

    public double getVelocidadeExata(){
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
    
    public int getNivelDificuldade() {
        return (int)(velocidadeAtual - velocidadeBase + 1);
    }
}