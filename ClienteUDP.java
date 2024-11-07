import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Scanner;

public class ClienteUDP {
    public static void main(String[] args) {
        String ipServidor = "172.29.39.181"; // Cambié localhost por la IP del servidor
        int puertoServidor = 1234;
        int puertoCliente = 4321;

        try (DatagramSocket socketCliente = new DatagramSocket(puertoCliente)) {
            InetAddress direccionServidor = InetAddress.getByName(ipServidor);
            Scanner scanner = new Scanner(System.in);

            String mensajeEnviar = " ";
            while (true) {
                if ("chao".equalsIgnoreCase(mensajeEnviar)) {
                    break;
                } else if (" ".equalsIgnoreCase(mensajeEnviar)) {
                    enviarMensajeUDP(socketCliente, direccionServidor, puertoServidor, "");
                    mensajeEnviar = "";
                } else {
                    // Leer respuesta del servidor
                    String mensajeRecibido = recibirMensajeUDP(socketCliente);
                    if (mensajeRecibido.equals("la respuesta es correcta")
                            || mensajeRecibido.equals("la respuesta es incorrecta")) {
                        System.out.println("******" + mensajeRecibido + "******");
                        if (mensajeRecibido.startsWith("TIENES UN TOTAL DE:")) {
                            socketCliente.close();
                            break;
                        }
                    } else {
                        System.out.println(mensajeRecibido);
                        if (mensajeRecibido.startsWith("----")) {
                            socketCliente.close();
                            break;
                        }
                        // Escribir respuesta
                        System.out.println("Escribe tu respuesta: ");
                        mensajeEnviar = scanner.nextLine();
                        // Enviar mensaje
                        enviarMensajeUDP(socketCliente, direccionServidor, puertoServidor, mensajeEnviar);
                    }
                }
            }

            scanner.close();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void enviarMensajeUDP(DatagramSocket socketCliente, InetAddress direccionServidor,
            int puertoServidor, String mensaje) throws IOException {
        byte[] buffer = mensaje.getBytes();
        DatagramPacket paqueteEnvio = new DatagramPacket(buffer, buffer.length, direccionServidor, puertoServidor);
        socketCliente.send(paqueteEnvio);
    }

    private static String recibirMensajeUDP(DatagramSocket socketCliente) throws IOException {
        byte[] buffer = new byte[1024];
        DatagramPacket paqueteRecibo = new DatagramPacket(buffer, buffer.length);
        socketCliente.receive(paqueteRecibo);
        return new String(paqueteRecibo.getData(), 0, paqueteRecibo.getLength());
    }
}
