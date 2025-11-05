package src;

public class GerenciadorDificuldade {
    private int velocidadeBase = 3;
    private int velocidadeAtual = 3;
    private long tempoInicio;
    private int intervaloAumento = 1000;  // MUDOU: 1 segundo entre verificações
    private int incrementoVelocidade = 1;
    private long ultimoAumento;
    private int segundosDesdeUltimoNivel = 0; // NOVO: contador de segundos
    
    public GerenciadorDificuldade() {
        this.tempoInicio = System.currentTimeMillis();
        this.ultimoAumento = tempoInicio;
    }
    
    public void atualizar() {
        long tempoAtual = System.currentTimeMillis();
        
        // Verifica a cada 1 segundo (em vez de 30)
        if (tempoAtual - ultimoAumento > intervaloAumento) {
            segundosDesdeUltimoNivel++; // NOVO: conta segundos
            
            // Aumenta velocidade a cada 30 segundos acumulados
            if (segundosDesdeUltimoNivel >= 30) {
                velocidadeAtual += incrementoVelocidade;
                segundosDesdeUltimoNivel = 0; // Reinicia contador
                System.out.println("Dificuldade aumentada! Velocidade: " + velocidadeAtual);
            }
            
            ultimoAumento = tempoAtual;
        }
    }
    
    // Resto do código permanece igual...
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
        segundosDesdeUltimoNivel = 0; // NOVO: reset do contador
    }
    
    public int getNivelDificuldade() {
        return velocidadeAtual - velocidadeBase + 1;
    }
}