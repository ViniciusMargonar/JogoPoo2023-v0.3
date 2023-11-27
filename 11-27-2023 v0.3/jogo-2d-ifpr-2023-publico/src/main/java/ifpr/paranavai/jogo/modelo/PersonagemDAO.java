package ifpr.paranavai.jogo.modelo;

import java.util.List;

public interface PersonagemDAO {
    public List<Personagem> buscarTodos();

    public Personagem buscarPorId(Integer id);

    public void atualizar(Personagem personagem);

    public void excluir(Personagem personagem);

    public void inserir(Personagem personagem);
}
