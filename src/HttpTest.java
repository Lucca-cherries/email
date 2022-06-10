package src;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class HttpTest {
    public static void main(String[] args) throws IOException {
//        HttpClient httpClient = new HttpClient();

        String st = "$$(\"#username\")[0].value = \"test_jgq\";\n" +
                "$$(\"#password\")[0].value = \"Jgq12345\";\n" +
                "$$(\"#phone\")[0].value = \"18512527646\";\n" +
                "$(\"#phoneCodeButton\").click();";

        HttpClient.doPost(
                "https://mail.163.com/register/index.htm?from=126mail&utm_source=126mail",
        st);

//        String code = httpClient.doGet("http://zhjw.scu.edu.cn/img/captcha.jpg");
//        System.out.println(code);
//
//        File file =new File("1.txt");
//
//        if(!file.exists()){
//            file.createNewFile();
//        }
//
//        //使用true，即进行append file
//
//        FileWriter fileWriter = new FileWriter(file.getName());
//
//
//        fileWriter.write(code);
//
//        fileWriter.close();


//        String instructor = "$$(\"#input_username\")[0].value=2020141450013;\n";
//        instructor += "$$(\"#input_password\")[0].value=\"@Jgq164612\";\n";
//        $$("#input_checkcode")[0].value=\"8py5\"
//        $("#loginButton").click()
//
//        httpClient.doPost("http://zhjw.scu.edu.cn/login", instructor);

    }
}
