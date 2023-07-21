package br.com.guigb9.videostreaming.service;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@Service
public class VideoService {

    public String convertToHLS(String inputFilePath, String outputDirectory, String outputFileName) {
        try {
            // Comando do ffmpeg para converter o vídeo para HLS
            String[] command = {
                    "ffmpeg",
                    "-i", inputFilePath,
                    "-profile:v", "baseline",
                    "-level", "3.0",
                    "-start_number", "0",
                    "-hls_time", "10",
                    "-hls_list_size", "0",
                    "-f", "hls",
                    outputDirectory + outputFileName + ".m3u8"
            };

            ProcessBuilder processBuilder = new ProcessBuilder(command);
            Process process = processBuilder.start();

            InputStream inputStream = process.getInputStream();
            InputStream errorStream = process.getErrorStream();
            BufferedReader inputReader = new BufferedReader(new InputStreamReader(inputStream));
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(errorStream));
            String line;
            while ((line = inputReader.readLine()) != null) {
                System.out.println(line);
            }
            while ((line = errorReader.readLine()) != null) {
                System.out.println(line);
            }

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                return outputDirectory;
            } else {
                return "";
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return outputDirectory;
    }


}
