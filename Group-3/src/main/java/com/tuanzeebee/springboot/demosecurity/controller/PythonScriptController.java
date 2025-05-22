package com.tuanzeebee.springboot.demosecurity.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequestMapping("/python")
public class PythonScriptController {
    private static final Logger logger = LoggerFactory.getLogger(PythonScriptController.class);

    @Autowired
    private ResourceLoader resourceLoader;

    @Value("${python.path}")
    private String pythonPath;

    @Value("${python.script.path}")
    private String pythonScriptPath;

    @PostMapping("/run-script1")
    @ResponseBody
    public ResponseEntity<String> runScript1(@RequestParam(defaultValue = "10") int num_recipes) {
        try {
            logger.info("Bắt đầu chạy script getdatarecipes.py với num_recipes={}", num_recipes);
            
            // Kiểm tra Python có được cài đặt không
            if (!checkPythonInstallation()) {
                logger.error("Python không được cài đặt hoặc không thể chạy");
                return ResponseEntity.badRequest().body("Lỗi: Python không được cài đặt hoặc không thể chạy");
            }

            // Lấy đường dẫn tuyệt đối của file Python
            String scriptPath = getScriptPath("getdatarecipes.py");
            if (scriptPath == null) {
                logger.error("Không tìm thấy file script getdatarecipes.py");
                return ResponseEntity.badRequest().body("Lỗi: Không tìm thấy file script getdatarecipes.py");
            }

            logger.info("Đường dẫn script: {}", scriptPath);
            logger.info("Sử dụng Python path: {}", pythonPath);

            ProcessBuilder processBuilder = new ProcessBuilder(pythonPath, scriptPath, String.valueOf(num_recipes));
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            
            String output = readProcessOutput(process);
            int exitCode = process.waitFor();
            
            if (exitCode == 0) {
                logger.info("Script chạy thành công");
                return ResponseEntity.ok("Script getdatarecipes.py chạy thành công!\n" + output);
            } else {
                logger.error("Script chạy thất bại với mã lỗi: {}", exitCode);
                return ResponseEntity.badRequest().body("Script getdatarecipes.py chạy thất bại với mã lỗi: " + exitCode + "\nOutput: " + output);
            }
        } catch (Exception e) {
            logger.error("Lỗi khi chạy script", e);
            return ResponseEntity.internalServerError().body("Lỗi khi chạy script getdatarecipes.py: " + e.getMessage());
        }
    }

    @PostMapping("/run-script2")
    @ResponseBody
    public ResponseEntity<String> runScript2() {
        try {
            logger.info("Bắt đầu chạy script changeicon.py");
            
            // Kiểm tra Python có được cài đặt không
            if (!checkPythonInstallation()) {
                logger.error("Python không được cài đặt hoặc không thể chạy");
                return ResponseEntity.badRequest().body("Lỗi: Python không được cài đặt hoặc không thể chạy");
            }

            // Lấy đường dẫn tuyệt đối của file Python
            String scriptPath = getScriptPath("changeicon.py");
            if (scriptPath == null) {
                logger.error("Không tìm thấy file script changeicon.py");
                return ResponseEntity.badRequest().body("Lỗi: Không tìm thấy file script changeicon.py");
            }

            logger.info("Đường dẫn script: {}", scriptPath);
            logger.info("Sử dụng Python path: {}", pythonPath);

            ProcessBuilder processBuilder = new ProcessBuilder(pythonPath, scriptPath);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            
            String output = readProcessOutput(process);
            int exitCode = process.waitFor();
            
            if (exitCode == 0) {
                logger.info("Script chạy thành công");
                return ResponseEntity.ok("changeicon chạy thành công!\n" + output);
            } else {
                logger.error("Script chạy thất bại với mã lỗi: {}", exitCode);
                return ResponseEntity.badRequest().body("changeicon chạy thất bại với mã lỗi: " + exitCode + "\nOutput: " + output);
            }
        } catch (Exception e) {
            logger.error("Lỗi khi chạy script", e);
            return ResponseEntity.internalServerError().body("Lỗi khi chạy changeicon: " + e.getMessage());
        }
    }

    private boolean checkPythonInstallation() {
        try {
            logger.info("Kiểm tra Python installation với path: {}", pythonPath);
            
            // Danh sách các lệnh Python có thể có
            String[] pythonCommands = {"py", "python", "python3"};
            
            for (String cmd : pythonCommands) {
                try {
                    logger.info("Thử với lệnh: {}", cmd);
                    ProcessBuilder checkPython = new ProcessBuilder(cmd, "--version");
                    Process checkProcess = checkPython.start();
                    String versionOutput = readProcessOutput(checkProcess);
                    int checkExitCode = checkProcess.waitFor();
                    
                    if (checkExitCode == 0) {
                        logger.info("Tìm thấy Python version: {}", versionOutput.trim());
                        pythonPath = cmd; // Cập nhật pythonPath
                        return true;
                    }
                } catch (Exception e) {
                    logger.warn("Không thể chạy lệnh {}: {}", cmd, e.getMessage());
                }
            }
            
            // Kiểm tra trong PATH
            try {
                ProcessBuilder whereCmd = new ProcessBuilder("where", "python");
                Process whereProcess = whereCmd.start();
                String whereOutput = readProcessOutput(whereProcess);
                int whereExitCode = whereProcess.waitFor();
                
                if (whereExitCode == 0 && !whereOutput.trim().isEmpty()) {
                    logger.info("Tìm thấy Python trong PATH: {}", whereOutput.trim());
                    pythonPath = "python";
                    return true;
                }
            } catch (Exception e) {
                logger.warn("Không thể tìm Python trong PATH: {}", e.getMessage());
            }
            
            logger.error("Không tìm thấy Python installation");
            return false;
        } catch (Exception e) {
            logger.error("Lỗi khi kiểm tra Python", e);
            return false;
        }
    }

    private String getScriptPath(String scriptName) {
        try {
            // Thử với đường dẫn tương đối
            String fullPath = "classpath:" + pythonScriptPath + "/" + scriptName;
            logger.info("Tìm kiếm script tại: {}", fullPath);
            
            try {
                String scriptPath = resourceLoader.getResource(fullPath).getFile().getAbsolutePath();
                File scriptFile = new File(scriptPath);
                
                if (scriptFile.exists()) {
                    logger.info("Tìm thấy script tại: {}", scriptPath);
                    return scriptPath;
                }
            } catch (Exception e) {
                logger.warn("Không tìm thấy script tại đường dẫn tương đối: {}", fullPath);
            }
            
            // Thử với đường dẫn tuyệt đối
            String absolutePath = "src/main/resources/" + pythonScriptPath + "/" + scriptName;
            logger.info("Thử tìm kiếm script tại đường dẫn tuyệt đối: {}", absolutePath);
            
            File scriptFile = new File(absolutePath);
            if (scriptFile.exists()) {
                logger.info("Tìm thấy script tại: {}", scriptFile.getAbsolutePath());
                return scriptFile.getAbsolutePath();
            }
            
            logger.error("Không tìm thấy file script tại cả hai đường dẫn");
            return null;
        } catch (Exception e) {
            logger.error("Lỗi khi lấy đường dẫn script", e);
            return null;
        }
    }

    private String readProcessOutput(Process process) throws Exception {
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
                logger.info("Process output: {}", line);
            }
        }
        
        // Đọc cả stderr
        try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
            String line;
            while ((line = errorReader.readLine()) != null) {
                output.append("Error: ").append(line).append("\n");
                logger.error("Process error: {}", line);
            }
        }
        
        return output.toString();
    }
} 