package clientPackage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        try {
            // Saisie de l'adresse IP du serveur distant (ou IP locale)
            System.out.print("Entrez l'adresse IP du serveur (ex: 192.168.x.x) : ");
            String ipAddressStr = scanner.nextLine();
            int port = 1234;

            // Utilisation de InetAddress pour résoudre l'adresse IP
            InetAddress serverIp = InetAddress.getByName(ipAddressStr);

            Socket socket = new Socket();
            System.out.println("Tentative de connexion au serveur " + serverIp.getHostAddress() + " sur le port " + port + "...");
            socket.connect(new InetSocketAddress(serverIp, port));
            System.out.println("Connecté au serveur avec succès !");

            // Streams de communication
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            DataInputStream dis = new DataInputStream(socket.getInputStream());

            int choix = -1;
            while (choix != 0) {
                // Menu interactif
                System.out.println("\n--- MENU CALCULATRICE DISTANTE ---");
                System.out.println("1. Addition (+)");
                System.out.println("2. Soustraction (-)");
                System.out.println("3. Multiplication (*)");
                System.out.println("4. Division (/)");
                System.out.println("0. Quitter");
                System.out.print("Votre choix : ");
                choix = scanner.nextInt();

                // Envoi du choix au serveur
                dos.writeInt(choix);

                if (choix >= 1 && choix <= 4) {
                    System.out.print("Saisir le premier nombre (opérande 1) : ");
                    double op1 = scanner.nextDouble();
                    System.out.print("Saisir le deuxième nombre (opérande 2) : ");
                    double op2 = scanner.nextDouble();

                    // Envoi des deux nombres
                    dos.writeDouble(op1);
                    dos.writeDouble(op2);

                    // Lecture du statut de réponse du serveur
                    int status = dis.readInt();
                    if (status == 0) {
                        double resultat = dis.readDouble();
                        System.out.println(">>> Résultat reçu du serveur : " + resultat);
                    } else if (status == 1) {
                        System.out.println(">>> Erreur serveur : Division par zéro impossible !");
                    }
                } else if (choix != 0) {
                    System.out.println("Choix invalide, veuillez réespayer.");
                }
            }

            System.out.println("Fermeture du client...");
            scanner.close();
            dis.close();
            dos.close();
            socket.close();
        } catch (IOException e) {
            System.err.println("Erreur de connexion : " + e.getMessage());
        }
    }
}