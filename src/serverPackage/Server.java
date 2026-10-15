package serverPackage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) {
        int port = 1234;
        try {
            // Étape 1 : Obtenir l'adresse IP locale de la machine serveur
            InetAddress ipServeur = InetAddress.getLocalHost();
            System.out.println("Adresse IP du serveur : " + ipServeur.getHostAddress());

            // Création de la socket d'écoute associée à l'adresse IP et au port
            ServerSocket serverSocket = new ServerSocket();
            serverSocket.bind(new InetSocketAddress(ipServeur, port));
            System.out.println("Serveur calculatrice à l'écoute sur " + ipServeur.getHostAddress() + ":" + port + "...");

            // Acceptation de la connexion d'un client distant
            Socket socket = serverSocket.accept();
            System.out.println("Client connecté depuis : " + socket.getRemoteSocketAddress());

            // Initialisation des flux de données
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

            boolean enCours = true;
            while (enCours) {
                // Lecture du choix de l'opération envoyée par le client
                int choix = dis.readInt();
                if (choix == 0) {
                    System.out.println("Le client a demandé la fermeture de la connexion.");
                    enCours = false;
                } else {
                    // Lecture des deux opérandes
                    double op1 = dis.readDouble();
                    double op2 = dis.readDouble();
                    double resultat = 0;
                    boolean erreurDivisionZero = false;

                    // Traitement du calcul selon le choix de l'opération
                    switch (choix) {
                        case 1: // Addition
                            resultat = op1 + op2;
                            System.out.println("Calcul : " + op1 + " + " + op2 + " = " + resultat);
                            break;
                        case 2: // Soustraction
                            resultat = op1 - op2;
                            System.out.println("Calcul : " + op1 + " - " + op2 + " = " + resultat);
                            break;
                        case 3: // Multiplication
                            resultat = op1 * op2;
                            System.out.println("Calcul : " + op1 + " * " + op2 + " = " + resultat);
                            break;
                        case 4: // Division
                            if (op2 != 0) {
                                resultat = op1 / op2;
                                System.out.println("Calcul : " + op1 + " / " + op2 + " = " + resultat);
                            } else {
                                System.out.println("Erreur : Division par zéro demandée.");
                                erreurDivisionZero = true;
                            }
                            break;
                        default:
                            System.out.println("Choix d'opération invalide reçu.");
                    }

                    // Envoi d'un code de statut (0 = OK, 1 = Erreur Division par zéro)
                    if (erreurDivisionZero) {
                        dos.writeInt(1); // Signalement d'une erreur
                    } else {
                        dos.writeInt(0); // Succès
                        dos.writeDouble(resultat); // Envoi du résultat au client
                    }
                }
            }

            // Fermeture des ressources
            dis.close();
            dos.close();
            socket.close();
            serverSocket.close();
            System.out.println("Serveur arrêté proprement.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}