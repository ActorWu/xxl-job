package com.xxl.job.executor.service.jobhandler;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeUnit;


/**
 * 任务Handler示例（Bean模式）
 *
 * 开发步骤：
 * 1、继承"IJobHandler"：“com.xxl.job.core.handler.IJobHandler”；
 * 2、注册到Spring容器：添加“@Component”注解，被Spring容器扫描为Bean实例；
 * 3、注册到执行器工厂：添加“@JobHandler(value="自定义jobhandler名称")”注解，注解value值对应的是调度中心新建任务的JobHandler属性的值。
 * 4、执行日志：需要通过 "XxlJobLogger.log" 打印执行日志；
 *
 * @author xuxueli 2015-12-19 19:43:36
 */
@JobHandler(value="restartJobHandler")
@Component
public class RestartJobHandler extends IJobHandler {

	@Override
	public ReturnT<String> execute(String param) throws Exception {
		XxlJobLogger.log("检测联合利华wsp系统是否正常运行");

		try {
			XxlJobLogger.log("访问系统");
			getConnection();
			XxlJobLogger.log("访问正常");
		} catch (IOException e) {
			e.printStackTrace();
            restart();
		}


		return SUCCESS;
	}


	private static void restart() {
		System.out.println("==============重启服务==================");
		OkHttpClient client = new OkHttpClient().newBuilder()
				.build();
		Request request = new Request.Builder()
				.url("https://cs.console.aliyun.com/hook/trigger?token=eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJjbHVzdGVySWQiOiJjZTIzY2RlYzAzZTMxNDkzZjk3NjZiOGE5YTUwZTZiMzUiLCJpZCI6Ijk1MjA4In0.q9Ny06TJ7BhPunWA8zvDGwedeqU2wUrx_XG1pLN6XaudFgDRRIKsQ9SblIMmF-0Gpqju9kKES-LwCFiwJOT3YFSn-NFZZZM-9gqlreu7oH6pSs1PtbhuMSxQFrsKiRHZu2OIYe9IZ3Rcl9YKlqw6ras7Z-PpQdeV7SCv-jKRYf8")
				.method("GET", null)
				.build();
		try {
			Response response = client.newCall(request).execute();
			String msg = response.body().string();
			System.out.println(msg);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("==================重启失败=====================");
		}
	}


	public static void getConnection() throws IOException {
		OkHttpClient client = new OkHttpClient().newBuilder().connectTimeout(5, TimeUnit.SECONDS).readTimeout(5,TimeUnit.SECONDS)
				.build();
		// 设置连接时间
		Request request = new Request.Builder()
                .url("http://wsp.ishanggang.com/ultufawsp/security/login.action")
//				.url("http://localhost:18181/ehr/employee_info/list?storeCode=&regionId=&provinceId=&cityId=&employeeCode=&usernameCh=&jobInfoId=&idCard=&superiorAe=&subordinateSe=&recordCode=&userStatus=&current=1&size=10")
				.method("GET", null)
				.addHeader("Cookie", "SESSION=0af1ef99-af39-4dfa-95c1-1600671772eb; INGRESSCOOKIE=1608281226.608.11831.887564")
				.build();
		Response response = client.newCall(request).execute();

		String body = response.body().string();
        System.out.println("访问正常");
	}
}
