package com.pixel.frame;

import java.util.Scanner;
import java.sql.*;

public class JavaApplication {
    private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=PixelFrame;encrypt=true;trustServerCertificate=true";
    private static final String USER = "PixelUser";
    private static final String PASSWORD = "Hola123!Segura";

    public static Connection connectDB() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("1. Iniciar sesion");
            System.out.println("2. Registrarse como cliente");
            System.out.println("3. Salir");
            int opcion = sc.nextInt();
            sc.nextLine();

            if (opcion == 1) {
                System.out.print("Usuario: ");
                String user = sc.nextLine();
                System.out.print("Contrasena: ");
                String pass = sc.nextLine();
                int id = validarUsuario(user, pass);

                if (id != 0) {
                    if (id < 0) {
                        menuAdmin(sc, -id);
                    } else {
                        menuCliente(sc, id);
                    }
                } else {
                    System.out.println("Credenciales incorrectas.");
                }

            } else if (opcion == 2) {
                System.out.print("Nuevo usuario: ");
                String user = sc.nextLine();
                System.out.print("Correo: ");
                String correo = sc.nextLine();
                System.out.print("Contrasena: ");
                String pass = sc.nextLine();

                try (Connection conn = connectDB()) {
                    String query = "INSERT INTO Usuarios (NombreUsuario, Correo, Contrasena, EsAdmin) VALUES (?, ?, ?, 0)";
                    PreparedStatement stmt = conn.prepareStatement(query);
                    stmt.setString(1, user);
                    stmt.setString(2, correo);
                    stmt.setString(3, pass);
                    stmt.executeUpdate();
                    System.out.println("Registro exitoso.");
                } catch (SQLException e) {
                    e.printStackTrace();
                    System.out.println("Error en el registro.");
                }
            } else {
                break;
            }
        }

        sc.close();
    }

    static int validarUsuario(String user, String pass) {
        try (Connection conn = connectDB()) {
            String query = "SELECT UsuarioID, EsAdmin FROM Usuarios WHERE NombreUsuario = ? AND Contrasena = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, user);
            stmt.setString(2, pass);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("UsuarioID");
                boolean esAdmin = rs.getBoolean("EsAdmin");
                return esAdmin ? -id : id;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    static void menuAdmin(Scanner sc, int adminId) {
        int opcion;
        do {
            System.out.println("\n-- MENU ADMIN --");
            System.out.println("1. Ver contenidos");
            System.out.println("2. Agregar pelicula o videojuego");
            System.out.println("3. Ver clientes");
            System.out.println("4. Eliminar cliente");
            System.out.println("0. Cerrar sesion");
            opcion = sc.nextInt();
            sc.nextLine();

            try (Connection conn = connectDB()) {
                switch (opcion) {
                    case 1:
                        String consulta = "SELECT ContenidoID, Titulo FROM Contenidos";
                        ResultSet rs = conn.createStatement().executeQuery(consulta);
                        while (rs.next()) {
                            System.out.println(rs.getInt("ContenidoID") + ". " + rs.getString("Titulo"));
                        }
                        break;
                    case 2:
                        System.out.print("Titulo: ");
                        String titulo = sc.nextLine();
                        System.out.print("Descripcion: ");
                        String descripcion = sc.nextLine();
                        System.out.print("Fecha de lanzamiento (yyyy-mm-dd): ");
                        String fecha = sc.nextLine();
                        System.out.print("ID TipoContenido (1=pelicula, 2=videojuego): ");
                        int tipoID = sc.nextInt();
                        sc.nextLine();
                        System.out.print("ID Genero: ");
                        int generoID = sc.nextInt();
                        sc.nextLine();

                        String insert = "INSERT INTO Contenidos (Titulo, Descripcion, FechaLanzamiento, TipoID, GeneroID) VALUES (?, ?, ?, ?, ?)";
                        PreparedStatement ps = conn.prepareStatement(insert);
                        ps.setString(1, titulo);
                        ps.setString(2, descripcion);
                        ps.setDate(3, Date.valueOf(fecha));
                        ps.setInt(4, tipoID);
                        ps.setInt(5, generoID);
                        ps.executeUpdate();
                        System.out.println("Contenido agregado.");
                        break;
                    case 3:
                        String clientes = "SELECT UsuarioID, NombreUsuario FROM Usuarios WHERE EsAdmin = 0";
                        rs = conn.createStatement().executeQuery(clientes);
                        while (rs.next()) {
                            System.out.println(rs.getInt("UsuarioID") + ". " + rs.getString("NombreUsuario"));
                        }
                        break;
                    case 4:
                        System.out.print("ID del cliente a eliminar: ");
                        int idEliminar = sc.nextInt();
                        sc.nextLine();
                        PreparedStatement eliminar = conn.prepareStatement("DELETE FROM Usuarios WHERE UsuarioID = ? AND EsAdmin = 0");
                        eliminar.setInt(1, idEliminar);
                        int afectados = eliminar.executeUpdate();
                        System.out.println(afectados > 0 ? "Cliente eliminado." : "No se pudo eliminar.");
                        break;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } while (opcion != 0);
    }

    static void menuCliente(Scanner sc, int userId) {
        int opcion;
        do {
            System.out.println("\n-- MENU CLIENTE --");
            System.out.println("1. Ver contenidos disponibles");
            System.out.println("2. Agregar contenido a favoritos");
            System.out.println("3. Ver favoritos");
            System.out.println("0. Cerrar sesion");
            opcion = sc.nextInt();
            sc.nextLine();

            try (Connection conn = connectDB()) {
                switch (opcion) {
                    case 1:
                        ResultSet rs = conn.createStatement().executeQuery("SELECT ContenidoID, Titulo FROM Contenidos");
                        while (rs.next()) {
                            System.out.println(rs.getInt("ContenidoID") + ". " + rs.getString("Titulo"));
                        }
                        break;
                    case 2:
                        System.out.print("ID del contenido a agregar: ");
                        int contenidoId = sc.nextInt();
                        sc.nextLine();
                        PreparedStatement favStmt = conn.prepareStatement("INSERT INTO Favoritos (UsuarioID, ContenidoID) VALUES (?, ?)");
                        favStmt.setInt(1, userId);
                        favStmt.setInt(2, contenidoId);
                        favStmt.executeUpdate();
                        System.out.println("Agregado a favoritos.");
                        break;
                    case 3:
                        PreparedStatement favQuery = conn.prepareStatement(
                                "SELECT C.Titulo FROM Favoritos F JOIN Contenidos C ON F.ContenidoID = C.ContenidoID WHERE F.UsuarioID = ?"
                        );
                        favQuery.setInt(1, userId);
                        rs = favQuery.executeQuery();
                        System.out.println("Tus favoritos:");
                        while (rs.next()) {
                            System.out.println("- " + rs.getString("Titulo"));
                        }
                        break;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } while (opcion != 0);
    }
}