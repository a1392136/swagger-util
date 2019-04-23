package com.xws.docdog;


import com.xws.docdog.service.SwaggerParseService;

import java.io.File;
import java.io.IOException;

public class DocdogApplication {

    public static void main(String[] args) throws IOException {
        File swaggerApiJsonFile = new File("D:\\cm-api.json");
        File saveFile = new File("D:\\cm-doc.xls");
        SwaggerParseService swaggerParseService = new SwaggerParseService();
        swaggerParseService.generateApiDoc(swaggerApiJsonFile, saveFile);
    }
}
