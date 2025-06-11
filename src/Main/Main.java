package Main;

import PessoasConhecemPessoas.PessoasConhecemPessoas;
import Parser.Parser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.nio.charset.StandardCharsets;

public class Main {
    public static void main(String[] args) {
        System.out.println("Diretório de trabalho atual: " + System.getProperty("user.dir"));

        // Define os nomes dos arquivos de entrada e saída
        final String ARQUIVO_ENTRADA = "entrada.txt";
        final String ARQUIVO_SAIDA = "saida.txt";

        PessoasConhecemPessoas sistemaPessoas = new PessoasConhecemPessoas();

        // Tenta inicializar o Parser com o arquivo de entrada
        File fileEntrada = new File(ARQUIVO_ENTRADA);
        Parser parser = new Parser(fileEntrada); // O construtor do Parser já trata FileNotFoundException

        // Validação: Verifica se o Parser foi inicializado corretamente (se o arquivo foi encontrado)
        if (parser == null || !parser.hasNext()) {
            System.err.println("Erro: Não foi possível ler o arquivo de entrada '" + ARQUIVO_ENTRADA + "'. Verifique se o arquivo existe e o caminho está correto.");
            return; // Encerra a execução se o arquivo de entrada não for encontrado
        }

        // Abre o arquivo de saída para escrita
        try (PrintWriter writer = new PrintWriter(new FileWriter(ARQUIVO_SAIDA, StandardCharsets.UTF_8))) { // Especifica UTF-8 para escrita
            System.out.println("\nIniciando processamento dos comandos...");
            while (parser.hasNext()) {
                String line = parser.nextLine().trim(); // Remove espaços em branco extras do início/fim da linha

                // Validação: Pula linhas vazias após o trim
                if (line.isEmpty()) {
                    continue;
                }

                String[] tokens = line.split(" ");

                // Validação: Verifica se há tokens na linha
                if (tokens.length == 0) {
                    System.err.println("Atenção: Linha vazia ou com apenas espaços ignorada: '" + line + "'");
                    continue;
                }

                String comando = tokens[0].replace(":", "").toLowerCase(); // Converte para minúsculas para robustez

                switch (comando) {
                    case "grupo":
                        // Validação: Comando 'grupo' precisa de pelo menos um nome (além da palavra 'grupo:')
                        if (tokens.length < 2) {
                            System.err.println("Erro: Comando 'grupo' inválido. Formato esperado: 'grupo: nome1 nome2 ...' - Linha: '" + line + "'");
                            break;
                        }
                        String[] nomesGrupo = new String[tokens.length - 1];
                        System.arraycopy(tokens, 1, nomesGrupo, 0, tokens.length - 1);

                        // Validação: Verifica se há nomes válidos no grupo
                        boolean temNomeInvalido = false;
                        for(String nome : nomesGrupo) {
                            if (nome.trim().isEmpty()) {
                                temNomeInvalido = true;
                                break;
                            }
                        }
                        if (temNomeInvalido) {
                             System.err.println("Erro: Comando 'grupo' contém nomes vazios. Linha: '" + line + "'");
                             break;
                        }

                        if (!sistemaPessoas.criarGrupo(nomesGrupo)) {
                            // Este caso significa que alguma pessoa já está em um grupo
                            System.out.println("Aviso: Falha ao criar grupo para '" + line + "'. Um ou mais membros já pertencem a um grupo existente.");
                        } else {
                            System.out.println("Sucesso: Grupo criado para '" + line + "'.");
                        }
                        break;

                    case "existe":
                        // Validação: Comando 'existe' precisa exatamente de 2 tokens
                        if (tokens.length != 2) {
                            System.err.println("Erro: Comando 'existe' inválido. Formato esperado: 'existe: nome' - Linha: '" + line + "'");
                            break;
                        }
                        String nomeExiste = tokens[1].trim();
                        // Validação: Nome não pode ser vazio
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
                        // Validação: Comando 'conhece' precisa exatamente de 3 tokens
                        if (tokens.length != 3) {
                            System.err.println("Erro: Comando 'conhece' inválido. Formato esperado: 'conhece: nome1 nome2' - Linha: '" + line + "'");
                            break;
                        }
                        String nome1 = tokens[1].trim();
                        String nome2 = tokens[2].trim();

                        // Validação: Nomes não podem ser vazios
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
                        // Validação: Comando desconhecido
                        System.err.println("Erro: Comando desconhecido ou mal formatado: '" + comando + "' - Linha: '" + line + "'");
                        break;
                }
            }
            System.out.println("\nProcessamento dos comandos finalizado.");
            System.out.println("Resultados gravados em '" + ARQUIVO_SAIDA + "'.");

        } catch (IOException e) {
            System.err.println("Erro crítico ao escrever no arquivo de saída '" + ARQUIVO_SAIDA + "': " + e.getMessage());
            return; // Encerra a execução se houver erro na escrita
        }

        // --- Adicionando a exibição do conteúdo no console do Eclipse ---
        System.out.println("\n--- Conteúdo do arquivo de saída (" + ARQUIVO_SAIDA + ") ---");
        try {
            // Usa Files.readAllLines para ler todo o conteúdo do arquivo
            List<String> linhasSaida = Files.readAllLines(Paths.get(ARQUIVO_SAIDA), StandardCharsets.UTF_8); // Lê com UTF-8

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