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

    public void adicionarPessoaNoFinal(String nome) {
        pessoasNaFila.addLast(nome);
    }

    public boolean adicionarPessoaAposConhecido(String novaPessoa, String pessoaConhecida) {
        int index = pessoasNaFila.indexOf(pessoaConhecida);
        if (index != -1) {
            pessoasNaFila.add(index + 1, novaPessoa);
            return true;
        }
        return false;
    }

    public String atenderProximo() {
        if (!pessoasNaFila.isEmpty()) {
            return pessoasNaFila.removeFirst();
        }
        return null;
    }

    public boolean removerPessoa(String nome) {
        return pessoasNaFila.remove(nome);
    }

    public boolean estaVazia() {
        return pessoasNaFila.isEmpty();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("#").append(id).append(" [");
        for (int i = 0; i < pessoasNaFila.size(); i++) {
            sb.append(pessoasNaFila.get(i));
            if (i < pessoasNaFila.size() - 1) {
                sb.append(" ");
            }
        }
        sb.append("]");
        return sb.toString();
    }
}
