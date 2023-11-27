package ifpr.paranavai.jogo.modelo;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.swing.ImageIcon;
import javax.swing.Timer;

import ifpr.paranavai.jogo.visao.Principal;
import ifpr.paranavai.jogo.controle.FaseControle;

@Entity
@Table(name = "tb_faseUm")

public class FaseUm extends Fase {

    @OneToOne
    @JoinColumn(name = "fk_fase")
    private Fase fase;

    @Transient
    private static final int DELAY = 5;

    @Transient
    private static final int QTDE_DE_INIMIGOS = 40;

    @Transient
    private static final int QTDE_DE_ASTEROIDES = 50;

    @Transient
    private static final int PONTOS_POR_INIMIGO = 10;

    @Transient
    private int contador = 0;
    
    //private PersonagemDAO personagemDAO = new PersonagemDAO();
    private FaseControle controle = new FaseControle();
    private PersonagemDAOImplement personagemDAOImplement = new PersonagemDAOImplement();

    public FaseUm() { // Linha adicionada (+)
        super(); // Chamada do construtor da classe super
        this.emJogo = true;
        ImageIcon carregando = new ImageIcon(getClass().getResource("/fundo.jpg"));
        this.fundo = carregando.getImage();

        this.personagem = new Personagem(); // + Criação do objeto Personagem

        this.inicializaElementosGraficosAdicionais();

        this.inimigos = controle.inicializaInimigos(QTDE_DE_INIMIGOS);

        this.timer = new Timer(DELAY, this); // + Criação do objeto Timer
        this.timer.start(); // + Iniciando o nosso jogo

    }

    // @Override
    // public void inicializaInimigos() {
    //     inimigos = new ArrayList<Inimigo>();

    //     for (int i = 0; i < QTDE_DE_INIMIGOS; i++) {
    //         int x = (int) ((Math.random() * 8000) + Principal.LARGURA_DA_JANELA);
    //         int y = (int) (Math.random() * Principal.ALTURA_DA_JANELA);
    //         Inimigo inimigo = new Inimigo(x, y);
    //         inimigos.add(inimigo);
    //     }
    // }

    @Override
    public void paint(Graphics g) {
        Graphics2D graficos = (Graphics2D) g;
        if (emJogo) {
            graficos.drawImage(fundo, 0, 0, null);
            
            if (contador >= 400) {
                super.desenhaCdSuper(graficos);
            }
            
            
            // Criando um laço de repetição (foreach). Iremos percorrer toda a lista.
            for (Asteroide asteroide : asteroides) {
                // Desenhar o asteroide na nossa tela.
                graficos.drawImage(asteroide.getImagem(), asteroide.getPosicaoEmX(), asteroide.getPosicaoEmY(), this);
            }
            
            graficos.drawImage(personagem.getImagem(), personagem.getPosicaoEmX(), personagem.getPosicaoEmY(), this);
            
            // Recuperar a nossa lista de tiros (getTiros) e atribuímos para uma variável
            // local chamada tiros.
            List<Tiro> tiros = personagem.getTiros();
            
            // Criando um laço de repetição (foreach). Iremos percorrer toda a lista.
            for (Tiro tiro : tiros) {
                // Desenhar o tiro na nossa tela.
                graficos.drawImage(tiro.getImagem(), tiro.getPosicaoEmX(), tiro.getPosicaoEmY(), this);
            }
            
            List<TiroSuper> tirosSuper = personagem.getSuperTiros();
            
            for (TiroSuper tiroSuper : tirosSuper) {
                // Desenhar o tiro na nossa tela.
                graficos.drawImage(tiroSuper.getImagem(), tiroSuper.getPosicaoEmX(), tiroSuper.getPosicaoEmY(), this);
            }
            
            // Criando um laço de repetição (foreach). Iremos percorrer toda a lista.
            for (Inimigo inimigo : inimigos) {
                // Desenhar o inimigo na nossa tela.
                graficos.drawImage(inimigo.getImagem(), inimigo.getPosicaoEmX(), inimigo.getPosicaoEmY(), this);
            }
            super.desenhaPontuacao(graficos);
            super.desenhaVidas(graficos);        
        } else {
            ImageIcon fimDeJogo = new ImageIcon(getClass().getResource("/fimdejogo.jpg"));
            graficos.drawImage(fimDeJogo.getImage(), 0, 0, this);
        }
        this.verificarColisoes();
        g.dispose();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            personagem.atirar();
        } else if (e.getKeyCode() == KeyEvent.VK_R && contador >= 400) {
            personagem.superTiro();
            contador = 0;
        } else if (e.getKeyCode() == KeyEvent.VK_Z) {
            salvarJogo();    
        } else {
            personagem.mover(e);
        }
    }

    private void salvarJogo() {
        // Chame o método inserir do personagemDAOImplement
        personagemDAOImplement.inserir(personagem);
        System.out.println("Jogo salvo com sucesso!");
    }

    @Override
    public void keyReleased(KeyEvent e) {
        personagem.parar(e);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        contador++;
        personagem.atualizar();

        // Criando um laço de repetição (foreach). Iremos percorrer toda a lista.
        for (Asteroide asteroide : this.asteroides) {
            asteroide.atualizar();
        }

        // Recuperar a nossa lista de tiros (getTiros) e atribuímos para uma variável
        // local chamada tiros.
        List<Tiro> tiros = personagem.getTiros();

        // Criando um laço de repetição (for). Iremos percorrer toda a lista.
        for (int i = 0; i < tiros.size(); i++) {
            // Obter o objeto tiro da posicao i do ArrayList
            Tiro tiro = tiros.get(i);
            // Verificar se (if) a posição do x (tiro.getPosicaoEmX()) é maior do que a
            // largura da nossa janela
            if (tiro.getPosicaoEmX() > Principal.LARGURA_DA_JANELA || !tiro.getEhVisivel())
                // Remover da lista se estiver fora do campo de visão (LARGURA_DA_JANELA)
                tiros.remove(tiro);
            else
                // Atualizar a posição do tiro.
                tiro.atualizar();
        }

        List<TiroSuper> tirosSuper = personagem.getSuperTiros();

        // Criando um laço de repetição (for). Iremos percorrer toda a lista.
        for (int i = 0; i < tirosSuper.size(); i++) {
            // Obter o objeto tiro da posicao i do ArrayList
            TiroSuper tiroSuper = tirosSuper.get(i);
            // Verificar se (if) a posição do x (tiro.getPosicaoEmX()) é maior do que a
            // largura da nossa janela
            if (tiroSuper.getPosicaoEmX() > Principal.LARGURA_DA_JANELA || !tiroSuper.getEhVisivel())
                // Remover da lista se estiver fora do campo de visão (LARGURA_DA_JANELA)
                tirosSuper.remove(tiroSuper);
            else
                // Atualizar a posição do tiro.
                tiroSuper.atualizar();
        }

        // Criando um laço de repetição (for). Iremos percorrer toda a lista.
        for (int i = 0; i < this.inimigos.size(); i++) {
            // Obter o objeto inimigo da posicao i do ArrayList
            Inimigo inimigo = this.inimigos.get(i);
            // Verificar se (if) a posição do x (inimigo.getPosicaoEmX()) é maior do que a
            // largura da nossa janela
            if (inimigo.getPosicaoEmX() < 0 || !inimigo.getEhVisivel())
                // Remover da lista se estiver fora do campo de visão (0)
                inimigos.remove(inimigo);
            else
                // Atualizar a posição do inimigo.
                inimigo.atualizar();
        }
        //this.verificarColisoes();
        repaint();
    }

   /*  private void salvarPersonagemNoBancoDeDados() {
        try {
            personagemDAO.salvarPersonagem(personagem);
            System.out.println("Personagem salvo no banco de dados com sucesso!");
        } catch (Exception ex) {
            System.err.println("Erro ao salvar o personagem no banco de dados: " + ex.getMessage());
        } finally {
            personagemDAO.fecharConexao();
        }
    }
*/

    @Override
    public void verificarColisoes() {
        Rectangle formaPersonagem = this.personagem.getRectangle();

        for (int i = 0; i < this.inimigos.size(); i++) {
            Inimigo inimigo = inimigos.get(i);
            Rectangle formaInimigo = inimigo.getRectangle();
            
            if (formaInimigo.intersects(formaPersonagem)) {
                personagem.perderVidas();
                inimigo.setEhVisivel(false);

                if (personagem.getVidas() <= 0) {
                emJogo = false;
                this.personagem.setEhVisivel(false);
                inimigo.setEhVisivel(false);
            }
            
            List<Tiro> tiros = this.personagem.getTiros();
            
            for (int j = 0; j < tiros.size(); j++) {
                Tiro tiro = tiros.get(j);
                Rectangle formaTiro = tiro.getRectangle();

                if (formaInimigo.intersects(formaTiro)) {
                    int pontuacaoAtual = this.personagem.getPontuacao();
                    this.personagem.setPontuacao(pontuacaoAtual + PONTOS_POR_INIMIGO);
                    inimigo.setEhVisivel(false);
                    tiro.setEhVisivel(false);
                }
            }
            List<TiroSuper> tirosSuper = personagem.getSuperTiros();

            for (int a = 0; a < tirosSuper.size(); a++) {
                TiroSuper tiroSuper = tirosSuper.get(a);
                Rectangle formaTiroSuper = tiroSuper.getRectangle();

                if (formaInimigo.intersects(formaTiroSuper)) {
                    int pontuacaoAtual = this.personagem.getPontuacao();
                    this.personagem.setPontuacao(pontuacaoAtual + PONTOS_POR_INIMIGO);
                    inimigo.setEhVisivel(false);
                    tiroSuper.setEhVisivel(false);
                    }
                }
            
            }
        }
    }

    @Override
    public void inicializaElementosGraficosAdicionais() {
        super.asteroides = new ArrayList<Asteroide>();

        for (int i = 0; i < QTDE_DE_ASTEROIDES; i++) {
            int x = (int) (Math.random() * Principal.LARGURA_DA_JANELA);
            int y = (int) (Math.random() * Principal.ALTURA_DA_JANELA);
            Asteroide asteroide = new Asteroide(x, y);
            super.asteroides.add(asteroide);
        }
    }

    @Override
    public void inicializaInimigos() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'inicializaInimigos'");
    }
}
