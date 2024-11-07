import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ServidorUDP {
    public static void main(String[] args) {
        int puertoServidor = 1234;

        try (DatagramSocket socketServidor = new DatagramSocket(puertoServidor)) {
            String[] preguntas = {
                    "¿Cuál es la capital de Ecuador?",
                    "¿El elemento hidrógeno se conoce con el símbolo de ?",
                    "¿Cuál es el número atómico del carbono?",
                    "¿Cuál es el número pi?",
                    "¿Cuál es el planeta más grande del sistema solar?"
            };
            String[] respuestas = {
                    "Quito",
                    "H",
                    "6",
                    "3.14",
                    "Jupiter"
            };
            int puntos = 0;
            int contadorMensajes = 1; // Contador para enumerar los mensajes.

            File archivo = new File("RegistroRespuestas.txt");

            if (!archivo.exists()) {
                archivo.createNewFile();
            }

            while (true) {
                String mensajeRecibido = recibirMensajeUDP(socketServidor);
                if (mensajeRecibido.equals(" ")) {
                    System.out.println("Se conectó un cliente en espacio");
                    continue;
                } else if (mensajeRecibido.equals("chao")) {
                    break;
                } else {
                    System.out.println("Se conectó un cliente");
                    // Enviar preguntas y registrar respuestas
                    for (int i = 0; i < preguntas.length; i++) {
                        enviarMensajeUDP(socketServidor, mensajeRecibido, preguntas[i]);
                        String respuestaCliente = recibirMensajeUDP(socketServidor);
                        boolean respuestaCorrecta = respuestaCliente.equals(respuestas[i]);

                        // Enviar respuesta al cliente
                        if (respuestaCorrecta) {
                            enviarMensajeUDP(socketServidor, mensajeRecibido, "la respuesta es correcta");
                            puntos++;
                        } else {
                            enviarMensajeUDP(socketServidor, mensajeRecibido, "la respuesta es incorrecta");
                        }

                        // Registrar la respuesta en el archivo
                        registrarRespuestaEnArchivo(archivo, contadorMensajes, mensajeRecibido, respuestaCliente,
                                respuestaCorrecta);
                        contadorMensajes++;
                    }
                    enviarMensajeUDP(socketServidor, mensajeRecibido,
                            "----" + " TIENES UN TOTAL DE: " + puntos + " puntos de " + preguntas.length + " ----");
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void registrarRespuestaEnArchivo(File archivo, int contadorMensajes, String ipCliente,
            String respuesta, boolean correcta) throws IOException {
        // Obtener fecha y hora actuales
        String fechaHora = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        // Escribir en el archivo
        try (FileWriter writer = new FileWriter(archivo, true)) {
            writer.append("Mensaje #" + contadorMensajes + " - Fecha y hora: " + fechaHora + " - IP Cliente: "
                    + ipCliente + " - Respuesta: " + respuesta + " - Correcta: " + correcta + "\n");
        }
    }

    private static void enviarMensajeUDP(DatagramSocket socketServidor, String destinatario, String mensaje)
            throws IOException {
        byte[] buffer = mensaje.getBytes();
        InetAddress direccionDestino = InetAddress.getByName(destinatario);
        int puertoDestino = 4321;
        DatagramPacket paqueteEnvio = new DatagramPacket(buffer, buffer.length, direccionDestino, puertoDestino);
        socketServidor.send(paqueteEnvio);
    }

    private static String recibirMensajeUDP(DatagramSocket socketServidor) throws IOException {
        byte[] buffer = new byte[1024];
        DatagramPacket paqueteRecibo = new DatagramPacket(buffer, buffer.length);
        socketServidor.receive(paqueteRecibo);
        return new String(paqueteRecibo.getData(), 0, paqueteRecibo.getLength());
    }
}
