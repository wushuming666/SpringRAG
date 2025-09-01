package org.example;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
        // 强制使用 CPU 版本，避免 CUDA 依赖问题
        System.setProperty("PYTORCH_VERSION", "2.5.1");
        System.setProperty("PYTORCH_FLAVOR", "cpu");

        // 添加 PyTorch 镜像设置
        if (System.getProperty("PYTORCH_MIRROR") == null) {
            System.setProperty("PYTORCH_MIRROR", "https://download.pytorch.org/libtorch");
        }
        SpringApplication.run(Application.class, args);
    }
}
