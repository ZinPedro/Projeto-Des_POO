package src;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

public class MenuInicial extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener {
    
    public interface MenuListener {
        void onJogarClicado();
        void onConfiguracoesClicado();
        void onSobreClicado();
    }
    
    private FundoEstrelado fundoEstrelado;
    private Botao[] botoes;
    private Font fonteTitulo, fonteSlogan, fonteBotao, fonteSobreTitulo, fonteSobreTexto;
    private String titulo = "AKABANEH";
    private String slogan = "O ESPAÇO NUNCA AKABA";
    private boolean sobreAberto = false;
    private int larguraTela, alturaTela;
    private MenuListener listener;
    private List<String> sobreConteudo;
    private Botao botaoVoltarSobre;
    
    // Variáveis para scroll com controle
    private int scrollOffset = 0;
    private int maxScrollOffset = 0;
    private Rectangle areaConteudo;
    private Rectangle areaBarraScroll;
    private Rectangle areaThumbScroll;
    private boolean arrastandoThumb = false;
    private int offsetArrastoY = 0;
    
    // Classe interna para os botões
    private class Botao {
        String texto;
        Rectangle bounds;
        boolean mouseOver;
        boolean clicado;
        int arredondamento = 25;
        Runnable acao;
        
        Botao(String texto, int x, int y, int largura, int altura, Runnable acao) {
            this.texto = texto;
            this.bounds = new Rectangle(x, y, largura, altura);
            this.mouseOver = false;
            this.clicado = false;
            this.acao = acao;
        }
        
        void desenhar(Graphics2D g2d) {
            // Desenha fundo do botão com transparência
            Color corFundo = new Color(0, 0, 0, mouseOver ? 150 : 100);
            g2d.setColor(corFundo);
            g2d.fill(new RoundRectangle2D.Double(
                bounds.x, bounds.y, 
                bounds.width, bounds.height, 
                arredondamento, arredondamento
            ));
            
            // Borda do botão
            g2d.setColor(new Color(255, 255, 255, mouseOver ? 200 : 150));
            g2d.setStroke(new BasicStroke(2f));
            g2d.draw(new RoundRectangle2D.Double(
                bounds.x, bounds.y, 
                bounds.width, bounds.height, 
                arredondamento, arredondamento
            ));
            
            // Texto do botão
            g2d.setFont(fonteBotao);
            g2d.setColor(Color.WHITE);
            FontMetrics fm = g2d.getFontMetrics();
            int textoX = bounds.x + (bounds.width - fm.stringWidth(texto)) / 2;
            int textoY = bounds.y + (bounds.height + fm.getAscent()) / 2 - 5;
            g2d.drawString(texto, textoX, textoY);
            
            // Efeito de clique
            if (clicado) {
                g2d.setColor(new Color(255, 255, 255, 80));
                g2d.fill(new RoundRectangle2D.Double(
                    bounds.x, bounds.y, 
                    bounds.width, bounds.height, 
                    arredondamento, arredondamento
                ));
            }
        }
        
        boolean contemPonto(int x, int y) {
            return bounds.contains(x, y);
        }
    }
    
    public MenuInicial(int larguraTela, int alturaTela) {
        this.larguraTela = larguraTela;
        this.alturaTela = alturaTela;
        
        setLayout(null);
        setOpaque(false);
        setPreferredSize(new Dimension(larguraTela, alturaTela));
        
        // Inicializa o fundo estrelado
        fundoEstrelado = new FundoEstrelado(larguraTela, alturaTela, 100);
        
        // Carrega fontes
        try {
            fonteTitulo = new Font("Arial", Font.BOLD, 72);
            fonteSlogan = new Font("Arial", Font.ITALIC, 24);
            fonteBotao = new Font("Arial", Font.BOLD, 28);
            fonteSobreTitulo = new Font("Arial", Font.BOLD, 36);
            fonteSobreTexto = new Font("Arial", Font.PLAIN, 18);
        } catch (Exception e) {
            fonteTitulo = new Font("SansSerif", Font.BOLD, 72);
            fonteSlogan = new Font("SansSerif", Font.ITALIC, 24);
            fonteBotao = new Font("SansSerif", Font.BOLD, 28);
            fonteSobreTitulo = new Font("SansSerif", Font.BOLD, 36);
            fonteSobreTexto = new Font("SansSerif", Font.PLAIN, 18);
        }
        
        // Cria os botões
        int larguraBotao = 250;
        int alturaBotao = 60;
        int espacamento = 15;
        int centroX = larguraTela / 2 - larguraBotao / 2;
        int inicioY = alturaTela / 2 - 20;
        
        botoes = new Botao[3];
        botoes[0] = new Botao("JOGAR", centroX, inicioY, larguraBotao, alturaBotao, () -> {
            if (listener != null) listener.onJogarClicado();
        });
        
        botoes[1] = new Botao("CONFIGURAÇÕES", centroX, inicioY + alturaBotao + espacamento, 
                              larguraBotao, alturaBotao, () -> {
            if (listener != null) listener.onConfiguracoesClicado();
        });
        
        botoes[2] = new Botao("SOBRE", centroX, inicioY + 2 * (alturaBotao + espacamento), 
                               larguraBotao, alturaBotao, () -> {
            sobreAberto = true;
            scrollOffset = 0; // Reseta o scroll quando abre
            if (listener != null) listener.onSobreClicado();
        });
        
        // Botão para voltar do menu Sobre
        botaoVoltarSobre = new Botao("VOLTAR", larguraTela / 2 - 125, alturaTela - 100, 
                                     250, 60, () -> {
            sobreAberto = false;
            scrollOffset = 0; // Reseta o scroll ao voltar
        });
        
        // Inicializa conteúdo do Sobre
        inicializarSobreConteudo();
        
        // Inicializa áreas de scroll
        inicializarAreasScroll();
        
        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
    }
    
    private void inicializarSobreConteudo() {
        sobreConteudo = new ArrayList<>();
        
        // Seção COMO JOGAR
        sobreConteudo.add("=== COMO JOGAR ===");
        sobreConteudo.add("");
        sobreConteudo.add("• OBJETIVO:");
        sobreConteudo.add("  Desvie dos asteroides e sobreviva o máximo de tempo possível!");
        sobreConteudo.add("  Quanto mais tempo você sobreviver, maior será sua pontuação.");
        sobreConteudo.add("");
        sobreConteudo.add("• CONTROLES:");
        sobreConteudo.add("  - Teclas W/S ou SETAS CIMA/BAIXO: Movimentação vertical");
        sobreConteudo.add("  - Teclas A/D ou SETAS ESQUERDA/DIREITA: Movimentação horizontal");
        sobreConteudo.add("  - Tecla P: Pausar/continuar o jogo");
        sobreConteudo.add("  - Tecla ESC: Voltar ao menu principal");
        sobreConteudo.add("  - Mouse: Para interagir com menus");
        sobreConteudo.add("");
        sobreConteudo.add("• MECÂNICAS DO JOGO:");
        sobreConteudo.add("  - Você começa com 3 vidas");
        sobreConteudo.add("  - Cada colisão com asteroide reduz 1 vida");
        sobreConteudo.add("  - A velocidade aumenta gradualmente com o tempo");
        sobreConteudo.add("  - Asteroides vêm de todas as direções");
        sobreConteudo.add("  - Fique atento ao seu redor!");
        sobreConteudo.add("");
        
        // Seção DICAS
        sobreConteudo.add("=== DICAS E ESTRATÉGIAS ===");
        sobreConteudo.add("");
        sobreConteudo.add("• Mantenha-se no centro da tela para ter mais tempo de reação");
        sobreConteudo.add("• Não fique parado! Asteroides podem vir de qualquer direção");
        sobreConteudo.add("• Use movimentos suaves, não bruscos");
        sobreConteudo.add("• Foque em sobreviver, não em desviar de todos os asteroides");
        sobreConteudo.add("• Pratique para aprender os padrões de movimento");
        sobreConteudo.add("");
        
        // Seção SOBRE O DESENVOLVIMENTO
        sobreConteudo.add("=== SOBRE O DESENVOLVIMENTO ===");
        sobreConteudo.add("");
        sobreConteudo.add("• Projeto:");
        sobreConteudo.add("  AKABANEH é um projeto de faculdade desenvovlvido na Pontifícia Universidade Católica de Campinas (PUC-Campinas) na matéria de Programação Orintado Objetos do curso de Engenharia da Computação");
        sobreConteudo.add("  Desenvolvedores: Pedro Henrique Coan Zin e Felipe Ishizawa Diniz");
        sobreConteudo.add("  Orientador: Ademar Takeo Akabane");
        sobreConteudo.add("");
        sobreConteudo.add("• TECNOLOGIAS UTILIZADAS:");
        sobreConteudo.add("  - Linguagem: Java 8+");
        sobreConteudo.add("  - Interface: Java Swing");
        sobreConteudo.add("  - Paradigma: Programação Orientada a Objetos");
        sobreConteudo.add("  - Engine: Customizada");
        sobreConteudo.add("");
        sobreConteudo.add("• CARACTERÍSTICAS TÉCNICAS:");
        sobreConteudo.add("  - Sistema de colisão por bounding boxes");
        sobreConteudo.add("  - Fundo estrelado gerado proceduralmente");
        sobreConteudo.add("  - Sistema de dificuldade progressiva");
        sobreConteudo.add("  - Menus interativos com efeitos visuais");
        sobreConteudo.add("  - Animação suave a 60 FPS");
        sobreConteudo.add("  - Controles responsivos e precisos");
        sobreConteudo.add("");
        sobreConteudo.add("• ARQUITETURA DO PROJETO:");
        sobreConteudo.add("  - Design modular e expansível");
        sobreConteudo.add("  - Fácil adição de novos elementos de jogo");
        sobreConteudo.add("  - Sistema de estados para menus");
        sobreConteudo.add("");
        sobreConteudo.add("• PRINCÍPIOS DE DESENVOLVIMENTO:");
        sobreConteudo.add("  - Código limpo e bem documentado");
        sobreConteudo.add("  - Separação de responsabilidades");
        sobreConteudo.add("  - Reutilização de componentes");
        sobreConteudo.add("  - Facilidade de manutenção");
    }
    
    private void inicializarAreasScroll() {
        int margem = 50;
        int conteudoX = margem;
        int conteudoY = 130;
        int conteudoLargura = larguraTela - 2 * margem;
        int conteudoAltura = alturaTela - conteudoY - 150;
        
        areaConteudo = new Rectangle(conteudoX, conteudoY, conteudoLargura, conteudoAltura);
        
        // Barra de scroll na direita
        int barraLargura = 15;
        int barraX = conteudoX + conteudoLargura - barraLargura - 5;
        int barraY = conteudoY + 5;
        int barraAltura = conteudoAltura - 10;
        
        areaBarraScroll = new Rectangle(barraX, barraY, barraLargura, barraAltura);
        areaThumbScroll = new Rectangle(barraX, barraY, barraLargura, 50); // Inicial
    }
    
    private void atualizarThumbScroll() {
        if (maxScrollOffset <= 0) return;
        
        // Calcula tamanho do thumb (parte móvel)
        float proporcaoVisivel = (float)areaConteudo.height / (areaConteudo.height + maxScrollOffset);
        int thumbAltura = Math.max(40, (int)(areaBarraScroll.height * proporcaoVisivel));
        
        // Calcula posição do thumb baseada no scrollOffset
        float proporcaoScroll = maxScrollOffset > 0 ? (float)scrollOffset / maxScrollOffset : 0;
        int thumbY = areaBarraScroll.y + (int)((areaBarraScroll.height - thumbAltura) * proporcaoScroll);
        
        areaThumbScroll.setBounds(areaBarraScroll.x, thumbY, areaBarraScroll.width, thumbAltura);
    }
    
    private void atualizarScrollPorPosicaoThumb(int mouseY) {
        int thumbY = mouseY - offsetArrastoY;
        
        // Limita o thumb dentro da barra de scroll
        thumbY = Math.max(areaBarraScroll.y, 
                  Math.min(thumbY, areaBarraScroll.y + areaBarraScroll.height - areaThumbScroll.height));
        
        // Atualiza posição do thumb
        areaThumbScroll.y = thumbY;
        
        // Calcula o scrollOffset correspondente
        float proporcao = (float)(thumbY - areaBarraScroll.y) / 
                         (areaBarraScroll.height - areaThumbScroll.height);
        scrollOffset = (int)(proporcao * maxScrollOffset);
        
        repaint();
    }
    
    private void atualizarScrollPorCliqueBarra(int mouseY) {
        // Clicou na barra, mas fora do thumb - move o thumb para a posição do clique
        if (mouseY < areaThumbScroll.y) {
            // Clicou acima do thumb - move para cima
            scrollOffset = Math.max(0, scrollOffset - areaConteudo.height);
        } else {
            // Clicou abaixo do thumb - move para baixo
            scrollOffset = Math.min(maxScrollOffset, scrollOffset + areaConteudo.height);
        }
        atualizarThumbScroll();
        repaint();
    }
    
    public void setMenuListener(MenuListener listener) {
        this.listener = listener;
    }
    
    public void atualizar() {
        fundoEstrelado.atualizar(1);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        // Desenha o fundo estrelado
        fundoEstrelado.desenhar(g2d);
        
        if (!sobreAberto) {
            // Desenha o menu principal
            desenharMenuPrincipal(g2d);
        } else {
            // Desenha o menu Sobre (tela inteira)
            desenharMenuSobre(g2d);
        }
    }
    
    private void desenharMenuPrincipal(Graphics2D g2d) {
        // Desenha o título do jogo
        g2d.setFont(fonteTitulo);
        g2d.setColor(Color.WHITE);
        FontMetrics fmTitulo = g2d.getFontMetrics();
        int tituloX = (larguraTela - fmTitulo.stringWidth(titulo)) / 2;
        int tituloY = alturaTela / 3 - 30;
        
        // Efeito de sombra no título
        g2d.setColor(new Color(0, 100, 255, 100));
        g2d.drawString(titulo, tituloX + 3, tituloY + 3);
        g2d.setColor(Color.WHITE);
        g2d.drawString(titulo, tituloX, tituloY);
        
        // Desenha o slogan
        g2d.setFont(fonteSlogan);
        g2d.setColor(new Color(200, 200, 255));
        FontMetrics fmSlogan = g2d.getFontMetrics();
        int sloganX = (larguraTela - fmSlogan.stringWidth(slogan)) / 2;
        int sloganY = tituloY + 50;
        g2d.drawString(slogan, sloganX, sloganY);
        
        // Desenha os botões
        for (Botao botao : botoes) {
            botao.desenhar(g2d);
        }
    }
    
    private void desenharMenuSobre(Graphics2D g2d) {
        // Fundo semi-transparente
        g2d.setColor(new Color(0, 0, 0, 220));
        g2d.fillRect(0, 0, larguraTela, alturaTela);
        
        // Título do menu Sobre
        g2d.setFont(fonteSobreTitulo);
        g2d.setColor(new Color(100, 150, 255));
        String tituloSobre = "SOBRE O JOGO";
        FontMetrics fmTitulo = g2d.getFontMetrics();
        int tituloX = (larguraTela - fmTitulo.stringWidth(tituloSobre)) / 2;
        int tituloY = 80;
        g2d.drawString(tituloSobre, tituloX, tituloY);
        
        // Linha decorativa abaixo do título
        g2d.setColor(new Color(100, 150, 255, 150));
        g2d.setStroke(new BasicStroke(2f));
        g2d.drawLine(tituloX, tituloY + 10, tituloX + fmTitulo.stringWidth(tituloSobre), tituloY + 10);
        
        // Atualiza áreas de scroll
        inicializarAreasScroll();
        
        // Fundo do conteúdo
        g2d.setColor(new Color(20, 20, 40, 200));
        g2d.fillRoundRect(areaConteudo.x, areaConteudo.y, 
                         areaConteudo.width, areaConteudo.height, 20, 20);
        
        // Borda do conteúdo
        g2d.setColor(new Color(100, 150, 255, 100));
        g2d.setStroke(new BasicStroke(2f));
        g2d.drawRoundRect(areaConteudo.x, areaConteudo.y, 
                         areaConteudo.width, areaConteudo.height, 20, 20);
        
        // Cria um clipping para a área de conteúdo
        Shape clipOriginal = g2d.getClip();
        g2d.clipRect(areaConteudo.x, areaConteudo.y, 
                    areaConteudo.width, areaConteudo.height);
        
        // Calcula altura total do conteúdo
        int linhaAltura = 28;
        int alturaTotal = calcularAlturaTotalConteudo(linhaAltura);
        
        // Atualiza o scroll máximo
        maxScrollOffset = Math.max(0, alturaTotal - areaConteudo.height + 40);
        scrollOffset = Math.max(0, Math.min(scrollOffset, maxScrollOffset));
        
        // Atualiza a posição do thumb
        atualizarThumbScroll();
        
        // Desenha o conteúdo com scroll
        int yOffset = areaConteudo.y + 20 - scrollOffset;
        desenharConteudoComScroll(g2d, areaConteudo.x + 20, yOffset, linhaAltura);
        
        // Restaura o clipping
        g2d.setClip(clipOriginal);
        
        // Desenha a barra de scroll se necessário
        if (maxScrollOffset > 0) {
            desenharBarraScroll(g2d);
        }
        
        // Botão Voltar
        botaoVoltarSobre.desenhar(g2d);
        
        // Instruções de scroll
        if (maxScrollOffset > 0) {
            g2d.setFont(new Font("Arial", Font.ITALIC, 14));
            g2d.setColor(new Color(150, 150, 200, 180));
            String instrucao = "Use a barra de rolagem à direita ou a roda do mouse";
            FontMetrics fmInstrucao = g2d.getFontMetrics();
            int instrucaoX = (larguraTela - fmInstrucao.stringWidth(instrucao)) / 2;
            int instrucaoY = areaConteudo.y + areaConteudo.height + 25;
            g2d.drawString(instrucao, instrucaoX, instrucaoY);
        }
        
        // Créditos no rodapé
        g2d.setFont(new Font("Arial", Font.ITALIC, 14));
        g2d.setColor(new Color(150, 150, 200));
        String creditos = "Desenvolvido com Java Swing | AKABANE © 2024";
        FontMetrics fmCreditos = g2d.getFontMetrics();
        int creditosX = (larguraTela - fmCreditos.stringWidth(creditos)) / 2;
        int creditosY = alturaTela - 30;
        g2d.drawString(creditos, creditosX, creditosY);
    }
    
    private int calcularAlturaTotalConteudo(int linhaAltura) {
        int alturaTotal = 40; // Margem superior e inferior
        
        for (String linha : sobreConteudo) {
            if (linha.length() > 60) {
                String[] partes = dividirLinha(linha, 60);
                alturaTotal += partes.length * linhaAltura;
            } else {
                alturaTotal += linhaAltura;
            }
        }
        
        return alturaTotal;
    }
    
    private void desenharConteudoComScroll(Graphics2D g2d, int x, int y, int linhaAltura) {
        g2d.setFont(fonteSobreTexto);
        
        for (String linha : sobreConteudo) {
            // Destaca os títulos das seções
            if (linha.startsWith("===")) {
                g2d.setColor(new Color(100, 200, 255));
                g2d.setFont(new Font(fonteSobreTexto.getFontName(), Font.BOLD, 20));
            } else if (linha.startsWith("•") && !linha.startsWith("• ")) {
                g2d.setColor(new Color(180, 220, 255));
                g2d.setFont(new Font(fonteSobreTexto.getFontName(), Font.BOLD, fonteSobreTexto.getSize()));
            } else {
                g2d.setColor(new Color(220, 220, 255));
                g2d.setFont(fonteSobreTexto);
            }
            
            // Quebra linhas longas
            if (linha.length() > 60) {
                String[] partes = dividirLinha(linha, 60);
                for (String parte : partes) {
                    g2d.drawString(parte, x, y);
                    y += linhaAltura;
                }
            } else {
                g2d.drawString(linha, x, y);
                y += linhaAltura;
            }
        }
    }
    
    private void desenharBarraScroll(Graphics2D g2d) {
        // Fundo da barra de scroll
        g2d.setColor(new Color(50, 50, 80, 150));
        g2d.fillRoundRect(areaBarraScroll.x, areaBarraScroll.y, 
                         areaBarraScroll.width, areaBarraScroll.height, 7, 7);
        
        // Borda da barra
        g2d.setColor(new Color(80, 80, 120, 200));
        g2d.setStroke(new BasicStroke(1f));
        g2d.drawRoundRect(areaBarraScroll.x, areaBarraScroll.y, 
                         areaBarraScroll.width, areaBarraScroll.height, 7, 7);
        
        // Thumb (parte móvel) - com efeito de hover/clique
        Color corThumb = arrastandoThumb ? 
            new Color(120, 170, 255, 220) : 
            areaThumbScroll.contains(getMousePosition()) ? 
            new Color(100, 150, 255, 200) : 
            new Color(80, 120, 220, 180);
        
        g2d.setColor(corThumb);
        g2d.fillRoundRect(areaThumbScroll.x, areaThumbScroll.y, 
                         areaThumbScroll.width, areaThumbScroll.height, 5, 5);
        
        // Borda do thumb
        g2d.setColor(new Color(140, 190, 255));
        g2d.setStroke(new BasicStroke(1f));
        g2d.drawRoundRect(areaThumbScroll.x, areaThumbScroll.y, 
                         areaThumbScroll.width, areaThumbScroll.height, 5, 5);
        
        // Linhas no thumb (opcional, para melhor visibilidade)
        g2d.setColor(new Color(200, 220, 255, 100));
        int centroX = areaThumbScroll.x + areaThumbScroll.width / 2;
        int espacoLinhas = areaThumbScroll.height / 4;
        
        for (int i = 1; i <= 3; i++) {
            int linhaY = areaThumbScroll.y + i * espacoLinhas;
            g2d.drawLine(centroX - 3, linhaY, centroX + 3, linhaY);
        }
    }
    
    private String[] dividirLinha(String linha, int comprimentoMax) {
        List<String> partes = new ArrayList<>();
        String[] palavras = linha.split(" ");
        StringBuilder atual = new StringBuilder();
        
        for (String palavra : palavras) {
            if (atual.length() + palavra.length() + 1 > comprimentoMax) {
                partes.add(atual.toString());
                atual = new StringBuilder(palavra);
            } else {
                if (atual.length() > 0) {
                    atual.append(" ");
                }
                atual.append(palavra);
            }
        }
        
        if (atual.length() > 0) {
            partes.add(atual.toString());
        }
        
        return partes.toArray(new String[0]);
    }
    
    // Métodos para manipulação do scroll
    private void atualizarScroll(int delta) {
        int novoScroll = scrollOffset + delta;
        scrollOffset = Math.max(0, Math.min(novoScroll, maxScrollOffset));
        atualizarThumbScroll();
        repaint();
    }
    
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (sobreAberto && areaConteudo.contains(e.getPoint())) {
            int unidades = e.getUnitsToScroll();
            atualizarScroll(unidades * 30); // Ajusta a sensibilidade do scroll
        }
    }
    
    @Override
    public void mouseClicked(MouseEvent e) {
        if (!sobreAberto) {
            // Verifica clique nos botões do menu principal
            for (Botao botao : botoes) {
                if (botao.contemPonto(e.getX(), e.getY())) {
                    botao.clicado = true;
                    repaint();
                    
                    Timer timer = new Timer(200, ev -> {
                        botao.clicado = false;
                        botao.acao.run();
                        repaint();
                    });
                    timer.setRepeats(false);
                    timer.start();
                    break;
                }
            }
        }
    }
    
    @Override
    public void mousePressed(MouseEvent e) {
        if (sobreAberto) {
            // Verifica clique no botão Voltar
            if (botaoVoltarSobre.contemPonto(e.getX(), e.getY())) {
                botaoVoltarSobre.clicado = true;
                repaint();
                
                Timer timer = new Timer(200, ev -> {
                    botaoVoltarSobre.clicado = false;
                    botaoVoltarSobre.acao.run();
                    repaint();
                });
                timer.setRepeats(false);
                timer.start();
                return;
            }
            
            // Verifica clique na barra de scroll
            if (maxScrollOffset > 0) {
                if (areaThumbScroll.contains(e.getPoint())) {
                    // Clicou no thumb - inicia arrasto
                    arrastandoThumb = true;
                    offsetArrastoY = e.getY() - areaThumbScroll.y;
                } else if (areaBarraScroll.contains(e.getPoint())) {
                    // Clicou na barra, mas fora do thumb
                    atualizarScrollPorCliqueBarra(e.getY());
                }
            }
        }
    }
    
    @Override
    public void mouseReleased(MouseEvent e) {
        arrastandoThumb = false;
    }
    
    @Override
    public void mouseEntered(MouseEvent e) {}
    
    @Override
    public void mouseExited(MouseEvent e) {
        if (sobreAberto) {
            botaoVoltarSobre.mouseOver = false;
        } else {
            for (Botao botao : botoes) {
                botao.mouseOver = false;
            }
        }
        repaint();
    }
    
    @Override
    public void mouseMoved(MouseEvent e) {
        if (sobreAberto) {
            botaoVoltarSobre.mouseOver = botaoVoltarSobre.contemPonto(e.getX(), e.getY());
        } else {
            for (Botao botao : botoes) {
                botao.mouseOver = botao.contemPonto(e.getX(), e.getY());
            }
        }
        repaint();
    }
    
    @Override
    public void mouseDragged(MouseEvent e) {
        if (sobreAberto && arrastandoThumb) {
            atualizarScrollPorPosicaoThumb(e.getY());
        }
    }
}