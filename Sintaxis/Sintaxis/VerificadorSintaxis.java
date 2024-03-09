import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class VerificadorSintaxis extends JFrame {

    private JTextArea codigoTextArea;
    private JTextArea erroresTextArea;
    private JButton verificarButton;
    
    public VerificadorSintaxis() {
        setTitle("Verificador de Sintaxis");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
								
        codigoTextArea = new JTextArea();
        erroresTextArea = new JTextArea();
        verificarButton = new JButton("Verificar Sintaxis");

        setLayout(new BorderLayout());

        JPanel panelCodigo = new JPanel(new BorderLayout());
        panelCodigo.add(new JScrollPane(codigoTextArea), BorderLayout.CENTER);
        panelCodigo.setBorder(BorderFactory.createTitledBorder("Código"));

        JPanel panelErrores = new JPanel(new BorderLayout());
        JScrollPane erroresScrollPane = new JScrollPane(erroresTextArea);
        erroresScrollPane.setPreferredSize(new Dimension(300, getHeight())); 
        panelErrores.add(erroresScrollPane, BorderLayout.CENTER);
        panelErrores.setBorder(BorderFactory.createTitledBorder("Errores"));

        add(panelCodigo, BorderLayout.CENTER);
        add(panelErrores, BorderLayout.EAST);
        add(verificarButton, BorderLayout.SOUTH);
								
        verificarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                verificarSintaxis();
            }
        });
    }
    
    private void verificarSintaxis() {
        String codigo = codigoTextArea.getText().trim(); 
        String mensajesError = obtenerMensajesError(codigo);

        if (mensajesError.isEmpty()) {
            String mensaje = obtenerMensaje(codigo);
            JOptionPane.showMessageDialog(this, mensaje);
        } else {
            erroresTextArea.setText(mensajesError);
        }
    }

    private String obtenerMensajesError(String codigo) {
        StringBuilder mensajesError = new StringBuilder();
        String[] lineas = codigo.split("\n");
								
        for (String linea : lineas) {
            if (!linea.trim().endsWith("#")) {
                mensajesError.append("Cada línea de código debe terminar con '#'.").append("\n");
            }
        }

        // Verificar si la estructura general es correcta
        if (!(codigo.startsWith("inicio#") && codigo.endsWith("out#"))) {
            // Verificar si falta 'inicio#' o 'out#'
            if (!codigo.startsWith("inicio#")) {
                mensajesError.append("Falta la palabra 'inicio#'.\n");
            }
            if (!codigo.endsWith("out#")) {
                mensajesError.append("Falta la palabra 'out#'.\n");
            }
        }
								
        int inicioMensaje = codigo.indexOf("Mensaje(\"");
        int finMensaje = codigo.indexOf("\")#", inicioMensaje);

        while (inicioMensaje != -1 && finMensaje != -1) {
            String mensaje = codigo.substring(inicioMensaje + 9, finMensaje);
            if (mensaje.contains("#")) {
                mensajesError.append("El mensaje no debe contener el carácter '#'.\n");
            }

            inicioMensaje = codigo.indexOf("Mensaje(\"", finMensaje + 1);
            finMensaje = codigo.indexOf("\")#", inicioMensaje);
        }
								
        if (inicioMensaje == -1 && finMensaje == -1) {
            mensajesError.append("La palabra 'Mensaje' debe estar presente y seguida por un paréntesis y una cadena entre comillas.\n");
        }

        return mensajesError.toString();
    }


    private String obtenerMensaje(String codigo) {
        int inicioMensaje = codigo.indexOf("Mensaje(\"");
        int finMensaje = codigo.indexOf("\")#", inicioMensaje);

        if (inicioMensaje != -1 && finMensaje != -1) {
            return codigo.substring(inicioMensaje + 9, finMensaje);
        } else {
            return "No se pudo extraer el mensaje.";
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                VerificadorSintaxis gui = new VerificadorSintaxis();
                gui.setVisible(true);
            }
        });
    }
}
