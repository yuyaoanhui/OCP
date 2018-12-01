package com.ocp.rabbit;

import org.mybatis.generator.api.ShellRunner;

/**
 * 运行Mybatis generator 生成代码
 * 
 * @author yu.yao
 *
 */
public class MybatisShellRun {
	public static void main(String[] args) {
		String configs = MybatisShellRun.class.getClassLoader().getResource("mybatis/config/mybatisGeneratorConfig.xml")
				.getFile();
		String[] arg = { "-configfile", configs, "-overwrite" };
		ShellRunner.main(arg);
	}
}