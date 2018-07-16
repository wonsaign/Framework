package demo;

import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class CacheDemo {

	public static void main(String[] args) {
		LoadingCache<String, String> cache = CacheBuilder.newBuilder()
				.maximumSize(1000).expireAfterAccess(1, TimeUnit.SECONDS)
				.refreshAfterWrite(1, TimeUnit.MINUTES)
				.build(new CacheLoader<String, String>() {

					@Override
					public String load(String arg0) throws Exception {
						return arg0 + ":" + System.currentTimeMillis();
					}
				});

		try {
//			cache.put("aa", "bb");
			System.out.println(cache.get("aa"));
			Thread.sleep(500);
			System.out.println(cache.get("aa"));
			Thread.sleep(2000);
			cache.getIfPresent("aa");
			System.out.println(cache.get("aa"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}

	public static <K, V> Cache<K, V> callableCached() throws Exception {
		Cache<K, V> cache = CacheBuilder.newBuilder().maximumSize(10000)
				.expireAfterWrite(10, TimeUnit.MINUTES).build();
		return cache;
	}
}