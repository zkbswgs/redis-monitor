package com.redis.monitor.redis.quartz;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;

import com.ibm.icu.text.SimpleDateFormat;
import com.redis.monitor.RedisJedisPool;
import com.redis.monitor.json.FastJson;
import com.redis.monitor.redis.BasicRedisCacheServer;

public class RedisMemeryJob {
	
	public void saveMemeryData() {
		 Set<String> uuidSet = RedisJedisPool.getRedisUuids();
		    if (uuidSet != null && uuidSet.size() > 0) {
		    	for (Iterator<String> itr = uuidSet.iterator(); itr.hasNext();) {
		    		String key = itr.next();
		    		FileOutputStream outputStream = null;
					try {
						SimpleDateFormat dayFormat = new SimpleDateFormat("yyyyMMdd");
					//	File root = new File(Thread.currentThread().getContextClassLoader().getResource("").getFile()).getParentFile();
						
						String fileName = "memery-" + key + "-" + dayFormat.format(new Date()) + ".txt";
						File file = new File(Thread.currentThread().getContextClassLoader().getResource("").getFile() + "\\monitor-log\\keys\\" + fileName);
						if (!file.exists()) {
							file.createNewFile();
						}
						outputStream = new FileOutputStream(file,true);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					BasicRedisCacheServer redisCacheServer = RedisJedisPool.getRedisCacheServer(key);
					String[] strs = redisCacheServer.getRedisInfo().split("\n");
				    for (int i = 0; i < strs.length; i++) {
				    	String s = strs[i];
				    	String[] detail = s.split(":");
				    	if (detail[0].equals("used_memory_human")) {
				    	  //TODO 写入文件
				    	  SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				    	  Map<String,String> map = new HashMap<String, String>();
				    	  map.put("used_memory_human", detail[1].substring(0, detail[1].length() - 1));
				    	  map.put("create_time", dateFormat.format(new Date()));
				    	  
				    	  try {
							IOUtils.write(FastJson.toJson(map) + "\n", outputStream);
						} catch (IOException e) {
							e.printStackTrace();
						}
				    	  break;
				    	}
				    }
				}
		    		
		    }
	}
	

}
