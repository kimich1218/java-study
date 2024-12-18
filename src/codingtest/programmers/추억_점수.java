package codingtest.programmers;

import java.util.HashMap;
import java.util.Map;

/**
 * 단순 구현 문제
 * HashMap을 사용하여 name과 year를 지정해주었음
 * HashMap의 containsKey를 활용하여 해당 name이 있으면 year를 더해주었음
 * 해당 사진마다 반복해야 해결
 */
public class 추억_점수 {
    public int[] solution(String[] name, int[] yearning, String[][] photo) {
        int[] answer = new int[photo.length];
        Map<String, Integer> map = new HashMap<>();
        for(int i = 0; i < name.length; i++){
            map.put(name[i], yearning[i]);
        }
        for(int i = 0; i < photo.length; i++){
            int sum = 0;
            for(int j = 0; j < photo[i].length; j++){
                if(map.containsKey(photo[i][j])){
                    sum += map.get(photo[i][j]);
                }
            }
            answer[i] = sum;
        }
        return answer;
    }
}
