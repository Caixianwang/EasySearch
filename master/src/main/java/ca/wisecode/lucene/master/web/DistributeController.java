package ca.wisecode.lucene.master.web;

import ca.wisecode.lucene.master.grpc.client.distribute.balance.BalanceService;
import ca.wisecode.lucene.master.grpc.client.distribute.remove.RemoveService;
import ca.wisecode.lucene.master.service.index.IndexBS;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: caixianwang2022@gmail.com
 * @date: 9/23/2024 11:58 PM
 * @Version: 1.0
 * @description:
 */
@RestController
@RequestMapping("/api/distribute")
@Slf4j
public class DistributeController {
    @Autowired
    private IndexBS indexBS;
    @Autowired
    private BalanceService balanceService;
    @Autowired
    private RemoveService removeService;

    @GetMapping("/test")
    public String test() {
        return "test";
    }

    @GetMapping("/balance")
    public String balance() {
        balanceService.balance();
        return "balanceService";
    }

    @GetMapping("/remove")
    public String remove(@RequestParam("host") String host, @RequestParam("port") int port) {
        removeService.remove(host, port);
        return "removeService";
    }

    @GetMapping("/startSlave")
    public String startSlave(@RequestParam("serverPort") int serverPort, @RequestParam("port") int port,@RequestParam("indexPath") String indexPath) {
        ProcessBuilder processBuilder = new ProcessBuilder();
        log.info("---start----");
        // 设置要执行的 bat 文件路径及参数
        List<String> command = new ArrayList<>();
        command.add("cmd.exe");  // 调用 cmd
        command.add("/c");  // 使用 /c 来执行命令并关闭窗口
        command.add("start"); // 使用 start 命令来在新窗口中打开
        command.add("cmd.exe"); // 启动另一个 cmd 窗口
        command.add("/k"); // 使用 /k 以保持窗口打开
        command.add("title port "+serverPort+" && E:\\IntelliJSpace\\runSlave.bat"); // .bat 文件的路径
        command.add(""+serverPort);
        command.add(""+port);
        command.add(indexPath);

        // 配置 ProcessBuilder 的命令
        processBuilder.command(command);
//        processBuilder.inheritIO(); // 将标准输入输出错误流重定向到当前进程

        try {
            // 启动进程
            Process process = processBuilder.start();
            log.info("---------------");
            // 等待进程结束并获取退出状态
//            int exitCode = process.waitFor();
//            log.info("Exit Code: " + exitCode);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return "startProcess";
    }
}
