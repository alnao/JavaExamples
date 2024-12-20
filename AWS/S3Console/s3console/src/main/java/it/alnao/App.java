package it.alnao;

import javax.swing.SwingUtilities;

import it.alnao.aws.s3console.view.S3CommanderFrame;

public class App 
{
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            S3CommanderFrame frame = new S3CommanderFrame();
            frame.setVisible(true);
        });
    }
    
}
