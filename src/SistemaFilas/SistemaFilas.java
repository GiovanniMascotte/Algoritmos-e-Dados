package SistemaFilas;

import FilaBrasileira.FilaBrasileira;
import PessoasConhecemPessoas.PessoasConhecemPessoas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SistemaFilas {
    private Map<String, FilaBrasileira> filas;
    private PessoasConhecemPessoas sistemaPessoas; // Para verificar quem conhece quem

    public SistemaFilas(PessoasConhecemPessoas sistemaPessoas) {
        this.filas = new HashMap<>();
        this.sistemaPessoas = sistemaPessoas;
    }

    /**
     * Cria uma nova fila (guichê) com o ID especificado.
     * @param id O identificador da fila.
     * @return true se a fila foi criada, false se já existia uma fila com o mesmo ID.
     */
    public boolean criaFila(String id) {
        if (filas.containsKey(id)) {
            return false; // Fila já existe
        }
        filas.put(id, new FilaBrasileira(id));
        return true;
    }

    /**
     * Atende a próxima pessoa em uma fila.
     * @param idFila O ID da fila a ser atendida.
     * @return O nome da pessoa atendida, ou null se a fila não existe ou está vazia.
     */
    public String atendeFila(String idFila) {
        FilaBrasileira fila = filas.get(idFila);
        if (fila != null) {
            return fila.atenderProximo();
        }
        return null; // Fila não encontrada
    }

    /**
     * Adiciona uma pessoa à fila seguindo a lógica da fila brasileira.
     * Procura a melhor posição (mais próxima do início) onde a pessoa pode se "encostar" em um conhecido.
     * Se não conhecer ninguém, é adicionada ao final da fila mais curta.
     *
     * @param nomePessoa O nome da pessoa que chegou.
     */
    public void chegou(String nomePessoa) {
        String melhorFilaId = null;
        int melhorPosicao = Integer.MAX_VALUE; // Posição mais próxima do início da fila
        String pessoaConhecidaNaMelhorPosicao = null;

        // 1. Tenta encontrar a melhor posição em alguma fila (onde a pessoa pode se "encostar")
        for (FilaBrasileira fila : filas.values()) {
            List<String> pessoasAtualNaFila = fila.getPessoasNaFila();
            for (int i = 0; i < pessoasAtualNaFila.size(); i++) {
                String pessoaNaFila = pessoasAtualNaFila.get(i);
                // Verifica se a nova pessoa conhece alguém já na fila
                if (sistemaPessoas.pessoasSeConhecem(nomePessoa, pessoaNaFila)) {
                    // +1 porque a inserção é APÓS o conhecido
                    if ((i + 1) < melhorPosicao) {
                        melhorPosicao = i + 1;
                        melhorFilaId = fila.getId();
                        pessoaConhecidaNaMelhorPosicao = pessoaNaFila;
                    }
                }
            }
        }

        // 2. Se encontrou uma posição com conhecido, insere lá
        if (melhorFilaId != null) {
            FilaBrasileira filaParaInserir = filas.get(melhorFilaId);
            filaParaInserir.adicionarPessoaAposConhecido(nomePessoa, pessoaConhecidaNaMelhorPosicao);
            return;
        }

        // 3. Se não conhece ninguém em nenhuma fila, adiciona no final da fila mais curta
        String filaMaisCurtaId = null;
        int menorTamanhoFila = Integer.MAX_VALUE;

        // Se não houver filas, a pessoa não pode ser adicionada
        if (filas.isEmpty()) {
            System.out.println("Aviso: Nenhuma fila disponível para '" + nomePessoa + "' chegar.");
            return;
        }

        for (FilaBrasileira fila : filas.values()) {
            if (fila.getPessoasNaFila().size() < menorTamanhoFila) {
                menorTamanhoFila = fila.getPessoasNaFila().size();
                filaMaisCurtaId = fila.getId();
            }
        }

        if (filaMaisCurtaId != null) {
            filas.get(filaMaisCurtaId).adicionarPessoaNoFinal(nomePessoa);
        } else {
            // Caso raro onde não há filas criadas, mas o loop anterior deveria ter capturado isso.
            // Poderia ocorrer se todas as filas estivessem vazias e o menorTamanhoFila continuasse MAX_VALUE.
            // Para robustez, vamos adicionar ao final da primeira fila disponível se não houver conhecido e nenhuma fila for "mais curta" (todas vazias ou com mesmo tamanho)
            if (!filas.isEmpty()) {
                 filas.values().iterator().next().adicionarPessoaNoFinal(nomePessoa);
            } else {
                System.out.println("Aviso: Não foi possível adicionar '" + nomePessoa + "'. Nenhuma fila existente.");
            }
        }
    }

    /**
     * Remove uma ou mais pessoas das filas.
     * @param nomesAsSair Lista de nomes das pessoas que desistiram.
     * @return Uma lista de nomes das pessoas que foram de fato removidas.
     */
    public List<String> desiste(String[] nomesAsSair) {
        List<String> pessoasRemovidas = new ArrayList<>();
        for (String nome : nomesAsSair) {
            boolean removido = false;
            for (FilaBrasileira fila : filas.values()) {
                if (fila.removerPessoa(nome)) {
                    pessoasRemovidas.add(nome);
                    removido = true;
                    break; // A pessoa foi encontrada e removida de uma fila, pode parar de procurar em outras
                }
            }
            if (!removido) {
                System.out.println("Aviso: Pessoa '" + nome + "' não encontrada em nenhuma fila para desistir.");
            }
        }
        return pessoasRemovidas;
    }

    /**
     * Imprime o estado atual de todas as filas.
     * @return Uma lista de strings formatadas, representando cada fila.
     */
    public List<String> imprimeFilas() {
        List<String> resultado = new ArrayList<>();
        // Garante uma ordem de impressão consistente (por ID da fila, por exemplo)
        filas.keySet().stream().sorted().forEach(id -> {
            resultado.add(filas.get(id).toString());
        });
        return resultado;
    }
}