package com.tuanzeebee.springboot.demosecurity.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequestMapping("/python")
public class PythonScriptController {

    @Autowired
    private ResourceLoader resourceLoader;

    @PostMapping("/run-script1")
    @ResponseBody
    public String runScript1(@RequestParam(defaultValue = "10") int num_recipes) {
        try {
            // Lấy đường dẫn tuyệt đối của file Python
            String scriptPath = resourceLoader.getResource("classpath:templates/python/getdatarecipes.py").getFile().getAbsolutePath();
            
            // Kiểm tra file có tồn tại không
            File scriptFile = new File(scriptPath);
            if (!scriptFile.exists()) {
                return "Không tìm thấy file script: " + scriptPath;
            }

            ProcessBuilder processBuilder = new ProcessBuilder("python", scriptPath, String.valueOf(num_recipes));
            processBuilder.redirectErrorStream(true); // Chuyển hướng stderr vào stdout
            Process process = processBuilder.start();
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                return "Script getdatarecipes.py chạy thành công!\n" + output.toString();
            } else {
                return "Script getdatarecipes.py chạy thất bại với mã lỗi: " + exitCode + "\nOutput: " + output.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Lỗi khi chạy script getdatarecipes.py: " + e.getMessage();
        }
    }

    @PostMapping("/run-script2")
    @ResponseBody
    public String runScript2() {
        try {
            // Lấy đường dẫn tuyệt đối của file Python
            String scriptPath = resourceLoader.getResource("classpath:templates/python/changeicon.py").getFile().getAbsolutePath();
            
            // Kiểm tra file có tồn tại không
            File scriptFile = new File(scriptPath);
            if (!scriptFile.exists()) {
                return "Không tìm thấy file script: " + scriptPath;
            }

            ProcessBuilder processBuilder = new ProcessBuilder("python", scriptPath);
            processBuilder.redirectErrorStream(true); // Chuyển hướng stderr vào stdout
            Process process = processBuilder.start();
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                return "changeicon chạy thành công!\n" + output.toString();
            } else {
                return "changeicon chạy thất bại với mã lỗi: " + exitCode + "\nOutput: " + output.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Lỗi khi chạy changeicon: " + e.getMessage();
        }
    }
} 