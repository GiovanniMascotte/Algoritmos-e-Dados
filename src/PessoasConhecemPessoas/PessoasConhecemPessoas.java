package PessoasConhecemPessoas;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PessoasConhecemPessoas {
    private Map<String, String> pessoaParaGrupo;
    private Map<String, Set<String>> grupos;
    private int proximoGrupoId;

    public PessoasConhecemPessoas() {
        this.pessoaParaGrupo = new HashMap<>();
        this.grupos = new HashMap<>();
        this.proximoGrupoId = 0;
    }
    
    public boolean criarGrupo(String[] nomes) {//verifica se a pessoa ja ta em algum grupo
        for (String nome : nomes) {
            if (pessoaParaGrupo.containsKey(nome)) {
                return false;
            }
        }

        String grupoId = "grupo_" + proximoGrupoId++;
        Set<String> novoGrupo = new HashSet<>();

        for (String nome : nomes) {//adiciona as pessoas ao novo grupo e associa cada pessoa ao ID do grupo
            novoGrupo.add(nome);
            pessoaParaGrupo.put(nome, grupoId);
        }
        grupos.put(grupoId, novoGrupo);
        return true;
    }
    
    public boolean existePessoa(String nome) {
        return pessoaParaGrupo.containsKey(nome);
    }
    
    public boolean pessoasSeConhecem(String nome1, String nome2) {
        if (!pessoaParaGrupo.containsKey(nome1) || !pessoaParaGrupo.containsKey(nome2)) {
            return false;
        }
        return pessoaParaGrupo.get(nome1).equals(pessoaParaGrupo.get(nome2));
    }
}
