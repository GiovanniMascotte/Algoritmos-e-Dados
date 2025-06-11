package PessoasConhecemPessoas;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PessoasConhecemPessoas {
    // Mapa: nome da pessoa -> ID do grupo
    private Map<String, String> pessoaParaGrupo;
    // Mapa: ID do grupo -> Set de nomes de pessoas
    private Map<String, Set<String>> grupos;
    // Contador para gerar IDs de grupo únicos
    private int proximoGrupoId;

    public PessoasConhecemPessoas() {
        this.pessoaParaGrupo = new HashMap<>();
        this.grupos = new HashMap<>();
        this.proximoGrupoId = 0;
    }

    /**
     * Cria um novo grupo de pessoas.
     * Uma pessoa não pode pertencer a dois grupos diferentes.
     *
     * @param nomes Array de nomes de pessoas a serem adicionadas ao grupo.
     * @return true se o grupo foi criado com sucesso, false caso alguma pessoa já esteja em outro grupo.
     */
    public boolean criarGrupo(String[] nomes) {
        // Primeiro, verifica se alguma das pessoas já está em um grupo
        for (String nome : nomes) {
            if (pessoaParaGrupo.containsKey(nome)) {
                return false; // Pessoa já está em outro grupo
            }
        }

        String grupoId = "grupo_" + proximoGrupoId++;
        Set<String> novoGrupo = new HashSet<>();

        // Adiciona as pessoas ao novo grupo e associa cada pessoa ao ID do grupo
        for (String nome : nomes) {
            novoGrupo.add(nome);
            pessoaParaGrupo.put(nome, grupoId);
        }
        grupos.put(grupoId, novoGrupo);
        return true;
    }

    /**
     * Verifica se uma pessoa existe (está em algum grupo).
     *
     * @param nome O nome da pessoa a ser verificada.
     * @return true se a pessoa existe, false caso contrário.
     */
    public boolean existePessoa(String nome) {
        return pessoaParaGrupo.containsKey(nome);
    }

    /**
     * Verifica se duas pessoas se conhecem (pertencem ao mesmo grupo).
     *
     * @param nome1 O nome da primeira pessoa.
     * @param nome2 O nome da segunda pessoa.
     * @return true se as pessoas se conhecem, false caso contrário.
     */
    public boolean pessoasSeConhecem(String nome1, String nome2) {
        // Se alguma das pessoas não existe, elas não podem se conhecer
        if (!pessoaParaGrupo.containsKey(nome1) || !pessoaParaGrupo.containsKey(nome2)) {
            return false;
        }
        // Verificam se estão no mesmo grupo comparando os IDs dos grupos
        return pessoaParaGrupo.get(nome1).equals(pessoaParaGrupo.get(nome2));
    }
}