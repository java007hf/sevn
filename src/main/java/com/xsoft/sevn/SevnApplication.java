package com.xsoft.sevn;

import com.xsoft.sevn.dtk.DTKTask;
import com.xsoft.sevn.webmagic.ZhihuTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SevnApplication implements CommandLineRunner {
	@Autowired
	private ZhihuTask zhihuTask;

	@Autowired
	private DTKTask dtkTask;

	public static void main(String[] args) {
		SpringApplication.run(SevnApplication.class, args);
	}

	@Override
	public void run (String... args) throws Exception {
//		zhihuTask.crawl();
		dtkTask.crawl();
	}
}
