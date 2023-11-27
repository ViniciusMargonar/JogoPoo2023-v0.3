package ifpr.paranavai.jogo.controle;

import java.util.List;
import ifpr.paranavai.jogo.modelo.Inimigo;
import java.util.ArrayList;
import ifpr.paranavai.jogo.visao.Principal;

public class FaseControle {
    
    public List<Inimigo> inicializaInimigos(int QTDE_DE_INIMIGOS) {
        List<Inimigo> inimigos = new ArrayList<Inimigo>();

        for (int i = 0; i < QTDE_DE_INIMIGOS; i++) {
            int x = (int) ((Math.random() * 8000) + Principal.LARGURA_DA_JANELA);
            int y = (int) (Math.random() * Principal.ALTURA_DA_JANELA);
            Inimigo inimigo = new Inimigo(x, y);
            inimigos.add(inimigo);
        }
        return inimigos;
    }
}
