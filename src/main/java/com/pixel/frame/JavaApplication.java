package com.pixel.frame;

import java.util.Scanner;

public class JavaApplication {

    static final int MAX = 100;
    static String[] usuarios = new String[MAX];
    static String[] contrasenas = new String[MAX];
    static String[] roles = new String[MAX];
    static int usuarioCount = 0;

    static String[] peliculas = new String[MAX];
    static int peliculaCount = 0;

    static String[] videojuegos = new String[MAX];
    static int videojuegoCount = 0;

    static String[][] favoritos = new String[MAX][MAX]; 

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        usuarios[usuarioCount] = "admin";
        contrasenas[usuarioCount] = "admin";
        roles[usuarioCount] = "admin";
        usuarioCount++;

        while (true) {
            System.out.println("1. Iniciar sesion");
            System.out.println("2. Registrarse como cliente");
            System.out.println("3. Salir");
            int opcion = sc.nextInt();
            sc.nextLine();

            if (opcion == 1) {
                System.out.print("Usuario: ");
                String user = sc.nextLine();
                System.out.print("Contraseña: ");
                String pass = sc.nextLine();
                int pos = validarUsuario(user, pass);

                if (pos != -1) {
                    if (roles[pos].equals("admin")) {
                        menuAdmin(sc);
                    } else {
                        menuCliente(sc, pos);
                    }
                } else {
                    System.out.println("Credenciales incorrectas.");
                }

            } else if (opcion == 2) {
                System.out.print("Nuevo usuario: ");
                String user = sc.nextLine();
                System.out.print("Contraseña: ");
                String pass = sc.nextLine();
                usuarios[usuarioCount] = user;
                contrasenas[usuarioCount] = pass;
                roles[usuarioCount] = "cliente";
                usuarioCount++;
                System.out.println("Registro exitoso.");
            } else {
                break;
            }
        }

        sc.close();
    }

    static int validarUsuario(String user, String pass) {
        for (int i = 0; i < usuarioCount; i++) {
            if (usuarios[i].equals(user) && contrasenas[i].equals(pass)) {
                return i;
            }
        }
        return -1;
    }

    static void menuAdmin(Scanner sc) {
        int opcion;
        do {
            System.out.println("\n-- MENU ADMIN --");
            System.out.println("1. Ver peliculas");
            System.out.println("2. Agregar pelicula");
            System.out.println("3. Ver videojuegos");
            System.out.println("4. Agregar videojuego");
            System.out.println("5. Ver clientes");
            System.out.println("6. Eliminar cliente");
            System.out.println("0. Cerrar sesion");
            opcion = sc.nextInt();
            sc.nextLine();

            switch (opcion) {
                case 1:
                    for (int i = 0; i < peliculaCount; i++) {
                        System.out.println((i + 1) + ". " + peliculas[i]);
                    }
                    break;
                case 2:
                    System.out.print("Nombre de la pelicula: ");
                    peliculas[peliculaCount++] = sc.nextLine();
                    break;
                case 3:
                    for (int i = 0; i < videojuegoCount; i++) {
                        System.out.println((i + 1) + ". " + videojuegos[i]);
                    }
                    break;
                case 4:
                    System.out.print("Nombre del videojuego: ");
                    videojuegos[videojuegoCount++] = sc.nextLine();
                    break;
                case 5:
                    for (int i = 0; i < usuarioCount; i++) {
                        if (roles[i].equals("cliente")) {
                            System.out.println((i + 1) + ". " + usuarios[i]);
                        }
                    }
                    break;
                case 6:
                    System.out.print("Nombre del cliente a eliminar: ");
                    String nombre = sc.nextLine();
                    for (int i = 0; i < usuarioCount; i++) {
                        if (usuarios[i].equals(nombre) && roles[i].equals("cliente")) {
                            usuarios[i] = "[ELIMINADO]";
                            System.out.println("Cliente eliminado.");
                            break;
                        }
                    }
                    break;
            }
        } while (opcion != 0);
    }

    static void menuCliente(Scanner sc, int userIndex) {
        int opcion;
        do {
            System.out.println("\n-- MENU CLIENTE --");
            System.out.println("1. Ver peliculas");
            System.out.println("2. Ver videojuegos");
            System.out.println("3. Agregar pelicula a favoritos");
            System.out.println("4. Ver favoritos");
            System.out.println("0. Cerrar sesion");
            opcion = sc.nextInt();
            sc.nextLine();

            switch (opcion) {
                case 1:
                    for (int i = 0; i < peliculaCount; i++) {
                        System.out.println((i + 1) + ". " + peliculas[i]);
                    }
                    break;
                case 2:
                    for (int i = 0; i < videojuegoCount; i++) {
                        System.out.println((i + 1) + ". " + videojuegos[i]);
                    }
                    break;
                case 3:
                    System.out.print("Nombre de pelicula o videojuego favorito: ");
                    String favorito = sc.nextLine();
                    for (int j = 0; j < MAX; j++) {
                        if (favoritos[userIndex][j] == null) {
                            favoritos[userIndex][j] = favorito;
                            System.out.println("Agregado a favoritos.");
                            break;
                        }
                    }
                    break;
                case 4:
                    System.out.println("Tus favoritos:");
                    for (int j = 0; j < MAX; j++) {
                        if (favoritos[userIndex][j] != null) {
                            System.out.println("- " + favoritos[userIndex][j]);
                        }
                    }
                    break;
            }
        } while (opcion != 0);
    }
}