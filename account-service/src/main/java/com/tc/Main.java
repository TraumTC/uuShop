package com.tc;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.VelocityTemplateEngine;
import com.baomidou.mybatisplus.generator.fill.Column;

import java.util.Collections;

public class Main {
    public static void main(String[] args) {
        String projectPath = System.getProperty("user.dir");
        String outputDir = projectPath + "/account-service/src/main/java";

        FastAutoGenerator.create("jdbc:mysql://localhost:3306/uushop", "root", "123456")
                .globalConfig(builder -> {
                    builder.author("tc")
                            .outputDir(outputDir)
                            .commentDate("yyyy-MM-dd")
                            .dateType(DateType.TIME_PACK)
                            .disableOpenDir();
                })
                .packageConfig(builder -> {
                    builder.parent("com.tc")
                            .entity("entity")
                            .mapper("mapper")
                            .service("service")
                            .serviceImpl("service.impl")
                            .controller("controller")
                            .pathInfo(Collections.singletonMap(OutputFile.xml, projectPath + "/account-service/src/main/resources/mapper"));
                })
                .strategyConfig(builder -> {
                    builder.addInclude("user", "admin")
                            .addTablePrefix("t_", "c_")
                            .entityBuilder()
                            .enableLombok()
                            .naming(NamingStrategy.underline_to_camel)
                            .columnNaming(NamingStrategy.underline_to_camel)
                            .addTableFills(
                                    new Column("create_time", com.baomidou.mybatisplus.annotation.FieldFill.INSERT),
                                    new Column("update_time", com.baomidou.mybatisplus.annotation.FieldFill.INSERT_UPDATE)
                            )
                            .enableTableFieldAnnotation();

                    builder.controllerBuilder()
                            .enableRestStyle()
                            .enableHyphenStyle();

                    builder.serviceBuilder()
                            .formatServiceFileName("%sService")
                            .formatServiceImplFileName("%sServiceImpl");
                })
                .templateEngine(new VelocityTemplateEngine())
                .execute();
    }
}
