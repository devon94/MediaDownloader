package com.devon;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by DevonDeonarine on 2017-01-03.
 */

public class Downloader {

    private static String imgRegex = "<img\\s+[^>]*src=\"([^\"]*)\"";
    private static Pattern imgPattern = Pattern.compile(imgRegex);

    private static String mp4Regex = "<source\\s+[^>]*src=\"([^\"]*)\"";
    private static Pattern mp4Pattern = Pattern.compile(mp4Regex);

    private static String titleRegex = "<title>(.*?)</title>";
    private static Pattern titlePattern = Pattern.compile(titleRegex);

    public void save(URL url)
    {

        BufferedReader in = null;
        String inputLine;
        String allHTML = null;
        StringBuilder s = new StringBuilder();
        int totalPics = 0;
        int totalVids = 0;

        try
        {
            in = new BufferedReader(new InputStreamReader(url.openStream()));
            while ((inputLine = in.readLine()) != null){
                s.append(inputLine);
            }
            allHTML = s.toString();
        }
        catch (IOException e)
        {
            print(e.getMessage());
            return;

        }

        Matcher titleMatcher = titlePattern.matcher(allHTML);
        titleMatcher.find();

        String title = titleMatcher.group();
        String folder = title.substring(title.indexOf('>') + 1,title.lastIndexOf('<'));

        boolean imgFound = false;
        Matcher imgCounter = imgPattern.matcher(allHTML);

        while ((imgFound = imgCounter.find()))
        {
            if (imgFound)
            {
                totalPics++;
            }
        }

        boolean vidFound = false;
        Matcher vidCounter = mp4Pattern.matcher(allHTML);
        while ((vidFound = vidCounter.find()))
        {
            if (vidFound)
            {
                totalVids++;
            }
        }

        imgFound = false;
        Matcher imgMatcher = imgPattern.matcher(allHTML);

        int downloadedPics = 0;
        new File(folder).mkdir();
        while ((imgFound = imgMatcher.find()))
        {
            if (imgFound)
            {
                String fileName;
                String image = imgMatcher.group();
                String newUrl = image.substring(image.indexOf('"') + 1, image.lastIndexOf('"'));
                if (newUrl.startsWith("//"))
                {
                    newUrl = "http:" + newUrl;
                }
                if (!newUrl.startsWith("http"))
                {
                    newUrl = "http://" + newUrl;
                }
                fileName = image.substring(image.lastIndexOf('/') + 1 ,image.length() - 1);
                try
                {
                    downloadedPics++;
                    int calc = (int) (((double)downloadedPics/(double)(totalPics+totalVids)) * 100);
                    DownloaderGUI.getInstance().progressBar.setValue(calc);
                    saveFile(newUrl, folder, fileName);
                }
                catch(IOException err)
                {
                    print("" + err);
                }
            }
        }

        int downloadedVids = 0;
        vidFound = false;
        Matcher vidMatcher = mp4Pattern.matcher(allHTML);
        while ((vidFound = vidMatcher.find()))
        {
            if (vidFound)
            {
                String fileName;
                String video = vidMatcher.group();
                String newUrl = video.substring(video.indexOf("src=") + 5, video.lastIndexOf('"'));
                if (newUrl.startsWith("//"))
                {
                    newUrl = "https:" + newUrl;
                }
                if (!newUrl.startsWith("http"))
                {
                    newUrl = "http://" + newUrl;
                }
                fileName = video.substring(video.lastIndexOf('/') + 1 ,video.length() - 1);
                try
                {
                    downloadedVids++;
                    int calc = (int) ((((double)downloadedPics + (double) downloadedVids)/(double)(totalPics+totalVids)) * 100);
                    DownloaderGUI.getInstance().progressBar.setValue(calc);
                    saveFile(newUrl, folder, fileName);
                }
                catch(IOException err)
                {
                    print("" + err);
                }
            }
        }

        try
        {
            in.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        JOptionPane.showMessageDialog(null, "Downloaded" + downloadedPics + "/" + totalPics + " images & " + downloadedVids + "/" + totalVids + " videos!", "Done", JOptionPane.WARNING_MESSAGE);
        DownloaderGUI.getInstance().downloadButton.setEnabled(true);
    }

    public void saveFile(String imageUrl, String folderName, String destinationFile) throws IOException
    {
        URL url = new URL(imageUrl);
        InputStream is = url.openStream();
        OutputStream os = new FileOutputStream(folderName + "/"+ destinationFile);

        byte[] b = new byte[2048];
        int length;

        while ((length = is.read(b)) != -1) {
            os.write(b, 0, length);
        }

        is.close();
        os.close();
    }


    public static void print(String string)
    {
        System.out.println(string);
    }
}