package Main;

import PessoasConhecemPessoas.PessoasConhecemPessoas;
import Parser.Parser;
import SistemaFilas.SistemaFilas;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.nio.charset.StandardCharsets;

public class Main {
    public static void main(String[] args) {
        //System.out.println("Diretório de trabalho atual: " + System.getProperty("user.dir"));

        final String ARQUIVO_ENTRADA = "entrada.txt";
        final String ARQUIVO_SAIDA = "saida.txt";

        PessoasConhecemPessoas sistemaPessoas = new PessoasConhecemPessoas();
        SistemaFilas sistemaFilas = new SistemaFilas(sistemaPessoas);

        File fileEntrada = new File(ARQUIVO_ENTRADA);
        Parser parser = new Parser(fileEntrada);

        if (parser == null || !parser.hasNext()) {
            System.err.println("Erro: Não foi possível ler o arquivo de entrada '" + ARQUIVO_ENTRADA + "'. Verifique se o arquivo existe e o caminho está correto.");
            return;
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(ARQUIVO_SAIDA, StandardCharsets.UTF_8))) {
            System.out.println("\nIniciando processamento dos comandos...");
            while (parser.hasNext()) {
                String line = parser.nextLine().trim(); //tira espaço em branco da linha

                if (line.isEmpty() || line.startsWith("#") || line.startsWith("---")) {
                    continue;
                }

                String[] tokens = line.split(" ");
                if (tokens.length == 0) {
                    System.err.println("Atenção: Linha vazia ou com apenas espaços ignorada: '" + line + "'");
                    continue;
                }
                String comando = tokens[0].replace(":", "").toLowerCase();
                //se nao tiver parametros ('imprime:'), sera um array vazio
                String[] parametros = Arrays.copyOfRange(tokens, 1, tokens.length);

                switch (comando) {
                    case "criafila":
                        if (parametros.length == 0) {
                            System.err.println("Erro: Comando 'criaFila' inválido. Formato esperado: 'criaFila: ID [ID1, ID2, ...]' - Linha: '" + line + "'");
                            break;
                        }
                        for (String idFila : parametros) {
                            idFila = idFila.trim();
                            if (!idFila.isEmpty()) {
                                if (sistemaFilas.criaFila(idFila)) {
                                    //System.out.println("Sucesso: Fila '" + idFila + "' criada."); // Opcional, para debug
                                } else {
                                    System.out.println("Aviso: Fila '" + idFila + "' já existe e não foi recriada.");
                                }
                            } else {
                                System.err.println("Aviso: Ignorando ID de fila vazio no comando 'criaFila': '" + line + "'");
                            }
                        }
                        break;

                    case "atendefila":
                        if (parametros.length == 0) {
                            System.err.println("Erro: Comando 'atendeFila' inválido. Formato esperado: 'atendeFila: ID [ID1, ID2, ...]' - Linha: '" + line + "'");
                            break;
                        }
                        for (String idFila : parametros) {
                            idFila = idFila.trim();
                            if (!idFila.isEmpty()) {
                                String pessoaAtendida = sistemaFilas.atendeFila(idFila);
                                if (pessoaAtendida != null) {
                                    // System.out.println("Sucesso: '" + pessoaAtendida + "' atendido(a) da fila '" + idFila + "'.");
                                } else {
                                    System.out.println("Aviso: Fila '" + idFila + "' não existe ou está vazia. Ninguém para atender.");
                                }
                            } else {
                                System.err.println("Aviso: Ignorando ID de fila vazio no comando 'atendeFila': '" + line + "'");
                            }
                        }
                        break;

                    case "chegou":
                        if (parametros.length == 0) {
                            System.err.println("Erro: Comando 'chegou' inválido. Formato esperado: 'chegou: nome [nome1, nome2, ...]' - Linha: '" + line + "'");
                            break;
                        }
                        for (String nomePessoa : parametros) {
                            nomePessoa = nomePessoa.trim();
                            if (!nomePessoa.isEmpty()) {
                                sistemaFilas.chegou(nomePessoa);
                                // System.out.println("Sucesso: '" + nomePessoa + "' adicionado(a) à fila.");
                            } else {
                                System.err.println("Aviso: Ignorando nome de pessoa vazio no comando 'chegou': '" + line + "'");
                            }
                        }
                        break;

                    case "desiste":
                        if (parametros.length == 0) {
                            System.err.println("Erro: Comando 'desiste' inválido. Formato esperado: 'desiste: nome [nome1, nome2, ...]' - Linha: '" + line + "'");
                            break;
                        }
                        List<String> pessoasQueDesistiram = sistemaFilas.desiste(parametros);
                        break;

                    case "imprime":
                        List<String> filasFormatadas = sistemaFilas.imprimeFilas();
                        if (filasFormatadas.isEmpty()) {
                            writer.println("[Nenhuma fila para imprimir.]");
                        } else {
                            for (String filaStr : filasFormatadas) {
                                writer.println(filaStr);
                            }
                        }
                        break;

                    case "grupo":
                        if (parametros.length == 0) {
                            System.err.println("Erro: Comando 'grupo' inválido. Formato esperado: 'grupo: nome1 nome2 ...' - Linha: '" + line + "'");
                            break;
                        }
                        String[] nomesGrupo = new String[parametros.length];
                        boolean temNomeInvalido = false;
                        for(int i = 0; i < parametros.length; i++) {
                            nomesGrupo[i] = parametros[i].trim();
                            if (nomesGrupo[i].isEmpty()) {
                                temNomeInvalido = true;
                                break;
                            }
                        }
                        if (temNomeInvalido) {
                                System.err.println("Erro: Comando 'grupo' contém nomes vazios. Linha: '" + line + "'");
                                break;
                        }
                        if (!sistemaPessoas.criarGrupo(nomesGrupo)) {
                            System.out.println("Aviso: Falha ao criar grupo para '" + line + "'. Um ou mais membros já pertencem a um grupo existente.");
                        } else {
                        }
                        break;

                    case "existe":
                        if (parametros.length != 1) {
                            System.err.println("Erro: Comando 'existe' inválido. Formato esperado: 'existe: nome' - Linha: '" + line + "'");
                            break;
                        }
                        String nomeExiste = parametros[0].trim();
                        if (nomeExiste.isEmpty()) {
                            System.err.println("Erro: Nome vazio no comando 'existe'. Linha: '" + line + "'");
                            break;
                        }
                        if (sistemaPessoas.existePessoa(nomeExiste)) {
                            writer.println(" [" + nomeExiste + "] existe!");
                        } else {
                            writer.println(" [" + nomeExiste + "] NÃO existe!");
                        }
                        break;

                    case "conhece":
                        if (parametros.length != 2) {
                            System.err.println("Erro: Comando 'conhece' inválido. Formato esperado: 'conhece: nome1 nome2' - Linha: '" + line + "'");
                            break;
                        }
                        String nome1 = parametros[0].trim();
                        String nome2 = parametros[1].trim();
                        if (nome1.isEmpty() || nome2.isEmpty()) {
                            System.err.println("Erro: Nomes vazios no comando 'conhece'. Linha: '" + line + "'");
                            break;
                        }
                        if (sistemaPessoas.pessoasSeConhecem(nome1, nome2)) {
                            writer.println(" [" + nome1 + "] conhece [" + nome2 + "]");
                        } else {
                            writer.println(" [" + nome1 + "] NÃO conhece [" + nome2 + "]");
                        }
                        break;

                    default:
                        System.err.println("Erro: Comando desconhecido ou mal formatado: '" + comando + "' - Linha: '" + line + "'");
                        break;
                }
            }
            System.out.println("\nProcessamento dos comandos finalizado.");
            System.out.println("Resultados gravados em '" + ARQUIVO_SAIDA + "'.");

        } catch (IOException e) {
            System.err.println("Erro crítico ao escrever no arquivo de saída '" + ARQUIVO_SAIDA + "': " + e.getMessage());
            return;
        }

        System.out.println("\n----- Conteúdo do arquivo de saída (" + ARQUIVO_SAIDA + ") -----");
        try {
            List<String> linhasSaida = Files.readAllLines(Paths.get(ARQUIVO_SAIDA), StandardCharsets.UTF_8);

            if (linhasSaida.isEmpty()) {
                System.out.println("[O arquivo de saída está vazio.]");
            } else {
                for (String linha : linhasSaida) {
                    System.out.println(linha);
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao tentar ler o arquivo de saída para exibição no console: " + e.getMessage());
        }
        System.out.println("----------------------------------------------------\n");
    }
}
