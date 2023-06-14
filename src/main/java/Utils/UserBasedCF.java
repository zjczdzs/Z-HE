package Utils;

import java.util.*;


public class UserBasedCF {
    private Map<Integer, Map<Integer, Double>> userItemRatingMatrix;  // 用户-物品评分矩阵
    private Map<Integer, Map<Integer, Double>> userSimilarityMatrix;  // 用户相似度矩阵

    public UserBasedCF(Map<Integer, Map<Integer, Double>> userItemRatingMatrix) {
        this.userItemRatingMatrix = userItemRatingMatrix;
        this.userSimilarityMatrix = new HashMap<Integer, Map<Integer, Double>>();
        calculateUserSimilarity();  // 计算用户相似度
    }

    // 按照Map的值进行降序排序
    public static Map<Integer, Double> sortByValueDescending(Map<Integer, Double> map) {
        List<Map.Entry<Integer, Double>> list = new LinkedList<Map.Entry<Integer, Double>>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<Integer, Double>>() {
            @Override
            public int compare(Map.Entry<Integer, Double> o1, Map.Entry<Integer, Double> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });

        Map<Integer, Double> result = new LinkedHashMap<Integer, Double>();
        for (Map.Entry<Integer, Double> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    // 计算用户相似度
    private void calculateUserSimilarity() {
        for (int user1 : userItemRatingMatrix.keySet()) {
            for (int user2 : userItemRatingMatrix.keySet()) {
                if (user1 != user2) {
                    double similarity = cosineSimilarity(user1, user2);  // 计算用户相似度
                    if (similarity > 0) {
                        if (!userSimilarityMatrix.containsKey(user1)) {
                            userSimilarityMatrix.put(user1, new HashMap<Integer, Double>());
                        }
                        userSimilarityMatrix.get(user1).put(user2, similarity);
                    }
                }
            }
        }
    }

    // 计算用户之间的余弦相似度
    private double cosineSimilarity(int user1, int user2) {
        Map<Integer, Double> user1Ratings = userItemRatingMatrix.get(user1);
        Map<Integer, Double> user2Ratings = userItemRatingMatrix.get(user2);

        double dotProduct = 0;
        double user1Norm = 0;
        double user2Norm = 0;

        for (int item : user1Ratings.keySet()) {
            if (user2Ratings.containsKey(item)) {
                dotProduct += user1Ratings.get(item) * user2Ratings.get(item);
            }
            user1Norm += Math.pow(user1Ratings.get(item), 2);
        }

        for (int item : user2Ratings.keySet()) {
            user2Norm += Math.pow(user2Ratings.get(item), 2);
        }

        double similarity = 0;
        if (user1Norm != 0 && user2Norm != 0) {
            similarity = dotProduct / (Math.sqrt(user1Norm) * Math.sqrt(user2Norm));
        }

        return similarity;
    }

    // 推荐Top-N个物品给指定的用户
    public Map<Integer, Double> recommend(int user, int N) {
        Map<Integer, Double> recommendations = new HashMap<Integer, Double>();
        Map<Integer, Double> userRatings = userItemRatingMatrix.get(user);

        for (int item : userItemRatingMatrix.keySet()) {
            if (!userRatings.containsKey(item)) {
                double score = predict(user, item);  // 预测用户对物品的评分
                recommendations.put(item, score);
            }
        }

        Map<Integer, Double> sortedRecommendations = sortByValueDescending(recommendations);
        Map<Integer, Double> topNRecommendations = new HashMap<Integer, Double>();
        int i = 0;
        for (int item : sortedRecommendations.keySet()) {
            topNRecommendations.put(item, sortedRecommendations.get(item));
            i++;
            if (i >= N) {
                break;
            }
        }

        return topNRecommendations;
    }

    // 预测用户对物品的评分
    private double predict(int user, int item) {
        Map<Integer, Double> similarUsers = userSimilarityMatrix.get(user);
        double weightedRatingSum = 0;
        double similaritySum = 0;

        for (int otherUser : similarUsers.keySet()) {
            if (userItemRatingMatrix.get(otherUser).containsKey(item)) {
                double similarity = similarUsers.get(otherUser);
                double rating = userItemRatingMatrix.get(otherUser).get(item);
                weightedRatingSum += similarity * rating;
                similaritySum += similarity;
            }
        }

        double score = 0;
        if (similaritySum != 0) {
            score = weightedRatingSum / similaritySum;
        }

        return score;
    }
}


