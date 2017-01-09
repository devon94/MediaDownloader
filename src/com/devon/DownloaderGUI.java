package com.devon;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by DevonDeonarine on 2017-01-03.
 */
public class DownloaderGUI extends JFrame
{

    public static ArrayList<URL> urls = new ArrayList<URL>();

    public static JFrame frame = new JFrame("Downloader");
    public static JPanel panel = new JPanel();
    public static JLabel statusLabel = new JLabel("Downloading" + "!");
    public static JLabel urlLabel = new JLabel("URL: ");
    public static JButton downloadButton = new JButton("Download");
    public static JTextField urlInput = new JTextField();
    public static JProgressBar progressBar = new JProgressBar(0, 100);

    public Downloader downloader;
    public static DownloaderGUI instance;

    public DownloaderGUI(){
        downloader = new Downloader();
        createView();
        setTitle("Downloader");
        setSize(300, 100);
        setResizable(true);
        setMinimumSize(new Dimension(325, 125));
        setMaximumSize(new Dimension(325, 125));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    public void createView()
    {
        panel.setLayout(new FlowLayout());
        panel.add(urlLabel);
        urlInput.setColumns(20);
        panel.add(urlInput);
        panel.add(downloadButton);
        progressBar.setPreferredSize(new Dimension(250, 25));
        panel.add(progressBar);
        getContentPane().add(panel);

        downloadButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
                progressBar.setValue(0);
                String urlString = urlInput.getText();
                try
                {
                    URL in = new URL(urlString);
                    downloadButton.setEnabled(false);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            downloader.save(in);
                        }
                    }).start();
                }
                catch (IOException err)
                {
                    print("" + err);
                    JOptionPane.showMessageDialog(null, "Please enter a valid URL!", "Oops", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
    }

    public static DownloaderGUI getInstance(){
        return instance;
    }

    public static void print(String string)
    {
        System.out.println(string);
    }

    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                instance = new DownloaderGUI();
                instance.setVisible(true);
            }
        });
    }

}