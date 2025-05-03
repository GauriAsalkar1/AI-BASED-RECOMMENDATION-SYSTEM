package com.example.movierecommender;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class MovieRecommendationEngine {

    public static void main(String[] args) {
        try {
            // Use absolute paths for Windows
            String ratingsPath = new File("src/main/resources/movie_ratings.csv").getAbsolutePath();
            String titlesPath = new File("src/main/resources/movie_titles.csv").getAbsolutePath();
            
            MovieRecommendationEngine engine = new MovieRecommendationEngine(ratingsPath, titlesPath);
            
            System.out.println("\nUser-Based Recommendations:");
            engine.getUserBasedRecommendations(1, 5).forEach(System.out::println);
            
            System.out.println("\nItem-Based Recommendations:");
            engine.getItemBasedRecommendations(1, 5).forEach(System.out::println);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

  
}
