package test.doctalk.app.service;

import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;
import test.doctalk.app.model.Github;

public interface GithubService {
    String SERVICE_ENDPOINT = "https://api.github.com";

    @GET("/search/users")
    Observable<Github> getUsers(@Query("q") String query,
                                @Query("sort") String sortType,
                                @Query("page_size") int pageSize,
                                @Query("page") int page);
}
