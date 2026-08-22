package src.gui;

import java.util.Scanner;
import src.service.ContatoService;

public class ConsoleGUI {
    private ContatoService service;
    private static Scanner scanner;

    public ConsoleGUI() {
        this.service = new ContatoService();
        this.scanner = new Scanner(System.in);
    }

    public static void exibirMenu() {
        int opcao = -1;

        while (opcao != 0) {
            System.out.println("=== AGENDA DE CONTATOS ===");
            System.out.println("1. Listar Contatos");
            System.out.println("2. Cadastrar Contato");
            System.out.println("3. Excluir Contato");
            System.out.println("0. Sair\n");

            System.out.print("Digite uma opcao: ");

            try {
                opcao = Integer.parseInt(scanner.nextLine());
            }
            catch (NumberFormatException e) {
                System.out.println("Opção inválida! Digite apenas números");
                continue;
            }

            switch (opcao) {
                case 1:
                    System.out.println("Não implementado ainda...");
                    break;
                case 2:
                    System.out.print("Nome: ");
                    String nome = scanner.nextLine();
                    System.out.println("Email: ");
                    String email = scanner.nextLine();
            }
        }
    }
}