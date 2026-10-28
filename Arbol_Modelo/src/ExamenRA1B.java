import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

/** Solo se completan los seis métodos TODO. Consola y coordinación resueltas. */
public final class ExamenRA1B {
    private static final Path DIRECTORIO_ENTRADA = Path.of("datos", "entrada");
    private static final Path FICHERO_1 = DIRECTORIO_ENTRADA.resolve("arboles1.jsonl");
    private static final Path FICHERO_2 = DIRECTORIO_ENTRADA.resolve("arboles2.jsonl");
    private static final Path FICHERO_3 = DIRECTORIO_ENTRADA.resolve("arboles3.jsonl");
    private static final Path FICHERO_ID = DIRECTORIO_ENTRADA.resolve("id_arbol.bin");
    public static final String SALIDA_NOMBRE = "arbol1.txt";
    public static final String SALIDA_FINAL = "arboles_ordenados.jsonl";

    private ExamenRA1B() {
    }

    public static void main(String[] args) {
        System.out.println("=== EXAMEN B: ÁRBOLES Y FICHEROS JSON ===");
        System.out.println("Directorio de entrada: " + DIRECTORIO_ENTRADA.toAbsolutePath());

        try (Scanner teclado = new Scanner(System.in)) {
            System.out.print("Introduce la ruta del directorio de salida: ");
            String ruta = teclado.nextLine().trim();
            if (ruta.isEmpty()) {
                System.err.println("La ruta de salida no puede estar vacía.");
                return;
            }
            Path salida;
            try {
                salida = Path.of(ruta);
            } catch (InvalidPathException e) {
                System.err.println("Ruta de salida incorrecta: " + e.getMessage());
                return;
            }
            if (!prepararDirectorioSalida(salida)) {
                System.err.println("No se puede continuar sin un directorio válido.");
                return;
            }

            Arbol arbol1 = ejercicio2LeerYEscribirArbol(FICHERO_1, salida);
            HashMap<Integer, Arbol> arboles2 = ejercicio3LeerArboles(FICHERO_2);
            ejercicio4LeerIdYMostrarArbol(FICHERO_ID, arboles2);
            HashMap<Integer, Arbol> arboles3 = ejercicio5LeerArbolesConErrores(FICHERO_3);
            ejercicio6FusionarYGenerar(arboles2, arboles3, arbol1, salida);
        }
    }

    /** Ejercicio 1 (1 punto): comprobar/crear directorio e informar de errores. */
    private static boolean prepararDirectorioSalida(Path salida) {
        try {
            if(Files.exists(salida)) {
                if(Files.isDirectory(salida)) {
                    System.out.println("Ya existe el directorio en el sistema.");
                    return true;
                } else {
                    System.out.println("Error: La ruta existe pero no es un directorio.");
                    return false;
                }
            } else {
                Files.createDirectories(salida);
                System.out.println("Se ha creado el directorio correctamente.");
                return true;
            }
        } catch (IOException e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
            return false;
        }
    }

    /** Ejercicio 2 (1 punto): leer el árbol y escribir su nombre en arbol1.txt. */
    private static Arbol ejercicio2LeerYEscribirArbol(Path entrada, Path salida) {
        Arbol arbolValido = null;

        try (BufferedReader bf = Files.newBufferedReader(entrada)){
            String linea;
            if((linea = bf.readLine()) != null) {
                arbolValido = ParseadorArboles.parsear(linea);
            }
        } catch (IOException e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
        } catch (FormatoJsonIncorrectoException e) {
            System.out.println("Formato Json incorrecto: " + e.getMessage());
        } catch (TipoDatoIncorrectoException e) {
            System.out.println("Tipo de dato incorrecto: " + e.getMessage());
        }

        if(arbolValido != null) {
            Path ficheroSalida = salida.resolve("arbol1.txt");
            try(BufferedWriter bw = Files.newBufferedWriter(ficheroSalida)) {
                bw.write(arbolValido.getId());
                bw.newLine();
            } catch (IOException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }

        return arbolValido;
    }

    /** Ejercicio 3 (2 puntos): leer 200 objetos JSON en un HashMap por ID. */
    private static HashMap<Integer, Arbol> ejercicio3LeerArboles(Path entrada) {
        HashMap<Integer, Arbol> arboles = new HashMap<>();
        String linea;
        try(BufferedReader bf = Files.newBufferedReader(entrada)){
            while( (linea = bf.readLine()) != null) {
                if(!linea.trim().isEmpty()) {
                    try {
                        Arbol arbol = ParseadorArboles.parsear(linea);
                        if(arbol != null) {
                            arboles.put(arbol.getId(), arbol);
                        }
                    } catch (FormatoJsonIncorrectoException e) {
                        System.out.println("Formato Json incorrecto: " + e.getMessage());
                    } catch (TipoDatoIncorrectoException e) {
                        System.out.println("Tipo de dato incorrecto: " + e.getMessage());
                    }
                }
            }
            System.out.println("Arboles introducidos: " + arboles.size());
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }

        return arboles;
    }

    /** Ejercicio 4 (1 punto): leer un entero binario y mostrar el árbol con ese ID. */
    private static void ejercicio4LeerIdYMostrarArbol(Path ficheroId, HashMap<Integer, Arbol> arboles) {
        try(DataInputStream dis = new DataInputStream(Files.newInputStream(ficheroId))) {

            int id = dis.readInt();
            Arbol arbol = arboles.get(id);

            if(arbol != null) {
                System.out.println(arbol);
            } else {
                System.out.println("No existe un arbol con este ID.");
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /** Ejercicio 5 (2 puntos): errores por línea y duplicados dentro del fichero 3. */
    private static HashMap<Integer, Arbol> ejercicio5LeerArbolesConErrores(Path entrada) {
        HashMap<Integer, Arbol> arboles = new HashMap<>();
        try(BufferedReader br = Files.newBufferedReader(entrada)) {
            String linea;
            while((linea = br.readLine()) != null) {
                if(!linea.trim().isEmpty()) {
                    Arbol arbol = ParseadorArboles.parsear(linea);
                    int id = arbol.getId();

                    if(arboles.containsKey(id)) {
                        System.out.println("Linea: " + linea + ": Arbol eliminado por duplicarse.");
                    } else {
                        arboles.put(id, arbol);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (FormatoJsonIncorrectoException e) {
            System.out.println("Formato Json incorrecto: " + e.getMessage());
        } catch (TipoDatoIncorrectoException e) {
            System.out.println("Tipo de dato incorrecto: " + e.getMessage());
        }

        return arboles;
    }

    /** Ejercicio 6 (3 puntos): CREATE_NEW y actualización completa del fichero. */
    private static void ejercicio6FusionarYGenerar(HashMap<Integer, Arbol> arboles2, HashMap<Integer, Arbol> arboles3, Arbol arbol1, Path salida) {
        if(Files.exists(salida)) {
            System.out.println("El fichero ya existe.");
            return;
        }

        HashMap<Integer, Arbol> mapaCombinado = new HashMap<>();
        try(BufferedWriter bw = Files.newBufferedWriter(salida, StandardOpenOption.CREATE_NEW)) {
            mapaCombinado = OperacionesArboles.combinar(arboles2, arboles3);
            Iterable<Arbol> listaOrdenada = OperacionesArboles.ordenar(mapaCombinado);

            for(Arbol arbol : listaOrdenada) {
                bw.write(arbol.toJson());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
            return;
        }

        try(BufferedWriter bw = Files.newBufferedWriter(salida)) {
            OperacionesArboles.incorporarSiAusente(mapaCombinado, arbol1);
            Iterable<Arbol> listaOrdenada = OperacionesArboles.ordenar(mapaCombinado);

            for(Arbol arbol : listaOrdenada) {
                bw.write(arbol.toJson());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}