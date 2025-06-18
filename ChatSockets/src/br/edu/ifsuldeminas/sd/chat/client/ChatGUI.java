package br.edu.ifsuldeminas.sd.chat.client;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.swing.*;

import br.edu.ifsuldeminas.sd.chat.ChatException;
import br.edu.ifsuldeminas.sd.chat.ChatFactory;
import br.edu.ifsuldeminas.sd.chat.MessageContainer;
import br.edu.ifsuldeminas.sd.chat.Sender;

public class ChatGUI extends JFrame implements MessageContainer {

    private static final long serialVersionUID = 1L; 
    private JTextField localPortField, remotePortField, messageField, nameField, remoteIpField; 
    private JTextArea chatArea; 
    private JButton connectButton, sendButton; 
    private JCheckBox tcpModeCheckBox; 
    private Sender sender; 
    private String userName; 

    public ChatGUI() {
        super("Chat TCP/UDP");
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel"); 
        } catch (Exception e) {
            System.err.println("Não foi possível carregar o Nimbus Look and Feel. Usando o padrão.");
        }

        setSize(600, 500); //
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //
        setLayout(new BorderLayout(10, 10)); //

        // --- Painel Superior para Configurações (GridBagLayout) ---
        JPanel configPanel = new JPanel(new GridBagLayout());
        configPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); //
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); 
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Seu Nome
        gbc.gridx = 0;
        gbc.gridy = 0;
        configPanel.add(new JLabel("Seu Nome:"), gbc); //
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 3; 
        nameField = new JTextField(); 
        configPanel.add(nameField, gbc);

        
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1; 
        configPanel.add(new JLabel("IP Remoto:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        remoteIpField = new JTextField("localhost"); 
        configPanel.add(remoteIpField, gbc);

        // Porta Local
        gbc.gridx = 2;
        gbc.gridy = 1;
        configPanel.add(new JLabel("Porta Local:"), gbc); 
        gbc.gridx = 3;
        gbc.gridy = 1;
        localPortField = new JTextField("3000"); 
        configPanel.add(localPortField, gbc);

        // Porta Remota
        gbc.gridx = 0;
        gbc.gridy = 2;
        configPanel.add(new JLabel("Porta Remota:"), gbc); 
        gbc.gridx = 1;
        gbc.gridy = 2;
        remotePortField = new JTextField("3001"); 
        configPanel.add(remotePortField, gbc);

     
        gbc.gridx = 2;
        gbc.gridy = 2;
        tcpModeCheckBox = new JCheckBox("Usar TCP");
        tcpModeCheckBox.setSelected(true); 
        configPanel.add(tcpModeCheckBox, gbc);

        // Botão Conectar
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 4; 
        gbc.fill = GridBagConstraints.NONE; 
        gbc.anchor = GridBagConstraints.CENTER; 
        connectButton = new JButton("Conectar"); 
        configPanel.add(connectButton, gbc);

        add(configPanel, BorderLayout.NORTH); 

        // --- Área de Chat ---
        chatArea = new JTextArea(); //
        chatArea.setEditable(false); //
        JScrollPane scrollPane = new JScrollPane(chatArea); //
        add(scrollPane, BorderLayout.CENTER); //

        // --- Painel Inferior para Envio de Mensagem ---
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 0)); //
        messageField = new JTextField(); //
        bottomPanel.add(messageField, BorderLayout.CENTER); //
        sendButton = new JButton("Enviar"); //
        bottomPanel.add(sendButton, BorderLayout.EAST); //
        add(bottomPanel, BorderLayout.SOUTH); //

        messageField.setEnabled(false); //
        sendButton.setEnabled(false); //

        setupActionListeners(); //

        setLocationRelativeTo(null); // Centraliza a janela
        setVisible(true); //
    }

    private void setupActionListeners() { //
        connectButton.addActionListener(e -> connect()); //
        sendButton.addActionListener(e -> sendMessage()); //
        messageField.addActionListener(e -> sendMessage()); //
    }

    private void connect() {
        try {
            userName = nameField.getText().trim(); //
            String remoteIp = remoteIpField.getText().trim(); // Novo campo
            int localPort = Integer.parseInt(localPortField.getText().trim()); //
            int serverPort = Integer.parseInt(remotePortField.getText().trim()); //
            boolean isTcp = tcpModeCheckBox.isSelected(); // Novo: verifica se TCP foi selecionado

            if (userName.isEmpty()) { //
                JOptionPane.showMessageDialog(this, "Por favor, informe seu nome.", "Erro", JOptionPane.ERROR_MESSAGE); //
                return;
            }
            if (remoteIp.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor, informe o IP Remoto.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            
            this.sender = ChatFactory.build(isTcp, remoteIp, serverPort, localPort, this); //

            nameField.setEditable(false); 
            remoteIpField.setEditable(false); 
            localPortField.setEditable(false); 
            remotePortField.setEditable(false); 
            tcpModeCheckBox.setEnabled(false); 
            connectButton.setEnabled(false); 
            messageField.setEnabled(true); 
            sendButton.setEnabled(true); 
            
            chatArea.append("Conectado como " + userName + (isTcp ? " (TCP)" : " (UDP)") + "!\n"); // Feedback visual

        } catch (NumberFormatException ex) { //
            JOptionPane.showMessageDialog(this, "As portas devem ser números válidos.", "Erro de Formato", JOptionPane.ERROR_MESSAGE); //
        } catch (ChatException ex) { //
            JOptionPane.showMessageDialog(this, "Erro ao conectar: " + ex.getCause().getMessage(), "Erro de Conexão", JOptionPane.ERROR_MESSAGE); //
        } catch (IllegalArgumentException ex) { 
            JOptionPane.showMessageDialog(this, "Erro de validação: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void sendMessage() { //
        String messageText = messageField.getText().trim(); //
        if (!messageText.isEmpty() && sender != null) { //
            try {
                String messageToSend = String.format("%s%s%s", messageText, MessageContainer.FROM, userName); //
                sender.send(messageToSend); //

                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
                chatArea.append(String.format("[%s] Eu> %s\n", timestamp, messageText)); // Com timestamp e "Eu"
                messageField.setText(""); //
            } catch (ChatException ex) { //
                JOptionPane.showMessageDialog(this, "Erro ao enviar mensagem: " + ex.getMessage(), "Erro de Envio", JOptionPane.ERROR_MESSAGE); //
            }
        }
    }

    @Override //
    public void newMessage(String message) { //
        if (message == null || message.trim().isEmpty()) { //
            return;
        }

        String[] messageParts = message.split(MessageContainer.FROM); //
        String content = messageParts[0]; //
        String from = (messageParts.length > 1) ? messageParts[1].trim() : "Desconhecido"; //

        SwingUtilities.invokeLater(() -> { //
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            chatArea.append(String.format("[%s] %s> %s\n", timestamp, from, content)); // Com timestamp
            chatArea.setCaretPosition(chatArea.getDocument().getLength()); //
        });
    }

    public static void main(String[] args) { //
        SwingUtilities.invokeLater(ChatGUI::new); //
    }
}