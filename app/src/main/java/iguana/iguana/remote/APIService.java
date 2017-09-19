package iguana.iguana.remote;

import iguana.iguana.models.Comment;
import iguana.iguana.models.CommentResult;
import iguana.iguana.models.Issue;
import iguana.iguana.models.IssueResult;
import iguana.iguana.models.NotificationResult;
import iguana.iguana.models.Project;
import iguana.iguana.models.ProjectResult;
import iguana.iguana.models.Timelog;
import iguana.iguana.models.TimelogResult;

import java.util.HashMap;
import java.util.Map;

import iguana.iguana.models.Token;
import iguana.iguana.models.UserResult;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

public interface APIService {

    @GET("api/")
    Call<Object> checkStatus(
    );

    @GET("api/projects/{name_short}")
    Call<Project> getProject(
            @Path("name_short") String name_short
    );

    @PUT("api/projects/{name_short}/")
    Call<Project> editProject(
            @Path("name_short") String name_short,
            @Body HashMap<String, Object> body
    );

    @DELETE("api/projects/{name_short}/")
    Call<Object> deleteProject(
            @Path("name_short") String name_short
    );

    @GET("api/projects/{name_short}/issues/?archived=false")
    Call<IssueResult> getProjectIssues(
            @Path("name_short") String name_short,
            @QueryMap Map<String, String> options
    );

    @GET("api/users/")
    Call<UserResult> getUsers(
            @QueryMap Map<String, String> options
    );

    @GET("api/projects/{name_short}/issues/{number}")
    Call<Issue> getProjectSpecificIssue(
            @Path("name_short") String name_short,
            @Path("number") long number
    );

    @GET("api/projects/{name_short}/issues/{issue_number}/timelogs/{timelog_number}")
    Call<Timelog> getProjectSpecificIssueSpecificTimelog(
            @Path("name_short") String name_short,
            @Path("issue_number") long issue_number,
            @Path("timelog_number") long timelog_number
    );

    @DELETE("api/projects/{name_short}/issues/{issue_number}/timelogs/{timelog_number}/")
    Call<Object> deleteTimelog(
            @Path("name_short") String name_short,
            @Path("issue_number") long issue_number,
            @Path("timelog_number") long timelog_number
    );

    @GET("api/projects/")
    Call<ProjectResult> getProjects(
    );

    @GET("api/notifications/")
    Call<NotificationResult> getNotifications(
    );

    @DELETE("api/notifications/{issue}/")
    Call<Object> deleteNotification(
            @Path("issue") String issue
            );


    @POST("api/projects/{name_short}/issues/")
    Call<Issue> createIssue(
            @Path("name_short") String name_short,
            @Body HashMap<String, Object> body
    );

    @PUT("api/projects/{name_short}/issues/{issue_number}/")
    Call<Issue> editIssue(
            @Path("name_short") String name_short,
            @Path("issue_number") Integer number,
            @Body HashMap<String, Object> body
    );

    @PATCH("api/projects/{name_short}/issues/{issue_number}/")
    Call<Issue> patchIssue(
            @Path("name_short") String name_short,
            @Path("issue_number") Integer number,
            @Body HashMap<String, Object> body
    );



    @PUT("api/projects/{name_short}/issues/{issue_number}/comments/{comment_number}/")
    Call<Comment> editComment(
            @Path("name_short") String name_short,
            @Path("issue_number") Integer number,
            @Path("comment_number") Integer seqnum,
            @Body HashMap<String, Object> body
    );

    @PUT("api/projects/{name_short}/issues/{issue_number}/timelogs/{log_number}/")
    Call<Timelog> editTimelog(
            @Path("name_short") String name_short,
            @Path("issue_number") Integer number,
            @Path("log_number") Integer seqnum,
            @Body HashMap<String, Object> body
    );

    @DELETE("api/projects/{name_short}/issues/{issue_number}/comments/{comment_number}/")
    Call<Object> deleteComment(
            @Path("name_short") String name_short,
            @Path("issue_number") Integer number,
            @Path("comment_number") Integer seqnum
    );

    @POST("api/projects/")
    Call<Project> createProject(
            @Body HashMap<String, Object> body
    );


    @GET("api/projects/{name_short}/issues/{number}")
    Call<Issue> getIssue(
            @Path("name_short") String name_short,
            @Path("number") String number
    );


    @GET("api/issues/?archived=false")
    Call<IssueResult> getIssues(
            @QueryMap Map<String, String> options
    );

    @GET("api/timelogs/")
    Call<TimelogResult> getTimelogs(
            @QueryMap Map<String, String> options
    );

    @GET("api/projects/{name_short}/issues/{issue_number}/timelogs/")
    Call<TimelogResult> getTimelogsForIssue(
            @Path("name_short") String name_short,
            @Path("issue_number") Integer number,
            @QueryMap Map<String, String> options
    );

    @GET("api/projects/{name_short}/issues/{issue_number}/comments/")
    Call<CommentResult> getComments(
            @Path("name_short") String name_short,
            @Path("issue_number") Integer number,
            @QueryMap Map<String, String> options
    );

    @POST("api/projects/{name_short}/issues/{issue_number}/comments/")
    Call<Comment> createComment(
            @Path("name_short") String name_short,
            @Path("issue_number") Integer number,
            @Body HashMap<String, String> body
    );

    @POST("api/projects/{name_short}/issues/{issue_number}/timelogs/")
    Call<Timelog> createTimelog(
            @Path("name_short") String name_short,
            @Path("issue_number") Integer number,
            @Body HashMap<String, String> body
    );

    @POST("api-token-auth/")
    Call<Token> getToken(
            @Body HashMap<String, String> body
    );

    @POST("delegate/")
    Call<Token> refreshToken(
            @Body HashMap<String, String> body
    );

}