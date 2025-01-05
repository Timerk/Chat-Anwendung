package Gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;


import Backend.FileMessage;
import java.awt.*;
import java.util.ArrayList;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.FileOutputStream;
import java.io.File;
import java.io.IOException;

public class FileReceiver extends JFrame{

    private ArrayList<FileMessage> files;

    public FileReceiver(ArrayList<FileMessage> files){

        this.files = files;

        this.setTitle("Scrollable JPanel Example");
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        
        JLabel titleLabel = new JLabel("Empfangene Dateien :");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 25));
        titleLabel.setBorder(new EmptyBorder(20, 0, 10 ,0));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        this.add(titleLabel);

        for(FileMessage file : files){
            JPanel panel = new JPanel();
            panel.setBorder(new EmptyBorder(10, 0, 10 ,0));
            JLabel label = new JLabel(file.getFileName());
            label.setFont(new Font("Arial", Font.BOLD, 20));
            label.addMouseListener(getMyMouseListeners());
            panel.add(label);
            mainPanel.add(panel);
        }

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        this.getContentPane().add(scrollPane);
        this.setSize(300, 400);
        this.setLocationRelativeTo(null);
        this.setVisible(true);

    }

    public MouseListener getMyMouseListeners(){

        return new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JLabel clickedLabel = (JLabel) e.getSource();
                String fileName = clickedLabel.getText();

                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Wählen Sie einen Speicherort");
                fileChooser.setSelectedFile(new File(fileName));

                int result = fileChooser.showOpenDialog(FileReceiver.this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    for (FileMessage fileMessage : files) {
                        if(fileMessage.getFileName().equals(fileName)){
                            FileOutputStream fileOutputStream;
                            try {
                                fileOutputStream = new FileOutputStream(selectedFile);
                                fileOutputStream.write(fileMessage.getFileData());
                                fileOutputStream.close();
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }  
                        }
                    }
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void mousePressed(MouseEvent e) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                // TODO Auto-generated method stub
                
            }
        };
    }
}


