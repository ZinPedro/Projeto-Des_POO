package src;
import javax.swing.*;
import java.awt.*;

public class JogoTravessia extends JFrame {
    
    private CardLayout cardLayout;
    private JPanel painelPrincipal;
    private MenuInicial menuInicial;
    private PainelJogo painelJogo;
    private Timer timerMenu;

    public JogoTravessia() {
        setTitle("AKABANE - O Espaço Nunca Akaba");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        cardLayout = new CardLayout();
        painelPrincipal = new JPanel(cardLayout);
        
        menuInicial = new MenuInicial(800, 600);
        painelJogo = new PainelJogo();
        
        painelPrincipal.add(menuInicial, "MENU");
        painelPrincipal.add(painelJogo, "JOGO");
        
        add(painelPrincipal);
        
        timerMenu = new Timer(16, e -> {
            menuInicial.atualizar();
            menuInicial.repaint();
        });
        timerMenu.start();
        
        configurarListeners();
        
        setVisible(true);
    }
    
    private void configurarListeners() {
        menuInicial.setMenuListener(new MenuInicial.MenuListener() {
            @Override
            public void onJogarClicado() {
                iniciarJogo();
            }
            
            @Override
            public void onConfiguracoesClicado() {
                mostrarConfiguracoes();
            }
            
            @Override
            public void onSobreClicado() {
                // O painel de sobre já é gerenciado pelo MenuInicial
            }
        });
        
        painelJogo.setJogoListener(new PainelJogo.JogoListener() {
            @Override
            public void voltarAoMenu() {
                voltarMenu();
            }
        });
    }
    
    private void iniciarJogo() {
        timerMenu.stop();
        painelJogo.resetarJogo();
        cardLayout.show(painelPrincipal, "JOGO");
        painelJogo.requestFocusInWindow();
    }
    
    private void voltarMenu() {
        cardLayout.show(painelPrincipal, "MENU");
        timerMenu.start();
        menuInicial.requestFocusInWindow();
    }
    
    private void mostrarConfiguracoes() {
        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.setBackground(new Color(30, 30, 50));
        
        JLabel titulo = new JLabel("CONFIGURAÇÕES");
        titulo.setFont(new Font("Arial", Font.BOLD, 24));
        titulo.setForeground(Color.WHITE);
        titulo.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(titulo);
        
        panel.add(new JLabel(" ")); // Espaço
        
        JLabel info = new JLabel("Configurações em desenvolvimento");
        info.setFont(new Font("Arial", Font.PLAIN, 16));
        info.setForeground(Color.LIGHT_GRAY);
        info.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(info);
        
        panel.add(new JLabel(" ")); // Espaço
        
        JButton voltar = new JButton("Voltar");
        voltar.setFont(new Font("Arial", Font.BOLD, 16));
        voltar.setBackground(new Color(70, 70, 120));
        voltar.setForeground(Color.WHITE);
        voltar.setFocusPainted(false);
        voltar.addActionListener(e -> {
            Window window = SwingUtilities.getWindowAncestor(panel);
            if (window != null) {
                window.dispose();
            }
        });
        panel.add(voltar);
        
        JOptionPane.showMessageDialog(this, panel, "Configurações", 
            JOptionPane.PLAIN_MESSAGE, null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new JogoTravessia());
    }
}