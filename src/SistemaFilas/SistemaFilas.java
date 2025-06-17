package SistemaFilas;

import FilaBrasileira.FilaBrasileira;
import PessoasConhecemPessoas.PessoasConhecemPessoas;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SistemaFilas {
    private Map<String, FilaBrasileira> filas;
    private PessoasConhecemPessoas sistemaPessoas;

    public SistemaFilas(PessoasConhecemPessoas sistemaPessoas) {
        this.filas = new HashMap<>();
        this.sistemaPessoas = sistemaPessoas;
    }

    public boolean criaFila(String id) {
        if (filas.containsKey(id)) {
            return false;
        }
        filas.put(id, new FilaBrasileira(id));
        return true;
    }

    public String atendeFila(String idFila) {
        FilaBrasileira fila = filas.get(idFila);
        if (fila != null) {
            String pessoaAtendida = fila.atenderProximo();
            if (pessoaAtendida != null) {
                System.out.println("DEBUG: '" + pessoaAtendida + "' atendido(a) da fila '" + idFila + "'.");
            }
            return pessoaAtendida;
        }
        return null;
    }

    public void chegou(String nomePessoa) {
        if (!sistemaPessoas.existePessoa(nomePessoa)) {
            System.out.println("Aviso: Pessoa '" + nomePessoa + "' não existe no sistema e não pode entrar na fila.");
            return;
        }

        String melhorFilaId = null;
        int melhorPosicao = Integer.MAX_VALUE;
        String pessoaConhecidaNaMelhorPosicao = null;

        for (FilaBrasileira fila : filas.values()) {
            List<String> pessoasAtualNaFila = fila.getPessoasNaFila();
            for (int i = 0; i < pessoasAtualNaFila.size(); i++) {
                String pessoaNaFila = pessoasAtualNaFila.get(i);
                if (sistemaPessoas.pessoasSeConhecem(nomePessoa, pessoaNaFila)) {
                    if ((i + 1) < melhorPosicao) {
                        melhorPosicao = i + 1;
                        melhorFilaId = fila.getId();
                        pessoaConhecidaNaMelhorPosicao = pessoaNaFila;
                    }
                }
            }
        }

        if (melhorFilaId != null) {
            FilaBrasileira filaParaInserir = filas.get(melhorFilaId);
            filaParaInserir.adicionarPessoaAposConhecido(nomePessoa, pessoaConhecidaNaMelhorPosicao);
            return;
        }

        if (filas.isEmpty()) {
            System.out.println("Aviso: Nenhuma fila disponível para '" + nomePessoa + "' chegar.");
            return;
        }

        String filaMaisCurtaId = null;
        int menorTamanhoFila = Integer.MAX_VALUE;

        for (FilaBrasileira fila : filas.values()) {
            if (fila.getPessoasNaFila().size() < menorTamanhoFila) {
                menorTamanhoFila = fila.getPessoasNaFila().size();
                filaMaisCurtaId = fila.getId();
            }
        }

        if (filaMaisCurtaId != null) {
            filas.get(filaMaisCurtaId).adicionarPessoaNoFinal(nomePessoa);
        } else {
             if (!filas.isEmpty()) {
                 filas.values().iterator().next().adicionarPessoaNoFinal(nomePessoa);
             } else {
                 System.out.println("Aviso: Não foi possível adicionar '" + nomePessoa + "'. Nenhuma fila existente após a busca.");
             }
        }
    }

    public List<String> desiste(String[] nomesAsSair) {
        List<String> pessoasRemovidas = new ArrayList<>();
        for (String nome : nomesAsSair) {
            String nomeTrimmed = nome.trim();
            boolean removido = false;
            for (FilaBrasileira fila : filas.values()) {
                if (fila.removerPessoa(nomeTrimmed)) {
                    pessoasRemovidas.add(nomeTrimmed);
                    removido = true;
                    break;
                }
            }
            if (!removido) {
                System.out.println("Aviso: Pessoa '" + nomeTrimmed + "' não encontrada em nenhuma fila para desistir.");
            }
        }
        return pessoasRemovidas;
    }

    public List<String> imprimeFilas() {
        List<String> resultado = new ArrayList<>();
        List<String> idsFilasOrdenados = new ArrayList<>(filas.keySet());
        Collections.sort(idsFilasOrdenados);

        for (String id : idsFilasOrdenados) {
            resultado.add(filas.get(id).toString());
        }
        return resultado;
    }
}
