package de.htwBerlin.ai.kbe.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.htwBerlin.ai.kbe.Contact;
import java.util.Collection;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.htwBerlin.ai.kbe.Song;
import de.htwBerlin.ai.kbe.storage.SongStorage;
import de.htwBerlin.ai.kbe.storage.TokenCreator;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import javax.ws.rs.HeaderParam;

@Path("/songs")
public class Songs {

    // GET http://localhost:8080/songsRX/rest/songs
    // Returns all songs
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getAllSongs(@HeaderParam("authorization") String authString) throws IOException {
        Collection<Song> songs;
        if (!isUserAuth(authString)) {
            return Response.status(Response.Status.FORBIDDEN).entity("User is not authenticated!").build();
        }
        System.out.println("Returns all Songs.");
        songs = SongStorage.getInstance().getAllSongs();
        return Response.ok(songs).build();
    }

    // GET http://localhost:8080/songsRX/rest/songs/10
    // Returns: 200 and song with id 10
    // Returns: 404 on provided id not found
    @GET
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response getSongs(@PathParam("id") Integer id,
            @HeaderParam("authorization") String authString) throws IOException {
        Song song = SongStorage.getInstance().getSong(id);
        if (!isUserAuth(authString)) {
            return Response.status(Response.Status.FORBIDDEN).entity("User is not authenticated!").build();
        }
        if (song != null) {
            System.out.println("Returning Song with ID " + id);
            return Response.ok(song).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity("No song found with id " + id).build();
        }
    }

    // POST http://localhost:8080/songsRX/rest/songs with song in payload
    // Returns: Status Code 201 and the new id of the song in the payload
    // (temp. solution)
    //
    // Besser: Status Code 201 und URI fuer den neuen Eintrag im http-header
    // 'Location' zurueckschicken, also:
    // return Response.created(uriInfo.getAbsolutePath()+<newId>).build();
    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces(MediaType.TEXT_PLAIN)
    public Response createSong(@HeaderParam("authorization") String authString,
            Song song) throws IOException {
        if (!isUserAuth(authString)) {
            return Response.status(Response.Status.FORBIDDEN).entity("User is not authenticated!").build();
        }
        System.out.println("createContact: Received song: " + song.toString());
        return Response.status(Response.Status.CREATED).entity(SongStorage.getInstance().addSong(song)).build();
    }

    // PUT http://localhost:8080/songsRX/rest/song/10 with updated song in
    // payload
    // Returns 204 on successful update
    // Returns 404 on song with provided id not found
    // Returns 400 on id in URL does not match id in song
    @PUT
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path("/{id}")
    public Response updateSong(@PathParam("id") Integer id,
            @HeaderParam("authorization") String authString, Song song) throws IOException {
        boolean update = SongStorage.getInstance().updateSong(song);
        if (!isUserAuth(authString)) {
            return Response.status(Response.Status.FORBIDDEN).entity("User is not authenticated!").build();
        }
        if (!Objects.equals(song.getId(), id)) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("ID in URL " + id + " does not match Song ID " + song.getId()).build();
        } else if (update) {
            System.out.println("Updating Song with " + id);
            return Response.status(Response.Status.NO_CONTENT).build();
        } else if (!update) {
            return Response.status(Response.Status.NOT_FOUND).entity("No song found with id " + id).build();
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }

    // DELETE http://localhost:8080/songsRX/rest/contacts/1
    // Returns 204 on successful delete
    // Returns 404 on provided id not found
    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Integer id,
            @HeaderParam("authorization") String authString) throws IOException {
        Song song = SongStorage.getInstance().deleteSong(id);
        if (!isUserAuth(authString)) {
            return Response.status(Response.Status.FORBIDDEN).entity("User is not authenticated!").build();
        }
        if (song != null) {
            return Response.status(Response.Status.NO_CONTENT).entity("Song was deleted " + id).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity("No song found with id " + id).build();
        }
    }

    private boolean isUserAuth(String authString) throws IOException {
        if (authString != null) {
            List<String> tokenList = TokenCreator.getInstance().getTokenList();
            if (tokenList.stream().anyMatch((token) -> (token.equals(authString)))) {
                return true;
            }
        }
        return false;
    }
}
