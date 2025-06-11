package Main;

import PessoasConhecemPessoas.PessoasConhecemPessoas;
import Parser.Parser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Main {
    public static void main(String[] args) {
        // Define o nome do arquivo de entrada e saída
        String arquivoEntrada = "entrada.txt";
        String arquivoSaida = "saida.txt";

        // Cria uma instância da estrutura de dados
        PessoasConhecemPessoas sistemaPessoas = new PessoasConhecemPessoas();

        // Inicializa o Parser com o arquivo de entrada
        File fileEntrada = new File(arquivoEntrada);
        Parser parser = new Parser(fileEntrada);

        // Verifica se o arquivo de entrada foi encontrado
        if (!parser.hasNext()) {
            System.out.println("Não foi possível ler o arquivo de entrada: " + arquivoEntrada);
            return;
        }

        // Abre o arquivo de saída para escrita
        try (PrintWriter writer = new PrintWriter(new FileWriter(arquivoSaida))) {
            while (parser.hasNext()) {
                String line = parser.nextLine();
                String[] tokens = line.split(" ");

                if (tokens.length == 0) {
                    continue; // Pula linhas vazias
                }

                String comando = tokens[0].replace(":", ""); // Remove o ":" se existir

                switch (comando) {
                    case "grupo":
                        // Espera o formato "grupo: Nome1 Nome2 Nome3"
                        if (tokens.length < 2) {
                            System.err.println("Comando 'grupo' inválido: " + line);
                            break;
                        }
                        // Os nomes começam a partir do segundo token
                        String[] nomesGrupo = new String[tokens.length - 1];
                        System.arraycopy(tokens, 1, nomesGrupo, 0, tokens.length - 1);

                        if (!sistemaPessoas.criarGrupo(nomesGrupo)) {
                            // Este caso significa que alguma pessoa já está em um grupo,
                            // mas a especificação não pede uma saída para isso,
                            // apenas que a pessoa não pode estar em dois grupos diferentes.
                            // Podemos adicionar um log ou tratamento se necessário.
                            System.out.println("Atenção: Um ou mais membros do grupo '" + line + "' já pertencem a um grupo existente. Grupo não criado.");
                        }
                        break;

                    case "existe":
                        // Espera o formato "existe: Nome"
                        if (tokens.length != 2) {
                            System.err.println("Comando 'existe' inválido: " + line);
                            break;
                        }
                        String nomeExiste = tokens[1];
                        if (sistemaPessoas.existePessoa(nomeExiste)) {
                            writer.println(" [" + nomeExiste + "] existe!");
                        } else {
                            writer.println(" [" + nomeExiste + "] NÃO existe!");
                        }
                        break;

                    case "conhece":
                        // Espera o formato "conhece: Nome1 Nome2"
                        if (tokens.length != 3) {
                            System.err.println("Comando 'conhece' inválido: " + line);
                            break;
                        }
                        String nome1 = tokens[1];
                        String nome2 = tokens[2];
                        if (sistemaPessoas.pessoasSeConhecem(nome1, nome2)) {
                            writer.println(" [" + nome1 + "] conhece [" + nome2 + "]");
                        } else {
                            writer.println(" [" + nome1 + "] NÃO conhece [" + nome2 + "]");
                        }
                        break;

                    default:
                        System.err.println("Comando desconhecido: " + comando);
                        break;
                }
            }
            System.out.println("Processamento concluído. Verifique o arquivo '" + arquivoSaida + "' para a saída.");

        } catch (IOException e) {
            System.err.println("Erro ao escrever no arquivo de saída: " + e.getMessage());
        }
    }
}