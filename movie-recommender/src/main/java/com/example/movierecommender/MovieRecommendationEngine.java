package com.example.movierecommender;

// Import required Mahout libraries for recommendation system
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

// Import Java IO and utilities
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Movie Recommendation Engine using Apache Mahout
 * Implements both user-based and item-based collaborative filtering
 */
public class MovieRecommendationEngine {
    
    // Core components for recommendations
    private DataModel model;                  // Stores user-item rating data
    private UserBasedRecommender userBasedRecommender;  // User-based recommendation engine
    private GenericItemBasedRecommender itemBasedRecommender;  // Item-based recommendation engine
    private Map<Long, String> movieTitles;    // Maps movie IDs to titles

    /**
     * Constructor - Initializes the recommendation engine
     * @param ratingsFile Path to CSV file containing user ratings
     * @param moviesFile Path to CSV file containing movie titles
     */
    public MovieRecommendationEngine(String ratingsFile, String moviesFile) 
            throws IOException, TasteException {
        
        // Step 1: Load rating data into Mahout's data model
        this.model = new FileDataModel(new File(ratingsFile));
        
        // Step 2: Load movie titles into memory
        this.movieTitles = loadMovieTitles(moviesFile);
        
        // Step 3: Initialize user-based recommendation components
        // - Calculate user similarity using Pearson correlation
        UserSimilarity userSimilarity = new PearsonCorrelationSimilarity(model);
        // - Find neighborhood of similar users (5 nearest neighbors)
        UserNeighborhood neighborhood = new NearestNUserNeighborhood(5, userSimilarity, model);
        // - Create user-based recommender
        this.userBasedRecommender = new GenericUserBasedRecommender(model, neighborhood, userSimilarity);
        
        // Step 4: Initialize item-based recommendation components
        // - Calculate item similarity using Pearson correlation
        ItemSimilarity itemSimilarity = new PearsonCorrelationSimilarity(model);
        // - Create item-based recommender
        this.itemBasedRecommender = new GenericItemBasedRecommender(model, itemSimilarity);
    }

    /**
     * Loads movie titles from CSV file into memory
     * @param moviesFile Path to CSV file containing movie IDs and titles
     * @return Map of movie IDs to titles
     */
    private Map<Long, String> loadMovieTitles(String moviesFile) throws IOException {
        Map<Long, String> titles = new HashMap<>();
        // Read all lines from the CSV file
        List<String> lines = Files.readAllLines(Paths.get(moviesFile));
        
        // Parse each line: format is "movieId,movieTitle"
        for (String line : lines) {
            String[] parts = line.split(",");
            if (parts.length >= 2) {
                // Add to map: key=movieId, value=movieTitle
                titles.put(Long.parseLong(parts[0]), parts[1].trim());
            }
        }
        return titles;
    }

    /**
     * Generates user-based recommendations
     * @param userId ID of user to get recommendations for
     * @param numRecommendations Number of recommendations to return
     * @return List of recommended movie titles with scores
     */
    public List<String> getUserBasedRecommendations(long userId, int numRecommendations) 
            throws TasteException {
        // Get raw recommendations from Mahout
        List<RecommendedItem> recommendations = userBasedRecommender.recommend(userId, numRecommendations);
        // Convert to readable movie titles
        return convertToMovieTitles(recommendations);
    }

    /**
     * Generates item-based recommendations
     * @param userId ID of user to get recommendations for
     * @param numRecommendations Number of recommendations to return
     * @return List of recommended movie titles with scores
     */
    public List<String> getItemBasedRecommendations(long userId, int numRecommendations) 
            throws TasteException {
        // Get raw recommendations from Mahout
        List<RecommendedItem> recommendations = itemBasedRecommender.recommend(userId, numRecommendations);
        // Convert to readable movie titles
        return convertToMovieTitles(recommendations);
    }

    /**
     * Converts Mahout's RecommendedItems to formatted strings
     * @param items List of recommended items
     * @return List of formatted strings "MovieTitle (score: X.XX)"
     */
    private List<String> convertToMovieTitles(List<RecommendedItem> items) {
        List<String> result = new ArrayList<>();
        for (RecommendedItem item : items) {
            // Look up movie title by ID
            String title = movieTitles.get(item.getItemID());
            if (title != null) {
                // Format as "Title (score: X.XX)"
                result.add(title + " (score: " + String.format("%.2f", item.getValue()) + ")");
            }
        }
        return result;
    }

    /**
     * Main entry point for the recommendation system
     */
    public static void main(String[] args) {
        try {
            // Use absolute paths for reliability
            String ratingsPath = new File("src/main/resources/movie_ratings.csv").getAbsolutePath();
            String titlesPath = new File("src/main/resources/movie_titles.csv").getAbsolutePath();
            
            // Initialize recommendation engine
            MovieRecommendationEngine engine = new MovieRecommendationEngine(ratingsPath, titlesPath);
            
            // Generate and display recommendations for user 1
            System.out.println("\nUser-Based Recommendations:");
            engine.getUserBasedRecommendations(1, 5).forEach(System.out::println);
            
            System.out.println("\nItem-Based Recommendations:");
            engine.getItemBasedRecommendations(1, 5).forEach(System.out::println);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
