package dat3.server_to_server.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import dat3.server_to_server.api_facade.TranslateFacade;
import dat3.server_to_server.entity.Movie;
import dat3.server_to_server.repository.MovieRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;

//ADD DTO CLASSES HERE
class Rating {
  @JsonProperty("Source")
  public String source;
  @JsonProperty("Value")
  public String value;
}

class MovieDTO {
  @JsonProperty("Title")
  public String title;
  @JsonProperty("Year")
  public String year;
  @JsonProperty("Rated")
  public String rated;
  @JsonProperty("Released")
  public String released;
  @JsonProperty("Runtime")
  public String runtime;
  @JsonProperty("Genre")
  public String genre;
  @JsonProperty("Director")
  public String director;
  @JsonProperty("Writer")
  public String writer;
  @JsonProperty("Actors")
  public String actors;
  @JsonProperty("Plot")
  public String plot;
  @JsonProperty("Language")
  public String language;
  @JsonProperty("Country")
  public String country;
  @JsonProperty("Awards")
  public String awards;
  @JsonProperty("Poster")
  public String poster;
  @JsonProperty("Ratings")
  public ArrayList<Rating> ratings;
  @JsonProperty("Metascore")
  public String metascore;
  public String imdbRating;
  public String imdbVotes;
  public String imdbID;
  @JsonProperty("Type")
  public String type;
  @JsonProperty("DVD")
  public String dVD;
  @JsonProperty("BoxOffice")
  public String boxOffice;
  @JsonProperty("Production")
  public String production;
  @JsonProperty("Website")
  public String website;
  @JsonProperty("Response")
  public String response;
}

@Service
public class MovieService {

  MovieRepository movieRepository;
  TranslateFacade translateFacade;

  public MovieService(MovieRepository movieRepository) {
    this.movieRepository = movieRepository;
    translateFacade = new TranslateFacade();
  }

  public Movie getMovieByImdbId(String imdbId) {
    return movieRepository.findByImdbID(imdbId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Movie not found"));
  }

  public Movie getMovieById(int id) {
    return movieRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Movie not found"));
  }

  public Movie addMovie(String imdbId) throws JsonProcessingException {
    RestTemplate restTemplate = new RestTemplate();

    String API_KEY = "8c3085d3";
    String plot = "full";
    String url = "http://www.omdbapi.com/?apikey=" + API_KEY + "&i=" + imdbId + "&plot=" + plot;
    //String url = "http://omdbapi.com/?apikey=8c3085d3&i=tt1392190&plot=full";

    //Fetch the movie
    //String response = restTemplate.getForObject(url, String.class);
    //System.out.println(response);

    //ADD code from snippet file here
    MovieDTO response = restTemplate.getForObject(url, MovieDTO.class);
    String plotDK = translateFacade.translateText(response.plot, 200);

    Movie movie = Movie.builder().title(response.title)
            .year(response.year)
            .rated(response.rated)
            .released(response.released)
            .runtime(response.runtime)
            .genre(response.genre)
            .director(response.director)
            .writer(response.writer)
            .actors(response.actors)
            .metascore(response.metascore)
            .imdbRating(response.imdbRating)
            .imdbVotes(response.imdbVotes)
            .website(response.website)
            .response(response.response)
            .plot(response.plot)
            .plotDK(plotDK)
            .poster(response.poster)
            .imdbID(response.imdbID)
            .build();

    try {
      movie = movieRepository.save(movie);
      return movie;
    } catch (DataIntegrityViolationException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getRootCause().getMessage());
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not add movie");
    }

  }

}
