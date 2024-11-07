import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class HiloClienteUDP extends Thread {
    private DatagramSocket socketServidor;
    private InetAddress direccionCliente;
    private int puertoCliente;

    public HiloClienteUDP(DatagramSocket socketServidor, InetAddress direccionCliente, int puertoCliente) {
        this.socketServidor = socketServidor;
        this.direccionCliente = direccionCliente;
        this.puertoCliente = puertoCliente;
    }

    public void run() {
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

        try {
            String mensajeRecibido;
            while ((mensajeRecibido = recibirMensajeUDP(socketServidor)) != null) {
                if (mensajeRecibido.equals(" ")) {
                    // CAPTURAR IP Y PUERTO DEL CLIENTE
                    DatagramPacket paqueteRecibo = new DatagramPacket(new byte[1024], 1024);
                    socketServidor.receive(paqueteRecibo);
                    InetAddress IP_cliente = paqueteRecibo.getAddress(); // IP del cliente
                    int puerto_cliente = paqueteRecibo.getPort(); // Puerto del cliente

                    System.out.println("IF Conexión desde el cliente: " + IP_cliente + ":" + puerto_cliente);

                    // Continuamos con el ciclo
                    continue;
                } else if (mensajeRecibido.equals("chao")) {
                    break;
                } else {
                    // CAPTURAR IP Y PUERTO DEL CLIENTE
                    DatagramPacket paqueteRecibo = new DatagramPacket(new byte[1024], 1024);
                    socketServidor.receive(paqueteRecibo);
                    InetAddress IP_cliente = paqueteRecibo.getAddress(); // IP del cliente
                    int puerto_cliente = paqueteRecibo.getPort(); // Puerto del cliente

                    System.out.println("ELSE Conexión desde el cliente: " + IP_cliente + ":" + puerto_cliente);
                    for (int i = 0; i < preguntas.length; i++) {
                        // Enviar pregunta al cliente
                        enviarMensajeUDP(socketServidor, direccionCliente, puertoCliente, preguntas[i]);

                        // Recibir respuesta del cliente
                        String respuestaCliente = recibirMensajeUDP(socketServidor);
                        boolean respuestaCorrecta = respuestaCliente.equals(respuestas[i]);

                        // Enviar la respuesta al cliente
                        if (respuestaCorrecta) {
                            enviarMensajeUDP(socketServidor, direccionCliente, puertoCliente,
                                    "la respuesta es correcta");
                            puntos++;
                        } else {
                            enviarMensajeUDP(socketServidor, direccionCliente, puertoCliente,
                                    "la respuesta es incorrecta");
                        }
                    }

                    // Informar al cliente sobre su total de puntos
                    enviarMensajeUDP(socketServidor, direccionCliente, puertoCliente,
                            "---- TIENES UN TOTAL DE: " + puntos + " puntos de " + preguntas.length + " ----");
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void enviarMensajeUDP(DatagramSocket socketServidor, InetAddress direccionCliente, int puertoCliente,
            String mensaje) throws IOException {
        byte[] buffer = mensaje.getBytes();
        DatagramPacket paqueteEnvio = new DatagramPacket(buffer, buffer.length, direccionCliente, puertoCliente);
        socketServidor.send(paqueteEnvio);
    }

    private static String recibirMensajeUDP(DatagramSocket socketServidor) throws IOException {
        byte[] buffer = new byte[1024];
        DatagramPacket paqueteRecibo = new DatagramPacket(buffer, buffer.length);
        socketServidor.receive(paqueteRecibo);
        return new String(paqueteRecibo.getData(), 0, paqueteRecibo.getLength());
    }
}
