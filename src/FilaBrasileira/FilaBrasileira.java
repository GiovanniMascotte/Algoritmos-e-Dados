package FilaBrasileira;

import java.util.LinkedList;
import java.util.List;

public class FilaBrasileira {
    private String id;
    private LinkedList<String> pessoasNaFila;

    public FilaBrasileira(String id) {
        this.id = id;
        this.pessoasNaFila = new LinkedList<>();
    }

    public String getId() {
        return id;
    }

    public List<String> getPessoasNaFila() {
        return pessoasNaFila;
    }

    /**
     * Adiciona uma pessoa ao final da fila.
     * @param nome O nome da pessoa a ser adicionada.
     */
    public void adicionarPessoaNoFinal(String nome) {
        pessoasNaFila.addLast(nome);
    }

    /**
     * Adiciona uma pessoa após uma pessoa conhecida na fila.
     * @param novaPessoa O nome da nova pessoa.
     * @param pessoaConhecida O nome da pessoa já na fila que a nova pessoa conhece.
     * @return true se a pessoa foi adicionada, false caso contrário (pessoa conhecida não encontrada).
     */
    public boolean adicionarPessoaAposConhecido(String novaPessoa, String pessoaConhecida) {
        int index = pessoasNaFila.indexOf(pessoaConhecida);
        if (index != -1) {
            pessoasNaFila.add(index + 1, novaPessoa);
            return true;
        }
        return false;
    }

    /**
     * Remove a primeira pessoa da fila.
     * @return O nome da pessoa removida, ou null se a fila estiver vazia.
     */
    public String atenderProximo() {
        if (!pessoasNaFila.isEmpty()) {
            return pessoasNaFila.removeFirst();
        }
        return null;
    }

    /**
     * Remove uma pessoa específica da fila.
     * @param nome O nome da pessoa a ser removida.
     * @return true se a pessoa foi removida, false caso contrário.
     */
    public boolean removerPessoa(String nome) {
        return pessoasNaFila.remove(nome);
    }

    /**
     * Verifica se a fila está vazia.
     * @return true se a fila estiver vazia, false caso contrário.
     */
    public boolean estaVazia() {
        return pessoasNaFila.isEmpty();
    }

    /**
     * Retorna a representação formatada da fila.
     * Ex: #Guiche1 [ Guga, Maria, Otavio ]
     * @return String formatada da fila.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("#").append(id).append(" [");
        for (int i = 0; i < pessoasNaFila.size(); i++) {
            sb.append(pessoasNaFila.get(i));
            if (i < pessoasNaFila.size() - 1) {
                sb.append(" "); // Espaço entre os nomes
            }
        }
        sb.append("]");
        return sb.toString();
    }
}