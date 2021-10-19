package danzao;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static danzao.GCJ02_WGS84.gcj02_To_Wgs84;
import static danzao.GCJ02_WGS84.wgs84_To_Gcj02;

/**
 * @author lincanxu
 * @create 2021-10-19 10:38
 * @desc
 **/
public class MergeCode {

    public static void main(String[] args) throws IOException {
        String filePath = "D://项目_丹灶//河流坐标";
        ArrayList sb = new ArrayList();
        File[] tempList = new File(filePath).listFiles();
        for (int i = 0; i < tempList.length; i++) {
            if (tempList[i].isFile()) {
                readTxtFile(tempList[i].getPath(),sb);
            }
        }
        System.out.println(sb.toString());
    }

    public static void readTxtFile(String filePath,ArrayList river){
        try {
            String encoding="UTF-8";
            File file=new File(filePath);
            if(file.isFile() && file.exists()){ //判断文件是否存在
                InputStreamReader read = new InputStreamReader(
                        new FileInputStream(file),encoding);//考虑到编码格式
                BufferedReader bufferedReader = new BufferedReader(read);
                StringBuffer sb = new StringBuffer();
                String lineTxt = null;
                while((lineTxt = bufferedReader.readLine()) != null){
                    sb.append(lineTxt);
                }
                read.close();
                JSONObject data = new JSONObject(sb.toString());
                JSONArray features = data.getJSONArray("features");
                for (int i = 0; i < features.length(); i++) {
                    JSONObject jsonObject = features.getJSONObject(i);
                    JSONObject geometry = jsonObject.getJSONObject("geometry");
                    JSONArray coordinates = geometry.getJSONArray("coordinates");
                    for (int j = 0; j < coordinates.length(); j++) {
                        JSONArray jsonArray = coordinates.getJSONArray(j);
                        LocateInfo locateInfo = wgs84_To_Gcj02(jsonArray.getDouble(1), jsonArray.getDouble(0));
                        jsonArray.put(0,locateInfo.getLongitude());
                        jsonArray.put(1,locateInfo.getLatitude());
                        coordinates.put(j,jsonArray);
                    }
                    geometry.put("coordinates",coordinates);
                    jsonObject.put("geometry",geometry);
                    river.add(jsonObject.toString());
                }
            }else{
                System.out.println("找不到指定的文件");
            }
        } catch (Exception e) {
            System.out.println("读取文件内容出错");
            e.printStackTrace();
        }

    }
}
