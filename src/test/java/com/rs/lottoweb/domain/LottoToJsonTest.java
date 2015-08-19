package com.rs.lottoweb.domain;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.http.client.fluent.Request;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;



public class LottoToJsonTest {

	public static final int min = 262; //2007-12-08;

	public static final int year = 2007;
	public static final int month = 12;
	public static final int day = 8;

	ClassPathResource lottoResource;
	Gson gson;
	File jsonLocation;
	File sqlLocation;

	@Before
	public void setUp() throws Exception{
		lottoResource = new ClassPathResource("lotto.json");
		gson = new Gson();
		jsonLocation = new File("target", "lotto.json");
		sqlLocation = new File("target", "data.sql");
	}


	@Test
	public void generateJsonFile() throws Exception{

		String url = "http://www.lottonumber.co.kr/ajax.winnum.php?cnt=";
		int current = getCurrentNumber();
		JsonArray jarr = new JsonArray();
		
		//json 읽어옴
		List<LottoHistory> lottoList = readJson();

		int start = 0;
		for(int i = min; i <= current; i++){
			//리스트에서 가져옴
			LottoHistory history = null;
			try {
				history = lottoList.get(start);
			} catch (Exception e) {}

			
			if(history == null || history.getRound() != i){
				//웹에서 제이슨을 가져와 history객체 생성
				String jsonString = Request.Get(url + i).execute().returnContent().asString();
				history = gson.fromJson(jsonString, LottoHistory.class);
				history.setRound(i);
			} else {
				//다음 리스트에서 가져오도록 start 증가
				start++;
			}
			//히스토리를 jsonarray에 추가
			jarr.add(gson.toJsonTree(history));
		}
		//jsonarray를 파일로 씀.
		BufferedWriter bw = new BufferedWriter(new FileWriter(jsonLocation));
		JsonWriter jw = new JsonWriter(bw);
		gson.toJson(jarr, jw);
		jw.flush();
		jw.close();
	}

	
	@Test
	public void generateSQL() throws Exception{
		
		String sql = "INSERT INTO LottoHistory VALUES(%d, %s, %d, %d, %d, %d, %d, %d, %d, %d, ARRAY [%d, %d, %d, %d, %d, %d]);\n";
		BufferedWriter bw = new BufferedWriter(new FileWriter(sqlLocation));
		
		//json 읽어옴
		List<LottoHistory> lottoList = readJson();
		
		//for문 돌림
		for(LottoHistory history : lottoList){
			String insertSql = String.format(sql
					, history.getRound()
					, history.getDat().equals("0000-00-00") ? "null" : "DATE '" + history.getDat() + "'"
					, history.getChucheomgi()
					, history.getNum1_ord()
					, history.getNum2_ord()
					, history.getNum3_ord()
					, history.getNum4_ord()
					, history.getNum5_ord()
					, history.getNum6_ord()
					, history.getNum7()
					, history.getNum1()
					, history.getNum2()
					, history.getNum3()
					, history.getNum4()
					, history.getNum5()
					, history.getNum6());
			
			bw.write(insertSql);
		}
		bw.flush();
		bw.close();
	}
	

	public List<LottoHistory> readJson() {
		try {
			JsonReader jr = new JsonReader(new FileReader(lottoResource.getFile()));
			Type collectionType = new TypeToken<List<LottoHistory>>(){}.getType();
			return gson.fromJson(jr, collectionType);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	public int getCurrentNumber(){
		Calendar old = Calendar.getInstance();
		old.set(year, month - 1, day + 1);

		long diff = new Date().getTime() - old.getTimeInMillis();

		long diffRound = diff / (1000 * 60 * 60 * 24 * 7);
		return (int) diffRound + min;
	}
}
