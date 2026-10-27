import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Scanner;

public class ExamenRA1B {

    private static final Path DIRECTORIO_ENTRADA = Path.of("datos", "entrada");
    private static final Path FICHERO_1 = DIRECTORIO_ENTRADA.resolve("arboles1.jsonl");
    private static final Path FICHERO_2 = DIRECTORIO_ENTRADA.resolve("arboles2.jsonl");
    private static final Path FICHERO_3 = DIRECTORIO_ENTRADA.resolve("arboles3.jsonl");
    private static final Path FICHERO_ID = DIRECTORIO_ENTRADA.resolve("id_arbol.bin");
    public static final String SALIDA_NOMBRE = "arbol1.txt";
    public static final String SALIDA_FINAL = "arboles_ordenados.jsonl";

    /** Ejercicio 1 (1 punto): comprobar/crear directorio e informar de errores. */
    private static boolean prepararDirectorioSalida(Path salida) {
        if (Files.notExists(salida)){
            try {
                Files.createDirectories(salida);
                System.out.println("Se ha creado el directorio.");
                return true;
            } catch (IOException e) {
                System.out.printf("Error al crear el directorio (%s)\n", e.toString());
                return false;
            }
        } else if (!Files.isDirectory(salida)) {
            System.out.println("Existe un fichero con ese nombre");
            return false;
        } else {
            System.out.println("El directorio ya existe");
            return true;
        }
    }

    /** Ejercicio 2 (1 punto): leer el árbol y escribir su nombre en arbol1.txt. */
    private static Arbol ejercicio2LeerYEscribirArbol(Path entrada, Path salida) {
        Arbol arbol = null;
        Path ficheroSalida = salida.resolve("arbol1.txt");

        try (BufferedReader bf = Files.newBufferedReader(entrada)){
            String linea;
            if ((linea = bf.readLine()) != null) {
                arbol = ParseadorArboles.parsear(linea);
            }
        } catch (IOException e) {
            System.out.println("Error al leer el archivo.");
        } catch (FormatoJsonIncorrectoException e) {
            System.out.println("Formato incorrecto en el json");
        } catch (TipoDatoIncorrectoException e) {
            System.out.println("Tipo de dato incorrecto");
        }

        if(arbol == null) {
            return null;
        }

        try {
            Files.writeString(ficheroSalida, arbol.getNombre() + "\n", StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.out.println("Error al escribir el archivo.");
        }

        return arbol;
    }

    /** Ejercicio 3 (2 puntos): leer 200 objetos JSON en un HashMap por ID. */
    private static HashMap<Integer, Arbol> ejercicio3LeerArboles(Path entrada) {
        HashMap<Integer, Arbol> arboles = new HashMap<>();
        String linea;

        try (BufferedReader bf = Files.newBufferedReader(entrada)) {
            while ((linea = bf.readLine()) != null) {
                // Ignorar líneas vacías por si acaso
                if (!linea.trim().isEmpty()) {
                    try {
                        Arbol arbol = ParseadorArboles.parsear(linea);
                        if (arbol != null) {
                            arboles.put(arbol.getId(), arbol);
                        }
                    } catch (FormatoJsonIncorrectoException | TipoDatoIncorrectoException e) {
                        System.out.println("Error al parsear el árbol: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error al leer el archivo.");
        }

        // Informar del total como pide el comentario del profesor
        System.out.println("Total de árboles cargados: " + arboles.size());

        return arboles;
    }

    /** Ejercicio 4 (1 punto): leer un entero binario y mostrar el árbol con ese ID. */
    private static void ejercicio4LeerIdYMostrarArbol(
            Path ficheroId, HashMap<Integer, Arbol> arboles) {
        // TODO: leer un entero binario de 32 bits (formato de DataInputStream.readInt).
        // Buscar por clave y mostrar el árbol con su toString(), o indicar que no existe.
        // Gestionar recursos y errores, incluido un fichero incompleto.
    }

    /** Ejercicio 5 (2 puntos): errores por línea y duplicados dentro del fichero 3. */
    private static HashMap<Integer, Arbol> ejercicio5LeerArbolesConErrores(Path entrada) {
        HashMap<Integer, Arbol> arboles = new HashMap<>();
        // TODO: leer en UTF-8 y tratar separadamente las dos excepciones y null.
        // Conservar la primera aparición de cada ID. Informar de línea y causa
        // para cada descarte; continuar con la siguiente línea. Mostrar el total.
        return arboles;
    }

    /** Ejercicio 6 (3 puntos): CREATE_NEW y actualización completa del fichero. */
    private static void ejercicio6FusionarYGenerar(
            HashMap<Integer, Arbol> arboles2, HashMap<Integer, Arbol> arboles3,
            Arbol arbol1, Path salida) {
        // TODO: usar OperacionesArboles.combinar(arboles2, arboles3).
        // Recorrer OperacionesArboles.ordenar(mapa) y escribir toJson() por línea.
        // Primera escritura: SALIDA_FINAL, UTF-8 y CREATE_NEW.
        // Si ya existe o la primera escritura falla, no realizar la Parte B.
        // Solo tras crear Y terminar la primera escritura: llamar a
        // incorporarSiAusente(mapa, arbol1), ordenar y reescribir todo truncando.
    }

}